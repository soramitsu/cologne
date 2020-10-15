import {Button, Container} from "semantic-ui-react";
import React from "react";
import {ethers} from "ethers";
import {connect} from "react-redux";
import {getTimeProviderContract, getUserTokenContract} from "../common/Eth";

class ServiceActions extends React.Component {
  state = {
    shown: false,
  };

  mintUserToken = async () => {
    const {
      user: {address},
    } = this.props;

    await getUserTokenContract().mint(
      address,
      ethers.utils.parseEther("13.37"),
    );
  };

  timeTravel = async (param) => {
    const timeContract = getTimeProviderContract();
    const time = await timeContract.getTime();

    switch (param) {
      case "fd":
        await timeContract.setTime(Number.parseInt(time.toString()) + 86400);
        break;
      case "f3d":
        await timeContract.setTime(
          Number.parseInt(time.toString()) + 86400 * 3,
        );
        break;
      case "fh":
        await timeContract.setTime(Number.parseInt(time.toString()) + 1800);
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
            this.setState((prevState) => ({
              shown: !prevState.shown,
            }));
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
            onClick={() => {
              this.timeTravel("fh");
            }}
          >
            Forward 30 minutes
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
              this.timeTravel("f3d");
            }}
          >
            Forward 3 Days
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
