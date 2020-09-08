import React from "react";
import styled from "react-emotion";
import Header from "../components/Header";

const Wrapper = styled("div")`
  width: 1024px;
  margin: 0 auto;
  @media (max-width: 480px) {
    width: 320px;
  }
  @media (min-width: 481px) and (max-width: 768px) {
    width: 480px;
  }
  @media (min-width: 769px) and (max-width: 1023px) {
    width: 768px;
  }
`;

const AuthSection = styled("section")`
  width: 100%;
  height: 100%;
  position: relative;
`;

const BasicLayout = props => (
  <Wrapper>
    <Header />
    <AuthSection>{props.children}</AuthSection>
  </Wrapper>
);

export default BasicLayout;
