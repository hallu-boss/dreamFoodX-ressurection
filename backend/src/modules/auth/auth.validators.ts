import { Request, Response, NextFunction } from "express";
import { z } from "zod";
import { ValidationError } from "../../utils/errors";

// Schemat rejestracji
const registrationSchema = z.object({
  name: z.string().min(2, "Name must be at least 2 characters"),
  surname: z.string().min(2, "Surname must be at least 2 characters"),
  email: z.email("Invalid email format"),
  password: z.string().min(6, "Password must be at least 6 characters"),
  cookingHours: z.number().nonnegative().optional().default(0),
});

// Schemat logowania
const loginSchema = z.object({
  email: z.email("Invalid email format"),
  password: z.string().min(1, "Password is required"),
});

/**
 * Middleware walidacji rejestracji
 */
export const validateRegistration = (req: Request, res: Response, next: NextFunction) => {
  try {
    registrationSchema.parse(req.body);
    next();
  } catch (error) {
    if (error instanceof z.ZodError) {
      next(new ValidationError(error.issues.map((e) => e.message).join(", ")));
    } else {
      next(error);
    }
  }
};

/**
 * Middleware walidacji logowania
 */
export const validateLogin = (req: Request, res: Response, next: NextFunction) => {
  try {
    loginSchema.parse(req.body);
    next();
  } catch (error) {
    if (error instanceof z.ZodError) {
      next(new ValidationError(error.issues.map((e) => e.message).join(", ")));
    } else {
      next(error);
    }
  }
};
