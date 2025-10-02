import { Request, Response, NextFunction } from "express";
import { BaseError } from "../utils/errors";

export const errorHandler = (
  err: unknown,
  req: Request,
  res: Response,
  next: NextFunction
) => {
  console.error(err);

  if (err instanceof BaseError) {
    // Nasze własne błędy (ValidationError, AuthError)
    return res.status(err.statusCode).json({ message: err.message });
  }

  // Domyślny błąd 500
  res.status(500).json({ message: "Internal server error" });
};
