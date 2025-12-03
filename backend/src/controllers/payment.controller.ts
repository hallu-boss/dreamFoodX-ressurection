// ./controllers/payment.controller.ts

import { Request, Response, NextFunction } from 'express';
import { PrismaClient } from '@prisma/client';
import Stripe from 'stripe';
import dotenv from 'dotenv';

dotenv.config();

const prisma = new PrismaClient();
// Inicjalizacja Stripe (klucz ładowany z .env/Dockera)
const stripe = new Stripe(process.env.STRIPE_API_SECRET_KEY as string, {
  apiVersion: '2024-06-20',
});

// --- A. Tworzenie Payment Intent (wywoływane przez aplikację mobilną) ---
export const createPaymentIntent = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const userId = (req as any).user.id;
    const { amount } = req.body; // Kwota w groszach

    // 1. Walidacja kwoty (MINIMALNA WERYFIKACJA)
    if (!amount || typeof amount !== 'number' || amount < 50) {
      // Stripe wymaga min. 0.50 PLN
      return res
        .status(400)
        .json({ error: 'Nieprawidłowa kwota płatności lub poniżej limitu.' });
    }

    // 2. Utwórz PaymentIntent w Stripe
    const paymentIntent = await stripe.paymentIntents.create({
      amount: amount,
      currency: 'pln',
      automatic_payment_methods: { enabled: true },
      // Metadane są kluczowe, aby Webhook wiedział, dla kogo zrealizować zamówienie
      metadata: {
        userId: userId.toString(),
        cartTotal: amount.toString(),
      },
    });

    // 3. Zwróć dane do klienta
    res.json({
      clientSecret: paymentIntent.client_secret,
      publishableKey: process.env.STRIPE_API_KEY,
    });
  } catch (error) {
    console.error('[POST /payment/create-intent]', error);
    res.status(500).json({ error: 'Błąd podczas tworzenia Payment Intent' });
  }
};

// --- B. Realizacja Zamówienia (wywoływana przez Webhook) ---

/**
 * Funkcja realizująca zamówienie w bazie danych (Prisma)
 */
async function fulfillOrder(paymentIntent: Stripe.PaymentIntent) {
  const userId = parseInt(paymentIntent.metadata.userId as string);
  console.log(`Realizowanie zamówienia dla userId: ${userId}`);

  const cart = await prisma.cart.findUnique({
    where: { userId },
    include: { items: true }, // Pobierz przepisy z koszyka
  });

  if (!cart || cart.items.length === 0) {
    console.error(`Błąd realizacji: Koszyk dla userId ${userId} jest pusty.`);
    return;
  }

  const recipeIds = cart.items.map((item) => item.recipeId);

  // 1. Dodaj przepisy do purchasedRecipes (relacja M2M)
  await prisma.user.update({
    where: { id: userId },
    data: {
      purchasedRecipes: {
        connect: recipeIds.map((id) => ({ id })), // Zakładamy relację m2m
      },
    },
  });

  // 2. Wyczyść koszyk
  await prisma.cartItem.deleteMany({
    where: { cartId: cart.id },
  });

  console.log(`Zamówienie dla userId ${userId} zrealizowane.`);
}

/**
 * Kontroler obsługujący Webhooki Stripe (musi używać express.raw w app.ts)
 */
export const stripeWebhook = async (req: Request, res: Response) => {
  const sig = req.headers['stripe-signature'];
  const webhookSecret = process.env.STRIPE_WEBHOOK_SECRET as string;
  let event: Stripe.Event;

  try {
    // Weryfikacja sygnatury Webhooka
    event = stripe.webhooks.constructEvent(
      req.body,
      sig as string,
      webhookSecret
    );
  } catch (err: any) {
    console.error(`❌ Webhook Error: ${err.message}`);
    return res.status(400).send(`Webhook Error: ${err.message}`);
  }

  // Obsługa zdarzenia po pomyślnej płatności
  if (event.type === 'payment_intent.succeeded') {
    const paymentIntent = event.data.object as Stripe.PaymentIntent;
    await fulfillOrder(paymentIntent);
  }

  // Zawsze zwracaj status 200 do Stripe
  res.json({ received: true });
};
