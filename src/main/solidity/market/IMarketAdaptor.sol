// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

interface IMarketAdaptor {
    /**
     * Swaps an exact amount of input tokens for as many output tokens as possible
     */
    function swapExactTokensForTokens(
        uint amountIn,
        uint amountOutMin,
        address[] calldata path,
        address to,
        uint deadline
    ) external returns (uint[] memory amounts);

    /**
     * Receive an exact amount of output tokens for as few input tokens as possible
     */
    function swapTokensForExactTokens(
        uint amountOut,
        uint amountInMax,
        address[] calldata path,
        address to,
        uint deadline
    ) external returns (uint[] memory amounts);

    /**
     * Given an input asset amount and an array of token addresses, calculates all subsequent maximum output token
     * amounts
     */
    function getAmountsOut(uint amountIn, address[] memory path) external view returns (uint[] memory amounts);

    /**
     * Given an output asset amount and an array of token addresses, calculates all preceding minimum input token
     * amounts
     */
    function getAmountsIn(uint amountOut, address[] memory path) external view returns (uint[] memory amounts);
}
