const HtmlWebpackPlugin = require("html-webpack-plugin");
const ExtractTextPlugin = require("extract-text-webpack-plugin");
const UglifyJsPlugin = require("uglifyjs-webpack-plugin");
const path = require("path");
// var Visualizer = require('webpack-visualizer-plugin');

module.exports = {
  entry: "./client/App.jsx",

  output: {
    path: path.resolve(__dirname, "..", "public"),
    publicPath: "/public/",
    filename: "[name].js",
    chunkFilename: "[id].chunk.js",
  },

  module: {
    rules: [
      {
        loader: "babel-loader",
        exclude: /node_modules/,
        test: /\.jsx?$/,
        resolve: {
          extensions: [".js", ".jsx"],
        },
      },
      {
        test: /\.(png|jpg|gif)(\?v=\d+\.\d+\.\d+)?$/,
        use: [
          {
            loader: "file-loader",
            options: {
              name: "assets/images/[name].[ext]",
            },
          },
        ],
      },
      {
        test: /\.(svg|ttf|woff|woff2|eot)(\?v=\d+\.\d+\.\d+)?$/,
        use: [
          {
            loader: "file-loader",
            options: {
              name: "assets/fonts/[name].[ext]",
            },
          },
        ],
      },
      {
        test: /\.css$/,
        exclude: path.resolve(__dirname, "client", "app"),
        loader: ExtractTextPlugin.extract({
          fallback: "style-loader",
          use: "css-loader?name=/assets/[name].[ext]",
        }),
      },
    ],
  },

  optimization: {
    minimizer: [
      new UglifyJsPlugin({
        uglifyOptions: {
          compress: {
            warnings: false,
          },
          minimize: true,
          sourceMap: false,
        },
      }),
    ],
  },

  plugins: [
    // new Visualizer(),
    new ExtractTextPlugin("[name].css"),
    new HtmlWebpackPlugin({
      template: "client/index.html",
    }),
  ],
};
