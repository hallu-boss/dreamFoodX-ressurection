import { Request, Response, NextFunction } from "express";
import jwt from "jsonwebtoken";
import { AuthError } from "./errors";

interface JwtPayload {
  id: string; // Mongo _id jako string
  email: string;
}

// Rozszerzenie Request, żeby TS wiedział o user
export interface AuthenticatedRequest extends Request {
  user?: JwtPayload;
}

export const generateToken = ({id, email}: JwtPayload) => {
  return jwt.sign({ id, email }, process.env.JWT_SECRET || "fallback-secret", { expiresIn: "24h" });
}

/**
 * @swagger
 * components:
 *   securitySchemes:
 *     bearerAuth:
 *       type: http
 *       scheme: bearer
 *       bearerFormat: JWT
 */
export const authenticate = (
  req: AuthenticatedRequest,
  res: Response,
  next: NextFunction
) => {
  try {
    const authHeader = req.headers.authorization;

    if (!authHeader || !authHeader.startsWith("Bearer ")) {
      throw new AuthError("Authentication required", 401);
    }

    const token = authHeader.split(" ")[1];

    try {
      const decoded = jwt.verify(
        token,
        process.env.JWT_SECRET || "fallback-secret"
      ) as JwtPayload;

      // Attach user to request object
      req.user = decoded;
      next();
    } catch (error) {
      throw new AuthError("Invalid or expired token", 401);
    }
  } catch (error) {
    next(error);
  }
};
