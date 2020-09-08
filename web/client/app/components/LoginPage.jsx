import React from "react";
import {connect} from "react-redux";
import {
  BigTitle,
} from "../common/styles";
import {ConnectButton, LoginWrapper} from "../common/styles";
import {loginUser} from "../redux/actions/User";
import MetaMaskOnboarding from '@metamask/onboarding';

const LoginPage = class extends React.Component {
  render() {
    return (
      <div>
        {
          typeof window.ethereum !== 'undefined' &&
            <LoginWrapper>
              <BigTitle>Please connect your Metamask wallet</BigTitle>
              <ConnectButton onClick={async () => {
                const accounts = await ethereum.request({ method: 'eth_requestAccounts' });
                if (accounts.length > 0) {
                  console.log("here!");
                  this.props.loginUser({
                    account: accounts[0]
                  });
                } else {
                  //TODO: proper error handling
                }
              }}>Connect</ConnectButton>
            </LoginWrapper>
        }
        {
          typeof window.ethereum == 'undefined' &&
          <BigTitle>For the system to work, you will need to install <a href="https://metamask.io/">Metamask</a></BigTitle>
        }
      </div>
    );
  }
};

const ONBOARD_TEXT = 'Click here to install MetaMask!';
const CONNECT_TEXT = 'Connect';
const CONNECTED_TEXT = 'Connected';

export function OnboardingButton() {
  const [buttonText, setButtonText] = React.useState(ONBOARD_TEXT);
  const [isDisabled, setDisabled] = React.useState(false);
  const [accounts, setAccounts] = React.useState([]);
  const onboarding = React.useRef();

  React.useEffect(() => {
    if (!onboarding.current) {
      onboarding.current = new MetaMaskOnboarding();
    }
  }, []);

  React.useEffect(() => {
    if (MetaMaskOnboarding.isMetaMaskInstalled()) {
      if (accounts.length > 0) {
        setButtonText(CONNECTED_TEXT);
        setDisabled(true);
        onboarding.current.stopOnboarding();
      } else {
        setButtonText(CONNECT_TEXT);
        setDisabled(false);
      }
    }
  }, [accounts]);

  React.useEffect(() => {
    function handleNewAccounts(newAccounts) {
      setAccounts(newAccounts);
    }
    if (MetaMaskOnboarding.isMetaMaskInstalled()) {
      window.ethereum
          .request({ method: 'eth_requestAccounts' })
          .then(handleNewAccounts);
      window.ethereum.on('accountsChanged', handleNewAccounts);
      return () => {
        window.ethereum.off('accountsChanged', handleNewAccounts);
      };
    }
  }, []);

  const onClick = () => {
    if (MetaMaskOnboarding.isMetaMaskInstalled()) {
      window.ethereum
          .request({ method: 'eth_requestAccounts' })
          .then((newAccounts) => setAccounts(newAccounts));
    } else {
      onboarding.current.startOnboarding();
    }
  };
  return (
      <button disabled={isDisabled} onClick={onClick}>
        {buttonText}
      </button>
  );
}

const mapDispatchToProps = dispatch => ({
  loginUser(user) {
    dispatch(loginUser(user));
  },
});

const mapStateToProps = state => ({
  user: state.user,
});

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(LoginPage);
