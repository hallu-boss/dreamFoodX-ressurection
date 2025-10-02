import express, { Request, Response } from "express";
import cors from "cors";
import { errorHandler } from "./utils/errorHandler";
import { loadRoutes } from "./loadRoutes";
import path from "path";

export const app = express();

app.use(cors());
app.use(express.json());

app.get("/", (req: Request, res: Response) => {
  res.json({ message: "Server is ready 🚀" });
});

app.use("/api", loadRoutes(path.join(__dirname, "routes/api")));

app.use(errorHandler);
