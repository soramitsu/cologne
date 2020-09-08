/* eslint-disable no-restricted-globals */
import store from "../redux/Store";

/**
 * Parse parameter from browser string by its name
 *
 * @param parameterName
 * @returns {*}
 */
export const findGetParameter = parameterName => {
  let result = null;
  let tmp = [];

  location.search
    .substr(1)
    .split("&")
    .forEach(item => {
      tmp = item.split("=");
      if (tmp[0] === parameterName) {
        result = decodeURIComponent(tmp[1]);
      }
    });

  return result;
};

export const rusFormatter = new Intl.NumberFormat("ru-RU", {
  style: "currency",
  currency: "USD",
  minimumFractionDigits: 2,
  currencyDisplay: "symbol",
});

export const engFormatter = new Intl.NumberFormat("en-US", {
  style: "currency",
  currency: "USD",
  minimumFractionDigits: 2,
  currencyDisplay: "symbol",
});

export const beforePriceShown = number =>
  store.getState().lang.formatter.format(number);
