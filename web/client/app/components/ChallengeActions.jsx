import {Dropdown, Table} from "semantic-ui-react";
import React from "react";
import ChallengeModal from "./Modals/ChallengeModal";

export default class ChallengeActions extends React.Component {
  render() {
    const {item} = this.props;

    return (
      <Table.Cell>
        <Dropdown button text="Choose action" style={{minWidth: "152px"}}>
          <Dropdown.Menu>
            <ChallengeModal item={item} />
          </Dropdown.Menu>
        </Dropdown>
      </Table.Cell>
    );
  }
}
