import {Dropdown, Table} from "semantic-ui-react";
import React from "react";
import BorrowModal from "./Modals/BorrowModal";
import CloseModal from "./Modals/CloseModal";

export default class VaultActions extends React.Component {
  render() {
    const {item} = this.props;

    return (
      <Table.Cell>
        <Dropdown button text="Choose action">
          <Dropdown.Menu>
            <BorrowModal item={item} />
            <CloseModal item={item} />

            <Dropdown.Item>Stake</Dropdown.Item>

            <Dropdown.Item>Buy</Dropdown.Item>
            <Dropdown.Item>Cover shortfall</Dropdown.Item>
            <Dropdown.Item>Slash</Dropdown.Item>
            <Dropdown.Item>startInitialLiquidityAuction</Dropdown.Item>
          </Dropdown.Menu>
        </Dropdown>
      </Table.Cell>
    );
  }
}
