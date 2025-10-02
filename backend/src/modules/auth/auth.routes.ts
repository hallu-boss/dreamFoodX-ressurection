import { Router } from "express";
import { register, login, getProfile } from "./auth.controller";
import { authenticate } from "./auth.middleware";
import { validateRegistration, validateLogin } from "./auth.validators";

const router = Router();

// Rejestracja nowego użytkownika
router.post("/register", validateRegistration, register);

// Logowanie użytkownika
router.post("/login", validateLogin, login);

// Pobranie profilu zalogowanego użytkownika
router.get("/profile", authenticate, getProfile);

export default router;