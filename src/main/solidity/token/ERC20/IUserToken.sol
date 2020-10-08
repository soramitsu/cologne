// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

import "./IERC20.sol";

interface IUserToken is IERC20 {
    function decimals() external view returns (uint8);
}
