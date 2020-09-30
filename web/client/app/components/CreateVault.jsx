import ethers from "ethers";
import React from "react";
import {Button, Container, Form} from "semantic-ui-react";
import {
  cologneDaoAddress,
  cologneDaoContract,
  userTokenContract,
} from "../common/Resources";

export default class CreateVault extends React.Component {
  state = {
    tokenAddress: "",
    vaultValue: "",
    tokenAmount: "",
    stakingAmount: "",
    isCreating: false,
    isStaking: false,
  };

  handleChange = (e, {name, value}) => {
    this.setState({[name]: value});
  };

  handleSubmit = async () => {
    const {
      tokenAddress,
      vaultValue,
      tokenAmount,
    } = this.state;

    // approve tx should be sent first for user token contract
    await userTokenContract.approve(
      cologneDaoAddress,
      ethers.utils.parseEther(tokenAmount),
    );

    // create vault itself
    await cologneDaoContract.createVault(
      tokenAddress,
      ethers.utils.parseEther(tokenAmount),
      ethers.utils.parseEther(vaultValue),
    );
  };

  render() {
    const {
      isCreating,
      vaultValue,
      tokenAddress,
      tokenAmount,
      isStaking,
      stakingAmount,
    } = this.state;

    return (
      <Container style={{marginTop: "1em"}}>
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
      </Container>
    );
  }
}
