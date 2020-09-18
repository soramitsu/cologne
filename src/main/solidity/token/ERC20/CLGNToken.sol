// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

import "./ERC20.sol";
import "./IMintableBurnableERC20.sol";
import "./../../utils/Ownable.sol";

contract CLGNToken is ERC20, IMintableBurnableERC20, Ownable {
    using SafeMath for uint256;

    constructor() ERC20("Test CLGN token", "TEST_CLGN") Ownable(_msgSender()) {
        // initial amount is 22,5 kk
        _mint(owner(), 22500000 * 10 ** decimals());
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
