import React from "react";
import {Switch} from "react-router-dom";
import AppWrapper from "./AppWrapper";
import IndexPage from "./components/IndexPage";
import BasicLayout from "./layouts/BasicLayout";
import AuthorizedLayout from "./layouts/AuthorizedLayout";
import LoginPage from "./components/LoginPage";
import MainPage from "./components/MainPage";

const Routes = (
  <AppWrapper>
    <Switch>
      <IndexPage exact path="/" />

      <BasicLayout path="/login">
        <LoginPage />
      </BasicLayout>

      <AuthorizedLayout path="/main">
        <MainPage />
      </AuthorizedLayout>
    </Switch>
  </AppWrapper>
);

export default Routes;
