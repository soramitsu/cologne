import {Button, Modal, Dropdown, Form, Message} from "semantic-ui-react";
import React from "react";
import ethers from "ethers";
import {Formik} from "formik";
import {connect} from "react-redux";
import {clgnTokenAbi, cologneDaoContract, signer} from "../../common/Resources";

class ChallengeModal extends React.Component {
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
    const {eauToLock, price} = values;
    const {
      item: {vaultContract},
    } = this.props;

    this.setState({
      error: false,
    });

    const eauTokenAddress = await cologneDaoContract.getEauTokenAddress();

    const eauTokenContract = new ethers.Contract(
      eauTokenAddress,
      clgnTokenAbi,
      signer,
    );

    await eauTokenContract.approve(
      vaultContract.address,
      ethers.utils.parseEther(Number.parseInt(eauToLock).toString()),
    );

    /**
     * Challenge
     * Lock EAU enough to buy out all User Tokens in case of default at specified price
     * @param price - price to buy out User Tokens in attoEAU
     * @param eauToLock - EAU to lock for purchase (must be >= value of Tokens in EAU at specified price)
     */
    const res = await vaultContract
      .challenge(
        ethers.utils.parseEther(price),
        ethers.utils.parseEther(eauToLock),
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

    if (!values.eauToLock) {
      errors.eauToLock = "Required";
    }

    if (!values.price) {
      errors.price = "Required";
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
        trigger={<Dropdown.Item>Challenge</Dropdown.Item>}
      >
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
