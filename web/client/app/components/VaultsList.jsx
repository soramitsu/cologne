import React from "react";
import {connect} from "react-redux";
import {Container, Grid, Header, Transition, Item} from "semantic-ui-react";
import ethers from "ethers";
import {cologneDaoContract, vaultAbi, signer} from "../common/Resources";
import CreateVault from "./CreateVault";
import VaultDetails from "./VaultDetails";

class VaultsList extends React.Component {
  state = {
    items: [],
    expanded: {},
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

  poll = async () => {
    const vaults = await cologneDaoContract.listVaults();
    const {
      user: {address},
    } = this.props;

    const enrichedVaults = await Promise.all(
      vaults.map(async (vault) => {
        const vaultContract = new ethers.Contract(vault, vaultAbi, signer);

        const price = ethers.utils.formatEther(await vaultContract.getPrice());

        const collateral = ethers.utils.formatEther(
          await vaultContract.getCollateralInEau(),
        );

        const totalDebt = ethers.utils.formatEther(
          await vaultContract.getTotalDebt(),
        );

        const tokenAmount = ethers.utils.formatEther(
          await vaultContract.getTokenAmount(),
        );

        const vaultState = await vaultContract.getState();

        const creditLimit = ethers.utils.formatEther(
          await vaultContract.canBorrow(),
        );

        const isOwner = await vaultContract.isOwner();

        const ownerAddress = await vaultContract.owner();

        const challengeLocked = ethers.utils.formatEther(
          await vaultContract.getChallengeLocked(address),
        );

        const redeemableChallenge = await vaultContract.getRedeemableChallenge(address);

        const challengeWinner = await vaultContract.getChallengeWinner();

        return {
          vaultContract,
          isOwner,
          address: vault,
          price,
          collateral,
          totalDebt,
          tokenAmount,
          creditLimit,
          vaultState,
          ownerAddress,
          challengeLocked,
          redeemableChallenge,
          challengeWinner,
        };
      }),
    );

    this.setState({
      items: enrichedVaults,
    });
  };

  toggleExpand = (address) => {
    this.setState((prevState) => ({
      expanded: {
        ...prevState.expanded,
        [address]: !prevState.expanded[address],
      },
    }));
  };

  startPolling() {
    const self = this;
    setTimeout(async () => {
      await self.poll();
      self.timer = setInterval(self.poll, 1000);
    }, 1000);
  }

  render() {
    const {items, expanded} = this.state;

    return (
      <Container>
        <Grid.Row>
          <Grid.Column>
            <CreateVault />
          </Grid.Column>
        </Grid.Row>

        <Header as="h3">Deployed Vaults</Header>

        <Transition.Group as={Item.Group} duration={200} divided size="huge">
          {items.map((item) => (
            <Item key={item.address}>
              <Item.Content>
                <Item.Header as="a">{item.address}</Item.Header>
                <Item.Meta>
                  Owned by {item.isOwner ? "you" : item.ownerAddress}, contains{" "}
                  {item.tokenAmount} tokens
                </Item.Meta>
                <Item.Description
                  onClick={() => this.toggleExpand(item.address)}
                >
                  {expanded[item.address] ? "Hide details" : "Expand details"}
                </Item.Description>
                {expanded[item.address] && <VaultDetails vault={item} />}
              </Item.Content>
            </Item>
          ))}
        </Transition.Group>
      </Container>
    );
  }
}

const mapStateToProps = (state) => ({
  user: state.user,
});

export default connect(mapStateToProps)(VaultsList);
