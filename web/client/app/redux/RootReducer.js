import {combineReducers} from "redux";
import User from "./reducers/User";
import Lang from "./reducers/Lang";

const appReducer = combineReducers({
  user: User,
  lang: Lang,
});

export default appReducer;
