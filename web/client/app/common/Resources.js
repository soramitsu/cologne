/*
    ABI section
 */

export const clgnTokenAbi = web3.eth.contract([
  {inputs: [], stateMutability: "nonpayable", type: "constructor"},
  {
    anonymous: false,
    inputs: [
      {indexed: true, internalType: "address", name: "owner", type: "address"},
      {
        indexed: true,
        internalType: "address",
        name: "spender",
        type: "address",
      },
      {indexed: false, internalType: "uint256", name: "value", type: "uint256"},
    ],
    name: "Approval",
    type: "event",
  },
  {
    anonymous: false,
    inputs: [
      {indexed: true, internalType: "address", name: "from", type: "address"},
      {indexed: true, internalType: "address", name: "to", type: "address"},
      {indexed: false, internalType: "uint256", name: "value", type: "uint256"},
    ],
    name: "Transfer",
    type: "event",
  },
  {
    inputs: [
      {internalType: "address", name: "owner", type: "address"},
      {internalType: "address", name: "spender", type: "address"},
    ],
    name: "allowance",
    outputs: [{internalType: "uint256", name: "", type: "uint256"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [
      {internalType: "address", name: "spender", type: "address"},
      {internalType: "uint256", name: "amount", type: "uint256"},
    ],
    name: "approve",
    outputs: [{internalType: "bool", name: "", type: "bool"}],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [{internalType: "address", name: "account", type: "address"}],
    name: "balanceOf",
    outputs: [{internalType: "uint256", name: "", type: "uint256"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [{internalType: "uint256", name: "amount", type: "uint256"}],
    name: "burn",
    outputs: [],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [
      {internalType: "address", name: "account", type: "address"},
      {internalType: "uint256", name: "amount", type: "uint256"},
    ],
    name: "burnFrom",
    outputs: [],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [],
    name: "decimals",
    outputs: [{internalType: "uint8", name: "", type: "uint8"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [
      {internalType: "address", name: "spender", type: "address"},
      {internalType: "uint256", name: "subtractedValue", type: "uint256"},
    ],
    name: "decreaseAllowance",
    outputs: [{internalType: "bool", name: "", type: "bool"}],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [
      {internalType: "address", name: "spender", type: "address"},
      {internalType: "uint256", name: "addedValue", type: "uint256"},
    ],
    name: "increaseAllowance",
    outputs: [{internalType: "bool", name: "", type: "bool"}],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [
      {internalType: "address", name: "account", type: "address"},
      {internalType: "uint256", name: "value", type: "uint256"},
    ],
    name: "mint",
    outputs: [],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [],
    name: "name",
    outputs: [{internalType: "string", name: "", type: "string"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [],
    name: "symbol",
    outputs: [{internalType: "string", name: "", type: "string"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [],
    name: "totalSupply",
    outputs: [{internalType: "uint256", name: "", type: "uint256"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [
      {internalType: "address", name: "recipient", type: "address"},
      {internalType: "uint256", name: "amount", type: "uint256"},
    ],
    name: "transfer",
    outputs: [{internalType: "bool", name: "", type: "bool"}],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [
      {internalType: "address", name: "sender", type: "address"},
      {internalType: "address", name: "recipient", type: "address"},
      {internalType: "uint256", name: "amount", type: "uint256"},
    ],
    name: "transferFrom",
    outputs: [{internalType: "bool", name: "", type: "bool"}],
    stateMutability: "nonpayable",
    type: "function",
  },
]);

export const cologneDaoAbi = web3.eth.contract([
  {
    inputs: [
      {internalType: "address", name: "mdlyToken", type: "address"},
      {internalType: "address", name: "eauToken", type: "address"},
      {internalType: "address", name: "mdlyPriceOracle", type: "address"},
      {internalType: "address", name: "mdlyMarket", type: "address"},
      {internalType: "address", name: "timeProvider", type: "address"},
    ],
    stateMutability: "nonpayable",
    type: "constructor",
  },
  {
    anonymous: false,
    inputs: [
      {indexed: true, internalType: "address", name: "vault", type: "address"},
      {indexed: true, internalType: "address", name: "owner", type: "address"},
    ],
    name: "VaultCreation",
    type: "event",
  },
  {
    inputs: [
      {internalType: "address", name: "token", type: "address"},
      {internalType: "uint256", name: "stake", type: "uint256"},
      {internalType: "uint256", name: "initialAmount", type: "uint256"},
      {internalType: "uint256", name: "tokenPrice", type: "uint256"},
    ],
    name: "createVault",
    outputs: [{internalType: "address", name: "", type: "address"}],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [],
    name: "getEauTokenAddress",
    outputs: [{internalType: "address", name: "", type: "address"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [],
    name: "getMdlyMarket",
    outputs: [
      {internalType: "contract IMarketAdaptor", name: "", type: "address"},
    ],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [],
    name: "getMdlyPriceOracle",
    outputs: [
      {internalType: "contract IPriceOracle", name: "", type: "address"},
    ],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [],
    name: "getMdlyTokenAddress",
    outputs: [{internalType: "address", name: "", type: "address"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [],
    name: "listVaults",
    outputs: [{internalType: "address[]", name: "", type: "address[]"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [
      {internalType: "address", name: "beneficiary", type: "address"},
      {internalType: "uint256", name: "amount", type: "uint256"},
    ],
    name: "mintEAU",
    outputs: [],
    stateMutability: "nonpayable",
    type: "function",
  },
]);

export const userTokenAbi = web3.eth.contract([
  {inputs: [], stateMutability: "nonpayable", type: "constructor"},
  {
    anonymous: false,
    inputs: [
      {indexed: true, internalType: "address", name: "owner", type: "address"},
      {
        indexed: true,
        internalType: "address",
        name: "spender",
        type: "address",
      },
      {indexed: false, internalType: "uint256", name: "value", type: "uint256"},
    ],
    name: "Approval",
    type: "event",
  },
  {
    anonymous: false,
    inputs: [
      {indexed: true, internalType: "address", name: "from", type: "address"},
      {indexed: true, internalType: "address", name: "to", type: "address"},
      {indexed: false, internalType: "uint256", name: "value", type: "uint256"},
    ],
    name: "Transfer",
    type: "event",
  },
  {
    inputs: [
      {internalType: "address", name: "owner", type: "address"},
      {internalType: "address", name: "spender", type: "address"},
    ],
    name: "allowance",
    outputs: [{internalType: "uint256", name: "", type: "uint256"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [
      {internalType: "address", name: "spender", type: "address"},
      {internalType: "uint256", name: "amount", type: "uint256"},
    ],
    name: "approve",
    outputs: [{internalType: "bool", name: "", type: "bool"}],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [{internalType: "address", name: "account", type: "address"}],
    name: "balanceOf",
    outputs: [{internalType: "uint256", name: "", type: "uint256"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [{internalType: "uint256", name: "amount", type: "uint256"}],
    name: "burn",
    outputs: [],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [
      {internalType: "address", name: "account", type: "address"},
      {internalType: "uint256", name: "amount", type: "uint256"},
    ],
    name: "burnFrom",
    outputs: [],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [],
    name: "decimals",
    outputs: [{internalType: "uint8", name: "", type: "uint8"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [
      {internalType: "address", name: "spender", type: "address"},
      {internalType: "uint256", name: "subtractedValue", type: "uint256"},
    ],
    name: "decreaseAllowance",
    outputs: [{internalType: "bool", name: "", type: "bool"}],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [
      {internalType: "address", name: "spender", type: "address"},
      {internalType: "uint256", name: "addedValue", type: "uint256"},
    ],
    name: "increaseAllowance",
    outputs: [{internalType: "bool", name: "", type: "bool"}],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [
      {internalType: "address", name: "account", type: "address"},
      {internalType: "uint256", name: "value", type: "uint256"},
    ],
    name: "mint",
    outputs: [],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [],
    name: "name",
    outputs: [{internalType: "string", name: "", type: "string"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [],
    name: "symbol",
    outputs: [{internalType: "string", name: "", type: "string"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [],
    name: "totalSupply",
    outputs: [{internalType: "uint256", name: "", type: "uint256"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [
      {internalType: "address", name: "recipient", type: "address"},
      {internalType: "uint256", name: "amount", type: "uint256"},
    ],
    name: "transfer",
    outputs: [{internalType: "bool", name: "", type: "bool"}],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [
      {internalType: "address", name: "sender", type: "address"},
      {internalType: "address", name: "recipient", type: "address"},
      {internalType: "uint256", name: "amount", type: "uint256"},
    ],
    name: "transferFrom",
    outputs: [{internalType: "bool", name: "", type: "bool"}],
    stateMutability: "nonpayable",
    type: "function",
  },
]);

export const vaultAbi =

export const multiplier = 1000000000000000000;

export const cologneDaoAddress = "0x79eafd0b5ec8d3f945e6bb2817ed90b046c0d0af";
export const userTokenAddress = "0x7d73424a8256c0b2ba245e5d5a3de8820e45f390";

export const userTokenContract = userTokenAbi.at(userTokenAddress);
export const cologneDaoContract = cologneDaoAbi.at(cologneDaoAddress);
