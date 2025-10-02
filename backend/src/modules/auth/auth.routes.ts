import { Router } from "express";
import { register, login, getProfile } from "./auth.controller";
import { authenticate } from "./auth.middleware";
import { validateRegistration, validateLogin } from "./auth.validators";

const router = Router();

/**
 * @swagger
 * tags:
 *   name: Auth
 *   description: Endpoints związane z autentykacją użytkowników
 */

/**
 * @swagger
 * /api/auth/register:
 *   post:
 *     summary: Rejestracja nowego użytkownika
 *     tags: [Auth]
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: "#/components/schemas/RegisterInput"
 *     responses:
 *       201:
 *         description: User registered successfully
 *         content:
 *           application/json:
 *             schema:
 *               $ref: "#/components/schemas/RegisterSuccess"
 *       400:
 *         description: Input data validation error
 *       409:
 *         description: User with this email already exists
 *       500:
 *         description: Internal server error
 */
router.post("/register", validateRegistration, register);

/**
 * @swagger
 * /api/auth/login:
 *   post:
 *     summary: Login to existing account
 *     tags: [Auth]
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: "#/components/schemas/LoginInput"
 *     responses:
 *       201:
 *         description: User logged successfully
 *         content:
 *           application/json:
 *             schema:
 *               $ref: "#/components/schemas/LoginSuccess"
 *       400:
 *         description: Input data validation error
 *       401:
 *         description: Invalid email or password
 *       500:
 *         description: Internal server error
 */
router.post("/login", validateLogin, login);

// Pobranie profilu zalogowanego użytkownika
router.get("/profile", authenticate, getProfile);

export default router;