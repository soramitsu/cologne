import React from "react";
import {connect} from "react-redux";
import {Button, Container, Header} from "semantic-ui-react";
import MetaMaskOnboarding from "@metamask/onboarding";
import {loginUser} from "../redux/actions/User";

const LoginPage = class extends React.Component {
  render() {
    return (
      <Container>
        {typeof window.ethereum !== "undefined" && (
          <Container textAlign="center" style={{marginTop: "7em"}}>
            <Header as="h1">Please connect your Metamask wallet</Header>
            <Button
              size="massive"
              color="orange"
              content="Connect"
              onClick={async () => {
                const accounts = await ethereum.request({
                  method: "eth_requestAccounts",
                });
                if (accounts.length > 0) {
                  this.props.loginUser({
                    account: accounts[0],
                  });
                } else {
                  // TODO: proper error handling
                }
              }}
            />
          </Container>
        )}
        {typeof window.ethereum === "undefined" && (
          <Header as="h1">
            For the system to work, you will need to install{" "}
            <a href="https://metamask.io/">Metamask</a>
          </Header>
        )}
      </Container>
    );
  }
};

const ONBOARD_TEXT = "Click here to install MetaMask!";
const CONNECT_TEXT = "Connect";
const CONNECTED_TEXT = "Connected";

export function OnboardingButton() {
  const [buttonText, setButtonText] = React.useState(ONBOARD_TEXT);
  const [isDisabled, setDisabled] = React.useState(false);
  const [accounts, setAccounts] = React.useState([]);
  const onboarding = React.useRef();

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
        .request({method: "eth_requestAccounts"})
        .then(handleNewAccounts);
      window.ethereum.on("accountsChanged", handleNewAccounts);

      return () => {
        window.ethereum.off("accountsChanged", handleNewAccounts);
      };
    }
  }, []);

  const onClick = () => {
    if (MetaMaskOnboarding.isMetaMaskInstalled()) {
      window.ethereum
        .request({method: "eth_requestAccounts"})
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

const mapDispatchToProps = (dispatch) => ({
  loginUser(user) {
    dispatch(loginUser(user));
  },
});

const mapStateToProps = (state) => ({
  user: state.user,
});

export default connect(mapStateToProps, mapDispatchToProps)(LoginPage);
