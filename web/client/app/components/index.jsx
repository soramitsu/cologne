import React from "react";
import {connect} from "react-redux";
import {Route} from "react-router-dom";
import styled from "react-emotion";

import Header from "./Header";

import VerificationPage from "./VerificationPage";
import AccountTypePage from "./AccountTypePage";
import EquipmentPage from "./EquipmentPage";
import PaymentPage from "./PaymentPage";
import TransactionPage from "./TransactionPage";
import StatisticsPage from "./StatisticsPage";
import ProfilePage from "./ProfilePage";

const Wrapper = styled("div")`
  width: 1024px;
  margin: 0 auto;
  @media (max-width: 480px) {
    width: 320px;
  }
`;

const MainSection = styled("section")`
  width: 100%;
  height: 100%;
  position: relative;
`;

const AuthComponent = () => (
  <Wrapper>
    <Header />
    <MainSection>
      <Route
        path="/main"
        render={routeProps => <EquipmentPage {...routeProps} />}
      />
    </MainSection>
  </Wrapper>
);

const mapStateToProps = state => state;

export default connect(mapStateToProps)(AuthComponent);
