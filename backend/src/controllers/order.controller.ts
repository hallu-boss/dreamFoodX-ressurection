import { Request, Response, NextFunction } from 'express';
import { PrismaClient } from '@prisma/client';
import { ValidationError, AuthError } from '../utils/errors';

const prisma = new PrismaClient();

interface AuthenticatedRequest extends Request {
  user: {
    id: number;
    email: string;
  };
}

interface CreateOrderRequest {
  paymentMethod: 'mock'; // Tylko symulacja płatności
  newsletter?: boolean;
}

/**
 * @swagger
 * components:
 *   schemas:
 *     OrderItem:
 *       type: object
 *       properties:
 *         id:
 *           type: integer
 *         recipeId:
 *           type: integer
 *         title:
 *           type: string
 *         price:
 *           type: number
 *         author:
 *           type: object
 *           properties:
 *             name:
 *               type: string
 *             surname:
 *               type: string
 *     
 *     Order:
 *       type: object
 *       properties:
 *         id:
 *           type: integer
 *         userId:
 *           type: integer
 *         total:
 *           type: number
 *         status:
 *           type: string
 *           enum: [pending, completed, failed]
 *         items:
 *           type: array
 *           items:
 *             $ref: '#/components/schemas/OrderItem'
 *         createdAt:
 *           type: string
 *           format: date-time
 */

export class OrderController {
  /**
   * @swagger
   * /api/orders/checkout:
   *   post:
   *     summary: Finalizuje zamówienie z koszyka
   *     tags: [Orders]
   *     security:
   *       - bearerAuth: []
   *     requestBody:
   *       required: true
   *       content:
   *         application/json:
   *           schema:
   *             type: object
   *             properties:
   *               paymentMethod:
   *                 type: string
   *                 enum: [mock]
   *                 example: mock
   *               newsletter:
   *                 type: boolean
   *                 description: Czy użytkownik chce otrzymywać newsletter
   *                 example: false
   *     responses:
   *       200:
   *         description: Zamówienie zostało pomyślnie zrealizowane
   *         content:
   *           application/json:
   *             schema:
   *               type: object
   *               properties:
   *                 success:
   *                   type: boolean
   *                   example: true
   *                 order:
   *                   $ref: '#/components/schemas/Order'
   *                 message:
   *                   type: string
   *                   example: "Zamówienie zostało pomyślnie zrealizowane"
   *       400:
   *         description: Błąd walidacji lub pusty koszyk
   *       401:
   *         description: Brak autoryzacji
   *       500:
   *         description: Błąd serwera
   */
  static async checkout(req: Request, res: Response, next: NextFunction) {
    try {
      const { user } = req as AuthenticatedRequest;
      const { paymentMethod, newsletter = false }: CreateOrderRequest = req.body;

      // Walidacja podstawowych danych
      if (!paymentMethod || paymentMethod !== 'mock') {
        throw new ValidationError('Invalid payment method', 400);
      }

      // Rozpocznij transakcję
      const result = await prisma.$transaction(async (tx) => {
        // Pobierz koszyk użytkownika z elementami
        const cart = await tx.cart.findUnique({
          where: { userId: user.id },
          include: {
            items: {
              include: {
                recipe: {
                  include: {
                    author: {
                      select: {
                        name: true,
                        surname: true,
                      }
                    }
                  }
                }
              }
            }
          }
        });

        if (!cart || cart.items.length === 0) {
          throw new ValidationError('Cart is empty', 400);
        }

        // Sprawdź czy użytkownik już nie posiada tych przepisów
        const userPurchases = await tx.user.findUnique({
          where: { id: user.id },
          include: {
            purchasedRecipes: {
              select: { id: true }
            }
          }
        });

        const alreadyOwnedRecipes = cart.items.filter(item => 
          userPurchases?.purchasedRecipes.some(purchase => purchase.id === item.recipeId)
        );

        if (alreadyOwnedRecipes.length > 0) {
          throw new ValidationError(`You already own some recipes: ${alreadyOwnedRecipes.map(r => r.recipe.title).join(', ')}`, 400);
        }

        // Oblicz całkowitą kwotę
        const total = cart.items.reduce((sum, item) => {
          return sum + Number(item.recipe.price);
        }, 0);

        // Przetworz płatność (symulacja)
        let paymentStatus = 'failed';
        
        if (paymentMethod === 'mock') {
          // Symulacja płatności - zawsze sukces w trybie demo
          paymentStatus = 'completed';
        }

        if (paymentStatus !== 'completed') {
          throw new ValidationError('Payment failed', 400);
        }

        // Dodaj przepisy do zakupionych przez użytkownika
        const recipeIds = cart.items.map(item => item.recipeId);
        
        await tx.user.update({
          where: { id: user.id },
          data: {
            purchasedRecipes: {
              connect: recipeIds.map(id => ({ id }))
            }
          }
        });

        // Opcjonalnie: zapisz informację o newsletter
        if (newsletter) {
          // Tu można dodać logikę zapisywania do newslettera
          // np. update użytkownika lub dodanie do tabeli newsletter_subscribers
        }

        // Wyczyść koszyk
        await tx.cartItem.deleteMany({
          where: { cartId: cart.id }
        });

        // Przygotuj dane zamówienia do zwrócenia
        const orderData = {
          id: Date.now(), // W rzeczywistej aplikacji to byłoby ID z tabeli orders
          userId: user.id,
          total,
          status: paymentStatus,
          items: cart.items.map(item => ({
            id: item.id,
            recipeId: item.recipeId,
            title: item.recipe.title,
            price: Number(item.recipe.price),
            author: {
              name: item.recipe.author.name,
              surname: item.recipe.author.surname,
            }
          })),
          createdAt: new Date(),
        };

        return orderData;
      });

      res.json({
        success: true,
        order: result,
        message: 'Order completed successfully'
      });

    } catch (error) {
      next(error);
    }
  }

