import {Button, Modal, Dropdown, Form, Message} from "semantic-ui-react";
import React from "react";
import ethers from "ethers";
import {Formik} from "formik";

export default class CloseModal extends React.Component {
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
    const {tokenAmount} = values;
    const {
      item: {vaultContract},
    } = this.props;

    this.setState({
      error: false,
    });

    const res = await vaultContract
      .borrow(ethers.utils.parseEther(tokenAmount))
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
    const {
      item: {creditLimit},
    } = this.props;

    if (!values.tokenAmount) {
      errors.tokenAmount = "Required";
    } else if (
      Number.parseFloat(creditLimit) < Number.parseFloat(values.tokenAmount)
    ) {
      errors.tokenAmount = "You can not exceed credit limit";
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
        trigger={<Dropdown.Item>Borrow</Dropdown.Item>}
      >
        <Modal.Header>Borrow from the vault</Modal.Header>
        <Modal.Content>
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
                  label="Amount to borrow"
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
            content="Borrow"
            color="green"
            onClick={this.formRef.current && this.formRef.current.handleSubmit}
            positive
          />
        </Modal.Actions>
      </Modal>
    );
  }
}
