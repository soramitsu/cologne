import {LOGIN_USER, LOGOUT_USER} from "../actions/User";

export default function User(state = {}, action = {}) {
  switch (action.type) {
    case LOGIN_USER:
      return {...state, ...action.user};
    case LOGOUT_USER:
      return {};
    default:
      return state;
  }
}
