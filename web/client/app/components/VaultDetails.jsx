import React from "react";
import {connect} from "react-redux";
import {Container, Header, Table, Tab} from "semantic-ui-react";
import {ethers} from "ethers";
import VaultsActions from "./VaultsActions";
import {stateFormatter} from "../common/Resources";
import ChallengeActions from "./ChallengeActions";

class VaultDetails extends React.Component {
  renderMainTab = () => {
    const {vault} = this.props;

    return (
      <Tab.Pane>
        <Table basic="very">
          <Table.Header>
            <Table.Row>
              <Table.HeaderCell>Token amount</Table.HeaderCell>
              <Table.HeaderCell>Price (EAU)</Table.HeaderCell>
              <Table.HeaderCell>Collateral (EAU)</Table.HeaderCell>
              <Table.HeaderCell>Total debt (EAU)</Table.HeaderCell>
              <Table.HeaderCell>Credit limit (EAU)</Table.HeaderCell>
              <Table.HeaderCell>Vault state</Table.HeaderCell>
              <Table.HeaderCell />
            </Table.Row>
          </Table.Header>

          <Table.Body>
            <Table.Row>
              <Table.Cell>
                <Header as="h5">{vault.tokenAmount}</Header>
              </Table.Cell>
              <Table.Cell>
                <Header as="h5">{vault.price}</Header>
              </Table.Cell>
              <Table.Cell>
                <Header as="h5">{vault.collateral}</Header>
              </Table.Cell>
              <Table.Cell>
                <Header as="h5">{vault.totalDebt}</Header>
              </Table.Cell>
              <Table.Cell>
                <Header as="h5">{vault.creditLimit}</Header>
              </Table.Cell>
              <Table.Cell>
                <Header as="h5">{stateFormatter(vault.vaultState)}</Header>
              </Table.Cell>
              <VaultsActions item={vault} />
            </Table.Row>
          </Table.Body>
        </Table>
      </Tab.Pane>
    );
  };

  renderChallengeTab = () => {
    const {vault} = this.props;

    console.log(vault.challengeWinner);

    return (
      <Table basic="very">
        <Table.Header>
          <Table.Row>
            <Table.HeaderCell>Challenge locked</Table.HeaderCell>
            <Table.HeaderCell>Redeemable challenge (EAU)</Table.HeaderCell>
            <Table.HeaderCell>Challenge winner</Table.HeaderCell>
            <Table.HeaderCell />
          </Table.Row>
        </Table.Header>

        <Table.Body>
          <Table.Row>
            <Table.Cell>
              <Header as="h5">{vault.challengeLocked}</Header>
            </Table.Cell>
            <Table.Cell>
              <Header as="h5">
                {ethers.utils.formatEther(vault.redeemableChallenge.eauAmount)}
              </Header>
            </Table.Cell>
            <Table.Cell>
              {vault.challengeWinner[0] ===
              "0x0000000000000000000000000000000000000000" ? (
                <Header as="h5">No bids yet</Header>
              ) : (
                <Header as="h5">
                  Highest bidder: {vault.challengeWinner[0]}
                  <br />
                  Bid: {ethers.utils.formatEther(vault.challengeWinner[1])}
                </Header>
              )}
            </Table.Cell>
            <ChallengeActions item={vault} />
          </Table.Row>
        </Table.Body>
      </Table>
    );
  };

  render() {
    const {vault} = this.props;

    const panes = [
      {
        menuItem: "General info",
        render: this.renderMainTab,
      },
    ];

    if (vault.vaultState === 2) {
      panes.push({
        menuItem: "Challenge tab",
        render: this.renderChallengeTab,
      });
    }

    return (
      <Container style={{marginTop: "1em"}}>
        <Tab panes={panes} />
      </Container>
    );
  }
}

const mapStateToProps = (state) => ({
  user: state.user,
});

export default connect(mapStateToProps)(VaultDetails);
