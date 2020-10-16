import React from "react";
import {useDispatch} from "react-redux";

import {Button, Container, Header} from "semantic-ui-react";
import MetaMaskOnboarding from "@metamask/onboarding";
import {loginUser} from "../redux/actions/User";

function LoginPage() {
  return (
    <Container>
      <OnboardingButton />
    </Container>
  );
}

export function OnboardingButton() {
  const [accounts, setAccounts] = React.useState([]);
  const [isVisible, setVisibility] = React.useState(false);
  const onboarding = React.useRef();
  const dispatch = useDispatch();

  React.useEffect(() => {
    if (!onboarding.current) {
      onboarding.current = new MetaMaskOnboarding();
    }
  }, []);

  React.useEffect(() => {
    if (MetaMaskOnboarding.isMetaMaskInstalled()) {
      console.log(MetaMaskOnboarding.isMetaMaskInstalled());
      if (accounts.length > 0) {
        setVisibility(false);
        onboarding.current.stopOnboarding();
      } else {
        setVisibility(true);
      }
    }
  }, [accounts]);

  React.useEffect(() => {
    function handleNewAccounts(newAccounts) {
      setAccounts(newAccounts);
      dispatch(
        loginUser({
          address: newAccounts[0],
        }),
      );
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
    <Container>
      {isVisible && (
        <Container textAlign="center" style={{marginTop: "7em"}}>
          <Header as="h1">Please connect your Metamask wallet</Header>
          <Button
            size="massive"
            color="orange"
            content="Connect"
            onClick={onClick}
          />
        </Container>
      )}
      {!isVisible && <Header as="h1">Wallet is already connected</Header>}
    </Container>
  );
}

export default LoginPage;
