import React from "react";
import {connect} from "react-redux";
import {Button, Container, Form, Header, Table} from "semantic-ui-react";
import {
  cologneDaoContract,
  userTokenContract,
  cologneDaoAddress,
  multiplier,
  clgnTokenAbi,
} from "../common/Resources";

class VaultsList extends React.Component {
  state = {
    items: [],
    tokenAddress: "",
    vaultValue: "",
    tokenAmount: "",
    stakingAmount: "",
    isCreating: false,
    isStaking: false,
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
    setTimeout(() => {
      self.poll();
      self.timer = setInterval(self.poll.bind(self), 1000);
    }, 1000);
  }

  poll() {
    cologneDaoContract.listVaults.call((error, result) => {
      if (!error) {
        // console.log(result);
        this.setState({
          items: result,
        });
      } else {
        console.log(error.code);
      }
    });
  }

  handleChange = (e, {name, value}) => {
    this.setState({[name]: value});
  };

  handleStakingChange = (e) => {
    this.setState((prevState) => ({
      isStaking: !prevState.isStaking,
    }));
  };

  handleSubmit = () => {
    const {
      tokenAddress,
      vaultValue,
      isStaking,
      stakingAmount,
      tokenAmount,
    } = this.state;

    // approve tx should be sent first for user token contract
    userTokenContract.approve.sendTransaction(
      cologneDaoAddress,
      tokenAmount * multiplier,
      (error, result) => {
        if (!error) {
          console.log(result);
        } else {
          console.log(error.code);
        }
      },
    );

    const toStake = isStaking ? stakingAmount * multiplier : 0;

    // approve tx should be sent first for clgn token contract
    if (isStaking) {
      cologneDaoContract.getMdlyTokenAddress.call((error, result) => {
        if (!error) {
          const clgnTokenAddress = result;
          const clgnTokenContract = clgnTokenAbi.at(clgnTokenAddress);

          clgnTokenContract.approve.sendTransaction(
            cologneDaoAddress,
            toStake * multiplier,
            (error, result) => {
              if (!error) {
                console.log(result);
              } else {
                console.log(error.code);
              }
            },
          );
        } else {
          console.log(error.code);
        }
      });
    }

    cologneDaoContract.createVault.sendTransaction(
      tokenAddress,
      toStake,
      tokenAmount * multiplier,
      vaultValue * multiplier,
      (error, result) => {
        if (!error) {
          console.log(result);
        } else {
          console.log(error.code);
        }
      },
    );
  };

  render() {
    const {
      isCreating,
      vaultValue,
      tokenAddress,
      tokenAmount,
      items,
      isStaking,
      stakingAmount,
    } = this.state;

    return (
      <Container>
        <Header as="h4">Deployed Vaults</Header>
        <Button
          color="green"
          onClick={() => {
            this.setState((prevState) => ({
              isCreating: !prevState.isCreating,
            }));
          }}
        >
          {!isCreating && "Create new Vault"}
          {isCreating && "Hide form"}
        </Button>

        {isCreating && (
          <Form style={{marginTop: "1em"}} onSubmit={this.handleSubmit}>
            <Form.Field>
              <label>Token address</label>
              <Form.Input
                placeholder="0x7d73424a8256c0b2ba245e5d5a3de8820e45f390"
                name="tokenAddress"
                value={tokenAddress}
                onChange={this.handleChange}
              />
            </Form.Field>

            <Form.Field>
              <label>Token amount</label>
              <Form.Input
                placeholder="100"
                name="tokenAmount"
                value={tokenAmount}
                onChange={this.handleChange}
              />
            </Form.Field>

            <Form.Field>
              <label>Vault value (in EAU)</label>
              <Form.Input
                placeholder="10"
                name="vaultValue"
                value={vaultValue}
                onChange={this.handleChange}
              />
            </Form.Field>

            <Form.Checkbox
              label="Staking enabled"
              name="isStaking"
              checked={isStaking}
              onChange={this.handleStakingChange}
            />

            {isStaking && (
              <Form.Field>
                <label>Staking amount (in CLGN)</label>
                <Form.Input
                  placeholder="10"
                  name="stakingAmount"
                  value={stakingAmount}
                  onChange={this.handleChange}
                />
              </Form.Field>
            )}

            <Button type="submit">Create</Button>
          </Form>
        )}

        <Table basic="very" celled collapsing>
          <Table.Header style={{marginTop: "1em"}}>
            <Table.Row>
              <Table.HeaderCell>Address</Table.HeaderCell>
              {/* <Table.HeaderCell>Balance</Table.HeaderCell> */}
            </Table.Row>
          </Table.Header>

          <Table.Body>
            {items.map((item) => (
              <Table.Row key={item}>
                <Table.Cell>
                  <Header as="h4">{item}</Header>
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
