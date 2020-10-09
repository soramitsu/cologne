/* eslint-disable react/forbid-prop-types */
import React from "react";
import {connect} from "react-redux";
import PropTypes from "prop-types";
import {Redirect} from "react-router-dom";
import {Container} from "semantic-ui-react";
import User from "../redux/propTypes/User";
import Header from "../components/Header";

const AuthorizedLayout = (props) => {
  const {
    user: {address},
    children,
  } = props;

  return (
    <Container>
      {!address && <Redirect to="/login" />}
      {address ? (
        <Container style={{marginTop: "3em"}}>
          <Header />
          <Container style={{marginTop: "7em"}}>{children}</Container>
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

const mapStateToProps = (state) => ({
  user: state.user,
  chain: state.chain,
});

export default connect(mapStateToProps)(AuthorizedLayout);
