import React from "react";

import {connect} from "react-redux";
import {Button, Container, Grid, Header} from "semantic-ui-react";
import {
  clgnTokenAbi,
  cologneDaoContract,
  userTokenContract,
} from "../common/Resources";
import VaultsList from "./VaultsList";

class MainPage extends React.Component {
  render() {
    return (
      <Container>
        <Header as="h2">User dashboard</Header>
        <Grid>
          <Grid.Row columns={4}>
            <Grid.Column>
              <VaultsList />
            </Grid.Column>

            <Grid.Column />

            <Grid.Column />

            <Grid.Column>
              <Header as="h3">Service actions</Header>
              <Button
                color="teal"
                onClick={() => {
                  userTokenContract.mint.sendTransaction(
                    this.props.user.account,
                    13370000000000000000,
                    (error, result) => {
                      if (!error) {
                        console.log(result);
                      }
                      {
                        console.log(result);
                      }
                    },
                  );
                }}
              >
                Mint user tokens
              </Button>

              <Button
                style={{marginTop: "1em"}}
                color="teal"
                onClick={() => {
                  cologneDaoContract.getMdlyTokenAddress.call(
                    (error, result) => {
                      if (!error) {
                        console.log(`Mdly address: ${result}`);
                        const clgnTokenAddress = result;
                        const clgnTokenContract = clgnTokenAbi.at(
                          clgnTokenAddress,
                        );

                        clgnTokenContract.mint.sendTransaction(
                          this.props.user.account,
                          1230000000000000000,
                          (error, result) => {
                            if (!error) {
                              console.log(result);
                            } else {
                              console.log(error.code);
                            }
                          },
                        );
                      } else {
                        console.log(error.code);
                      }
                    },
                  );
                }}
              >
                Mint CLGN
              </Button>
            </Grid.Column>
          </Grid.Row>
        </Grid>
      </Container>
    );
  }
}

const mapStateToProps = (state) => ({
  lang: state.lang.dict,
  user: state.user,
});

export default connect(mapStateToProps)(MainPage);
