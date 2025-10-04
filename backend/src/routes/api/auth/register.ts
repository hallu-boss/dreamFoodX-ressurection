import { Router, Request, Response, NextFunction } from "express";
import { z } from "zod";
import jwt from "jsonwebtoken";
import { AuthError, ValidationError } from "../../../utils/errors";
import { User } from "../../../mongo/models";
import { generateToken } from "../../../utils/authenticate";

const router = Router();

const registrationSchema = z.object({
  name: z.string().min(2, "Name must be at least 2 characters"),
  surname: z.string().min(2, "Surname must be at least 2 characters"),
  email: z.email("Invalid email format"),
  password: z.string().min(6, "Password must be at least 6 characters"),
  cookingHours: z.number().nonnegative().optional().default(0),
});

const validateRegistration = (req: Request, res: Response, next: NextFunction) => {
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



const register = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const { name, surname, email, password, cookingHours } = req.body;

    // Sprawdzenie, czy użytkownik już istnieje
    const existingUser = await User.findOne({ email });
    if (existingUser) {
      throw new AuthError("User with this email already exists", 409);
    }

    // Tworzenie użytkownika
    const user = await User.create({
      name,
      surname,
      email,
      password,
      cookingHours: cookingHours || 0,
    });

    // Generowanie JWT
    const token = generateToken({id: (user._id as string), email: user.email})

    res.status(201).json({
      message: "User registered successfully",
      token,
      user: {
        id: user._id,
        name: user.name,
        surname: user.surname,
        email: user.email,
        cookingHours: user.cookingHours,
      },
    });
  } catch (error) {
    next(error);
  }
};

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
router.post("/", validateRegistration, register);

export default router;
