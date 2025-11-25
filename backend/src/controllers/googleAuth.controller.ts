import { Request, Response, NextFunction } from 'express';
import { OAuth2Client } from 'google-auth-library';
//npm install google-auth-library
import jwt from 'jsonwebtoken';
import { PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();
const client = new OAuth2Client(process.env.GOOGLE_CLIENT_ID);

export const googleAuth = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const { idToken } = req.body;

    if (!idToken) {
      return res.status(400).json({ message: 'Missing Google idToken' });
    }

    // 1. Weryfikacja tokena Google
    const ticket = await client.verifyIdToken({
      idToken,
      audience: process.env.GOOGLE_CLIENT_ID,
    });

    const payload = ticket.getPayload();

    if (!payload || !payload.email) {
      return res.status(401).json({ message: 'Invalid Google token' });
    }

    const email = payload.email;
    const name = payload.given_name || '';
    const surname = payload.family_name || '';
    const picture = payload.picture || '';
    const googleId = payload.sub;

    // 2. Czy user istnieje w bazie?
    let user = await prisma.user.findUnique({
      where: { email },
    });

    // 3. Jeśli nie → tworzę użytkownika (bez hasła)
    if (!user) {
      user = await prisma.user.create({
        data: {
          name,
          surname,
          email,
          password: '', // Google nie daje hasła
          cookingHours: 0,
          googleId,
          avatarUrl: picture,
        },
      });
    }

    // 4. Tworzę JWT tak jak Twoje zwykłe logowanie
    const token = jwt.sign(
      { id: user.id, email: user.email },
      process.env.JWT_SECRET || 'fallback-secret',
      { expiresIn: '24h' }
    );

    return res.json({
      message: 'Google login successful',
      token,
      user: {
        id: user.id,
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
