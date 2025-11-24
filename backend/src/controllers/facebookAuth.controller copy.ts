import { Request, Response, NextFunction } from 'express';
import axios from 'axios'; // npm install axios
import jwt from 'jsonwebtoken';
import { PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();

export const facebookAuth = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    // Oczekujemy accessToken, który dostałeś w Androidzie (LoginResult.accessToken.token)
    const { accessToken } = req.body;

    if (!accessToken) {
      return res.status(400).json({ message: 'Missing Facebook accessToken' });
    }

    // 1. Weryfikacja tokena i pobranie danych z Graph API
    // Prosimy Facebooka o konkretne pola: id, imię, nazwisko, email, zdjęcie
    const fbUrl = `https://graph.facebook.com/me?fields=id,name,email,first_name,last_name,picture.type(large)&access_token=${accessToken}`;

    let fbData;
    try {
      const response = await axios.get(fbUrl);
      fbData = response.data;
    } catch (error) {
      // Jeśli token jest nieprawidłowy lub wygasł, Facebook zwróci błąd
      return res.status(401).json({ message: 'Invalid Facebook token' });
    }

    const { email, first_name, last_name, id } = fbData;
    // Facebook zwraca zdjęcie w zagnieżdżonej strukturze
    const picture = fbData.picture?.data?.url || '';

    // Facebook nie zawsze zwraca email (np. jeśli konto jest na numer telefonu)
    if (!email) {
      return res
        .status(400)
        .json({ message: 'Facebook account provided no email' });
    }

    // 2. Czy user istnieje w bazie?
    let user = await prisma.user.findUnique({
      where: { email },
    });

    // 3. Jeśli nie → tworzę użytkownika
    if (!user) {
      user = await prisma.user.create({
        data: {
          name: first_name || '',
          surname: last_name || '',
          email: email,
          password: '', // Hasło puste dla kont social
          cookingHours: 0,
          facebookId: id, // 🔥 Dodaj to pole w schema.prisma i zrób migrację!
          avatarUrl: picture,
        },
      });
    } else {
      // Opcjonalnie: Jeśli użytkownik już istnieje, możemy zaktualizować jego facebookId
      if (!user.facebookId) {
        user = await prisma.user.update({
          where: { id: user.id },
          data: { facebookId: id },
        });
      }
    }

    // 4. Tworzę JWT (identycznie jak w Google/Login)
    const token = jwt.sign(
      { id: user.id, email: user.email },
      process.env.JWT_SECRET || 'fallback-secret',
      { expiresIn: '24h' }
    );

    return res.json({
      message: 'Facebook login successful',
      token,
      user: {
        id: user.id,
        name: user.name,
        surname: user.surname,
        email: user.email,
        cookingHours: user.cookingHours,
        avatarUrl: user.avatarUrl,
      },
    });
  } catch (error) {
    next(error);
  }
};
