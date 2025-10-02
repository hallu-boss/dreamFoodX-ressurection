import { setupSwagger } from "./config/swagger";
import express, { Request, Response } from "express";
import dotenv from "dotenv";
import authRouters from './modules/auth/auth.routes';
import { errorHandler } from "./utils/errorHandler";

// wczytaj zmienne środowiskowe z pliku .env
dotenv.config();

const app = express();

// middleware do obsługi JSON
app.use(express.json());

setupSwagger(app);

// prosty endpoint testowy
app.get("/", (req: Request, res: Response) => {
  res.json({ message: "Backend serwisu przepisów działa 🚀" });
});

app.use("/api/auth", authRouters);

app.use(errorHandler);

// pobieramy port z env albo domyślnie 3000
const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
  console.log(`✅ Server is running on http://localhost:${PORT}`);
});

export default app;