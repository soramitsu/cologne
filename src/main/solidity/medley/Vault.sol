// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

import "./../token/ERC20/IERC20.sol";
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
    IERC20 _token;
    // amount of ERC20 token the owner has deposited
    uint _tokenAmount;
    // Owner token price assessed by the owner
    uint _price = 0;

    // Value of CLGN collateral in EAU
    uint _collateral;

    // Initial amount of loan without fees
    uint _principal = 0;
    uint _feeAccrued = 0;
    uint _debtUpdateTime;

    uint _limitBreachedTime = 0;

    ITimeProvider _timeProvider;

    // Close-out state - Initial Liquidity Dutch Auction started
    uint _closeOutTime = 0;
    address _closeOutInitiator;

    bool _slashed = false;
    bool _closed = false;

    modifier notSlashed() {
        require(!_slashed, "Vault is slashed");
        _;
    }

    modifier notClosed {
        require(!_closed, "Vault is closed");
        _;
    }

    modifier initialAuctionIsOver {
        // 180000 is a length of Initial Liquidity Auction in seconds
        require(_closeOutTime != 0 && (_timeProvider.getTime() - _closeOutTime >= 180000), "Vault::slash(): initial auction is not finished yet");
        _;
    }

    constructor(
        address owner,
        uint stake,
        address token,
        uint initialAmount,
        uint tokenPrice,
        ITimeProvider timeProvider) Ownable(owner) {
        _medleyDao = IMedleyDAO(msg.sender);
        _eauToken = EAUToken(_medleyDao.getEauTokenAddress());
        _clgnToken = CLGNToken(_medleyDao.getClgnTokenAddress());
        _token = IERC20(token);
        _tokenAmount = initialAmount;
        _price = tokenPrice;
        _timeProvider = timeProvider;
        _debtUpdateTime = _timeProvider.getTime();
        _stake(stake);
    }

    function buy(uint amount, uint maxPrice, address to) notClosed notSlashed public override {
        require(amount <= _token.balanceOf(address(this)), "Vault::buy(): Not enough tokens to sell");
        uint price = getPrice();
        require(price > 0, "Vault::buy(): Initial Liquidity Auction is over");
        require(price <= maxPrice, "Vault::buy(): Price too low");
        uint costInEau = amount.mul(price);

        // distribute penalty if Initial Liquidity Auction is active
        if (_closeOutTime != 0) {
            uint penalty = costInEau.div(10);
            require(_eauToken.transferFrom(msg.sender, address(this), penalty), "Vault::buy: cannot transfer EAU penalty.");

            _eauToken.approve(address(_medleyDao.getClgnMarket()), penalty);
            uint clgnBought = _buyClgnForEau(penalty);

            uint initiatorBounty = clgnBought.mul(33).div(100);
            require(_clgnToken.transfer(_closeOutInitiator, initiatorBounty), "Vault::buy: transfer CLGN bounty to initiator");

            uint bidderBounty = clgnBought.mul(33).div(100);
            require(_clgnToken.transfer(msg.sender, bidderBounty), "Vault::buy: transfer CLGN bounty to bidder");

            _clgnToken.burn(clgnBought.sub(initiatorBounty).sub(bidderBounty));

            costInEau = costInEau.sub(penalty);
        }

        payOff(costInEau);
        require(_token.transfer(to, amount), "Vault::buy: cannot transfer EAU.");

        if (_price != price)
            _price = price;

        emit Purchase(amount, price, to);
    }

    function borrow(uint amount) notClosed notSlashed onlyOwner public override {
        require(amount <= getCreditLimit(), "Credit limit is exhausted ");

        _medleyDao.mintEAU(owner(), amount);
        _principal += amount;
        _debtUpdateTime = _timeProvider.getTime();
        _recordAccounting(AccountingRecordType.Withdrawal, amount, _debtUpdateTime);
    }

    function payOff(uint amount) notClosed notSlashed public override {
        require(_eauToken.transferFrom(msg.sender, address(this), amount), "Vault: cannot transfer EAU.");

        _recordAccounting(AccountingRecordType.Deposit, amount, _timeProvider.getTime());
        _debtUpdateTime = _timeProvider.getTime();

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

        if (_principal.add(_feeAccrued) <= _tokenAmount.mul(_price).div(4)) {
            _limitBreachedTime = 0;
            _closeOutTime = 0;
        }

        uint price = getPrice();
        if (_price != price)
            _price = price;
    }

    function close() onlyOwner public override {
        require(getTotalDebt(_timeProvider.getTime()) == 0, "Vault::close(): close allowed only if debt is paid off");
        _token.transfer(owner(), _token.balanceOf(address(this)));
        _clgnToken.transfer(owner(), _clgnToken.balanceOf(address(this)));
        _eauToken.transfer(owner(), _eauToken.balanceOf(address(this)));
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


    function getTotalDebt() notClosed public view override returns (uint debt) {
        return getTotalDebt(_timeProvider.getTime());
    }

    function getTotalDebt(uint time) notClosed public view override returns (uint debt) {
        debt = _principal.add(getFees(time));
        return debt;
    }

    function getPrincipal() notClosed public view override returns (uint) {
        return _principal;
    }

    function getFees() notClosed public view override returns (uint) {
        return getFees(_timeProvider.getTime());
    }

    function getFees(uint time) notClosed public view override returns (uint fees) {
        (fees,) = _calculateFeesAccrued(time);
        return fees;
    }

    // How much the owner can borrow at the moment. Takes into account the value has already borrowed.
    function getCreditLimit() notClosed public view override returns (uint) {
        uint loan = getTotalDebt(_timeProvider.getTime());
        uint totalLoan = _tokenAmount.mul(_price).div(4);
        if (totalLoan <= loan) {
            return 0;
        } else {
            return totalLoan.sub(loan);
        }
    }

    /**
     * Checks if credit limit is breached
     */
    function isLimitBreached() notClosed public view returns (bool) {
        return getTotalDebt(_timeProvider.getTime()) != 0 && getCreditLimit() == 0;
    }

    /**
     * Get user token price
     * Initially assessed by the vault owner, may be reduced during Initial Liquidity Vault Auction
     */
    function getPrice() public view override returns (uint price) {
        return _getDutchAuctionPrice();
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
        if (_closeOutTime != 0 && (_timeProvider.getTime() - _closeOutTime >= 180000)) return VaultState.WaitingForSlashing;
        if (isLimitBreached() && _closeOutTime != 0) return VaultState.InitialLiquidityAuctionInProcess;
        if (isLimitBreached() && _closeOutTime == 0) return VaultState.Defaulted;
        return VaultState.Trading;
    }

    function _recordAccounting(AccountingRecordType recordType, uint amount, uint time) private {
        // TODO implement
    }

    /**
     * Adds CLGN to stake
     */
    function _stake(uint clgnAmount) private {
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
            uint discount = ((_timeProvider.getTime().sub(_closeOutTime)).div(tickPriceChange));
            discount = discount % 101;
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
            uint limit = _tokenAmount.mul(_price).div(4);
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
        _eauToken.distribute(amount - toBuyClgn);
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
}

