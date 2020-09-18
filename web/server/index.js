import path from "path";
import express from "express";
import {logger} from "./logger";

const app = express();
const port = 3000;

// Static
app.use("/public", express.static("public"));

// All other pages are supposed to serve bundle
app.get("/*", (req, res) => {
  res.sendFile(path.join(__dirname, "..", "./public/index.html"));
});

app.listen(port, () => logger.info(`running on localhost:${port}`));
