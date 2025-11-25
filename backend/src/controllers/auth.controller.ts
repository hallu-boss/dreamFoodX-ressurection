import { Request, Response, NextFunction } from 'express';
import bcrypt from 'bcrypt';
import jwt from 'jsonwebtoken';
import { PrismaClient } from '@prisma/client';
import { AuthError } from '../utils/errors';

const prisma = new PrismaClient();

export const register = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const { name, surname, email, password, cookingHours } = req.body;

    // Check if user already exists
    const existingUser = await prisma.user.findUnique({
      where: { email }
    });

    if (existingUser) {
      throw new AuthError('User with this email already exists', 409);
    }

    // Hash password
    const hashedPassword = await bcrypt.hash(password, 10);

    // Create new user
    const user = await prisma.user.create({
      data: {
        name,
        surname,
        email,
        password: hashedPassword,
        cookingHours: cookingHours || 0
      }
    });

    // Generate JWT token
    const token = jwt.sign(
      { id: user.id, email: user.email },
      process.env.JWT_SECRET || 'fallback-secret',
      { expiresIn: '24h' }
    );

    res.status(201).json({
      message: 'User registered successfully',
      token,
      user: {
        id: user.id,
        name: user.name,
        surname: user.surname,
        email: user.email,
        cookingHours: user.cookingHours
      }
    });
  } catch (error) {
    next(error);
  }
};

export const login = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const { email, password } = req.body;

    // Find user by email
    const user = await prisma.user.findUnique({
      where: { email }
    });

    if (!user) {
      throw new AuthError('Invalid email or password', 401);
    }

    // Verify password
    const isPasswordValid = await bcrypt.compare(password, user.password);

    if (!isPasswordValid) {
      throw new AuthError('Invalid email or password', 401);
    }

    // Generate JWT token
    const token = jwt.sign(
      { id: user.id, email: user.email },
      process.env.JWT_SECRET || 'fallback-secret',
      { expiresIn: '24h' }
    );

    res.status(200).json({
      message: 'Login successful',
      token,
      user: {
        id: user.id,
        name: user.name,
        surname: user.surname,
        email: user.email,
        cookingHours: user.cookingHours
      }
    });
  } catch (error) {
    next(error);
  }
};

export const getProfile = async (req: Request, res: Response, next: NextFunction) => {
  try {
    // req.user is set in the authenticate middleware
    const userId = (req as any).user.id;
    console.log("getProfile  ", + userId)
    const user = await prisma.user.findUnique({
      where: { id: userId },
      select: {
        id: true,
        name: true,
        surname: true,
        email: true,
        cookingHours: true,
        recipes: {
          select: {
            id: true,
            title: true,
            visible: true,
            category: true,
            price: true,
            image: true,
          }
        },
        purchasedRecipes: {
          select: {
            id: true,
            title: true,
            category: true,
            image: true,
          }
        },
        ingredients: {
          select: {
            id: true,
            category: true,
            title: true,
            unit: true
          }
        }
      }
    });

    if (!user) {
      throw new AuthError('User not found', 404);
    }

    res.status(200).json(user);
  } catch (error) {
    next(error);
  }
};



export const updateProfile = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const userId = (req as any).user.id;
    console.log("updateProfile")
    const { name, surname, email, cookingHours } = req.body;
    console.log("updateProfile" + name + "  " + surname + "  " + email + "  " + cookingHours )
    if (!name && !surname && !email && cookingHours === undefined) {
      return res.status(400).json({ message: "Brak danych do aktualizacji" });
    }

       // Check if user already exists
      const existingUser = await prisma.user.findUnique({
        where: { email }
      });

      if (existingUser && existingUser.id != userId) {
        throw new AuthError('User with this email already exists', 409);
      }

    const updatedUser = await prisma.user.update({
      where: { id: userId },
      data: {
        name,
        surname,
        email,
        cookingHours,
      },
      select: {
        id: true,
        name: true,
        surname: true,
        email: true,
        cookingHours: true,
      }
    });

    res.status(200).json(updatedUser);
  } catch (error: any) {
    if (error.code === "P2002") {
      return res.status(409).json({ message: "Email jest już używany" });
    }
    next(error);
  }
};


export const updatePassword = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const userId = (req as any).user.id;
    const { oldPassword, newPassword } = req.body;

    if (!oldPassword || !newPassword) {
      return res.status(400).json({ message: "Brak danych do aktualizacji" });
    }

    // Pobierz aktualne hasło użytkownika
    const user = await prisma.user.findUnique({
      where: { id: userId },
      select: { password: true }
    });

    if (!user) {
      return res.status(404).json({ message: "Użytkownik nie istnieje" });
    }

    // Sprawdź czy stare hasło jest poprawne
    const isValidOldPassword = await bcrypt.compare(oldPassword, user.password);
    if (!isValidOldPassword) {
      return res.status(401).json({ message: "Niepoprawne stare hasło" });
    }

    // Hash nowego hasła
    const hashedPassword = await bcrypt.hash(newPassword, 10);

    await prisma.user.update({
      where: { id: userId },
      data: {
        password: hashedPassword,
      }
    });

    res.status(200).json({ message: "Hasło zostało pomyślnie zmienione" });
  } catch (error: any) {
    if (error.code === "P2002") {
      return res.status(409).json({ message: "Nie można zmienić hasła" });
    }
    next(error);
  }
};

