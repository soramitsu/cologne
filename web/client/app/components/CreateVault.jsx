import ethers from "ethers";
import React from "react";
import {Button, Container, Form, Message} from "semantic-ui-react";
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
    isCreating: false,
    error: false,
  };

  handleChange = (e, {name, value}) => {
    this.setState({[name]: value});
  };

  handleSubmit = async () => {
    const {tokenAddress, vaultValue, tokenAmount} = this.state;

    this.setState({
      error: false,
    });

    // approve tx should be sent first for user token contract
    await userTokenContract.approve(
      cologneDaoAddress,
      ethers.utils.parseEther(tokenAmount),
    );

    // create vault itself
    const res = await cologneDaoContract
      .createVault(
        tokenAddress,
        ethers.utils.parseEther(tokenAmount),
        ethers.utils.parseEther(vaultValue),
      )
      .catch((error) => {
        this.setState({error});
      });

    if (res) {
      this.setState({
        error: false,
      });
    }
  };

  render() {
    const {
      isCreating,
      vaultValue,
      tokenAddress,
      tokenAmount,
      error,
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
          <Form style={{marginTop: "1em"}} onSubmit={this.handleSubmit} error>
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

            {error && (
              <Message
                error
                header="Something went wrong"
                content={
                  (error.data && error.data.message) ||
                  (error.message && error.message)
                }
              />
            )}

            <Button type="submit">Create</Button>
          </Form>
        )}
      </Container>
    );
  }
}
