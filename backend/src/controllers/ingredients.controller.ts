import { PrismaClient } from "@prisma/client";
import { Request, Response, NextFunction } from "express";

const prisma = new PrismaClient();

export const getPublicIngredients = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const ingredients = await prisma.ingredient.findMany({
      where: {
        ownerId: null,
      },
    });
    res.json(ingredients);
  } catch (error) {
    console.error("Błąd podczas pobierania składników:", error);
    res.status(500).json({ error: "Nie udało się pobrać składników" });
  }
};

export const addIngredient = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const userId = (req as any).user.id;
    

    const { category, title, unit } = req.body;
    console.log("addIngredient: ", userId, " ", category, title, unit )

    try {
      const ingredient = await prisma.ingredient.create({
        data: {
          category,
          title,
          unit,
          ownerId: userId,
        },
      });
      return res.status(201).json(ingredient);
    } catch (error) {
      return res.status(500).json({ error: "Nie udało się dodać składnika" });
    }
  } catch (error) {
    next(error);
  }
};

export const getIngredients = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const userId = (req as any).user.id;

    try {
      const ingredients = await prisma.ingredient.findMany({
        where: {
          ownerId: userId,
        },
        include: {
          steps: {
            select: {
              id: true,
            },
          },
        },
      });

      // Dodaj pole deletable do każdego składnika
      const ingredientsWithDeletable = ingredients.map((ingredient) => ({
        id: ingredient.id,
        title: ingredient.title,
        unit: ingredient.unit,
        category: ingredient.category,
        ownerId: ingredient.ownerId,
        deletable: ingredient.steps.length === 0, // false jeśli składnik jest używany w przepisach
      }));

      res.json(ingredientsWithDeletable);
    } catch (error) {
      return res
        .status(500)
        .json({ error: "Nie udało się pobrać listy składników" });
    }
  } catch (error) {
    next(error);
  }
};

export const updateIngredient = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const userId = (req as any).user.id;
    const ingredientId = parseInt(req.params.id);

    const { category, title, unit } = req.body;

    console.log("updateIngredient: ", userId, " ", category, title, unit )

    // Walidacja ID
    if (isNaN(ingredientId)) {
      return res.status(400).json({ error: "Nieprawidłowe ID składnika" });
    }

    // Walidacja danych wejściowych
    if (!category || !title || !unit) {
      return res.status(400).json({
        error: "Wszystkie pola (category, title, unit) są wymagane",
      });
    }

    try {
      // Sprawdź czy składnik należy do użytkownika
      const existingIngredient = await prisma.ingredient.findFirst({
        where: {
          id: ingredientId,
          ownerId: userId,
        },
      });

      if (!existingIngredient) {
        return res.status(404).json({
          error:
            "Składnik nie został znaleziony lub nie masz uprawnień do jego edycji",
        });
      }

      // Aktualizuj składnik
      const updatedIngredient = await prisma.ingredient.update({
        where: {
          id: ingredientId,
        },
        data: {
          category,
          title,
          unit,
        },
      });

      return res.status(200).json(updatedIngredient);
    } catch (error) {
      return res
        .status(500)
        .json({ error: "Nie udało się zaktualizować składnika" });
    }
  } catch (error) {
    next(error);
  }
};

export const deleteIngredient = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const userId = (req as any).user.id;
    const ingredientId = parseInt(req.params.id);

    // Walidacja ID
    if (isNaN(ingredientId)) {
      return res.status(400).json({ error: "Nieprawidłowe ID składnika" });
    }

    try {
      // Sprawdź czy składnik należy do użytkownika
      const existingIngredient = await prisma.ingredient.findFirst({
        where: {
          id: ingredientId,
          ownerId: userId,
        },
      });

      if (!existingIngredient) {
        return res.status(404).json({
          error:
            "Składnik nie został znaleziony lub nie masz uprawnień do jego usunięcia",
        });
      }

      // Sprawdź czy składnik jest używany w jakichś przepisach
      const usedInRecipes = await prisma.recipeStep.findFirst({
        where: {
          ingredientId: ingredientId,
        },
      });

      if (usedInRecipes) {
        return res.status(400).json({
          error: "Nie można usunąć składnika, który jest używany w przepisach",
        });
      }

      // Usuń składnik
      await prisma.ingredient.delete({
        where: {
          id: ingredientId,
        },
      });

      return res.status(200).json({
        message: "Składnik został pomyślnie usunięty",
        id: ingredientId,
      });
    } catch (error) {
      return res.status(500).json({ error: "Nie udało się usunąć składnika" });
    }
  } catch (error) {
    next(error);
  }
};

export const addMultipleIngredients = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const userId = (req as any).user.id;
    const { ingredients } = req.body;

    // Walidacja danych wejściowych
    if (!Array.isArray(ingredients) || ingredients.length === 0) {
      return res.status(400).json({
        error: "Pole 'ingredients' musi być niepustą tablicą",
      });
    }

    // Walidacja każdego składnika
    for (let i = 0; i < ingredients.length; i++) {
      const ingredient = ingredients[i];
      if (!ingredient.category || !ingredient.title || !ingredient.unit) {
        return res.status(400).json({
          error: `Składnik ${
            i + 1
          }: Wszystkie pola (category, title, unit) są wymagane`,
        });
      }
    }

    try {
      // Dodaj wszystkie składniki w jednej transakcji
      const createdIngredients = await prisma.$transaction(
        ingredients.map((ingredient) =>
          prisma.ingredient.create({
            data: {
              category: ingredient.category,
              title: ingredient.title,
              unit: ingredient.unit,
              ownerId: userId,
            },
          })
        )
      );

      return res.status(201).json({
        message: `Pomyślnie dodano ${createdIngredients.length} składników`,
        ingredients: createdIngredients,
      });
    } catch (error) {
      return res.status(500).json({ error: "Nie udało się dodać składników" });
    }
  } catch (error) {
    next(error);
  }
};
