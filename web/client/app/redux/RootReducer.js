import {combineReducers} from "redux";
import User from "./reducers/User";

const appReducer = combineReducers({
  user: User,
});

export default appReducer;
