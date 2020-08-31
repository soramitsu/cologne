// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

import "./../token/ERC20/IERC20.sol";
import "./../token/ERC20/EAUToken.sol";
import "./../token/ERC20/MDLYToken.sol";
import "./IVault.sol";
import "./IMedleyDAO.sol";
import "./../math/SafeMath.sol";
import "./ITimeProvider.sol";

contract Vault is IVault {
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

    address _owner;

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

    bool _limitBreached = false;
    uint _breachTime = 0;

    bool _closed = false;

    ITimeProvider _timeProvider;

    modifier notClosed {
        require(!_closed, "Vault is closed");
        _;
    }

    constructor(address owner, uint stake, address token, uint initialAmount, uint tokenPrice, ITimeProvider timeProvider) {
        _medleyDao = IMedleyDAO(msg.sender);
        _owner = owner;
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
        require(price <= maxPrice, "Vault::buy(): Price too low");
        uint costInEau = amount * price;

        payOff(costInEau);
        require(_token.transfer(to, amount), "Vault::buy: cannot transfer EAU.");

        emit Purchase(amount, price, to);
    }

    function borrow(uint amount) notClosed public override {
        require(msg.sender == _owner, "Only owner can borrow");
        require(amount <= getCreditLimit(), "Credit limit is exhausted ");

        _medleyDao.mintEAU(_owner, amount);
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
    }

    function close() public override {
        require(msg.sender == _owner);
        require(getTotalDebt(_timeProvider.getTime()) == 0, "Vault::close(): close allowed only if debt is payed off");
        _token.transfer(_owner, _token.balanceOf(address(this)));
        _mdlyToken.transfer(_owner, _mdlyToken.balanceOf(address(this)));
        _eauToken.transfer(_owner, _eauToken.balanceOf(address(this)));
        _closed = true;
    }

    function slash() notClosed public override {
        // TODO implement
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

    function getTotalDebt(uint time) notClosed public view override returns (uint debt) {
        (debt,) = _calculateFeesAccrued(time);
        debt = _principal.add(debt);
        return debt;
    }

    function getPrincipal() notClosed public view override returns (uint) {
        return _principal;
    }

    function getPrice() public view override returns (uint price) {
        (, price) = _calculateFeesAccrued(_timeProvider.getTime());
        return price;
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
     * Get price
     * @param breachTime - time when limit was breached (0 if was not)
     * @return price
     */
    function _getPrice(uint breachTime) private view returns (uint price) {
        // 30 min
        uint tickPriceChange = 1800;
        price = _price;
        if (breachTime != 0) {
            require(_timeProvider.getTime() >= breachTime, "Vault::getPrice(): Incorrect state: Limit is breached in the future!");
            uint discount = ((_timeProvider.getTime().sub(breachTime)).div(tickPriceChange));
            discount = discount % 101;
            price = price.mul(100 - discount).div(100);
        }
        return price;
    }

    function _calculateFeesAccrued(uint time) private view returns (uint feeAccrued, uint price) {
        require(time >= _debtUpdateTime, "Cannot calculate fee in the past");

        // period to accrue fee in seconds (one day)
        uint period = 86400;

        // TODO calculate rate accrording the table
        // rate per period multiplied by 1'000'000
        uint rate = uint(100000).div(365);

        uint breachTime = 0;
        price = _getPrice(breachTime);
        feeAccrued = _feeAccrued;
        for (uint i = _debtUpdateTime; i < time; i = i + period) {
            uint limit = _tokenAmount.mul(price).div(4);
            // limit has been breached
            if (_principal.add(feeAccrued) > limit) {
                if (breachTime == 0) {
                    breachTime = i;
                }
                price = _getPrice(breachTime);
            }
            feeAccrued = feeAccrued + (_principal + feeAccrued) * rate / 1000000;
        }
        return (feeAccrued, price);
    }

    /**
     * Pay off fees accrued and distribute 50% of EAU paid and buy and burn MDLY for another 50%.
     * @param amount - amount to pay off in EAU
     * @return leftover - amount left after paying in EAU
     */
    function _payOffFees(uint amount) private returns (uint leftover) {
        uint totalFeesAccrued;
        (totalFeesAccrued,) = _calculateFeesAccrued(_timeProvider.getTime());
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
        // buy at price from oracle -10%
        // TODO clarify price
        uint mdlyBoughtExpected = _medleyDao.getMdlyPriceOracle().consult(address(_eauToken), toBuyMdly).mul(9).div(10);
        address[] memory path = new address[](2);
        path[0] = address(_eauToken);
        path[1] = address(_mdlyToken);
        // TODO clarify deadline for Uniswap
        uint deadline = _timeProvider.getTime() + 10000;
        uint[] memory amounts = _medleyDao.getMdlyMarket().swapExactTokensForTokens(toBuyMdly, mdlyBoughtExpected, path, address(this), deadline);
        uint mdlyBought = amounts[1];
        require(toBuyMdly == amounts[0], "Vault::payOff(): not exact amount of EAU sold to buy MDLY");
        require(mdlyBoughtExpected >= mdlyBought, "Vault::payOff(): MDLY bought is less than expected");
        _mdlyToken.burn(mdlyBought);

        // 50% of EAU are distributed
        _eauToken.distribute(feesPaid - toBuyMdly);

        return leftover;
    }
}

