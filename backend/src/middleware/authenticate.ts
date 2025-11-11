import { Request, Response, NextFunction } from 'express';
import jwt from 'jsonwebtoken';
import { AuthError } from '../utils/errors';

/**
 * @swagger
 * components:
 *   securitySchemes:
 *     bearerAuth:
 *       type: http
 *       scheme: bearer
 *       bearerFormat: JWT
 *       description: JWT token autoryzacyjny
 */

interface JwtPayload {
  id: number;
  email: string;
}

/**
 * Middleware do uwierzytelniania użytkowników poprzez token JWT
 * @param req - Obiekt żądania Express
 * @param res - Obiekt odpowiedzi Express
 * @param next - Funkcja przejścia do następnego middleware
 */
export const authenticate = (req: Request, res: Response, next: NextFunction) => {
  try {
    const authHeader = req.headers.authorization;

    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      throw new AuthError('Authentication required', 401);
    }

    const token = authHeader.split(' ')[1];

    try {
      const decoded = jwt.verify(
        token,
        process.env.JWT_SECRET || 'fallback-secret'
      ) as JwtPayload;

      // Attach user to request object
      (req as any).user = decoded;
      next();
    } catch (error) {
      throw new AuthError('Invalid or expired token', 401);
    }
  } catch (error) {
    next(error);
  }
};
