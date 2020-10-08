import throttle from "lodash/throttle";
import {applyMiddleware, createStore} from "redux";
import thunk from "redux-thunk";
import rootReducer from "./RootReducer";
import {loadState, saveState} from "../common/SessionStorage";

const persistedState = loadState();

const store = createStore(rootReducer, persistedState, applyMiddleware(thunk));

store.subscribe(
  throttle(() => {
    saveState({
      user: store.getState().user,
    });
  }, 1000),
);

export default store;
