import PropTypes from "prop-types";

export default PropTypes.shape({
  address: PropTypes.oneOfType([PropTypes.bool, PropTypes.string]),
});
