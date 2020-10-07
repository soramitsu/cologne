// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

import "./../market/uniswap/IUniswapV2Router01.sol";
import "./../market/IMarketAdaptor.sol";

contract UniswapMarketAdaptor is IMarketAdaptor {

    IUniswapV2Router01 _uniswapRouter;

    /**
     * Address of Uniswap Router
     * See https://uniswap.org/docs/v2/smart-contracts/router02/ for addresses
     */
    constructor(address router) {
        _uniswapRouter = IUniswapV2Router01(router);
    }

    // @dev See {IMarketAdaptor-swapExactTokensForTokens}
    function swapExactTokensForTokens(
        uint amountIn,
        uint amountOutMin,
        address[] calldata path,
        address to,
        uint deadline
    ) public override returns (uint[] memory amounts) {
        return _uniswapRouter.swapExactTokensForTokens(amountIn, amountOutMin, path, to, deadline);
    }

    // @dev See {IMarketAdaptor-swapTokensForExactTokens}
    function swapTokensForExactTokens(
        uint amountOut,
        uint amountInMax,
        address[] calldata path,
        address to,
        uint deadline
    ) public override returns (uint[] memory amounts) {
        return _uniswapRouter.swapTokensForExactTokens(amountOut, amountInMax, path, to, deadline);
    }

    // @dev See {IMarketAdaptor-getAmountsOut}
    function getAmountsOut(uint amountIn, address[] memory path) public view override returns (uint[] memory amounts) {
        return _uniswapRouter.getAmountsOut(amountIn, path);
    }

    // @dev See {IMarketAdaptor-getAmountsIn}
    function getAmountsIn(uint amountOut, address[] memory path) public view override returns (uint[] memory amounts) {
        return _uniswapRouter.getAmountsIn(amountOut, path);
    }
}
