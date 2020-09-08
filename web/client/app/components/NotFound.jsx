import React from "react";
import {Link} from "react-router-dom";
import styled from "react-emotion";

const Wrapper = styled("div")`
  text-align: center;
`;

const NotFoundComponent = () => (
  <Wrapper>
    <h1>Sorry, page not found</h1>
    <Link to="/">Go to main page</Link>
  </Wrapper>
);

export default NotFoundComponent;
