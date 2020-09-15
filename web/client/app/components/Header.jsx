import React from "react";
import {
  Container,
  Menu,
} from 'semantic-ui-react'

import {connect} from "react-redux";

import {changeLang, LANG_ENG, LANG_RUS} from "../redux/actions/Lang";
import {Link, NavLink} from "react-router-dom";

class Header extends React.Component {

  render() {
    const {
      user: {account},
    } = this.props;

    return (
        <Menu fixed='top'>
          <Container>
            <Menu.Item as='h3' header>
              Cologne
            </Menu.Item>
            <Menu.Item
                header
                as={Link}
                to="/main"
                activeclassname="active"
            >
              Dashboard
            </Menu.Item>
            <Menu.Item
                header
                as={Link}
                to="/login"
                activeclassname="active"
            >
              Login
            </Menu.Item>
            <Menu.Menu position='right' >
              {account &&
              <Menu.Item as='a'>
                {account}
              </Menu.Item>
              }
            </Menu.Menu>
          </Container>
        </Menu>
    );
  }
}

const mapDispatchToProps = dispatch => ({
  changeLang(lang) {
    dispatch(changeLang(lang));
  },
});

const mapStateToProps = state => ({
  lang: state.lang.dict,
  user: state.user,
});

export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(Header);


let contractMedleyDao = web3.eth.contract([{"inputs":[{"internalType":"address","name":"mdlyToken","type":"address"},{"internalType":"address","name":"eauToken","type":"address"},{"internalType":"address","name":"mdlyPriceOracle","type":"address"},{"internalType":"address","name":"mdlyMarket","type":"address"},{"internalType":"address","name":"timeProvider","type":"address"}],"stateMutability":"nonpayable","type":"constructor"},{"anonymous":false,"inputs":[{"indexed":true,"internalType":"address","name":"vault","type":"address"},{"indexed":true,"internalType":"address","name":"owner","type":"address"}],"name":"VaultCreation","type":"event"},{"inputs":[{"internalType":"address","name":"token","type":"address"},{"internalType":"uint256","name":"stake","type":"uint256"},{"internalType":"uint256","name":"initialAmount","type":"uint256"},{"internalType":"uint256","name":"tokenPrice","type":"uint256"}],"name":"createVault","outputs":[{"internalType":"address","name":"","type":"address"}],"stateMutability":"nonpayable","type":"function"},{"inputs":[],"name":"getEauTokenAddress","outputs":[{"internalType":"address","name":"","type":"address"}],"stateMutability":"view","type":"function"},{"inputs":[],"name":"getMdlyMarket","outputs":[{"internalType":"contract IMarketAdaptor","name":"","type":"address"}],"stateMutability":"view","type":"function"},{"inputs":[],"name":"getMdlyPriceOracle","outputs":[{"internalType":"contract IPriceOracle","name":"","type":"address"}],"stateMutability":"view","type":"function"},{"inputs":[],"name":"getMdlyTokenAddress","outputs":[{"internalType":"address","name":"","type":"address"}],"stateMutability":"view","type":"function"},{"inputs":[],"name":"listVaults","outputs":[{"internalType":"address[]","name":"","type":"address[]"}],"stateMutability":"view","type":"function"},{"inputs":[{"internalType":"address","name":"beneficiary","type":"address"},{"internalType":"uint256","name":"amount","type":"uint256"}],"name":"mintEAU","outputs":[],"stateMutability":"nonpayable","type":"function"}])
let contract = contractMedleyDao.at("0x79eafd0b5ec8d3f945e6bb2817ed90b046c0d0af");
console.log(contract);