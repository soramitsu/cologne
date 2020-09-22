import React from "react";

import {connect} from "react-redux";
import {Button, Container, Grid, Header} from "semantic-ui-react";
import {ethers} from "ethers";
import {
  clgnTokenAbi,
  cologneDaoContract,
  signer,
  userTokenContract,
} from "../common/Resources";
import VaultsList from "./VaultsList";

class MainPage extends React.Component {
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

    const address = await cologneDaoContract.getMdlyTokenAddress();

    const clgnTokenContract = new ethers.Contract(
      address,
      clgnTokenAbi,
      signer,
    );

    await clgnTokenContract.mint(account, ethers.utils.parseEther("13.37"));
  };

  render() {
    return (
      <Container>
        <Header as="h2">User dashboard</Header>
        <Grid>
          <Grid.Row stackable>
            <Grid.Column width={14}>
              <VaultsList />
            </Grid.Column>

            <Grid.Column width={2}>
              <Header as="h4">Service actions</Header>
              <Button color="teal" onClick={this.mintUserToken}>
                Mint user tokens
              </Button>

              <Button
                style={{marginTop: "1em"}}
                color="teal"
                onClick={this.mintClgnToken}
              >
                Mint CLGN
              </Button>
            </Grid.Column>
          </Grid.Row>
        </Grid>
      </Container>
    );
  }
}

const mapStateToProps = (state) => ({
  user: state.user,
});

export default connect(mapStateToProps)(MainPage);
