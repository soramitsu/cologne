// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

import "./../token/ERC20/IERC20.sol";
import "./IVault.sol";
import "./IMedleyDAO.sol";
import "../math/SafeMath.sol";

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

    constructor(address owner, uint stake, address token, uint initialAmount, uint tokenPrice) {
        _medleyDao = IMedleyDAO(msg.sender);
        _owner = owner;
        _token = IERC20(token);
        _tokenAmount = initialAmount;
        _price = tokenPrice;
        _stake(stake);
    }

    function swap() public override {
        // TODO implement
    }

    function borrow(uint amount) public override {
        require(msg.sender == _owner, "Only owner can borrow");
        require(amount <= getCreditLimit(), "Credit limit is exhausted ");

        _medleyDao.mintEAU(_owner, amount);
        _principal += amount;
        _debtUpdateTime = block.timestamp;
        _recordAccounting(kWithdrowal, amount, _debtUpdateTime);
    }

    function payOff(uint amount) public override {
        // TODO pay off fees
        // TODO pay off principal
    }

    function close() public override {
        // TODO check if can close

        // TODO transfer User Tokens to the owner
        // TODO transfer EAU from vault to owner
        // TODO transfer MDLY staked to the owner
    }

    function slash() public override {
    }

    // How much the owner can borrow at the moment. Takes into account the value has already borrowed.
    function getCreditLimit() public view override returns (uint) {
        uint loan = getTotalDebt(block.timestamp);
        uint totalLoan = _tokenAmount.mul(_price).div(4);
        if (totalLoan <= loan) {
            return 0;
        } else {
            return totalLoan.sub(loan);
        }
    }

    function getTotalDebt(uint time) public view override returns (uint) {
        return _principal.add(_calculateFeesAccrued(time));
    }

    function getPrincipal() public view override returns (uint) {
        return _principal;
    }

    function getPrice() public view override returns (uint) {
        // TODO implement Dutch auction price change
        return _price;
    }

    function getState() public view override {
    }


    function _stake(uint mdlyAmount) private {
        _collateral = _medleyDao.getMdlyPriceOracle().consult(_medleyDao.getMdlyTokenAddress(), mdlyAmount);
    }

    function _calculateFeesAccrued(uint time) private pure returns (uint) {
        // TODO implement
        return 0;
    }

    function _recordAccounting(uint recordType, uint amount, uint time) private {
        // TODO implement
    }
}

