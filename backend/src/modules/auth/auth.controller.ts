import { Request, Response, NextFunction } from "express";
import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";
import User from "./auth.model";
import { AuthError } from "../../utils/errors";

// Rejestracja nowego użytkownika
export const register = async (req: Request, res: Response, next: NextFunction) => {
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
    const token = jwt.sign(
      { id: user._id, email: user.email },
      process.env.JWT_SECRET || "fallback-secret",
      { expiresIn: "24h" }
    );

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

// Logowanie użytkownika
export const login = async (req: Request, res: Response, next: NextFunction) => {
  try {
/**
 * @swagger
 * components:
 *  schemas:
 *      LoginInput:
 *          type: object
 *          properties:
 *              email:
 *                  type: string
 *                  example: "jan.kowalski@example.com"
 *              password:
 *                type: string
 *                example: "secret123"
 */
    const { email, password } = req.body;

    const user = await User.findOne({ email });
    if (!user) {
      throw new AuthError("Invalid email or password", 401);
    }

    const isPasswordValid = await bcrypt.compare(password, user.password);
    if (!isPasswordValid) {
      throw new AuthError("Invalid email or password", 401);
    }

    const token = jwt.sign(
      { id: user._id, email: user.email },
      process.env.JWT_SECRET || "fallback-secret",
      { expiresIn: "24h" }
    );

/**
* @swagger
* components:
*   schemas:
*     LoginSuccess:
*       type: object
*       properties:
*         message:
*           type: string
*           example: "Login successful"
*         token:
*           type: string
*           example: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
*         user:
*           type: object
*           properties:
*             id:
*               type: string
*               example: "652f1a0e3a6f2b001f43c1a7"
*             name:
*               type: string
*               example: "Jan"
*             surname:
*               type: string
*               description: Nazwisko użytkownika
*               example: "Kowalski"
*             email:
*               type: string
*               example: "jan.kowalski@example.com"
*             cookingHours:
*               type: integer
*               example: 5
*/
    res.status(200).json({
      message: "Login successful",
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

// Pobranie profilu zalogowanego użytkownika
export const getProfile = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const userId = (req as any).user.id;

    const user = await User.findById(userId)
      .select("name surname email cookingHours recipes purchasedRecipes ingredients")
      .populate({
        path: "recipes",
        select: "title visible category price image",
      })
      .populate({
        path: "purchasedRecipes",
        select: "title category image",
      })
      .populate({
        path: "ingredients",
        select: "category title unit",
      });

    if (!user) {
      throw new AuthError("User not found", 404);
    }

    res.status(200).json(user);
  } catch (error) {
    next(error);
  }
};
