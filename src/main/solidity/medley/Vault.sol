// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

import "./../token/ERC20/ERC20.sol";
import "./../token/ERC20/EAUToken.sol";
import "./../token/ERC20/CLGNToken.sol";
import "./IVault.sol";
import "./IMedleyDAO.sol";
import "./../math/SafeMath.sol";
import "./../math/Math.sol";
import "./ITimeProvider.sol";
import "./../utils/Ownable.sol";

contract Vault is IVault, Ownable {
    using SafeMath for uint;

    // Types of accounting records
    enum AccountingRecordType {Deposit, Withdrawal}

    struct AccountingRecord {
        uint recordType;
        uint amount;
        uint time;
    }

    AccountingRecord[] _accountingBook;

    IMedleyDAO _medleyDao;

    EAUToken _eauToken;
    CLGNToken _clgnToken;

    // Owner ERC20 token
    ERC20 _userToken;

    // amount of ERC20 token the owner has deposited
    uint _tokenAmount;

    // Owner token price assessed by the owner in attoEAU (10^-18 EAU for 1 UserToken)
    uint _price = 0;

    // Value of CLGN collateral in EAU
    uint _collateral;

    // Initial amount of loan without fees
    uint _principal = 0;

    uint _feeAccrued = 0;

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
        _medleyDao = IMedleyDAO(msg.sender);
        _eauToken = EAUToken(_medleyDao.getEauTokenAddress());
        _clgnToken = CLGNToken(_medleyDao.getClgnTokenAddress());
        _userToken = ERC20(token);
        _tokenAmount = initialAmount;
        _price = tokenPrice;
        _timeProvider = timeProvider;
        _debtUpdateTime = _timeProvider.getTime();
    }

    function stake(uint amount) notClosed public override {
        _stake(amount);
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
                require(_eauToken.transferFrom(spender, address(this), penalty), "Vault::buy: cannot transfer EAU penalty.");
            require(_eauToken.balanceOf(address(this)) >= penalty, "Vault:buy: Not enough EAU for penalty");

            _eauToken.approve(address(_medleyDao.getClgnMarket()), penalty);
            uint clgnBought = _buyClgnForEau(penalty);

            uint initiatorBounty = clgnBought.mul(33).div(100);
            require(_clgnToken.transfer(_closeOutInitiator, initiatorBounty), "Vault::buy: transfer CLGN bounty to initiator");

            uint bidderBounty = clgnBought.mul(33).div(100);
            require(_clgnToken.transfer(to, bidderBounty), "Vault::buy: transfer CLGN bounty to bidder");

            _clgnToken.burn(clgnBought.sub(initiatorBounty).sub(bidderBounty));

            costInEau = costInEau.sub(penalty);
        }

        _payOff(spender, costInEau);
        require(_userToken.transfer(to, amount), "Vault::buy: cannot transfer User Token.");
        _tokenAmount -= amount;

        if (_price != price)
            _price = price;

        emit Purchase(amount, price, to);
    }

    function borrow(uint amount) notClosed notSlashed onlyOwner public override {
        require(amount <= canBorrow(), "Credit limit is exhausted ");

        _medleyDao.mintEAU(owner(), amount);
        _principal += amount;
        _debtUpdateTime = _timeProvider.getTime();
        _recordAccounting(AccountingRecordType.Withdrawal, amount, _debtUpdateTime);
    }

    function payOff(uint amount) notClosed notSlashed public override {
        _payOff(msg.sender, amount);
    }

    function _payOff(address spender, uint amount) private {
        if (spender != address(this))
            require(_eauToken.transferFrom(spender, address(this), amount), "Vault: cannot transfer EAU.");
        require(_eauToken.balanceOf(address(this)) >= amount, "Vault:payOff: Not enough EAU to pay off");

        _recordAccounting(AccountingRecordType.Deposit, amount, _timeProvider.getTime());

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

        uint price = getPrice();
        if (_price != price)
            _price = price;

        _debtUpdateTime = _timeProvider.getTime();
    }

    function close() onlyOwner public override {
        require(_getTotalDebt(_timeProvider.getTime()) == 0, "Vault::close(): close allowed only if debt is paid off");
        _userToken.transfer(owner(), _userToken.balanceOf(address(this)));
        _tokenAmount = 0;
        _clgnToken.transfer(owner(), _clgnToken.balanceOf(address(this)));
        _eauToken.transfer(owner(), _eauToken.balanceOf(address(this)).sub(eauForChallengers));
        _closed = true;
    }

    function startInitialLiquidityAuction() notClosed notSlashed public override {
        require(isLimitBreached(), "Vault::startInitialLiquidityAuction(): credit limit is not breached");
        require(_closeOutTime == 0, "Vault::startInitialLiquidityAuction(): close-out already initiated");

        _closeOutTime = _timeProvider.getTime();
        _closeOutInitiator = msg.sender;

        (_feeAccrued, _limitBreachedTime) = _calculateFeesAccrued(_timeProvider.getTime());
        _debtUpdateTime = _timeProvider.getTime();
    }

    function slash() notClosed notSlashed initialAuctionIsOver public override {
        // determine amount CLGN to sell
        uint debt = getTotalDebt();

        address[] memory path = new address[](2);
        path[0] = address(_clgnToken);
        path[1] = address(_eauToken);
        uint[] memory amounts = _medleyDao.getClgnMarket().getAmountsIn(debt, path);
        uint clgnToSell = amounts[0];
        if (clgnToSell > _collateral)
            clgnToSell = _collateral;

        // distribute penalty
        uint penalty = clgnToSell.div(10);
        uint part = penalty.div(4);
        // 25% of penalty to close out initiator
        require(_clgnToken.transfer(_closeOutInitiator, part), "Vault::slash(): pay close out bounty error");
        // 25% of penalty to slashing initiator
        require(_clgnToken.transfer(msg.sender, part), "Vault::slash(): pay slashing bounty error");
        // remaining to burn
        _clgnToken.burn(penalty.sub(part.mul(2)));
        _collateral -= penalty;

        // sell staked CLGN for EAU
        if (clgnToSell > _collateral)
            clgnToSell = _collateral;
        amounts = _medleyDao.getClgnMarket().getAmountsOut(clgnToSell, path);
        uint eauToBuy = amounts[1];
        if (eauToBuy > debt)
            eauToBuy = debt;
        uint eauLeftover = _sellClgnForEau(clgnToSell, eauToBuy);
        _collateral -= clgnToSell;

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
        require(eauLeftover <= _feeAccrued, "Slashing: Too many EAU got from slashing");
        _distributeFee(eauLeftover);

        // forgive fees
        _feeAccrued = 0;
        _slashed = true;
    }

    function coverShortfall() notClosed initialAuctionIsOver public override {
        require(_slashed, "Cover shortfall: can be called only after slashing");

        uint debt = getTotalDebt();
        require(debt > 0, "Cover shortfall: no shortfall to cover");
        uint clgnHolderBalanceInEau = _medleyDao.getClgnPriceOracle().consult(address(_clgnToken), _clgnToken.balanceOf(msg.sender));
        require(clgnHolderBalanceInEau >= debt.div(20), "Only CLGN holder with at least 5% of remaining outstanding EAU debt can initiate a CLGN mint");

        uint bounty = debt.div(10);
        uint clgnToMint = _medleyDao.getClgnPriceOracle().consult(address(_eauToken), debt.add(bounty));
        _medleyDao.mintCLGN(address(this), clgnToMint);
        _sellClgnForEau(clgnToMint, debt.add(bounty));
        _eauToken.burn(debt);
        _principal = 0;
        _debtUpdateTime = _timeProvider.getTime();
        require(_eauToken.transfer(msg.sender, bounty), "Vault::coverShortfall(): cannot transfer EAU bounty.");
    }


    function getTotalDebt() public view override returns (uint debt) {
        return _getTotalDebt(_timeProvider.getTime());
    }

    function _getTotalDebt(uint time) private view returns (uint debt) {
        if (_closed) return 0;
        debt = _principal.add(_getFees(time));
        return debt;
    }

    function getPrincipal() public view override returns (uint) {
        if (_closed) return 0;
        return _principal;
    }

    function getFees() public view override returns (uint) {
        return _getFees(_timeProvider.getTime());
    }

    function getTotalFeesRepaid() public view override returns (uint) {
        return _totalFeesRepaid;
    }

    function getFeeRate() public view override returns (uint) {
        // Initial rate is 101%
        uint feeRate = (101 * 10 ** 18);

        // Discount for stake
        if (getPrincipal() == 0) {
            // 1% if no debt
            feeRate = 10 ** 18;
        } else {
            uint stakeDiscount = (getCollateralInEau()).mul(100 * 10 ** 18).div(getPrincipal());
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

    function _getFees(uint time) private view returns (uint fees) {
        if (_closed) return 0;
        (fees,) = _calculateFeesAccrued(time);
        return fees;
    }

    function getCreditLimit() public view override returns (uint) {
        return _tokenAmount.mul(getPrice()).div(4).div(10 ** _userToken.decimals());
    }

    // How much the owner can borrow at the moment. Takes into account the value has already borrowed.
    function canBorrow() public view override returns (uint) {
        if (_closed) return 0;
        uint debt = _getTotalDebt(_timeProvider.getTime());
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
        return _getTotalDebt(_timeProvider.getTime()) != 0 && canBorrow() == 0;
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
        return _medleyDao.getClgnPriceOracle().consult(address(_clgnToken), _collateral);
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
        require(eauToLock >= _tokenAmount.mul(price).div(10 ** _userToken.decimals()), "Vault:challenge: lock amount in EAU not enough");
        require(_eauToken.transferFrom(msg.sender, address(this), eauToLock), "Vault:challenge: cannot transfer EAU.");
        eauForChallengers += eauToLock;
        Challenge memory newChallenge = Challenge(price, eauToLock);
        _challengers[msg.sender] = newChallenge;
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

        require(_eauToken.transfer(msg.sender, eauAmount), "Vault:redeemChallenge(): cannot transfer EAU");
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

    function _recordAccounting(AccountingRecordType recordType, uint amount, uint time) private {
        // TODO implement
    }

    /**
     * Adds CLGN to stake
     */
    function _stake(uint clgnAmount) private {
        require(_clgnToken.transferFrom(msg.sender, address(this), clgnAmount), "Vault::stake: cannot transfer ClGN.");
        _collateral = _collateral + clgnAmount;
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

    function _calculateFeesAccrued(uint time) private view returns (uint feeAccrued, uint limitBreachedTime) {
        require(time >= _debtUpdateTime, "Cannot calculate fee in the past");
        uint endTime = time;
        // Stop calculate fee when close-out process started
        if (_closeOutTime != 0 && _closeOutTime < time)
            endTime = _closeOutTime;

        // period to accrue fee in seconds (one day)
        uint period = 86400;

        // TODO calculate rate according the table
        // rate per period multiplied by 1'000'000
        uint rate = uint(100000).div(365);

        limitBreachedTime = _limitBreachedTime;
        feeAccrued = _feeAccrued;
        for (uint i = _debtUpdateTime; i < endTime; i = i + period) {
            uint limit = _tokenAmount.mul(_price).div(_userToken.decimals()).div(4);
            // limit has been breached
            if (_principal.add(feeAccrued) > limit) {
                if (limitBreachedTime == 0) {
                    limitBreachedTime = i;
                }
            }
            feeAccrued = feeAccrued + (_principal + feeAccrued) * rate / 1000000;
        }
        return (feeAccrued, limitBreachedTime);
    }

    /**
     * Pay off fees accrued and distribute 50% of EAU paid and buy and burn CLGN for another 50%.
     * @param amount - amount to pay off in EAU
     * @return leftover - amount left after paying in EAU
     */
    function _payOffFees(uint amount) private returns (uint leftover) {
        uint totalFeesAccrued;
        uint limitBreachedTime;
        (totalFeesAccrued, limitBreachedTime) = _calculateFeesAccrued(_timeProvider.getTime());
        _limitBreachedTime = limitBreachedTime;
        uint feesPaid = 0;
        leftover = amount;
        if (leftover > totalFeesAccrued) {
            feesPaid = totalFeesAccrued;
            leftover = leftover - totalFeesAccrued;
            _feeAccrued = 0;
        } else {
            feesPaid = leftover;
            _feeAccrued = totalFeesAccrued - leftover;
            leftover = 0;
        }
        _totalFeesRepaid += feesPaid;
        _distributeFee(feesPaid);

        return leftover;
    }

    function _distributeFee(uint amount) private {
        uint toBuyClgn = amount.div(2);

        // 50% to buy and burn CLGN
        _eauToken.approve(address(_medleyDao.getClgnMarket()), toBuyClgn);
        uint clgnBought = _buyClgnForEau(toBuyClgn);
        _clgnToken.burn(clgnBought);

        // 50% of EAU are distributed
        _eauToken.distribute(amount.sub(toBuyClgn));
    }

    /**
     * Buy as many CLGN as possible for exact EAU amount
     * @param eauAmount - to spend
     * @return bought - amount bought
     */
    function _buyClgnForEau(uint eauAmount) private returns (uint bought) {
        address[] memory path = new address[](2);
        path[0] = address(_eauToken);
        path[1] = address(_clgnToken);

        // buy at price from oracle -10%
        // TODO clarify price
        uint clgnBoughtExpected = _medleyDao.getClgnPriceOracle().consult(address(_eauToken), eauAmount).mul(9).div(10);

        // TODO clarify deadline for Uniswap
        uint deadline = _timeProvider.getTime() + 10000;

        uint[] memory amounts = _medleyDao.getClgnMarket().swapExactTokensForTokens(eauAmount, clgnBoughtExpected, path, address(this), deadline);
        bought = amounts[1];
        require(eauAmount == amounts[0], "Vault::buyCLGN(): not exact amount of EAU sold to buy CLGN");
        require(clgnBoughtExpected <= bought, "Vault::buyCLGN(): CLGN bought is less than expected");
        return bought;
    }

    /**
     * Sell as few CLGN as possible to get exact amount of eau
     */
    function _sellClgnForEau(uint maxClgnAmount, uint exactEauAmount) private returns (uint bought) {
        _clgnToken.approve(address(_medleyDao.getClgnMarket()), maxClgnAmount);

        address[] memory path = new address[](2);
        path[0] = address(_clgnToken);
        path[1] = address(_eauToken);

        // TODO check oracle price
        // uint clgnToSell = _medleyDao.getClgnPriceOracle().consult(address(_eauToken), debt);

        // TODO clarify deadline for Uniswap
        uint deadline = _timeProvider.getTime() + 10000;

        uint[] memory amounts = _medleyDao.getClgnMarket().swapTokensForExactTokens(exactEauAmount, maxClgnAmount, path, address(this), deadline);
        uint sold = amounts[0];
        bought = amounts[1];
        require(sold <= maxClgnAmount, "Vault::sellCLGN(): CLGN sold is more than expected");
        require(bought == exactEauAmount, "Vault::sellCLGN(): not exact amount of EAU bought for CLGN");
        return bought;
    }

    function _initialAuctionIsOver() internal view returns (bool) {
        // 180000 is a length of Initial Liquidity Auction in seconds
        return _closeOutTime != 0 && (_timeProvider.getTime() - _closeOutTime >= 180000);
    }
}

