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
  constructor(message: string, statusCode = 401) {
    super(message, statusCode);
  }
}

export class ValidationError extends BaseError {
  constructor(message: string, statusCode = 400) {
    super(message, statusCode);
  }
}
