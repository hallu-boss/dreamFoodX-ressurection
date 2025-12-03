import express from 'express';
import cors from 'cors';
import dotenv from 'dotenv';
import * as swaggerUi from 'swagger-ui-express';
import authRoutes from './routes/auth.routes';
import healthRoute from './routes/health.routes';
import ingredientsRoute from './routes/ingredients.routes';
import recipeRoute from './routes/recipe.routes';
import cartRouter from './routes/cart.routes';
import { orderRoutes } from './routes/order.routes';
import { errorHandler } from './middleware/errorHandler';
import swaggerSpec from './swagger.config';
import { stripeWebhook } from './controllers/payment.controller';
import paymentRoutes from './routes/payment.routes';
dotenv.config();

const app = express();
const port = process.env.PORT || 5000;

// Middleware
app.use(cors());
app.use(express.json());

// Swagger documentation
app.use('/api-docs', swaggerUi.serve);
app.use('/api-docs', swaggerUi.setup(swaggerSpec));
app.get('/api-docs.json', (req, res) => {
  res.setHeader('Content-Type', 'application/json');
  res.send(swaggerSpec);
});

// Routes
app.use('/api/auth', authRoutes);
app.use('/api/ingredients', ingredientsRoute);
app.use('/api', healthRoute);
app.use('/api/recipe', recipeRoute);
app.use('/api/cart', cartRouter);
app.use('/api/orders', orderRoutes);
app.use('/api/payment', paymentRoutes);
app.post(
  '/api/payment/webhook',
  express.raw({ type: 'application/json' }),
  stripeWebhook
);
app.get('/', (req, res) => {
  res.send(
    'API działa prawidłowo. Użyj /api/health aby sprawdzić status. Dokumentacja dostępna pod /api-docs'
  );
});

// Error handling middleware
app.use(errorHandler);

app.listen(port, () => {
  console.log(`Server running on port ${port}`);
  console.log(
    `Swagger documentation available at http://localhost:${port}/api-docs`
  );
});
