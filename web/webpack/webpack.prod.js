const path = require("path");
const webpack = require("webpack");
const {merge} = require("webpack-merge");
const commonConfig = require("./webpack.common.js");

module.exports = merge(commonConfig, {
  target: "web",
  mode: "production",

  output: {
    path: path.resolve(__dirname, "..", "public"),
    filename: "bundle.js",
  },

  plugins: [
    new webpack.DefinePlugin({
      "process.env": {
        NODE_ENV: JSON.stringify("production"),
      },
    }),
  ],
});
