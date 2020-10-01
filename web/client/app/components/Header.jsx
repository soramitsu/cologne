import React from "react";
import {Container, Menu} from "semantic-ui-react";

import {connect} from "react-redux";

import {NavLink} from "react-router-dom";
import {changeLang} from "../redux/actions/Lang";
import {timeProviderContract} from "../common/Resources";
import {timeConverter} from "../common/Utils";

class Header extends React.Component {
  state = {
    time: "",
  };

  componentDidMount() {
    this.startPolling();
  }

  componentWillUnmount() {
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
  }

  startPolling() {
    const self = this;
    setTimeout(async () => {
      await self.poll();
      self.timer = setInterval(self.poll, 1000);
    }, 1000);
  }

  poll = async () => {
    const time = await timeProviderContract.getTime();
    this.setState({
      time: timeConverter(time.toString()),
    });
  };

  render() {
    const {
      user: {account},
    } = this.props;

    const {time} = this.state;

    return (
      <Menu fixed="top">
        <Container>
          <Menu.Item as="h3" header>
            Cologne
          </Menu.Item>
          <Menu.Item header as={NavLink} to="/main" activeclassname="active">
            Dashboard
          </Menu.Item>
          <Menu.Item header as={NavLink} to="/login" activeclassname="active">
            Login
          </Menu.Item>
          <Menu.Item>Current time: {time}</Menu.Item>
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
