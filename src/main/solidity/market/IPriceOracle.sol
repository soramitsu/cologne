// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

/**
 * Exchange rate of Token1/Token2 feed.
 */
interface IPriceOracle {
    /**
     * Updates oracle prices. The more often is called the better.
     */
    function update() external;

    /**
     * Get exchange rate.
     * @param token is Token1 or Token2 for amount in
     * @param amountIn to exchange
     * @return amountOut to be received on exchange
     */
    function consult(address token, uint256 amountIn) external view returns (uint256 amountOut);
}
