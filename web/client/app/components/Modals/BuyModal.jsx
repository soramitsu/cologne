import {Button, Modal, Dropdown, Form, Message} from "semantic-ui-react";
import React from "react";
import ethers from "ethers";
import {Formik} from "formik";
import {connect} from "react-redux";
import {getCologneDaoContract, getSigner} from "../../common/Eth";
import {tokenAbi} from "../../common/Abi";

class BuyModal extends React.Component {
  state = {
    open: false,
    error: false,
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
    const {tokenAmount, maxPrice} = values;
    const {
      item: {vaultContract},
      user: {account},
    } = this.props;

    this.setState({
      error: false,
    });

    const eauTokenAddress = await getCologneDaoContract().getEauTokenAddress();

    const eauTokenContract = new ethers.Contract(
      eauTokenAddress,
      tokenAbi,
      getSigner(),
    );

    await eauTokenContract.approve(
      vaultContract.address,
      ethers.utils.parseEther(
        (Number.parseFloat(maxPrice) * Number.parseInt(tokenAmount)).toString(),
      ),
    );

    const res = await vaultContract
      .buy(
        ethers.utils.parseEther(tokenAmount),
        ethers.utils.parseEther(maxPrice),
        account,
      )
      .catch((error) => this.setState({error}));

    if (res) {
      this.setState({
        open: false,
        error: false,
      });
    }
  };

  closeForm = () => {
    this.setState({
      error: false,
      open: false,
    });
  };

  formValidate = (values) => {
    const errors = {};

    if (!values.tokenAmount) {
      errors.tokenAmount = "Required";
    }

    if (!values.maxPrice) {
      errors.maxPrice = "Required";
    }

    return errors;
  };

  render() {
    const {open, error} = this.state;

    return (
      <Modal
        size="tiny"
        onClose={this.closeForm}
        onOpen={this.openForm}
        open={open}
        trigger={<Dropdown.Item>Buy</Dropdown.Item>}
      >
        <Modal.Header>Buy tokens from the contract</Modal.Header>
        <Modal.Content>
          <Formik
            innerRef={this.formRef}
            initialValues={{tokenAmount: "", maxPrice: ""}}
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
                <Form.Group widths="equal">
                  <Form.Input
                    label="Amount to buy"
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

                  <Form.Input
                    label="Maximum buy price"
                    fluid
                    error={
                      errors.maxPrice && touched.maxPrice && errors.maxPrice
                    }
                    placeholder="10000"
                    name="maxPrice"
                    value={values.maxPrice}
                    onBlur={handleBlur}
                    onChange={handleChange}
                  />
                </Form.Group>
              </Form>
            )}
          </Formik>

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
        </Modal.Content>
        <Modal.Actions>
          <Button color="black" onClick={this.closeForm}>
            Cancel
          </Button>
          <Button
            type="submit"
            content="Buy"
            color="green"
            onClick={this.formRef.current && this.formRef.current.handleSubmit}
            positive
          />
        </Modal.Actions>
      </Modal>
    );
  }
}

const mapStateToProps = (state) => ({
  user: state.user,
});

export default connect(mapStateToProps)(BuyModal);
