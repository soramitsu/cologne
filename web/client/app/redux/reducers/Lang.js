import ENG from "../../i18n/eng.json";
import RUS from "../../i18n/rus.json";
import {engFormatter, rusFormatter} from "../../common/Utils";
import {LANG_RUS, LANG_ENG} from "../actions/Lang";

export const initialState = {
  lang: LANG_ENG,
  dict: ENG,
  formatter: engFormatter,
};

export default function Lang(state = initialState, action = {}) {
  switch (action.type) {
    case LANG_RUS:
      return {
        lang: LANG_RUS,
        dict: RUS,
        formatter: rusFormatter,
      };
    case LANG_ENG:
      return {
        lang: LANG_ENG,
        dict: ENG,
        formatter: engFormatter,
      };
    default:
      return state;
  }
}
