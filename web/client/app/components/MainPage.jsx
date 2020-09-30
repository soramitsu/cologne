import React from "react";

import {connect} from "react-redux";
import {Container, Grid, Header} from "semantic-ui-react";
import VaultsList from "./VaultsList";
import ServiceActions from "./ServiceActions";

class MainPage extends React.Component {
  render() {
    return (
      <Container>
        <Header as="h2">User dashboard</Header>
        <ServiceActions />
        <Grid>
          <Grid.Row>
            <Grid.Column>
              <VaultsList />
            </Grid.Column>
          </Grid.Row>
        </Grid>
      </Container>
    );
  }
}

const mapStateToProps = (state) => ({
  user: state.user,
});

export default connect(mapStateToProps)(MainPage);
