// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

import "./ITimeProvider.sol";

contract TimeProviderMock is ITimeProvider {
    uint _time;

    constructor() {
        _time = block.timestamp;
    }

    function setTime(uint time) public {
        require(time >= _time, "TimeProviderMock: Cannot set past time.");
        _time = time;
    }

    function getTime() public view override returns (uint) {
        return _time;
    }
}
