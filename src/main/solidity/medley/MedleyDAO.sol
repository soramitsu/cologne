// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

import "./IMedleyDAO.sol";
import "./Vault.sol";
import "./../token/ERC20/IUserToken.sol";
import "./../token/ERC20/IEAUToken.sol";
import "./../token/ERC20/ICLGNToken.sol";
import "./../market/IMarketAdaptor.sol";
import "./ITimeProvider.sol";

contract MedleyDAO is IMedleyDAO {
    address[] _vaults;
    // to check if address is registered vault
    mapping(address => bool) _isVault;

    ICLGNToken _clgnToken;
    IEAUToken _eauToken;

    IMarketAdaptor _clgnMarket;

    ITimeProvider _timeProvider;

    constructor(address clgnToken, address eauToken, address clgnMarket, address timeProvider) {
        _clgnToken = ICLGNToken(clgnToken);
        _eauToken = IEAUToken(eauToken);
        _clgnMarket = IMarketAdaptor(clgnMarket);
        _timeProvider = ITimeProvider(timeProvider);
    }

    function createVault(address token, uint initialAmount, uint tokenPrice) external override returns (address) {
        address vault = address(new Vault(msg.sender, token, initialAmount, tokenPrice, _timeProvider));
        IUserToken tokenContract = IUserToken(token);
        require(tokenContract.transferFrom(msg.sender, vault, initialAmount), "MedleyDAO: Transfer of user tokens not allowed");
        _vaults.push(vault);
        _isVault[vault] = true;
        emit VaultCreation(vault, msg.sender);
        return vault;
    }

    function mintEAU(address beneficiary, uint amount) external override {
        require(_isVault[msg.sender], "Only vault can mint EAU");
        _eauToken.mint(beneficiary, amount);
    }

    function mintCLGN(address beneficiary, uint amount) external override {
        require(_isVault[msg.sender], "Only vault can mint CLGN");
        _clgnToken.mint(beneficiary, amount);
    }

    function getClgnTokenAddress() public view override returns (address) {
        return address(_clgnToken);
    }

    function getEauTokenAddress() public view override returns (address) {
        return address(_eauToken);
    }

    function listVaults() public view override returns (address [] memory) {
        return _vaults;
    }

    function getClgnMarket() external view override returns (IMarketAdaptor) {
        return _clgnMarket;
    }
}
