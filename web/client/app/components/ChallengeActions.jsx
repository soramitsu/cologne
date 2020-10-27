import {Dropdown, Table} from "semantic-ui-react";
import React from "react";
import ChallengeModal from "./Modals/ChallengeModal";

export default function ChallengeActions(props) {
  const {item} = props;

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
