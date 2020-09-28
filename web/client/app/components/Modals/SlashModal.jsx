import {Button, Modal, Dropdown, Message} from "semantic-ui-react";
import React from "react";

export default class SlashModal extends React.Component {
  state = {
    open: false,
    error: false,
  };

  openForm = () => {
    this.setState({
      open: true,
    });
  };

  handleSubmit = async () => {
    const {
      item: {vaultContract},
    } = this.props;

    this.setState({
      error: false,
    });

    const res = await vaultContract
      .slash()
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
        trigger={<Dropdown.Item>Slash</Dropdown.Item>}
      >
        <Modal.Header>Slash</Modal.Header>
        <Modal.Content>
          Slash the vault: {address}?
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
