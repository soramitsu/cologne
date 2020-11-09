import {Dropdown, Table} from "semantic-ui-react";
import React from "react";
import StakeModal from "./Modals/StakeModal";
import WithdrawStakeModal from "./Modals/WithdrawStakeModal";

export default function StakingActions(props) {
  const {item} = props;

  return (
    <Table.Cell>
      <Dropdown
        button
        closeOnBlur
        text="Choose action"
        style={{minWidth: "152px"}}
      >
        <Dropdown.Menu>
          <StakeModal item={item} />
          <WithdrawStakeModal item={item} />
        </Dropdown.Menu>
      </Dropdown>
    </Table.Cell>
  );
}
