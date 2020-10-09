/* eslint-disable react/no-children-prop */
import "semantic-ui-css/semantic.min.css";
import React from "react";
import {render} from "react-dom";
import {Provider} from "react-redux";
import {createBrowserHistory} from "history";
import {Router} from "react-router-dom";
import detectEthereumProvider from "@metamask/detect-provider";
import store from "./app/redux/Store";
import Routes from "./app/Routes";
import {changeChain} from "./app/redux/actions/Chain";

export const History = createBrowserHistory();

const renderApp = () => {
  render(
    <Provider store={store}>
      <Router history={History} children={Routes} />
    </Provider>,
    document.getElementById("App"),
  );
};

const getProvider = async () => {
  const provider = await detectEthereumProvider();
  if (provider) {
    store.dispatch(
      changeChain({
        id: provider.chainId,
      }),
    );
  } else {
    console.log("Please install MetaMask!");
  }

  return provider;
};

store.subscribe(renderApp);

renderApp();
export const provider = getProvider();
