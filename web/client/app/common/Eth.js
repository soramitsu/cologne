import ethers from "ethers";
import store from "../redux/Store";
import {loginUser} from "../redux/actions/User";
import {changeChain} from "../redux/actions/Chain";
import {cologneDaoAbi, timeProviderAbi, tokenAbi} from "./Abi";
import {networkMapper} from "./Constants";
import detectEthereumProvider from "@metamask/detect-provider";
import {provider} from "../../App";

// States emitted by Vault translated into string representation
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

if (provider) {
  // Network changed handler
  window.ethereum.on("chainChanged", (chainId) => {
    console.log(`Network has changed, new network id: ${chainId}`);
    store.dispatch(
        changeChain({
          id: chainId,
        }),
    );
  });

// Account changed handler
  window.ethereum.on("accountsChanged", (newAccounts) => {
    console.log(`Account has changed, new account: ${newAccounts[0]}`);
    store.dispatch(
        loginUser({
          address: newAccounts[0],
        }),
    );
  });
}

export const getProvider = () =>
  new ethers.providers.Web3Provider(window.ethereum);

export const getSigner = () => getProvider().getSigner();

export const getUserTokenContract = () => {
  const storeState = store.getState();

  return new ethers.Contract(
    networkMapper(storeState.chain.id).userTokenAddress,
    tokenAbi,
    getSigner(),
  );
};

export const getCologneDaoContract = () => {
  const storeState = store.getState();

  return new ethers.Contract(
    networkMapper(storeState.chain.id).cologneDaoAddress,
    cologneDaoAbi,
    getSigner(),
  );
};

export const getTimeProviderContract = () => {
  const storeState = store.getState();

  return new ethers.Contract(
    networkMapper(storeState.chain.id).timeProviderAddress,
    timeProviderAbi,
    getSigner(),
  );
};
