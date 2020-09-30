import PropTypes from "prop-types";

export default PropTypes.shape({
  account: PropTypes.oneOfType([PropTypes.bool, PropTypes.string]),
});
