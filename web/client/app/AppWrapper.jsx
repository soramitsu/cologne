import React from "react";
import PropTypes from "prop-types";
import styled from "react-emotion";

const Wrapper = styled("div")`
  position: relative;
  width: 100%;
  height: auto;
  min-height: 100%;
  overflow-x: hidden;
`;

const AppWrapper = props => <Wrapper>{props.children}</Wrapper>;

AppWrapper.propTypes = {
  children: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.object),
    PropTypes.object,
  ]),
};

export default AppWrapper;
