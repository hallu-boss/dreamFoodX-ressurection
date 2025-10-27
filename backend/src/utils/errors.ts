/**
 * @swagger
 * components:
 *   schemas:
 *     Error:
 *       type: object
 *       properties:
 *         error:
 *           type: string
 *           description: Ogólny komunikat błędu
 *         statusCode:
 *           type: integer
 *           description: Kod statusu HTTP błędu
 *       example:
 *         error: "Internal server error"
 *         statusCode: 500
 *         
 *     BaseError:
 *       type: object
 *       properties:
 *         message:
 *           type: string
 *           description: Komunikat błędu
 *         statusCode:
 *           type: integer
 *           description: Kod statusu HTTP błędu
 *         name:
 *           type: string
 *           description: Nazwa klasy błędu
 *       example:
 *         message: "Base error occurred"
 *         statusCode: 500
 *         name: "BaseError"
 *     
 *     ValidationError:
 *       allOf:
 *         - $ref: '#/components/schemas/BaseError'
 *         - type: object
 *           properties:
 *             name:
 *               example: "ValidationError"
 *           example:
 *             message: "Invalid input data"
 *             statusCode: 400
 *             name: "ValidationError"
 *     
 *     AuthError:
 *       allOf:
 *         - $ref: '#/components/schemas/BaseError'
 *         - type: object
 *           properties:
 *             name:
 *               example: "AuthError"
 *           example:
 *             message: "Authentication required"
 *             statusCode: 401
 *             name: "AuthError"
 */

export class BaseError extends Error {
  statusCode: number;

  constructor(message: string, statusCode: number) {
    super(message);
    this.statusCode = statusCode;
    this.name = this.constructor.name;
    Error.captureStackTrace(this, this.constructor);
  }
}

export class AuthError extends BaseError {
  constructor(message: string, statusCode: number) {
    super(message, statusCode);
  }
}

export class ValidationError extends BaseError {
  constructor(message: string, statusCode: number) {
    super(message, statusCode);
  }
}
