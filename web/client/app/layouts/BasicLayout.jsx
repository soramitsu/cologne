import React from "react";
import {Container} from "semantic-ui-react";
import Header from "../components/Header";

const BasicLayout = (props) => (
  <Container style={{marginTop: "3em"}}>
    <Header />
    <Container style={{marginTop: "7em"}}>{props.children}</Container>
  </Container>
);

export default BasicLayout;
