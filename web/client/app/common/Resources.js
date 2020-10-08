/*
    ABI section
 */

import ethers from "ethers";
import {loginUser} from "../redux/actions/User";

export const clgnTokenAbi = [
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
];

export const cologneDaoAbi = [
  {
    inputs: [
      {internalType: "address", name: "clgnToken", type: "address"},
      {internalType: "address", name: "eauToken", type: "address"},
      {internalType: "address", name: "clgnPriceOracle", type: "address"},
      {internalType: "address", name: "clgnMarket", type: "address"},
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
    name: "getClgnMarket",
    outputs: [
      {internalType: "contract IMarketAdaptor", name: "", type: "address"},
    ],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [],
    name: "getClgnPriceOracle",
    outputs: [
      {internalType: "contract IPriceOracle", name: "", type: "address"},
    ],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [],
    name: "getClgnTokenAddress",
    outputs: [{internalType: "address", name: "", type: "address"}],
    stateMutability: "view",
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
    name: "mintCLGN",
    outputs: [],
    stateMutability: "nonpayable",
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
];

export const userTokenAbi = [
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
];

export const vaultAbi = [
  {
    inputs: [
      {internalType: "address", name: "owner", type: "address"},
      {internalType: "address", name: "token", type: "address"},
      {internalType: "uint256", name: "initialAmount", type: "uint256"},
      {internalType: "uint256", name: "tokenPrice", type: "uint256"},
      {
        internalType: "contract ITimeProvider",
        name: "timeProvider",
        type: "address",
      },
    ],
    stateMutability: "nonpayable",
    type: "constructor",
  },
  {
    anonymous: false,
    inputs: [
      {
        indexed: true,
        internalType: "address",
        name: "previousOwner",
        type: "address",
      },
      {
        indexed: true,
        internalType: "address",
        name: "newOwner",
        type: "address",
      },
    ],
    name: "OwnershipTransferred",
    type: "event",
  },
  {
    anonymous: false,
    inputs: [
      {
        indexed: false,
        internalType: "uint256",
        name: "amount",
        type: "uint256",
      },
      {indexed: true, internalType: "uint256", name: "price", type: "uint256"},
      {indexed: true, internalType: "address", name: "to", type: "address"},
    ],
    name: "Purchase",
    type: "event",
  },
  {
    inputs: [{internalType: "uint256", name: "amount", type: "uint256"}],
    name: "borrow",
    outputs: [],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [
      {internalType: "uint256", name: "amount", type: "uint256"},
      {internalType: "uint256", name: "maxPrice", type: "uint256"},
      {internalType: "address", name: "to", type: "address"},
    ],
    name: "buy",
    outputs: [],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [],
    name: "canBorrow",
    outputs: [{internalType: "uint256", name: "", type: "uint256"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [
      {internalType: "uint256", name: "price", type: "uint256"},
      {internalType: "uint256", name: "eauToLock", type: "uint256"},
    ],
    name: "challenge",
    outputs: [],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [],
    name: "close",
    outputs: [],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [],
    name: "coverShortfall",
    outputs: [],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [{internalType: "address", name: "challenger", type: "address"}],
    name: "getChallengeLocked",
    outputs: [{internalType: "uint256", name: "eauLocked", type: "uint256"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [],
    name: "getChallengeWinner",
    outputs: [
      {internalType: "address", name: "", type: "address"},
      {internalType: "uint256", name: "", type: "uint256"},
    ],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [],
    name: "getCollateralInEau",
    outputs: [{internalType: "uint256", name: "", type: "uint256"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [],
    name: "getCreditLimit",
    outputs: [{internalType: "uint256", name: "", type: "uint256"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [],
    name: "getFees",
    outputs: [{internalType: "uint256", name: "", type: "uint256"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [],
    name: "getPrice",
    outputs: [{internalType: "uint256", name: "price", type: "uint256"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [],
    name: "getPrincipal",
    outputs: [{internalType: "uint256", name: "", type: "uint256"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [{internalType: "address", name: "challenger", type: "address"}],
    name: "getRedeemableChallenge",
    outputs: [
      {internalType: "uint256", name: "eauAmount", type: "uint256"},
      {internalType: "uint256", name: "userTokenAmount", type: "uint256"},
    ],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [],
    name: "getState",
    outputs: [
      {internalType: "enum IVault.VaultState", name: "", type: "uint8"},
    ],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [],
    name: "getTokenAmount",
    outputs: [{internalType: "uint256", name: "", type: "uint256"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [],
    name: "getTotalDebt",
    outputs: [{internalType: "uint256", name: "debt", type: "uint256"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [],
    name: "getTotalFeesRepaid",
    outputs: [{internalType: "uint256", name: "", type: "uint256"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [],
    name: "isLimitBreached",
    outputs: [{internalType: "bool", name: "", type: "bool"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [],
    name: "isOwner",
    outputs: [{internalType: "bool", name: "", type: "bool"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [],
    name: "owner",
    outputs: [{internalType: "address", name: "", type: "address"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [{internalType: "uint256", name: "amount", type: "uint256"}],
    name: "payOff",
    outputs: [],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [],
    name: "redeemChallenge",
    outputs: [
      {internalType: "uint256", name: "eauAmount", type: "uint256"},
      {internalType: "uint256", name: "userTokenAmount", type: "uint256"},
    ],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [],
    name: "renounceOwnership",
    outputs: [],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [],
    name: "slash",
    outputs: [],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [{internalType: "uint256", name: "amount", type: "uint256"}],
    name: "stake",
    outputs: [],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [],
    name: "startInitialLiquidityAuction",
    outputs: [],
    stateMutability: "nonpayable",
    type: "function",
  },
  {
    inputs: [{internalType: "address", name: "newOwner", type: "address"}],
    name: "transferOwnership",
    outputs: [],
    stateMutability: "nonpayable",
    type: "function",
  },
];

export const timeProviderAbi = [
  {inputs: [], stateMutability: "nonpayable", type: "constructor"},
  {
    inputs: [],
    name: "getTime",
    outputs: [{internalType: "uint256", name: "", type: "uint256"}],
    stateMutability: "view",
    type: "function",
  },
  {
    inputs: [{internalType: "uint256", name: "time", type: "uint256"}],
    name: "setTime",
    outputs: [],
    stateMutability: "nonpayable",
    type: "function",
  },
];

export const VaultStates = [
  "Trading",
  "Defaulted",
  "Initial Liquidity Auction In Progress",
  "Waiting For Slashing",
  "Waiting For Clgn Auction",
  "Slashed",
  "Closed",
  "Sold Out",
];

export const stateFormatter = (state) => VaultStates[state];

// Network changed handler
window.ethereum.on("chainChanged", (chainId) => {
  console.log(`Network has changed, new network id: ${chainId}`);
  window.location.reload();
});

// Account changed handler
window.ethereum.on("accountsChanged", (newAccounts) => {
  console.log(`Account has changed, new account: ${newAccounts[0]}`);
  loginUser({
    address: newAccounts[0],
  });
  window.location.reload();
});

export const provider = new ethers.providers.Web3Provider(window.ethereum);

export const signer = provider.getSigner();
export const cologneDaoAddress = "0x79eafd0b5ec8d3f945e6bb2817ed90b046c0d0af";
export const userTokenAddress = "0x7d73424a8256c0b2ba245e5d5a3de8820e45f390";
export const timeProviderAddress = "0x6e05f58eedda592f34dd9105b1827f252c509de0";

export const userTokenContract = new ethers.Contract(
  userTokenAddress,
  userTokenAbi,
  signer,
);

export const cologneDaoContract = new ethers.Contract(
  cologneDaoAddress,
  cologneDaoAbi,
  signer,
);

export const timeProviderContract = new ethers.Contract(
  timeProviderAddress,
  timeProviderAbi,
  signer,
);
