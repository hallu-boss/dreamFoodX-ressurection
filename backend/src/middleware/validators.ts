import { Request, Response, NextFunction } from 'express';
import { z } from 'zod';
import { ValidationError } from '../utils/errors';

const registrationSchema = z.object({
  name: z.string().min(2, 'Name must be at least 2 characters'),
  surname: z.string().min(2, 'Surname must be at least 2 characters'),
  email: z.string().email('Invalid email format'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
  cookingHours: z.number().nonnegative().optional().default(0)
});

const loginSchema = z.object({
  email: z.string().email('Invalid email format'),
  password: z.string().min(1, 'Password is required')
});

const stepSchema = z.object({
  nazwa: z.string().min(1),
  stepType: z.string().min(1),
  opis: z.string(),
  skladnik: z.number().int().positive(),
  ilosc: z.string().optional(),
  czas: z.string().min(1),
  temperatura: z.number().int(),
  predkoscOstrzy: z.number().int(),
});

const recipeSchema = z.object({
  nazwa: z.string().min(3),
  opis: z.string().min(10),
  kategoria: z.string().min(1),
  cena: z.number().positive(),
  obraz: z.string().optional(),
  kroki: z.array(stepSchema).min(1),
});

export const validateRegistration = (req: Request, res: Response, next: NextFunction) => {
  try {
    registrationSchema.parse(req.body);
    next();
  } catch (error) {
    if (error instanceof z.ZodError) {
      next(new ValidationError(error.errors.map(e => e.message).join(', '), 400));
    } else {
      next(error);
    }
  }
};

export const validateLogin = (req: Request, res: Response, next: NextFunction) => {
  try {
    loginSchema.parse(req.body);
    next();
  } catch (error) {
    if (error instanceof z.ZodError) {
      next(new ValidationError(error.errors.map(e => e.message).join(', '), 400));
    } else {
      next(error);
    }
  }
};

export const validateRecipe = (req: Request, res: Response, next: NextFunction) => {
  try {
    recipeSchema.parse(req.body);
    next();
  } catch (error) {
    if (error instanceof z.ZodError) {
      next(new ValidationError(error.errors.map(e => e.message).join(', '), 400));
    } else {
      next(error);
    }
  }
};
