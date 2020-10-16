import React from "react";
import {connect} from "react-redux";
import {Container, Grid, Header, Transition, Item} from "semantic-ui-react";
import ethers from "ethers";
import CreateVault from "./CreateVault";
import VaultDetails from "./VaultDetails";
import {vaultAbi} from "../common/Abi";
import {getCologneDaoContract, getSigner} from "../common/Eth";

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
    const signer = getSigner();
    const contract = getCologneDaoContract();
    const vaults = await contract.listVaults();

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

        const redeemableChallenge = await vaultContract.getRedeemableChallenge(
          address,
        );

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
                <Item.Header
                  as="a"
                  style={{color: item.vaultState === 6 ? "gray" : ""}}
                >
                  {item.address} {item.vaultState === 6 ? "(closed)" : ""}
                </Item.Header>
                <Item.Meta>
                  Owned by {item.isOwner ? "you" : item.ownerAddress}, contains{" "}
                  {item.tokenAmount} tokens
                </Item.Meta>
                {item.vaultState !== 6 ? (
                  <Item.Description
                    onClick={() => this.toggleExpand(item.address)}
                  >
                    {expanded[item.address] ? "Hide details" : "Expand details"}
                  </Item.Description>
                ) : (
                  ""
                )}
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
