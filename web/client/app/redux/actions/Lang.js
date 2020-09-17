export const LANG_ENG = "LANG_ENG";
export const LANG_RUS = "LANG_RUS";

export const changeLang = (lang) => {
  switch (lang) {
    case LANG_RUS:
      return {
        type: LANG_RUS,
      };
    default:
      return {
        type: LANG_ENG,
      };
  }
};
