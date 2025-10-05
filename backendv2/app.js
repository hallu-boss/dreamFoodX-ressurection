import express from "express";
import { PORT } from "./config/env.js";

import authRouter from "./routes/auth.routes.js";
import userRouter from "./routes/user.routes.js";
import ingredientRouter from "./routes/ingredient.routes.js";
import connectToDatabase from "./database/mongodb.js";
import errorMiddleware from "./middlewares/error.middleware.js";
import cookieParser from "cookie-parser";

const app = express();

app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(cookieParser());

app.use('/api/v1/auth', authRouter);
app.use('/api/v1/user', userRouter);
app.use('/api/v1/ingredient', ingredientRouter);

app.use(errorMiddleware);

app.get("/", (req, res) => {
    res.send('Welcome to dreamFood API!');
})

app.listen(PORT, async () => {
    console.log(`dreamFood API server is running on http://localhost:${PORT}`);

    await connectToDatabase();
})

export default app;