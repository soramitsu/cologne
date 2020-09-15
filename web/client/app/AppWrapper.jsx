import React from "react";
import PropTypes from "prop-types";
import {Container} from "semantic-ui-react";

const AppWrapper = props => <Container>{props.children}</Container>;

AppWrapper.propTypes = {
  children: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.object),
    PropTypes.object,
  ]),
};

export default AppWrapper;
