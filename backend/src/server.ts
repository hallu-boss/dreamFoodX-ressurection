import { setupSwagger } from "./config/swagger";
import dotenv from "dotenv";
import { app } from "./app";

// wczytaj zmienne środowiskowe z pliku .env
dotenv.config();

setupSwagger(app);

// pobieramy port z env albo domyślnie 3000
const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
  console.log(`✅ Server is running on http://localhost:${PORT}`);
});