##Java binding generation for Solidity contracts
###Dependencies
1) Solidity compiler
```
brew tap ethereum/ethereum
brew install solidity
```
2) Web3j Java wrapper for the compiled contracts
```
brew tap web3j/web3j
brew install web3j
```
Now run
```
gradle generateEthereumContractsBindings
```
to build Solidity code and generate Java bindings
