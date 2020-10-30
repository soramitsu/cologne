import React from "react";
import {Container, Menu} from "semantic-ui-react";

import {connect} from "react-redux";

import {NavLink} from "react-router-dom";
import {timeConverter} from "../common/Utils";
import {getTimeProviderContract} from "../common/Eth";

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
    const time = await getTimeProviderContract().getTime();
    this.setState({
      time: timeConverter(time.toString()),
    });
  };

  render() {
    const {
      user: {address},
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

          <Menu.Item>Current time: {time}</Menu.Item>
          <Menu.Menu position="right">
            {address && (
              <Menu.Item as="a">Connected address: {address}</Menu.Item>
            )}
          </Menu.Menu>
        </Container>
      </Menu>
    );
  }
}

const mapStateToProps = (state) => ({
  user: state.user,
});

export default connect(mapStateToProps)(Header);
