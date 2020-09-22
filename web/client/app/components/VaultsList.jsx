import React from "react";
import {connect} from "react-redux";
import {Container, Header, Table} from "semantic-ui-react";
import ethers from "ethers";
import {cologneDaoContract, vaultAbi, signer} from "../common/Resources";
import CreateVault from "./CreateVault";

class VaultsList extends React.Component {
  state = {
    items: [],
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

  startPolling() {
    const self = this;
    setTimeout(async () => {
      await self.poll();
      self.timer = setInterval(self.poll, 1000);
    }, 1000);
  }

  poll = async () => {
    const vaults = await cologneDaoContract.listVaults();

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

        const creditLimit = ethers.utils.formatEther(
          await vaultContract.getCreditLimit(),
        );

        return {
          address: vault,
          price,
          collateral,
          totalDebt,
          tokenAmount,
          creditLimit,
        };
      }),
    );

    this.setState({
      items: enrichedVaults,
    });
  };

  render() {
    const {items} = this.state;

    return (
      <Container>
        <CreateVault />

        <Header as="h3">Deployed Vaults</Header>

        <Table basic="very" celled collapsing>
          <Table.Header style={{marginTop: "1em"}}>
            <Table.Row>
              <Table.HeaderCell>Address</Table.HeaderCell>
              <Table.HeaderCell>Price in EAU</Table.HeaderCell>
              <Table.HeaderCell>Collateral in EAU</Table.HeaderCell>
              <Table.HeaderCell>Total debt</Table.HeaderCell>
              <Table.HeaderCell>Total amount</Table.HeaderCell>
              <Table.HeaderCell>Credit limit</Table.HeaderCell>
            </Table.Row>
          </Table.Header>

          <Table.Body>
            {items.map((item) => (
              <Table.Row key={item.address}>
                <Table.Cell>
                  <Header as="h4">{item.address}</Header>
                </Table.Cell>
                <Table.Cell>
                  <Header as="h4">{item.price}</Header>
                </Table.Cell>
                <Table.Cell>
                  <Header as="h4">{item.collateral}</Header>
                </Table.Cell>
                <Table.Cell>
                  <Header as="h4">{item.totalDebt}</Header>
                </Table.Cell>
                <Table.Cell>
                  <Header as="h4">{item.tokenAmount}</Header>
                </Table.Cell>
                <Table.Cell>
                  <Header as="h4">{item.creditLimit}</Header>
                </Table.Cell>
              </Table.Row>
            ))}
          </Table.Body>
        </Table>
      </Container>
    );
  }
}

const mapStateToProps = (state) => ({
  lang: state.lang.dict,
  user: state.user,
});

export default connect(mapStateToProps)(VaultsList);