  /**
   * @swagger
   * /api/orders/my-purchases:
   *   get:
   *     summary: Pobiera listę zakupionych przepisów użytkownika
   *     tags: [Orders]
   *     security:
   *       - bearerAuth: []
   *     responses:
   *       200:
   *         description: Lista zakupionych przepisów
   *         content:
   *           application/json:
   *             schema:
   *               type: object
   *               properties:
   *                 purchases:
   *                   type: array
   *                   items:
   *                     type: object
   *                     properties:
   *                       id:
   *                         type: integer
   *                       title:
   *                         type: string
   *                       price:
   *                         type: number
   *                       image:
   *                         type: string
   *                       author:
   *                         type: object
   *                         properties:
   *                           name:
   *                             type: string
   *                           surname:
   *                             type: string
   *       401:
   *         description: Brak autoryzacji
   */
  static async getMyPurchases(req: Request, res: Response, next: NextFunction) {
    try {
      const { user } = req as AuthenticatedRequest;

      const userWithPurchases = await prisma.user.findUnique({
        where: { id: user.id },
        include: {
          purchasedRecipes: {
            include: {
              author: {
                select: {
                  name: true,
                  surname: true,
                }
              }
            }
          }
        }
      });

      const purchases = userWithPurchases?.purchasedRecipes.map(recipe => ({
        id: recipe.id,
        title: recipe.title,
        description: recipe.description,
        price: Number(recipe.price),
        image: recipe.image,
        category: recipe.category,
        author: {
          name: recipe.author.name,
          surname: recipe.author.surname,
        },
        createdAt: recipe.createdAt,
      })) || [];

      res.json({
        purchases
      });

    } catch (error) {
      next(error);
    }
  }

  /**
   * @swagger
   * /api/orders/validate-cart:
   *   get:
   *     summary: Waliduje koszyk przed checkout (sprawdza dostępność, ceny itp.)
   *     tags: [Orders]
   *     security:
   *       - bearerAuth: []
   *     responses:
   *       200:
   *         description: Walidacja koszyka
   *         content:
   *           application/json:
   *             schema:
   *               type: object
   *               properties:
   *                 valid:
   *                   type: boolean
   *                 total:
   *                   type: number
   *                 itemCount:
   *                   type: integer
   *                 warnings:
   *                   type: array
   *                   items:
   *                     type: string
   *                 errors:
   *                   type: array
   *                   items:
   *                     type: string
   */
  static async validateCart(req: Request, res: Response, next: NextFunction) {
    try {
      const { user } = req as AuthenticatedRequest;

      const cart = await prisma.cart.findUnique({
        where: { userId: user.id },
        include: {
          items: {
            include: {
              recipe: {
                include: {
                  author: {
                    select: {
                      name: true,
                      surname: true,
                    }
                  }
                }
              }
            }
          }
        }
      });

      const warnings: string[] = [];
      const errors: string[] = [];

      if (!cart || cart.items.length === 0) {
        errors.push('Koszyk jest pusty');
        return res.json({
          valid: false,
          total: 0,
          itemCount: 0,
          warnings,
          errors
        });
      }

      // Sprawdź czy użytkownik już nie posiada niektórych przepisów
      const userPurchases = await prisma.user.findUnique({
        where: { id: user.id },
        include: {
          purchasedRecipes: {
            select: { id: true, title: true }
          }
        }
      });

      const alreadyOwned = cart.items.filter(item => 
        userPurchases?.purchasedRecipes.some(purchase => purchase.id === item.recipeId)
      );

      if (alreadyOwned.length > 0) {
        alreadyOwned.forEach(item => {
          warnings.push(`Już posiadasz przepis: ${item.recipe.title}`);
        });
      }

      // Sprawdź dostępność przepisów
      const unavailableRecipes = cart.items.filter(item => !item.recipe.visible);
      if (unavailableRecipes.length > 0) {
        unavailableRecipes.forEach(item => {
          errors.push(`Przepis nie jest dostępny: ${item.recipe.title}`);
        });
      }

      const total = cart.items.reduce((sum, item) => sum + Number(item.recipe.price), 0);
      const isValid = errors.length === 0;

      res.json({
        valid: isValid,
        total,
        itemCount: cart.items.length,
        warnings,
        errors
      });

    } catch (error) {
      next(error);
    }
  }
}