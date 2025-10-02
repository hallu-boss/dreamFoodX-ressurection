import express, { Request, Response } from "express";
import cors from "cors";
import authRouters from './modules/auth/auth.routes';
import { errorHandler } from "./utils/errorHandler";

export const app = express();

app.use(cors());
app.use(express.json());

app.get("/", (req: Request, res: Response) => {
  res.json({ message: "Backend serwisu przepisów działa 🚀" });
});

// Routes
app.use("/api/auth", authRouters);

// Error handler
app.use(errorHandler);
