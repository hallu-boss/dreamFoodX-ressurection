import { setupSwagger } from "./config/swagger";
import express, { Request, Response } from "express";
import dotenv from "dotenv";

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

// pobieramy port z env albo domyślnie 3000
const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
  console.log(`✅ Server is running on http://localhost:${PORT}`);
});
