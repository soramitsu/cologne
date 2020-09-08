import React from "react";

import {connect} from "react-redux";

class MainPage extends React.Component {
  state = {
    items: [],
  };

  render() {
    return (
      <div>
        Hi!
      </div>
    );
  }
}

const mapStateToProps = state => ({
  lang: state.lang.dict,
});

export default connect(mapStateToProps)(MainPage);
