import { Router } from 'express';

import { authenticate } from '../middleware/authenticate';
import { OrderController } from '../controllers/order.controller';

const router = Router();

/**
 * @swagger
 * tags:
 *   name: Orders
 *   description: Endpointy do zarządzania zamówieniami i płatnościami
 */

// Finalizacja zamówienia
router.post('/checkout', authenticate, OrderController.checkout);

// Pobieranie zakupionych przepisów użytkownika
router.get('/my-purchases', authenticate, OrderController.getMyPurchases);

// Walidacja koszyka przed checkout
router.get('/validate-cart', authenticate, OrderController.validateCart);

export { router as orderRoutes };
