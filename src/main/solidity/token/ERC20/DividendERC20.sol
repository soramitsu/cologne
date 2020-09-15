// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

import "../../math/SafeMath.sol";
import "./ERC20Snapshot.sol";

contract DividendERC20 is ERC20Snapshot {
    using SafeMath for uint256;

    uint256 _lastDistributionId;

    // distributions - snapshot id (number of distribution) => amount distributed
    mapping(uint256 => uint256) _distributions;

    // last dividend distribution accrued
    mapping(address => uint256) _lastAccrued;

    constructor(string memory name, string memory symbol) ERC20(name, symbol) {
    }

    function snapshot() private returns (uint256) {
        return _snapshot();
    }

    function distribute(uint256 amount) external returns (uint256) {
        require(transfer(address(this), amount), "ERC20 Dividends disrtibution error");
        _lastDistributionId = snapshot();
        _distributions[_lastDistributionId] = amount;
        return _lastDistributionId;
    }

    function dividensAccrued(address holder) public view returns (uint256) {
        require(_lastDistributionId > 0, "There has not been distributions yet");
        uint256 amount = 0;
        for (uint256 distributionId = _lastAccrued[holder] + 1; distributionId <= _lastDistributionId; distributionId++) {
            uint256 distributed = _distributions[distributionId];
            amount += distributed
            .mul(balanceOfAt(holder, distributionId))
            .div(totalSupplyAt(distributionId) - distributed);
        }
        return amount;
    }

    function withdrawDividends(address holder) external {
        uint256 dividends = dividensAccrued(holder);
        require(dividends > 0, "No dividends accrued");
        this.transfer(holder, dividends);
        _lastAccrued[holder] = _lastDistributionId;
    }
}
