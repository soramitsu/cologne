import {Button, Modal, Dropdown, Message} from "semantic-ui-react";
import React from "react";
import {connect} from "react-redux";

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
    const {
      item: {vaultContract},
    } = this.props;

    this.setState({
      error: false,
    });

    await vaultContract
      .redeemChallenge()
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
    const {
      item: {address},
    } = this.props;

    return (
      <Modal
        size="tiny"
        onClose={this.closeForm}
        onOpen={this.openForm}
        open={open}
        trigger={<Dropdown.Item>Redeem challenge</Dropdown.Item>}
      >
        <Modal.Header>Redeem challenge</Modal.Header>
        <Modal.Content>
          Redeem challenge from the vault: {address}?
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
            No
          </Button>
          <Button
            type="submit"
            content="Yes"
            color="green"
            onClick={this.handleSubmit}
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
