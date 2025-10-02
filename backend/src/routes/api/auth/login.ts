import { Router, Request, Response, NextFunction } from "express";
import z from "zod";
import bcrypt from "bcrypt";
import jwt from "jsonwebtoken";
import { AuthError, ValidationError } from "../../../utils/errors";
import { User } from "../../../mongo/models";

const router = Router();

const loginSchema = z.object({
  email: z.email("Invalid email format"),
  password: z.string().min(1, "Password is required"),
});

const validateLogin = (req: Request, res: Response, next: NextFunction) => {
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

/**
 * @swagger
 * components:
 *   schemas:
 *     LoginInput:
 *       type: object
 *       properties:
 *         email:
 *           type: string
 *           example: jan.kowalski@example.com
 *         password:
 *           type: string
 *           example: secret123
 */
interface LoginInput {
  email: string;
  password: string;
}

/**
 * @swagger
 * components:
 *   schemas:
 *     LoginSuccess:
 *       type: object
 *       properties:
 *         message:
 *           type: string
 *           example: Login successful
 *         token:
 *           type: string
 *           example: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
 *         user:
 *           type: object
 *           properties:
 *             id:
 *               type: string
 *               example: 652f1a0e3a6f2b001f43c1a7
 *             name:
 *               type: string
 *               example: Jan
 *             surname:
 *               type: string
 *               example: Kowalski
 *             email:
 *               type: string
 *               example: jan.kowalski@example.com
 *             cookingHours:
 *               type: integer
 *               example: 5
 */
interface LoginSuccess {
  message: string;
  token: string;
  user: {
    id: string;
    name: string;
    surname: string;
    email: string;
    cookingHours: number;
  };
}

const login = async (req: Request, res: Response, next: NextFunction) => {
  try {

    const { email, password } = req.body as LoginInput;

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

    const response: LoginSuccess = {
      message: "Login successful",
      token,
      user: {
        id: (user._id as string).toString(),
        name: user.name,
        surname: user.surname,
        email: user.email,
        cookingHours: user.cookingHours,
      },
    };

    res.status(200).json(response);
  } catch (error) {
    next(error);
  }
};


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
router.post("/", validateLogin, login);

export default router;