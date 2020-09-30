import throttle from "lodash/throttle";
import {applyMiddleware, createStore} from "redux";
import thunk from "redux-thunk";
import rootReducer from "./RootReducer";
import {loadState, saveState} from "../common/SessionStorage";
// import {engFormatter, rusFormatter} from "../common/Utils";
import {LANG_RUS} from "./actions/Lang";

const persistedState = loadState();

// const getFormatter = (lang) => {
//   switch (lang) {
//     case LANG_RUS:
//       return rusFormatter;
//     default:
//       return engFormatter;
//   }
// };

// if (persistedState && persistedState.lang) {
//   // Updating formatters as they are not stored
//   persistedState.lang = {
//     ...persistedState.lang,
//     formatter: getFormatter(persistedState.lang.lang),
//   };
// }

const store = createStore(rootReducer, persistedState, applyMiddleware(thunk));

store.subscribe(
  throttle(() => {
    saveState({
      user: store.getState().user,
      lang: {
        dict: store.getState().lang.dict,
        // lang: store.getState().lang.lang,
      },
    });
  }, 1000),
);

export default store;
