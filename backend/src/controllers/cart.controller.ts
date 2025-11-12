import { Request, Response, NextFunction } from "express";
import { PrismaClient } from "@prisma/client";
import { ValidationError } from "../utils/errors";

const prisma = new PrismaClient();

/**
 * Pobiera koszyk użytkownika
 * GET /api/cart
 */
export const getCart = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const userId = (req as any).user.id;
    
    // Znajdź lub utwórz koszyk dla użytkownika
    let cart = await prisma.cart.findUnique({
      where: { userId },
      include: {
        items: {
          include: {
            recipe: {
              select: {
                id: true,
                title: true,
                price: true,
                image: true,
                author: {
                  select: {
                    id: true,
                    name: true,
                    surname: true,
                  },
                },
              },
            },
          },
          orderBy: {
            addedAt: "desc", // Najnowsze na górze
          },
        },
      },
    });

    // Jeśli koszyk nie istnieje, utwórz pusty
    if (!cart) {
      cart = await prisma.cart.create({
        data: { userId },
        include: {
          items: {
            include: {
              recipe: {
                select: {
                  id: true,
                  title: true,
                  price: true,
                  image: true,
                  author: {
                    select: {
                      id: true,
                      name: true,
                      surname: true,
                    },
                  },
                },
              },
            },
          },
        },
      });
    }

    // Formatuj odpowiedź
    const formattedItems = cart.items.map((item) => ({
      id: item.id,
      recipeId: item.recipeId,
      title: item.recipe.title,
      price: Number(item.recipe.price),
      image: item.recipe.image,
      author: item.recipe.author,
      addedAt: item.addedAt.toISOString(),
    }));

    const total = formattedItems.reduce((sum, item) => sum + item.price, 0);

    res.json({
      id: cart.id,
      items: formattedItems,
      count: formattedItems.length,
      total: Math.round(total * 100) / 100, // Zaokrąglij do 2 miejsc po przecinku
      updatedAt: cart.updatedAt,
    });
  } catch (error) {
    console.error("[GET /cart]", error);
    res.status(500).json({ error: "Internal server error" });
  }
};

/**
 * Dodaje przepis do koszyka
 * POST /api/cart/add
 */
export const addToCart = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const userId = (req as any).user.id;
    const { recipeId } = req.body;

    // Walidacja danych wejściowych
    if (!recipeId || isNaN(parseInt(recipeId))) {
      return res.status(400).json({ error: "Nieprawidłowe ID przepisu" });
    }

    const recipeIdInt = parseInt(recipeId);

    // Sprawdź czy przepis istnieje i jest dostępny
    const recipe = await prisma.recipe.findFirst({
      where: {
        id: recipeIdInt,
        visible: true,
      },
      select: {
        id: true,
        title: true,
        price: true,
        image: true,
        userId: true, // ID autora
        author: {
          select: {
            id: true,
            name: true,
            surname: true,
          },
        },
        purchasers: {
          where: { id: userId },
          select: { id: true },
        },
      },
    });

    if (!recipe) {
      return res.status(404).json({ error: "Przepis nie został znaleziony" });
    }

    // Sprawdź czy użytkownik nie jest autorem przepisu
    if (recipe.userId === userId) {
      return res
        .status(400)
        .json({ error: "Nie możesz dodać własnego przepisu do koszyka" });
    }

    // Sprawdź czy użytkownik już nie kupił tego przepisu
    if (recipe.purchasers.length > 0) {
      return res
        .status(400)
        .json({ error: "Ten przepis już został przez Ciebie kupiony" });
    }

    // Sprawdź czy przepis jest płatny
    if (Number(recipe.price) === 0) {
      return res
        .status(400)
        .json({ error: "Darmowe przepisy nie wymagają dodawania do koszyka" });
    }

    // Znajdź lub utwórz koszyk
    let cart = await prisma.cart.findUnique({
      where: { userId },
    });

    if (!cart) {
      cart = await prisma.cart.create({
        data: { userId },
      });
    }

    // Sprawdź czy przepis już jest w koszyku
    const existingItem = await prisma.cartItem.findUnique({
      where: {
        cartId_recipeId: {
          cartId: cart.id,
          recipeId: recipeIdInt,
        },
      },
    });

    if (existingItem) {
      return res.status(400).json({ error: "Przepis już jest w koszyku" });
    }

    // Dodaj przepis do koszyka
    const cartItem = await prisma.cartItem.create({
      data: {
        cartId: cart.id,
        recipeId: recipeIdInt,
      },
    });

    // Zaktualizuj timestamp koszyka
    await prisma.cart.update({
      where: { id: cart.id },
      data: { updatedAt: new Date() },
    });

    // Zwróć informacje o dodanym elemencie
    const formattedItem = {
      id: cartItem.id,
      recipeId: recipe.id,
      title: recipe.title,
      price: Number(recipe.price),
      image: recipe.image,
      author: recipe.author,
      addedAt: cartItem.addedAt.toISOString(),
    };

    res.status(201).json({
      message: "Przepis został dodany do koszyka",
      item: formattedItem,
    });
  } catch (error) {
    console.error("[POST /cart/add]", error);
    res.status(500).json({ error: "Internal server error" });
  }
};

/**
 * Usuwa przepis z koszyka
 * DELETE /api/cart/remove/:recipeId
 */
export const removeFromCart = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const userId = (req as any).user.id;
    const recipeId = parseInt(req.params.recipeId);
    console.log("removeFromCart  ", + recipeId )
    // Walidacja ID
    if (isNaN(recipeId)) {
      return res.status(400).json({ error: "Nieprawidłowe ID przepisu" });
    }

    // Znajdź koszyk użytkownika
    const cart = await prisma.cart.findUnique({
      where: { userId },
    });
    console.log("removeFromCart cart  ", + recipeId )
    if (!cart) {
      return res.status(404).json({ error: "Koszyk nie został znaleziony" });
    }

    
    const deletedItem = await prisma.cartItem.deleteMany({
      where: {
        cartId: cart.id,
        recipeId: recipeId,
      },
    });
    console.log("removeFromCart cadeletedItemrt  ", + recipeId )
    if (deletedItem.count === 0) {
      return res
        .status(404)
        .json({ error: "Przepis nie został znaleziony w koszyku" });
    }
  
    // Zaktualizuj timestamp koszyka
    await prisma.cart.update({
      where: { id: cart.id },
      data: { updatedAt: new Date() },
    });

    res.json({
      message: "Przepis został usunięty z koszyka",
      recipeId: recipeId,
    });
  } catch (error) {
    console.error("[DELETE /cart/remove/:recipeId]", error);
    res.status(500).json({ error: "Internal server error" });
  }
};

/**
 * Czyści cały koszyk
 * DELETE /api/cart/clear
 */
export const clearCart = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const userId = (req as any).user.id;

    // Znajdź koszyk użytkownika
    const cart = await prisma.cart.findUnique({
      where: { userId },
    });

    if (!cart) {
      return res.status(404).json({ error: "Koszyk nie został znaleziony" });
    }

    // Usuń wszystkie elementy z koszyka
    await prisma.cartItem.deleteMany({
      where: {
        cartId: cart.id,
      },
    });

    // Zaktualizuj timestamp koszyka
    await prisma.cart.update({
      where: { id: cart.id },
      data: { updatedAt: new Date() },
    });

    res.json({
      message: "Koszyk został wyczyszczony",
    });
  } catch (error) {
    console.error("[DELETE /cart/clear]", error);
    res.status(500).json({ error: "Internal server error" });
  }
};
