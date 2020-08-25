// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

interface IVault {
    function swap() external;

    function borrow(uint amount) external;

    function payOff(uint amount) external;

    function close() external;

    function slash() external;

    // Get amount of EAU the owner can borrow now
    function getCreditLimit() external view returns (uint);

    function getPrice() external view returns (uint);

    function getState() external view;

}
