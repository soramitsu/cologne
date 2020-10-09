import {combineReducers} from "redux";
import User from "./reducers/User";
import Chain from "./reducers/Chain";

const appReducer = combineReducers({
  user: User,
  chain: Chain,
});

export default appReducer;
