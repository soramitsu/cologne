// SPDX-License-Identifier: MIT

pragma solidity ^0.7.0;

import "./IMedleyDAO.sol";
import "./Vault.sol";
import "./../token/ERC20/IERC20.sol";
import "./../token/ERC20/EAUToken.sol";
import "./../token/ERC20/CLGNToken.sol";
import "./../market/IPriceOracle.sol";
import "./../market/IMarketAdaptor.sol";
import "./ITimeProvider.sol";

contract MedleyDAO is IMedleyDAO {
    address[] _vaults;
    // to check if address is registered vault
    mapping(address => bool) _isVault;

    CLGNToken _clgnToken;
    EAUToken _eauToken;

    // CLGN/DAO price feed
    IPriceOracle _clgnPriceOracle;

    IMarketAdaptor _clgnMarket;

    ITimeProvider _timeProvider;

    constructor(address clgnToken, address eauToken, address clgnPriceOracle, address clgnMarket, address timeProvider) {
        _clgnToken = CLGNToken(clgnToken);
        _eauToken = EAUToken(eauToken);
        _clgnPriceOracle = IPriceOracle(clgnPriceOracle);
        _clgnMarket = IMarketAdaptor(clgnMarket);
        _timeProvider = ITimeProvider(timeProvider);
    }

    function createVault(address token, uint stake, uint initialAmount, uint tokenPrice) external override returns (address) {
        address vault = address(new Vault(msg.sender, stake, token, initialAmount, tokenPrice, _timeProvider));
        IERC20 tokenContract = IERC20(token);
        require(tokenContract.transferFrom(msg.sender, vault, initialAmount),
            "MedleyDAO: Transfer of user tokens not allowed");
        require(_clgnToken.transferFrom(msg.sender, vault, stake), "MedleyDAO: Transfer of CLGN tokens not allowed");
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

    function getClgnPriceOracle() external view override returns (IPriceOracle) {
        return _clgnPriceOracle;
    }

    function getClgnMarket() external view override returns (IMarketAdaptor) {
        return _clgnMarket;
    }
}
