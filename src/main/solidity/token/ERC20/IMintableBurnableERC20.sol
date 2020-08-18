// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

import "./IERC20.sol";

interface IMintableBurnableERC20 is IERC20 {
    function mint(address account, uint256 value) external;

    function burn(uint256 amount) external;

    function burnFrom(address account, uint256 amount) external;
}
