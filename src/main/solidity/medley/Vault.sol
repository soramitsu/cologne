// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

import "./../token/ERC20/IERC20.sol";
import "./../token/ERC20/EAUToken.sol";
import "./../token/ERC20/MDLYToken.sol";
import "./IVault.sol";
import "./IMedleyDAO.sol";
import "./../math/SafeMath.sol";
import "./../math/Math.sol";
import "./ITimeProvider.sol";
import "./../utils/Ownable.sol";

contract Vault is IVault, Ownable {
    using SafeMath for uint;

    // Types of accounting records
    uint constant kDeposit = 1;
    uint constant kWithdrowal = 2;

    struct AccountingRecord {
        uint recordType;
        uint amount;
        uint time;
    }

    AccountingRecord[] _accountingBook;

    IMedleyDAO _medleyDao;

    EAUToken _eauToken;
    MDLYToken _mdlyToken;

    // Owner ERC20 token
    IERC20 _token;
    // amount of ERC20 token the owner has deposited
    uint _tokenAmount;
    // Owner token price assessed by the owner
    uint _price = 0;

    // Value of MDLY collateral in EAU
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

    bool _closed = false;

    modifier notClosed {
        require(!_closed, "Vault is closed");
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
        _mdlyToken = MDLYToken(_medleyDao.getMdlyTokenAddress());
        _token = IERC20(token);
        _tokenAmount = initialAmount;
        _price = tokenPrice;
        _timeProvider = timeProvider;
        _debtUpdateTime = _timeProvider.getTime();
        _stake(stake);
    }

    function buy(uint amount, uint maxPrice, address to) notClosed public override {
        require(amount <= _token.balanceOf(address(this)), "Vault::buy(): Not enough tokens to sell");
        uint price = getPrice();
        require(price > 0, "Vault::buy(): Initial Liquidity Auction is over");
        require(price <= maxPrice, "Vault::buy(): Price too low");
        uint costInEau = amount.mul(price);

        // distribute penalty if Initial Liquidity Auction is active
        if (_closeOutTime != 0) {
            uint penalty = costInEau.div(10);
            require(_eauToken.transferFrom(msg.sender, address(this), penalty), "Vault::buy: cannot transfer EAU penalty.");

            _eauToken.approve(address(_medleyDao.getMdlyMarket()), penalty);
            uint mdlyBought = _buyMdlyForEau(penalty);

            uint initiatorBounty = mdlyBought.mul(33).div(100);
            require(_mdlyToken.transfer(_closeOutInitiator, initiatorBounty), "Vault::buy: transfer MDLY bounty to initiator");

            uint bidderBounty = mdlyBought.mul(33).div(100);
            require(_mdlyToken.transfer(msg.sender, bidderBounty), "Vault::buy: transfer MDLY bounty to bidder");

            _mdlyToken.burn(mdlyBought.sub(initiatorBounty).sub(bidderBounty));

            costInEau = costInEau.sub(penalty);
        }

        payOff(costInEau);
        require(_token.transfer(to, amount), "Vault::buy: cannot transfer EAU.");

        if (_price != price)
            _price = price;

        emit Purchase(amount, price, to);
    }

    function borrow(uint amount) notClosed onlyOwner public override {
        require(amount <= getCreditLimit(), "Credit limit is exhausted ");

        _medleyDao.mintEAU(owner(), amount);
        _principal += amount;
        _debtUpdateTime = _timeProvider.getTime();
        _recordAccounting(kWithdrowal, amount, _debtUpdateTime);
    }

    function payOff(uint amount) notClosed public override {
        require(_eauToken.transferFrom(msg.sender, address(this), amount), "Vault: cannot transfer EAU.");

        _recordAccounting(kDeposit, amount, _timeProvider.getTime());
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
        _mdlyToken.transfer(owner(), _mdlyToken.balanceOf(address(this)));
        _eauToken.transfer(owner(), _eauToken.balanceOf(address(this)));
        _closed = true;
    }

    function startInitialLiquidityAuction() notClosed public override {
        require(isLimitBreached(), "Vault::startInitialLiquidityAuction(): credit limit is not breached");
        require(_closeOutTime == 0, "Vault::startInitialLiquidityAuction(): close-out already called");

        _closeOutTime = _timeProvider.getTime();
        _closeOutInitiator = msg.sender;

        // TODO run Dutch Auction
    }

    function slash() notClosed public override {
        // if getPrice() == 0
        // TODO implement
    }

    function getTotalDebt() notClosed public view override returns (uint debt) {
        return getTotalDebt(_timeProvider.getTime());
    }

    function getTotalDebt(uint time) notClosed public view override returns (uint debt) {
        uint fees;
        (fees,) = _calculateFeesAccrued(time);
        debt = _principal.add(fees);
        return debt;
    }

    function getPrincipal() notClosed public view override returns (uint) {
        return _principal;
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
     * Get MDLY collateral in EAU
     */
    function getCollateralInEau() public view override returns (uint) {
        return _medleyDao.getMdlyPriceOracle().consult(_medleyDao.getMdlyTokenAddress(), _collateral);
    }

    function getState() public view override {
    }

    function _recordAccounting(uint recordType, uint amount, uint time) private {
        // TODO implement
    }

    /**
     * Adds MDLY to stake
     */
    function _stake(uint mdlyAmount) private {
        _collateral = _collateral + mdlyAmount;
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
     * Pay off fees accrued and distribute 50% of EAU paid and buy and burn MDLY for another 50%.
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
        uint toBuyMdly = feesPaid.div(2);

        uint mdlyBought = _buyMdlyForEau(toBuyMdly);
        _mdlyToken.burn(mdlyBought);

        // 50% of EAU are distributed
        _eauToken.distribute(feesPaid - toBuyMdly);

        return leftover;
    }

    /**
     * Buy as many MDLY as possible for exact EAU amount
     * @param eauAmount - to spend
     * @return bought - amount bought
     */
    function _buyMdlyForEau(uint eauAmount) private returns (uint bought) {
        address[] memory path = new address[](2);
        path[0] = address(_eauToken);
        path[1] = address(_mdlyToken);

        // buy at price from oracle -10%
        // TODO clarify price
        uint mdlyBoughtExpected = _medleyDao.getMdlyPriceOracle().consult(address(_eauToken), eauAmount).mul(9).div(10);

        // TODO clarify deadline for Uniswap
        uint deadline = _timeProvider.getTime() + 10000;

        uint[] memory amounts = _medleyDao.getMdlyMarket().swapExactTokensForTokens(eauAmount, mdlyBoughtExpected, path, address(this), deadline);
        bought = amounts[1];
        require(eauAmount == amounts[0], "Vault::buyMDLY(): not exact amount of EAU sold to buy MDLY");
        require(mdlyBoughtExpected <= bought, "Vault::buyMDLY(): MDLY bought is less than expected");
        return bought;
    }
}

