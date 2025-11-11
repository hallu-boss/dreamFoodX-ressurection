import express from "express";
import { authenticate } from "../middleware/authenticate";
import {
  getCart,
  addToCart,
  removeFromCart,
  clearCart,
} from "../controllers/cart.controller";

const router = express.Router();

/**
 * @swagger
 * components:
 *   schemas:
 *     CartItem:
 *       type: object
 *       properties:
 *         id:
 *           type: integer
 *           description: ID elementu koszyka
 *         recipeId:
 *           type: integer
 *           description: ID przepisu
 *         title:
 *           type: string
 *           description: Tytuł przepisu
 *         price:
 *           type: number
 *           description: Cena przepisu
 *         image:
 *           type: string
 *           description: URL obrazu przepisu
 *         author:
 *           type: object
 *           properties:
 *             id:
 *               type: integer
 *             name:
 *               type: string
 *             surname:
 *               type: string
 *         addedAt:
 *           type: string
 *           format: date-time
 *           description: Data dodania do koszyka
 *
 *     Cart:
 *       type: object
 *       properties:
 *         id:
 *           type: integer
 *           description: ID koszyka
 *         items:
 *           type: array
 *           items:
 *             $ref: '#/components/schemas/CartItem'
 *         count:
 *           type: integer
 *           description: Liczba elementów w koszyku
 *         total:
 *           type: number
 *           description: Łączna wartość koszyka
 *         updatedAt:
 *           type: string
 *           format: date-time
 *           description: Data ostatniej aktualizacji
 */

/**
 * @swagger
 * /api/cart:
 *   get:
 *     summary: Pobiera koszyk użytkownika
 *     tags: [Cart]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Koszyk użytkownika
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Cart'
 *       401:
 *         description: Brak autoryzacji
 *       500:
 *         description: Błąd serwera
 */
router.get("/", authenticate, getCart);

/**
 * @swagger
 * /api/cart/add:
 *   post:
 *     summary: Dodaje przepis do koszyka
 *     tags: [Cart]
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               recipeId:
 *                 type: integer
 *                 description: ID przepisu do dodania
 *             required:
 *               - recipeId
 *     responses:
 *       201:
 *         description: Przepis dodany do koszyka
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 message:
 *                   type: string
 *                 item:
 *                   $ref: '#/components/schemas/CartItem'
 *       400:
 *         description: Błędne dane lub przepis już w koszyku
 *       401:
 *         description: Brak autoryzacji
 *       404:
 *         description: Przepis nie znaleziony
 *       500:
 *         description: Błąd serwera
 */
router.post("/add", authenticate, addToCart);

/**
 * @swagger
 * /api/cart/remove/{recipeId}:
 *   delete:
 *     summary: Usuwa przepis z koszyka
 *     tags: [Cart]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: recipeId
 *         required: true
 *         schema:
 *           type: integer
 *         description: ID przepisu do usunięcia
 *     responses:
 *       200:
 *         description: Przepis usunięty z koszyka
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 message:
 *                   type: string
 *                 recipeId:
 *                   type: integer
 *       400:
 *         description: Nieprawidłowe ID przepisu
 *       401:
 *         description: Brak autoryzacji
 *       404:
 *         description: Koszyk lub przepis nie znaleziony
 *       500:
 *         description: Błąd serwera
 */
router.delete("/remove/:recipeId", authenticate, removeFromCart);

/**
 * @swagger
 * /api/cart/clear:
 *   delete:
 *     summary: Czyści cały koszyk
 *     tags: [Cart]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Koszyk wyczyszczony
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 message:
 *                   type: string
 *       401:
 *         description: Brak autoryzacji
 *       404:
 *         description: Koszyk nie znaleziony
 *       500:
 *         description: Błąd serwera
 */
router.delete("/clear", authenticate, clearCart);

export default router;
