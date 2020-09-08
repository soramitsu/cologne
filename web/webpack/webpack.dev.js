const path = require("path");
const webpack = require("webpack");
const webpackMerge = require("webpack-merge");
const commonConfig = require("./webpack.common.js");

module.exports = webpackMerge(commonConfig, {
  devtool: "cheap-module-eval-source-map",
  target: "web",
  mode: "development",

  devServer: {
    historyApiFallback: true,
    stats: "minimal",
    contentBase: path.join(__dirname, "..", "public"),
    compress: false,
    port: 9020,
    hot: true,
    proxy: {
      "/api": "http://localhost:3000",
    },
  },

  output: {
    path: path.resolve(__dirname, "..", "public"),
    filename: "bundle.js",
  },

  plugins: [
    new webpack.DefinePlugin({
      "process.env": {
        NODE_ENV: JSON.stringify("development"),
      },
    }),
    new webpack.HotModuleReplacementPlugin(),
  ],
});
