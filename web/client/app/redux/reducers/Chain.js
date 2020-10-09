import {CHANGE_CHAIN} from "../actions/Chain";

export default function Chain(state = {}, action = {}) {
  switch (action.type) {
    case CHANGE_CHAIN:
      return {...state, ...action.chain};
    default:
      return state;
  }
}
