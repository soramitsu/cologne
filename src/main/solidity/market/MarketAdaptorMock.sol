// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

import "./IMarketAdaptor.sol";
import "../math/SafeMath.sol";
import "../token/ERC20/ERC20.sol";

/**
 * MDLY/EAU market
 */
contract MarketAdaptorMock is IMarketAdaptor {
    using SafeMath for uint256;

    ERC20 _mdly;
    ERC20 _eau;
    // fixed rate MDLY/EAU
    uint256 _rate = 2;

    constructor(address mdly, address eau) {
        _mdly = ERC20(mdly);
        _eau = ERC20(eau);
    }

    /**
     * Swaps an exact amount of input tokens for as many output tokens as possible
     */
    function swapExactTokensForTokens(
        uint amountIn,
        uint amountOutMin,
        address[] calldata path,
        address to,
        uint //deadline
    ) external override returns (uint[] memory amounts) {
        require(path.length == 2 && (path[0] == address(_mdly) && path[1] == address(_eau))
        || (path[0] == address(_eau) && path[1] == address(_mdly)), "Token not supported");

        uint[] memory ret = getAmountsOut(amountIn, path);
        require(ret[1] >= amountOutMin, "amountOutMin too large");
        if (path[0] == address(_mdly)) {
            require(_mdly.transferFrom(to, address(this), ret[0]), "MDLY transfer error");
            require(_eau.transfer(to, ret[1]), "EAU transfer error");
        } else if (path[0] == address(_eau)) {
            require(_eau.transferFrom(to, address(this), ret[0]), "EAU transfer error");
            require(_mdly.transfer(to, ret[1]), "MDLY transfer error");
        }
        return ret;
    }

    /**
     * Receive an exact amount of output tokens for as few input tokens as possible
     */
    function swapTokensForExactTokens(
        uint amountOut,
        uint amountInMax,
        address[] calldata path,
        address to,
        uint //deadline
    ) external override returns (uint[] memory amounts) {
        require(path.length == 2 && (path[0] == address(_mdly) && path[1] == address(_eau))
        || (path[0] == address(_eau) && path[1] == address(_mdly)), "Token not supported");

        uint[] memory ret = getAmountsIn(amountOut, path);
        require(ret[0] <= amountInMax, "amountInMax too small");
        if (path[0] == address(_mdly)) {
            require(_mdly.transferFrom(to, address(this), ret[0]), "MDLY transfer error");
            require(_eau.transfer(to, ret[1]), "EAU transfer error");
        } else if (path[0] == address(_eau)) {
            require(_eau.transferFrom(to, address(this), ret[0]), "EAU transfer error");
            require(_mdly.transfer(to, ret[1]), "MDLY transfer error");
        }
        return ret;
    }

    /**
     * Given an input asset amount and an array of token addresses, calculates all subsequent maximum output token
     * amounts
     */
    function getAmountsOut(uint amountIn, address[] memory path) public view override returns (uint[] memory amounts) {
        require(path.length == 2 && (path[0] == address(_mdly) && path[1] == address(_eau))
        || (path[0] == address(_eau) && path[1] == address(_mdly)), "Token not supported");

        uint[] memory ret = new uint[](2);
        ret[0] = amountIn;
        if (path[0] == address(_mdly)) {
            ret[1] = ret[0].mul(_rate);
        } else if (path[0] == address(_eau)) {
            ret[1] = ret[0].div(_rate);
        }
        return ret;
    }

    /**
     * Given an output asset amount and an array of token addresses, calculates all preceding minimum input token
     * amounts
     */
    function getAmountsIn(uint amountOut, address[] memory path) public view override returns (uint[] memory amounts) {
        require(path.length == 2 && (path[0] == address(_mdly) && path[1] == address(_eau))
        || (path[0] == address(_eau) && path[1] == address(_mdly)), "Token not supported");

        uint[] memory ret = new uint[](2);
        ret[1] = amountOut;
        if (path[0] == address(_mdly)) {
            ret[0] = ret[1].div(_rate);
        } else if (path[0] == address(_eau)) {
            ret[0] = ret[1].mul(_rate);
        }
        return ret;
    }
}
