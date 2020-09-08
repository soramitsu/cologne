import React from "react";
// import {Link, NavLink} from "react-router-dom";

import {connect} from "react-redux";
import {
  HeaderWrapper,
  HeaderAuthPart,
  Menu,
  // NotificationButton,
  MoneyBlock,
  Avatar,
  // LangSwitcher,
  // LangBtn,
  // Burger,
  // AdaptiveMenu,
  // AdaptiveMenuTop,
  // MenuCloseButton,
} from "../common/styles";

// import logoImg from "../../../assets/images/logo.png";
import avatarImg from "../../../assets/images/avatar.png";
import {changeLang, LANG_ENG, LANG_RUS} from "../redux/actions/Lang";
import {beforePriceShown} from "../common/Utils";
import {NavLink} from "react-router-dom";

const avatarStyle = {backgroundImage: `url(${avatarImg})`};

class Header extends React.Component {
  // state = {
  //   showAdaptiveMenu: false,
  // };

  // toggleAdaptiveMenu = () => {
  //   const {showAdaptiveMenu} = this.state;
  //   this.setState({showAdaptiveMenu: !showAdaptiveMenu});
  // };

  render() {
    const {
      user: {account},
    } = this.props;

    // const {showAdaptiveMenu} = this.state;

    return (
      <div>
        <HeaderWrapper>
          <HeaderAuthPart>
            <Menu>
              <NavLink activeClassName="active" to="/main">
                Main dashboard
              </NavLink>
            </Menu>

            {account && <MoneyBlock>{account}</MoneyBlock>}
            {/*{account && <Avatar style={avatarStyle} />}*/}

          </HeaderAuthPart>
        </HeaderWrapper>
      </div>
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
