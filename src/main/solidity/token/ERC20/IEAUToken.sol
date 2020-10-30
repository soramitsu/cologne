// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

import "./IERC20.sol";

interface IEAUToken is IERC20 {
    function decimals() external view returns (uint8);

    function mint(address account, uint256 value) external;

    function burn(uint256 amount) external;

    function distribute(uint256 amount) external returns (uint256);
}
