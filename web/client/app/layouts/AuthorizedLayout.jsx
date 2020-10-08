/* eslint-disable react/forbid-prop-types */
import React from "react";
import {connect} from "react-redux";
import PropTypes from "prop-types";
import {Redirect} from "react-router-dom";
import {Container} from "semantic-ui-react";
import User from "../redux/propTypes/User";
import Header from "../components/Header";

//TODO: styles should be properly recompiled

const AuthorizedLayout = (props) => {
  const {
    user: {address},
    children,
  } = props;

  return (
    <Container>
      {/*<style>*/}
      {/*  {`*/}
      {/*    html, body {*/}
      {/*      background-color: #1b1c1d !important;*/}
      {/*      color: #fff !important;*/}
      {/*    }*/}
      {/*    .top.fixed.menu {*/}
      {/*      background-color: #1b1c1d !important;*/}
      {/*      color: #fff !important;*/}
      {/*    }*/}
      {/*    .ui.menu .item {*/}
      {/*      color: #fff !important;*/}
      {/*    }*/}
      {/*    .ui.table thead th{*/}
      {/*      color: #fff;*/}
      {/*    }*/}
      {/*    .ui.header {*/}
      {/*      color: #fff !important;*/}
      {/*    }*/}
      {/*    .ui.items>.item>.content>a.header{*/}
      {/*        color: #fff;*/}
      {/*    }*/}
      {/*    .ui.items>.item .meta {*/}
      {/*        color: #fff;*/}
      {/*    }          */}
      {/*    .ui.items>.item>.content>.description {*/}
      {/*        color: #fff;*/}
      {/*    }*/}
      {/*`}*/}
      {/*</style>*/}
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
  settings: state.settings,
});

export default connect(mapStateToProps)(AuthorizedLayout);
