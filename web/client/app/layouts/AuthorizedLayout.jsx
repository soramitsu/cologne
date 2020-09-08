/* eslint-disable react/forbid-prop-types */
import React from "react";
import styled from "react-emotion";
import {connect} from "react-redux";
import PropTypes from "prop-types";
import {Redirect} from "react-router-dom";
import User from "../redux/propTypes/User";
import Header from "../components/Header";

const Wrapper = styled("div")`
  width: 1024px;
  margin: 0 auto;
  @media (max-width: 480px) {
    width: 320px;
  }
  @media (min-width: 481px) and (max-width: 768px) {
    width: 480px;
  }
  @media (min-width: 769px) and (max-width: 1023px) {
    width: 768px;
  }
`;

const MainSection = styled("section")`
  width: 100%;
  height: 100%;
  position: relative;
`;

const AuthorizedLayout = props => {
  const {
    user: {account},
    children,
  } = props;

  return (
    <div>
      {!account && <Redirect to="/login" />}
      {account ? (
        <Wrapper>
          <Header />
          <MainSection>{children}</MainSection>
        </Wrapper>
      ) : null}
    </div>
  );
};

AuthorizedLayout.propTypes = {
  children: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.object),
    PropTypes.object,
  ]),
  user: User,
};

const mapStateToProps = state => ({
  user: state.user,
  settings: state.settings,
});

export default connect(mapStateToProps)(AuthorizedLayout);
