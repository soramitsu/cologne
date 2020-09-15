/* eslint-disable react/forbid-prop-types */
import React from "react";
import {connect} from "react-redux";
import PropTypes from "prop-types";
import {Redirect} from "react-router-dom";
import User from "../redux/propTypes/User";
import Header from "../components/Header";
import {Container, Grid} from "semantic-ui-react";

const AuthorizedLayout = props => {
  const {
    user: {account},
    children,
  } = props;

  return (
    <Container>
      {!account && <Redirect to="/login" />}
      {account ? (
          <Container style={{ marginTop: '3em' }}>
              <Header />
              <Container style={{ marginTop: '7em' }}>{children}</Container>
          </Container>
      ) : null}
    </Container>
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
