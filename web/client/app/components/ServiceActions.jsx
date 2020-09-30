import {Button, Container, Header} from "semantic-ui-react";
import React from "react";
import {ethers} from "ethers";
import {connect} from "react-redux";
import {
  clgnTokenAbi,
  cologneDaoContract,
  signer,
  timeProviderContract,
  userTokenContract,
} from "../common/Resources";

class ServiceActions extends React.Component {
  state = {
    shown: true,
  };

  mintUserToken = async () => {
    const {
      user: {account},
    } = this.props;

    await userTokenContract.mint(account, ethers.utils.parseEther("13.37"));
  };

  mintClgnToken = async () => {
    const {
      user: {account},
    } = this.props;

    const address = await cologneDaoContract.getClgnTokenAddress();

    const clgnTokenContract = new ethers.Contract(
      address,
      clgnTokenAbi,
      signer,
    );

    await clgnTokenContract.mint(account, ethers.utils.parseEther("13.37"));
  };

  transfer = async () => {
    const marketAddress = await cologneDaoContract.getClgnMarket();
    const clgnAddress = await cologneDaoContract.getClgnTokenAddress();
    const eauTokenAddress = await cologneDaoContract.getEauTokenAddress();

    const clgnTokenContract = new ethers.Contract(
      clgnAddress,
      clgnTokenAbi,
      signer,
    );

    await clgnTokenContract.transfer(
      marketAddress,
      ethers.utils.parseEther("12376679"),
    );

    const eauTokenContract = new ethers.Contract(
      eauTokenAddress,
      clgnTokenAbi,
      signer,
    );

    await eauTokenContract.transfer(
      marketAddress,
      ethers.utils.parseEther("12376679"),
    );
  };

  timeTravel = async (param) => {
    const time = await timeProviderContract.getTime();

    switch (param) {
      case "fd":
        await timeProviderContract.setTime(
          Number.parseInt(time.toString()) + 86400,
        );
        break;
      case "bd":
        await timeProviderContract.setTime(
          Number.parseInt(time.toString()) - 86400,
        );
        break;
      case "fh":
        await timeProviderContract.setTime(
          Number.parseInt(time.toString()) + 3600,
        );
        break;
      case "bh":
        await timeProviderContract.setTime(
          Number.parseInt(time.toString()) - 3600,
        );
        break;
      default:
        break;
    }
  };

  render() {
    const {shown} = this.state;

    return (
      <Container>
        <Button
          color="teal"
          onClick={() => {
            this.setState((prevState) => {
              this.setState({
                shown: !prevState.shown,
              });
            });
          }}
        >
          {shown ? "Hide service actions" : "Show service actions"}
        </Button>
        <Container
          style={{
            display: shown ? "block" : "none",
          }}
        >
          <Button color="teal" onClick={this.mintUserToken}>
            Mint user tokens
          </Button>

          <Button
            style={{marginTop: "1em"}}
            color="teal"
            onClick={this.transfer}
          >
            Move CLGN to market
          </Button>

          <Button
            style={{marginTop: "1em"}}
            color="teal"
            onClick={() => {
              this.timeTravel("fd");
            }}
          >
            Forward 1 Day
          </Button>

          <Button
            style={{marginTop: "1em"}}
            color="teal"
            onClick={() => {
              this.timeTravel("bd");
            }}
          >
            Back 1 Day
          </Button>

          <Button
            style={{marginTop: "1em"}}
            color="teal"
            onClick={() => {
              this.timeTravel("fh");
            }}
          >
            Forward 1 hour
          </Button>

          <Button
            style={{marginTop: "1em"}}
            color="teal"
            onClick={() => {
              this.timeTravel("bh");
            }}
          >
            Back 1 hour
          </Button>
        </Container>
      </Container>
    );
  }
}

const mapStateToProps = (state) => ({
  user: state.user,
});

export default connect(mapStateToProps)(ServiceActions);
