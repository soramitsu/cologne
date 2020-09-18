import React from "react";
import PropTypes from "prop-types";
import {Container} from "semantic-ui-react";

const AppWrapper = (props) => {
  const {children} = props;

  return <Container>{children}</Container>;
};

AppWrapper.propTypes = {
  children: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.object),
    PropTypes.object,
  ]),
};

export default AppWrapper;
