// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

/**
 * Provides current time
 * Used moslty in testing proposes
 */
interface ITimeProvider {
    function getTime() external view returns (uint);
}
