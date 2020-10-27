import ethers from "ethers";
import React from "react";
import {Button, Container, Form, Message} from "semantic-ui-react";
import {connect} from "react-redux";
import {ganacheAddresses, networkMapper} from "../common/Constants";
import {
  getCologneDaoContract,
  getSigner,
  getUserTokenContract,
} from "../common/Eth";
import {tokenAbi} from "../common/Abi";

class CreateVault extends React.Component {
  state = {
    tokenAddress: "",
    vaultValue: "",
    tokenAmount: "",
    isCreating: false,
    error: false,
    loading: false,
  };

  handleChange = (e, {name, value}) => {
    this.setState({[name]: value});
  };

  handleSubmit = async () => {
    const {tokenAddress, vaultValue, tokenAmount} = this.state;
    const {chain} = this.props;

    const clgnContract = getCologneDaoContract();
    const signer = getSigner();

    const tokenContract = new ethers.Contract(tokenAddress, tokenAbi, signer);

    this.setState({
      loading: true,
    });

    // approve tx should be sent first for user token contract
    let res = await tokenContract
      .approve(
        networkMapper(chain.id).cologneDaoAddress,
        ethers.utils.parseEther(tokenAmount),
      )
      .catch((error) => {
        this.setState({
          error,
          loading: false,
        });
      });

    await res.wait(1);

    // create vault itself
    res = await clgnContract
      .createVault(
        tokenAddress,
        ethers.utils.parseEther(tokenAmount),
        ethers.utils.parseEther(vaultValue),
      )
      .catch((error) => {
        this.setState({error});
      });

    this.setState({
      loading: false,
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
      loading,
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
                style={{wordBreak: "break-all"}}
                content={
                  (error.data && error.data.message) ||
                  (error.message && error.message)
                }
              />
            )}

            {loading ? (
              <Button loading type="submit">
                Create
              </Button>
            ) : (
              <Button type="submit">Create</Button>
            )}
          </Form>
        )}
      </Container>
    );
  }
}

const mapStateToProps = (state) => ({
  chain: state.chain,
});

export default connect(mapStateToProps)(CreateVault);
