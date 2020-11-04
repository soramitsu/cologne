// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

import "./../token/ERC20/SafeERC20.sol";
import "./../token/ERC20/IEAUToken.sol";
import "./../token/ERC20/ICLGNToken.sol";
import "./IVault.sol";
import "./ICologneDAO.sol";
import "./../math/SafeMath.sol";
import "./../math/Math.sol";
import "./ITimeProvider.sol";
import "./../utils/Ownable.sol";
import "../token/ERC20/IUserToken.sol";

contract Vault is IVault, Ownable {
    using SafeMath for uint;
    using SafeERC20 for IUserToken;
    using SafeERC20 for IEAUToken;
    using SafeERC20 for ICLGNToken;

    ICologneDAO _cologneDao;

    IEAUToken _eauToken;
    ICLGNToken _clgnToken;

    // Owner ERC20 token
    IUserToken _userToken;

    // amount of ERC20 token the owner has deposited
    uint _tokenAmount;

    // Owner token price assessed by the owner in attoEAU (10^-18 EAU for 1 UserToken)
    uint _price = 0;

    // Initial amount of loan without fees
    uint _principal = 0;

    uint _feeAccrued = 0;

    struct Stake {
        bool stakeholder; // flag to show that address is recorded as stakeholder
        uint stake; // stake amount by address in CLGN
        uint rewardAccrued; // reward accrued for co-staking in EAU
    }

    mapping(address => Stake) _stakeByUser;
    address[] _stakeholders; // all stake holders
    uint _coStakerRewardAccrued; // total reward for costakeholders
    uint _stakeTotal; // amount staked by all stakeholders
    uint _coStakerRewardStash;

    uint _debtUpdateTime;

    uint _limitBreachedTime = 0;

    // Total repaid in fees accrued for fee discount
    uint _totalFeesRepaid = 0;

    ITimeProvider _timeProvider;

    // Close-out state - Initial Liquidity Dutch Auction started
    uint _closeOutTime = 0;
    address _closeOutInitiator;

    bool _slashed = false;
    bool _closed = false;

    /**
     * Challenge info - price and EAU amount locked
     */
    struct Challenge {
        uint price;
        uint eauAmountToLock;
    }

    // All challenges (challenger => challenge info)
    mapping(address => Challenge) _challengers;

    // Challenger address with highest price
    address _challengeWinnerAddress = address(0);

    // EAU reserved for challengers
    uint eauForChallengers = 0;

    modifier notSlashed() {
        require(!_slashed, "Vault is slashed");
        _;
    }

    modifier closed {
        require(_closed, "Vault is not closed");
        _;
    }

    modifier notClosed {
        require(!_closed, "Vault is closed");
        _;
    }

    modifier initialAuctionIsOver {
        require(_initialAuctionIsOver(), "Vault: initial auction is not finished yet");
        _;
    }

    modifier initialAuctionIsNotOver {
        require(!_slashed && !_closed && !_initialAuctionIsOver(), "Vault: initial auction is over");
        _;
    }

    constructor(
        address owner,
        address token,
        uint initialAmount,
        uint tokenPrice,
        ITimeProvider timeProvider) Ownable(owner) {
        _cologneDao = ICologneDAO(msg.sender);
        _eauToken = IEAUToken(_cologneDao.getEauTokenAddress());
        _clgnToken = ICLGNToken(_cologneDao.getClgnTokenAddress());
        _userToken = IUserToken(token);
        _tokenAmount = initialAmount;
        _price = tokenPrice;
        _timeProvider = timeProvider;
        _debtUpdateTime = _timeProvider.getTime();
    }

    function getTokenAddress() public override view returns (address) {
        return address(_userToken);
    }

    function stake(uint clgnAmount) notClosed public override {
        _clgnToken.safeTransferFrom(msg.sender, address(this), clgnAmount);
        _updateDebt();
        _stakeByUser[msg.sender].stake = _stakeByUser[msg.sender].stake.add(clgnAmount);
        _stakeTotal = _stakeTotal.add(clgnAmount);
        // add stakeholder to the list of stakeholders
        if (!_stakeByUser[msg.sender].stakeholder) {
            _stakeholders.push(msg.sender);
            _stakeByUser[msg.sender].stakeholder = true;
        }
    }

    // @dev See {IVault-getStake}
    function getStake(address account) public override view returns (uint) {
        return _stakeByUser[account].stake;
    }

    // @dev See {IVault-getStakeReward}
    function getStakeRewardAccrued(address stakeholder) public override view returns (uint reward) {
        uint coStakerRewardOld;
        (, coStakerRewardOld,) = _calculateFeesAccrued(_timeProvider.getTime());
        uint coStake = _stakeTotal - _stakeByUser[owner()].stake;
        if (coStake != 0) {
            uint coStakerReward = _coStakerRewardAccrued - coStakerRewardOld;
            reward = _stakeByUser[stakeholder].rewardAccrued + coStakerReward * _stakeByUser[stakeholder].stake / coStake;
        }
        return reward;
    }

    function getStakeRewardAccrued() public override view returns (uint reward) {
        (, reward,) = _calculateFeesAccrued(_timeProvider.getTime());
        return reward;
    }

    // @dev See {IVault-getStakeRewardToClaim}
    function getStakeRewardToClaim(address stakeholder) public override view returns (uint) {
        if (_coStakerRewardAccrued == 0) {
            return 0;
        }
        return getStakeRewardAccrued(stakeholder) * _coStakerRewardStash / _coStakerRewardAccrued;
    }

    // @dev See {IVault-withdrawStakeReward}
    function claimStakeReward(address toAddress) public override returns (uint) {
        _updateDebt();

        if (msg.sender == owner()) return 0;

        uint reward = getStakeRewardToClaim(msg.sender);
        _eauToken.safeTransfer(toAddress, reward);
        _stakeByUser[msg.sender].rewardAccrued -= reward;
        _coStakerRewardStash -= reward;
        _coStakerRewardAccrued -= reward;
        return reward;
    }

    // @dev See {IVault-withdrawStake}
    function withdrawStake() public override returns (uint) {
        require(!isOwner() || _closed, "Vault owner can unnstake only after the vault is closed");
        claimStakeReward(msg.sender);
        uint toWithdraw = getStake(msg.sender);
        if (toWithdraw != 0) {
            _clgnToken.safeTransfer(msg.sender, toWithdraw);
        }
        _stakeByUser[msg.sender].stake = 0;
        return toWithdraw;
    }

    // @dev See {IVault-buy}
    function buy(uint amount, uint maxPrice, address to) notClosed notSlashed public override {
        _buy(msg.sender, amount, maxPrice, to);
    }

    function _buy(address spender, uint amount, uint maxPrice, address to) private {
        require(amount <= _userToken.balanceOf(address(this)), "Vault::buy(): Not enough tokens to sell");
        uint price = getPrice();
        require(price > 0, "Vault::buy(): Initial Liquidity Auction is over");
        require(msg.sender == _challengeWinnerAddress || price > _challengers[_challengeWinnerAddress].price, "Vault::buy(): Initial Liquidity Auction is over. Only challenger can buy out.");
        require(price <= maxPrice, "Vault::buy(): Price too low");
        uint costInEau = amount.mul(price).div(10 ** _userToken.decimals());

        // distribute penalty if Initial Liquidity Auction is active
        if (_closeOutTime != 0) {
            uint penalty = costInEau.div(10);
            if (spender != address(this))
                _eauToken.safeTransferFrom(spender, address(this), penalty);
            require(_eauToken.balanceOf(address(this)) >= penalty, "Vault:buy: Not enough EAU for penalty");

            uint clgnBought = _buyClgnForEau(penalty);

            uint initiatorBounty = clgnBought.mul(33).div(100);
            _clgnToken.safeTransfer(_closeOutInitiator, initiatorBounty);

            uint bidderBounty = clgnBought.mul(33).div(100);
            _clgnToken.safeTransfer(to, bidderBounty);

            _clgnToken.burn(clgnBought.sub(initiatorBounty).sub(bidderBounty));

            costInEau = costInEau.sub(penalty);
        }

        _payOff(spender, costInEau);
        _userToken.safeTransfer(to, amount);
        _tokenAmount -= amount;

        if (_price != price)
            _price = price;

        emit Purchase(amount, price, to);
    }

    function borrow(uint amount) notClosed notSlashed onlyOwner public override {
        require(amount <= canBorrow(), "Credit limit is exhausted ");

        _cologneDao.mintEAU(owner(), amount);
        _principal += amount;

        _updateDebt();
    }

    function payOff(uint amount) notClosed notSlashed public override {
        _updateDebt();
        uint debt = getTotalDebt();
        if (debt < amount) {
            amount = debt;
        }
        _payOff(msg.sender, amount);
    }

    function _payOff(address spender, uint amount) private {
        if (spender != address(this))
            _eauToken.safeTransferFrom(spender, address(this), amount);
        require(_eauToken.balanceOf(address(this)) >= amount, "Vault:payOff: Not enough EAU to pay off");

        uint leftover = _payOffFees(amount);

        uint principalPaid = 0;
        if (leftover > _principal) {
            principalPaid = _principal;
            leftover = leftover - _principal;
            _principal = 0;
        } else {
            principalPaid = leftover;
            _principal = _principal - leftover;
        }
        _eauToken.burn(principalPaid);

        if (_principal.add(_feeAccrued) <= _tokenAmount.mul(_price).div(10 ** _userToken.decimals()).div(4)) {
            _limitBreachedTime = 0;
            _closeOutTime = 0;
        }

        _price = getPrice();
    }

    function close() onlyOwner public override {
        require(getTotalDebt() == 0, "Vault::close(): close allowed only if debt is paid off");
        _closed = true;
        _userToken.safeTransfer(owner(), _userToken.balanceOf(address(this)));
        _tokenAmount = 0;
        _eauToken.safeTransfer(owner(), _eauToken.balanceOf(address(this)).sub(eauForChallengers));
        withdrawStake();
    }

    function startInitialLiquidityAuction() notClosed notSlashed public override {
        require(isLimitBreached(), "Vault::startInitialLiquidityAuction(): credit limit is not breached");
        require(_closeOutTime == 0, "Vault::startInitialLiquidityAuction(): close-out already initiated");

        _closeOutTime = _timeProvider.getTime();
        _closeOutInitiator = msg.sender;

        _updateDebt();
    }

    function slash() notClosed notSlashed initialAuctionIsOver public override {
        _updateDebt();
        uint stakeTotalOld = _stakeTotal;

        // determine amount CLGN to sell
        uint debt = getTotalDebt();
        uint clgnToSell = _getClgnInForEauOut(debt);
        if (clgnToSell > _stakeTotal)
            clgnToSell = _stakeTotal;

        // distribute penalty
        uint penalty = clgnToSell.div(10);
        uint part = penalty.div(4);
        // 25% of penalty to close out initiator
        _clgnToken.safeTransfer(_closeOutInitiator, part);
        // 25% of penalty to slashing initiator
        _clgnToken.safeTransfer(msg.sender, part);
        // remaining to burn
        _clgnToken.burn(penalty.sub(part.mul(2)));
        _stakeTotal -= penalty;

        // sell staked CLGN for EAU
        if (clgnToSell > _stakeTotal)
            clgnToSell = _stakeTotal;
        uint eauToBuy = _getEauOutForClgnIn(clgnToSell);
        if (eauToBuy > debt)
            eauToBuy = debt;
        uint eauLeftover = _sellClgnForEau(clgnToSell, eauToBuy);
        _stakeTotal -= clgnToSell;

        // reduce stakes
        uint slashed = stakeTotalOld - _stakeTotal;
        if (slashed != 0) {
            for (uint i = 0; i < _stakeholders.length; i++) {
                _stakeByUser[_stakeholders[i]].stake -= slashed * _stakeByUser[_stakeholders[i]].stake / stakeTotalOld;
            }
        }

        // pay off principal
        if (_principal <= eauLeftover) {
            _eauToken.burn(_principal);
            eauLeftover -= _principal;
            _principal = 0;
        } else {
            _eauToken.burn(eauLeftover);
            _principal -= eauLeftover;
            eauLeftover = 0;
        }

        // pay off fees accrued
        require(eauLeftover <= _feeAccrued + _coStakerRewardAccrued, "Slashing: Too many EAU got from slashing");
        _payOffFees(eauLeftover);

        // forgive fees
        _feeAccrued = 0;
        _coStakerRewardAccrued = 0;
        _slashed = true;
    }

    function coverShortfall() notClosed initialAuctionIsOver public override {
        require(_slashed, "Cover shortfall: can be called only after slashing");

        uint debt = getTotalDebt();
        require(debt > 0, "Cover shortfall: no shortfall to cover");
        require(_getEauOutForClgnIn(_clgnToken.balanceOf(msg.sender)) >= debt.div(20), "Only CLGN holder with at least 5% of remaining outstanding EAU debt can initiate a CLGN mint");

        uint bounty = debt.div(10);
        uint clgnToMint = _getClgnInForEauOut(debt.add(bounty));
        _cologneDao.mintCLGN(address(this), clgnToMint);
        _sellClgnForEau(clgnToMint, debt.add(bounty));
        _eauToken.burn(debt);
        _principal = 0;
        _debtUpdateTime = _timeProvider.getTime();
        _eauToken.safeTransfer(msg.sender, bounty);
    }


    function getTotalDebt() public view override returns (uint debt) {
        if (_closed) return 0;
        uint fees;
        uint reward;
        (fees, reward,) = _calculateFeesAccrued(_timeProvider.getTime());
        return _principal.add(fees).add(reward).sub(_coStakerRewardStash);
    }

    function updateDebt() public override {
        _updateDebt();
    }

    function getPrincipal() public view override returns (uint) {
        if (_closed) return 0;
        return _principal;
    }

    function getTotalFeesRepaid() public view override returns (uint) {
        return _totalFeesRepaid;
    }

    function getFeeRate() public view override returns (uint) {
        return _getFeeRate(getCollateralInEau());
    }

    function _getFeeRate(uint collateral) private view returns (uint) {
        // Initial rate is 101%
        uint feeRate = (101 * 10 ** 18);

        // Discount for stake
        if (getPrincipal() == 0) {
            // 1% if no debt
            feeRate = 10 ** 18;
        } else {
            uint stakeDiscount = collateral.mul(100 * 10 ** 18).div(getPrincipal());
            if (feeRate >= stakeDiscount)
                feeRate -= stakeDiscount;
            else
                feeRate = 10 ** 18;
        }

        // discount is 0.1% for every 1,000 EAU paid off
        uint discount = (_totalFeesRepaid).div(10000);
        if (feeRate >= discount)
            feeRate -= discount;
        else
            feeRate = 10 ** 18;

        // 1% is minimal fee rate
        if (feeRate < (10 ** 18))
            feeRate = 10 ** 18;

        return feeRate;
    }

    function getFees() public view override returns (uint fees) {
        if (_closed) return 0;
        (fees, ,) = _calculateFeesAccrued(_timeProvider.getTime());
        return fees;
    }

    /**
     * How many EAU the owner can borrow.
     * The amount is 25% of user token assessed in EAU.
     * Total principal is limited at 10.000 EAU
     * Covered by 80% of CLGN owner stake
     */
    function getCreditLimit() public view override returns (uint) {
        // 25% of user token assessed in EAU
        uint creditLimit = _tokenAmount.mul(getPrice()).div(4).div(10 ** _userToken.decimals());

        // Total principal is limited at 10.000 EAU
        uint maxLimit = 10000 * (10 ** _eauToken.decimals());
        if (maxLimit > _principal) {
            maxLimit -= _principal;
        } else {
            maxLimit = 0;
        }
        creditLimit = Math.min(creditLimit, maxLimit);

        // Covered by 80% of CLGN owner stake
        uint collateralLimit = getCollateralInEau().mul(10).div(8);

        return Math.min(creditLimit, collateralLimit);
    }

    /**
     * How much the owner can borrow at the moment.
     * Takes into account the value has already borrowed.
     */
    function canBorrow() public view override returns (uint) {
        if (_closed) return 0;
        uint debt = getTotalDebt();
        uint creditLimit = getCreditLimit();
        if (creditLimit <= debt) {
            return 0;
        } else {
            return creditLimit.sub(debt);
        }
    }

    /**
     * Checks if credit limit is breached
     */
    function isLimitBreached() public view returns (bool) {
        if (_closed) return false;
        return getTotalDebt() != 0 && canBorrow() == 0;
    }

    // @dev See {IVault-getPrice}
    function getPrice() public view override returns (uint price) {
        return Math.max(_getDutchAuctionPrice(), _challengers[_challengeWinnerAddress].price);
    }

    /**
     * Get user token amount
     */
    function getTokenAmount() public view override returns (uint) {
        return _tokenAmount;
    }

    /**
     * Get CLGN collateral in EAU
     */
    function getCollateralInEau() public view override returns (uint) {
        return _getEauOutForClgnIn(_stakeTotal);
    }

    function getState() public view override returns (VaultState) {
        if (_closed) return VaultState.Closed;
        if (_slashed && (getTotalDebt() != 0)) return VaultState.WaitingForClgnAuction;
        if (_slashed) return VaultState.Slashed;
        // tkns sold out or challenge has happened
        if (_tokenAmount == 0
        || (_challengeWinnerAddress != address(0)) && getPrice() == _challengers[_challengeWinnerAddress].price) return VaultState.SoldOut;
        if (_initialAuctionIsOver()) return VaultState.WaitingForSlashing;
        if (isLimitBreached() && _closeOutTime != 0) return VaultState.InitialLiquidityAuctionInProcess;
        if (isLimitBreached() && _closeOutTime == 0) return VaultState.Defaulted;
        return VaultState.Trading;
    }

    // @dev See {IVault-challenge}
    function challenge(uint price, uint eauToLock) initialAuctionIsNotOver public override {
        uint currentPrice = getPrice();
        require(price != 0, "Vault:challenge: price cannot be 0");
        require(price < currentPrice, "Vault:challenge: price too high");
        require(eauToLock.add(_challengers[msg.sender].eauAmountToLock) >= _tokenAmount.mul(price).div(10 ** _userToken.decimals()), "Vault:challenge: lock amount in EAU not enough");
        _eauToken.safeTransferFrom(msg.sender, address(this), eauToLock);
        eauForChallengers += eauToLock;
        _challengers[msg.sender].price = price;
        _challengers[msg.sender].eauAmountToLock += eauToLock;
        if (price > _challengers[_challengeWinnerAddress].price) {
            _challengeWinnerAddress = msg.sender;
        }
    }

    // @dev See {IVault-redeemChallenge}
    function redeemChallenge() public override returns (uint eauAmount, uint userTokenAmount) {
        (eauAmount, userTokenAmount) = getRedeemableChallenge(msg.sender);

        // buy user tokens if Initial Liquidity auction passed
        if (userTokenAmount != 0) {
            _buy(address(this), userTokenAmount, _challengers[msg.sender].price, msg.sender);
            _challengers[msg.sender].eauAmountToLock = 0;
        } else {
            _challengers[msg.sender].eauAmountToLock -= eauAmount;
        }

        _eauToken.safeTransfer(msg.sender, eauAmount);
        eauForChallengers -= eauAmount;

        return (eauAmount, userTokenAmount);
    }

    // @dev See {IVault-getChallengeLocked}
    function getChallengeLocked(address challenger) public override view returns (uint eauLocked) {
        if (challenger == _challengeWinnerAddress) {
            return _challengers[_challengeWinnerAddress].price.mul(_tokenAmount).div(10 ** _userToken.decimals());
        }
        return 0;
    }

    // @dev See {IVault-getRedeemableChallenge}
    function getRedeemableChallenge(address challenger) public override view returns (uint eauAmount, uint userTokenAmount) {
        userTokenAmount = 0;
        eauAmount = _challengers[challenger].eauAmountToLock;

        if (challenger == _challengeWinnerAddress) {
            uint price = getPrice();
            eauAmount -= _tokenAmount.mul(_challengers[challenger].price).div(10 ** _userToken.decimals());

            // if Initial Liquidity auction passed
            if (price == _challengers[challenger].price) {
                // calculate eau spent
                userTokenAmount = _tokenAmount;
            }
        }

        return (eauAmount, userTokenAmount);
    }

    // @dev See {IVault-getChallengeWinner}
    function getChallengeWinner() public view override returns (address, uint) {
        return (_challengeWinnerAddress, _challengers[_challengeWinnerAddress].price);
    }

    /**
     * Get Dutch auction price
     * @return price
     */
    function _getDutchAuctionPrice() private view returns (uint price) {
        // 30 min
        uint tickPriceChange = 1800;
        price = _price;
        if (_closeOutTime != 0) {
            require(_timeProvider.getTime() >= _closeOutTime, "Vault::getPrice(): Incorrect state: Limit is breached in the future!");
            uint discount = Math.min(((_timeProvider.getTime().sub(_closeOutTime)).div(tickPriceChange)), 100);
            price = price.mul(100 - discount).div(100);
        }
        return price;
    }

    /**
     * Update fees accrued over period
     */
    function _calculateFeesAccrued(uint time) private view returns (uint feeAccrued, uint coStakerReward, uint limitBreachedTime) {
        if (time == _debtUpdateTime) {
            return (_feeAccrued, _coStakerRewardAccrued, _limitBreachedTime);
        }

        require(time > _debtUpdateTime, "Cannot calculate fee in the past");

        uint endTime = time;
        // Stop calculate fee when close-out process started
        if (_closeOutTime != 0 && _closeOutTime < time)
            endTime = _closeOutTime;

        // period to accrue fee in seconds (one day)
        uint period = 86400;

        // rate per period multiplied by 10 ** 20
        uint rateDivider = 10 ** 20;
        uint rate = _getFeeRate(getCollateralInEau());
        uint rateWithoutCoStakers = _getFeeRate(_getEauOutForClgnIn(getStake(owner())));

        limitBreachedTime = _limitBreachedTime;
        feeAccrued = _feeAccrued;
        uint feeAccruedWithoutCoStakers = _feeAccrued;
        for (uint i = _debtUpdateTime; i < endTime; i = i + period) {
            uint limit = _tokenAmount.mul(_price).div(_userToken.decimals()).div(4);
            // limit has been breached
            if (_principal.add(feeAccrued) > limit) {
                if (limitBreachedTime == 0) {
                    limitBreachedTime = i;
                }
            }
            uint feeTemp = (_principal.add(feeAccrued)).mul(rate);
            feeTemp = feeTemp.div(365).div(rateDivider);
            feeAccrued += feeTemp;

            feeTemp = (_principal.add(feeAccruedWithoutCoStakers)).mul(rateWithoutCoStakers);
            feeTemp = feeTemp.div(365).div(rateDivider);
            feeAccruedWithoutCoStakers += feeTemp;
        }
        coStakerReward = _coStakerRewardAccrued.add((feeAccruedWithoutCoStakers.sub(feeAccrued)).div(2));
        return (feeAccrued, coStakerReward, limitBreachedTime);
    }

    function _updateDebt() internal {
        if (_debtUpdateTime == _timeProvider.getTime())
            return;
        uint coStakerReward = _coStakerRewardAccrued;
        (_feeAccrued, _coStakerRewardAccrued, _limitBreachedTime) = _calculateFeesAccrued(_timeProvider.getTime());

        // co-stake w/o owner (owner is not rewarded)
        uint coStake = _stakeTotal - _stakeByUser[owner()].stake;
        if (coStake != 0) {
            // reward accrued for last period
            coStakerReward = _coStakerRewardAccrued - coStakerReward;
            for (uint i = 0; i < _stakeholders.length; i++) {
                _stakeByUser[_stakeholders[i]].rewardAccrued += coStakerReward * _stakeByUser[_stakeholders[i]].stake / coStake;
            }
        }
        _debtUpdateTime = _timeProvider.getTime();
    }

    /**
     * Pay off fees accrued and distribute 50% of EAU paid and buy and burn CLGN for another 50%.
     * @param amount - amount to pay off in EAU
     * @return leftover - amount left after paying in EAU
     */
    function _payOffFees(uint amount) private returns (uint leftover) {
        _updateDebt();

        if (_feeAccrued == 0)
            return amount;

        uint feesPaid = 0;
        leftover = amount;
        if (leftover > _feeAccrued + _coStakerRewardAccrued - _coStakerRewardStash) {
            feesPaid = _feeAccrued;
            leftover = leftover - _feeAccrued - _coStakerRewardAccrued + _coStakerRewardStash;
            _coStakerRewardStash = _coStakerRewardAccrued;
            _feeAccrued = 0;
        } else {
            feesPaid = leftover.mul(_feeAccrued).div(_feeAccrued + _coStakerRewardAccrued - _coStakerRewardStash);
            leftover -= feesPaid;
            // co-staker reward
            _feeAccrued -= feesPaid;
            _coStakerRewardStash += leftover;
            leftover = 0;
        }
        _totalFeesRepaid += feesPaid;
        _distributeFee(feesPaid);

        return leftover;
    }

    function _distributeFee(uint amount) private {
        // 50% to buy and burn CLGN
        uint toBuyClgn = amount.div(2);
        uint clgnBought = _buyClgnForEau(toBuyClgn);
        _clgnToken.burn(clgnBought);

        // 50% of EAU are distributed
        _eauToken.distribute(amount.sub(toBuyClgn));
    }

    /**
     * Returns how much EAU can get for selling clgnInAmount
     */
    function _getEauOutForClgnIn(uint clgnInAmount) private view returns (uint eauOutAmount) {
        if (clgnInAmount == 0) return 0;
        address[] memory path = new address[](2);
        path[0] = address(_clgnToken);
        path[1] = address(_eauToken);
        uint[] memory amounts = _cologneDao.getClgnMarket().getAmountsOut(clgnInAmount, path);
        return amounts[1];
    }

    /**
     * Returns how much CLGN need to buy eauOutAmount
     */
    function _getClgnInForEauOut(uint eauOutAmount) private view returns (uint clgnAmount) {
        if (eauOutAmount == 0) return 0;
        address[] memory path = new address[](2);
        path[0] = address(_clgnToken);
        path[1] = address(_eauToken);
        uint[] memory amounts = _cologneDao.getClgnMarket().getAmountsIn(eauOutAmount, path);
        return amounts[0];
    }

    /**
     * Buy as many CLGN as possible for exact EAU amount
     * @param eauAmount - to spend
     * @return bought - amount bought
     */
    function _buyClgnForEau(uint eauAmount) private returns (uint bought) {
        if (eauAmount == 0) return 0;
        _eauToken.safeApprove(address(_cologneDao.getClgnMarket()), eauAmount);

        address[] memory path = new address[](2);
        path[0] = address(_eauToken);
        path[1] = address(_clgnToken);

        // deadline is 1h
        uint deadline = block.timestamp + 3600;

        uint[] memory amounts = _cologneDao.getClgnMarket().swapExactTokensForTokens(eauAmount, 0, path, address(this), deadline);
        bought = amounts[1];
        require(eauAmount == amounts[0], "Vault::buyCLGN(): not exact amount of EAU sold to buy CLGN");
        return bought;
    }

    /**
     * Sell as few CLGN as possible to get exact amount of eau
     */
    function _sellClgnForEau(uint maxClgnAmount, uint exactEauAmount) private returns (uint bought) {
        if (maxClgnAmount == 0 || exactEauAmount == 0) return 0;
        _clgnToken.safeApprove(address(_cologneDao.getClgnMarket()), maxClgnAmount);

        address[] memory path = new address[](2);
        path[0] = address(_clgnToken);
        path[1] = address(_eauToken);

        // deadline is 1h
        uint deadline = block.timestamp + 3600;

        uint[] memory amounts = _cologneDao.getClgnMarket().swapTokensForExactTokens(exactEauAmount, maxClgnAmount, path, address(this), deadline);
        uint sold = amounts[0];
        bought = amounts[1];
        require(sold <= maxClgnAmount, "Vault::sellCLGN(): CLGN sold is more than expected");
        if (sold < maxClgnAmount) {
            _clgnToken.safeDecreaseAllowance(address(_cologneDao.getClgnMarket()), maxClgnAmount.sub(sold));
        }
        require(bought == exactEauAmount, "Vault::sellCLGN(): not exact amount of EAU bought for CLGN");
        return bought;
    }

    function _initialAuctionIsOver() internal view returns (bool) {
        // 180000 is a length of Initial Liquidity Auction in seconds
        return _closeOutTime != 0 && (_timeProvider.getTime() - _closeOutTime >= 180000);
    }
}
