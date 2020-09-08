const path = require("path");
const webpack = require("webpack");
const webpackMerge = require("webpack-merge");
const commonConfig = require("./webpack.common.js");

module.exports = webpackMerge(commonConfig, {
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
