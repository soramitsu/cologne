// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

import "./IPriceOracle.sol";
import "../math/SafeMath.sol";

/**
 * Mock of IPriceOracle for testing.
 */
contract PriceOracleMock is IPriceOracle {
    using SafeMath for uint256;

    // Exchange rate is fixed and equals Token1/Token2 = 2
    uint256 _rate = 2;
    address _token1;
    address _token2;

    constructor(address token1, address token2) {
        _token1 = token1;
        _token2 = token2;
    }

    function update() external override {
        // nothing
    }

    function consult(address token, uint256 amountIn) external view override returns (uint256 amountOut) {
        require(token == _token1 || token == _token2, "Unknown token");
        if (token == _token1) {
            return amountIn.mul(_rate);
        }
        return amountIn.div(_rate);
    }
}
