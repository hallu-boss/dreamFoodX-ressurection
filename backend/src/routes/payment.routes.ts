// ./routes/payment.routes.ts

import express from 'express';
import { authenticate } from '../middleware/authenticate';
import {
  createPaymentIntent,
  stripeWebhook,
} from '../controllers/payment.controller';

const router = express.Router();

// Wymaga autoryzacji i tworzy PaymentIntent
router.post('/create-intent', authenticate, createPaymentIntent);

// Webhook nie wymaga autoryzacji (tylko weryfikacji podpisu Stripe)
// Ta ścieżka będzie zdefiniowana z middleware w app.ts
// router.post("/webhook", stripeWebhook);

export default router;
