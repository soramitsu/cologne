/* eslint-disable react/no-children-prop */
import "babel-polyfill";
import "normalize.css";
import "./assets/fonts/fonts.css";
import "rc-slider/assets/index.css";
import "./assets/custom.css";

import React from "react";
import {render} from "react-dom";
import {Provider} from "react-redux";
import {createBrowserHistory} from "history";
import {Router} from "react-router-dom";
import store from "./app/redux/Store";
import Routes from "./app/Routes";

export const History = createBrowserHistory();

const renderApp = () => {
  render(
    <Provider store={store}>
      <Router history={History} children={Routes} />
    </Provider>,
    document.getElementById("App"),
  );
};

store.subscribe(renderApp);

renderApp();