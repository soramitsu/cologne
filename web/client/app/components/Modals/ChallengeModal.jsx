import {Button, Modal, Dropdown, Form, Message, Dimmer, Loader} from "semantic-ui-react";
import React from "react";
import ethers from "ethers";
import {Formik} from "formik";
import {connect} from "react-redux";
import {getCologneDaoContract, getSigner} from "../../common/Eth";
import {tokenAbi} from "../../common/Abi";

class ChallengeModal extends React.Component {
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
    const {eauToLock, price} = values;
    const {
      item: {vaultContract},
    } = this.props;

    this.setState({
      error: false,
    });

    const clgnContract = getCologneDaoContract();
    const eauTokenAddress = await clgnContract.getEauTokenAddress();
    const signer = getSigner();

    const eauTokenContract = new ethers.Contract(
      eauTokenAddress,
      tokenAbi,
      signer,
    );

    this.setState({
      loading: true
    });

    let res = await eauTokenContract.approve(
      vaultContract.address,
      ethers.utils.parseEther(eauToLock),
    );

    await res.wait(1);

    this.setState({
      loading: false
    });

    /**
     * Challenge
     * Lock EAU enough to buy out all User Tokens in case of default at specified price
     * @param price - price to buy out User Tokens in attoEAU
     * @param eauToLock - EAU to lock for purchase (must be >= value of Tokens in EAU at specified price)
     */
    res = await vaultContract
      .challenge(
        ethers.utils.parseEther(price),
        // price * await vaultContract.getTokenAmount()
        ethers.utils.parseEther(eauToLock),
      )
      .catch((error) => this.setState({error}));

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
    });
  };

  formValidate = (values) => {
    const errors = {};

    if (!values.eauToLock) {
      errors.eauToLock = "Required";
    }

    if (!values.price) {
      errors.price = "Required";
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
        trigger={<Dropdown.Item>Challenge</Dropdown.Item>}
      >
        {
          loading ? <Dimmer active inverted>
            <Loader>Approving spending...</Loader>
          </Dimmer> : ""
        }

        <Modal.Header>Challenge the contract</Modal.Header>
        <Modal.Content>
          <Formik
            innerRef={this.formRef}
            initialValues={{eauToLock: "", price: ""}}
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
                    label="EAU to lock"
                    fluid
                    error={
                      errors.eauToLock && touched.eauToLock && errors.eauToLock
                    }
                    placeholder="100"
                    name="eauToLock"
                    value={values.eauToLock}
                    onBlur={handleBlur}
                    onChange={handleChange}
                  />

                  <Form.Input
                    label="Price"
                    fluid
                    error={errors.price && touched.price && errors.price}
                    placeholder="10000"
                    name="price"
                    value={values.price}
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

export default connect(mapStateToProps)(ChallengeModal);
