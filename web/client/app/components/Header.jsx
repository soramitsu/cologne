import React from "react";
import {Container, Menu} from "semantic-ui-react";

import {connect} from "react-redux";

import {Link, NavLink} from "react-router-dom";
import {changeLang, LANG_ENG, LANG_RUS} from "../redux/actions/Lang";

class Header extends React.Component {
  render() {
    const {
      user: {account},
    } = this.props;

    return (
      <Menu fixed="top">
        <Container>
          <Menu.Item as="h3" header>
            Cologne
          </Menu.Item>
          <Menu.Item header as={Link} to="/main" activeclassname="active">
            Dashboard
          </Menu.Item>
          <Menu.Item header as={Link} to="/login" activeclassname="active">
            Login
          </Menu.Item>
          <Menu.Menu position="right">
            {account && (
              <Menu.Item as="a">Connected address: {account}</Menu.Item>
            )}
          </Menu.Menu>
        </Container>
      </Menu>
    );
  }
}

const mapDispatchToProps = (dispatch) => ({
  changeLang(lang) {
    dispatch(changeLang(lang));
  },
});

const mapStateToProps = (state) => ({
  lang: state.lang.dict,
  user: state.user,
});

export default connect(mapStateToProps, mapDispatchToProps)(Header);
