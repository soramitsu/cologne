// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

import "./DividendERC20.sol";
import "./IMintableBurnableERC20.sol";
import "./../../utils/Ownable.sol";

contract EAUToken is DividendERC20, IMintableBurnableERC20, Ownable {
    using SafeMath for uint256;

    constructor() DividendERC20("Test EAU liquidity token", "TEST_EAU") Ownable(_msgSender()) {
    }

    function mint(address account, uint256 value) onlyOwner external override {
        _mint(account, value);
    }

    /**
     * @dev Destroys `amount` tokens from the caller.
     *
     * See {ERC20-_burn}.
     */
    function burn(uint256 amount) external override {
        _burn(_msgSender(), amount);
    }

    /**
     * @dev Destroys `amount` tokens from `account`, deducting from the caller's
     * allowance.
     *
     * See {ERC20-_burn} and {ERC20-allowance}.
     *
     * Requirements:
     *
     * - the caller must have allowance for ``accounts``'s tokens of at least
     * `amount`.
     */
    function burnFrom(address account, uint256 amount) external override {
        uint256 decreasedAllowance = allowance(account, _msgSender()).sub(amount, "ERC20: burn amount exceeds allowance");

        _approve(account, _msgSender(), decreasedAllowance);
        _burn(account, amount);
    }
}
