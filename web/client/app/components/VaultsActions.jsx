import {Dropdown, Table} from "semantic-ui-react";
import React from "react";
import BorrowModal from "./Modals/BorrowModal";
import CloseModal from "./Modals/CloseModal";
import StakeModal from "./Modals/StakeModal";
import BuyModal from "./Modals/BuyModal";
import PayOffModal from "./Modals/PayOffModal";
import CoverShortfallModal from "./Modals/CoverShortfallModal";
import SlashModal from "./Modals/SlashModal";
import StartAuctionModal from "./Modals/StartAuctionModal";

export default class VaultActions extends React.Component {
  render() {
    const {item} = this.props;

    return (
      <Table.Cell>
        <Dropdown button text="Choose action" style={{minWidth: "152px"}}>
          <Dropdown.Menu>
            <BorrowModal item={item} />
            <CloseModal item={item} />
            <StakeModal item={item} />
            <PayOffModal item={item} />
            <BuyModal item={item} />
            <CoverShortfallModal item={item} />
            <SlashModal item={item} />
            <StartAuctionModal item={item} />
          </Dropdown.Menu>
        </Dropdown>
      </Table.Cell>
    );
  }
}
