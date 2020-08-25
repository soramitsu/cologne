// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

import "./IMedleyDAO.sol";
import "./Vault.sol";
import "./../token/ERC20/IERC20.sol";
import "./../token/ERC20/EAUToken.sol";
import "./../token/ERC20/MDLYToken.sol";
import "./../market/IPriceOracle.sol";

contract MedleyDAO is IMedleyDAO {
    address[] _vaults;
    // to check if address is registered vault
    mapping(address => bool) _isVault;

    MDLYToken _mdlyToken;
    EAUToken _eauToken;

    // MDLY/DAO price feed
    IPriceOracle _mdlyPriceOracle;

    constructor(address mdlyToken, address eauToken, address mdlyPriceOracle) {
        _mdlyToken = MDLYToken(mdlyToken);
        _eauToken = EAUToken(eauToken);
        _mdlyPriceOracle = IPriceOracle(mdlyPriceOracle);
    }

    function createVault(address token, uint stake, uint initialAmount, uint tokenPrice) external override returns (address) {
        address vault = address(new Vault(msg.sender, stake, token, initialAmount, tokenPrice));
        IERC20 tokenContract = IERC20(token);
        require(tokenContract.transferFrom(msg.sender, vault, initialAmount),
            "MedleyDAO: Transfer of user tokens not allowed");
        require(_mdlyToken.transferFrom(msg.sender, vault, stake), "MedleyDAO: Transfer of MDLY tokens not allowed");
        _vaults.push(vault);
        _isVault[vault] = true;
        emit VaultCreation(vault, msg.sender);
        return vault;
    }

    function mintEAU(address beneficiary, uint amount) external override {
        require(_isVault[msg.sender], "Only vault can mint EAU");
        _eauToken.mint(beneficiary, amount);
    }

    function getMdlyTokenAddress() public view override returns (address) {
        return address(_mdlyToken);
    }

    function getEauTokenAddress() public view override returns (address) {
        return address(_eauToken);
    }

    function listVaults() public view override returns (address [] memory) {
        return _vaults;
    }

    function getMdlyPriceOracle() external view override returns (IPriceOracle) {
        return _mdlyPriceOracle;
    }

    event VaultCreation(address indexed vault, address indexed owner);
}
