import {Button, Modal, Dropdown, Form, Message, Dimmer, Loader} from "semantic-ui-react";
import React from "react";
import ethers from "ethers";
import {Formik} from "formik";
import {getCologneDaoContract, getSigner} from "../../common/Eth";
import {tokenAbi} from "../../common/Abi";

export default class StakeModal extends React.Component {
  state = {
    open: false,
    error: false,
    loading: false,
  };

  constructor() {
    super();
    this.formRef = React.createRef();
  }

  openForm = () => {
    this.setState({
      open: true,
    });
  };

  handleSubmit = async (values) => {
    const {tokenAmount} = values;
    const {
      item: {vaultContract},
    } = this.props;

    this.setState({
      error: false,
    });

    const clgnContract = getCologneDaoContract();
    const signer = getSigner();

    const clgnTokenAddress = await clgnContract.getClgnTokenAddress();

    const clgnTokenContract = new ethers.Contract(
      clgnTokenAddress,
      tokenAbi,
      signer
    );

    let res = await clgnTokenContract.approve(
      vaultContract.address,
      ethers.utils.parseEther(tokenAmount),
    );

      this.setState({
          loading: true
      });

    await res.wait(1);

      this.setState({
          loading: false
      });

    res = await vaultContract
      .stake(ethers.utils.parseEther(tokenAmount))
      .catch((error) => this.setState({error, loading: false}));

    if (res) {
      this.setState({
        open: false,
        error: false,
        loading: false
      });
    }
  };

  closeForm = () => {
    this.setState({
      error: false,
      open: false,
      loading: false
    });
  };

  formValidate = (values) => {
    const errors = {};

    if (!values.tokenAmount) {
      errors.tokenAmount = "Required";
    }

    return errors;
  };

  render() {
    const {open, error, loading} = this.state;

    return (
      <Modal
        size="tiny"
        onClose={this.closeForm}
        onOpen={this.openForm}
        open={open}
        trigger={<Dropdown.Item>Stake</Dropdown.Item>}
      >
        <Modal.Header>Stake CLGN</Modal.Header>
        <Modal.Content>
            {
                loading ? <Dimmer active inverted>
                    <Loader>Approving spending...</Loader>
                </Dimmer> : ""
            }

          <Formik
            innerRef={this.formRef}
            initialValues={{tokenAmount: ""}}
            validate={this.formValidate}
            onSubmit={this.handleSubmit}
          >
            {({
              values,
              errors,
              touched,
              handleChange,
              handleBlur,
              handleSubmit,
            }) => (
              <Form onSubmit={handleSubmit}>
                <Form.Input
                  label="Amount to stake"
                  fluid
                  error={
                    errors.tokenAmount &&
                    touched.tokenAmount &&
                    errors.tokenAmount
                  }
                  placeholder="100"
                  name="tokenAmount"
                  value={values.tokenAmount}
                  onBlur={handleBlur}
                  onChange={handleChange}
                />
              </Form>
            )}
          </Formik>

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
        </Modal.Content>
        <Modal.Actions>
          <Button color="black" onClick={this.closeForm}>
            Cancel
          </Button>
          <Button
            type="submit"
            content="Stake"
            color="green"
            onClick={this.formRef.current && this.formRef.current.handleSubmit}
            positive
          />
        </Modal.Actions>
      </Modal>
    );
  }
}
