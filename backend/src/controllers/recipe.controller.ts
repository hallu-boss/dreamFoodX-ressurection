import { Request, Response, NextFunction } from 'express';
import { PrismaClient, RecipeStep, StepType } from '@prisma/client';
import { ValidationError } from '../utils/errors';
import { Decimal } from '@prisma/client/runtime/library';

const prisma = new PrismaClient();

interface NewRecipeStep {
  title: string;
  stepType: 'ADD_INGREDIENT' | 'COOKING' | 'DESCRIPTION';

  ingredientId?: number;
  amount?: number;

  time?: string;
  temperature?: number;
  mixSpeed?: number;

  description?: string;
}

interface NewRecipeInfo {
  title: string;
  description: string;
  category: string;
  visible: boolean;
  price: number;
  steps: NewRecipeStep[];
  // Brak pola image w interfejsie, będzie obsługiwane osobno
}

/**
 * Pobiera okładki przepisów do wyświetlenia na stronie głównej
 * GET /api/recipe/covers
 */
export const getRecipeCovers = async (
  req: Request,
  res: Response,
  next: NextFunction,
) => {
  try {
    // Pobierz parametry zapytania
    const page = Math.max(1, parseInt(req.query.page as string) || 1);
    const limit = Math.min(
      50,
      Math.max(1, parseInt(req.query.limit as string) || 12),
    );
    const type = req.query.type as string; // 'featured', 'new', 'popular', 'category'
    const category = req.query.category as string;
    const search = req.query.search as string;

    // Sprawdź czy użytkownik jest zalogowany (opcjonalne - bez middleware authenticate)
    const authHeader = req.headers.authorization;
    let userId: number | undefined;

    if (authHeader && authHeader.startsWith('Bearer ')) {
      try {
        const token = authHeader.split(' ')[1];
        const jwt = require('jsonwebtoken');
        const decoded = jwt.verify(
          token,
          process.env.JWT_SECRET || 'fallback-secret',
        ) as any;
        userId = decoded.id;
      } catch (error) {
        // Token nieprawidłowy - ignoruj i kontynuuj bez userId
        userId = undefined;
      }
    }

    // Oblicz offset dla paginacji
    const offset = (page - 1) * limit;

    // Zbuduj warunki where na podstawie typu
    const whereConditions: any = {
      visible: true, // Tylko widoczne przepisy
    };

    // Daty dla filtrowania
    const now = new Date();
    const oneWeekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
    const twoWeeksAgo = new Date(now.getTime() - 14 * 24 * 60 * 60 * 1000);

    // Filtrowanie według typu
    switch (type) {
      case 'new':
        // Nowe - dodane w przeciągu ostatniego tygodnia
        whereConditions.createdAt = {
          gte: oneWeekAgo,
        };
        break;

      case 'featured':
        // Wyróżnione - będą filtrowane po pobraniu danych (średnia ocena > 3 + czas < 2 tygodnie)
        whereConditions.createdAt = {
          gte: twoWeeksAgo,
        };
        break;

      case 'popular':
        // Popularne - będą filtrowane po pobraniu danych (średnia ocena >= 4)
        // Nie dodajemy warunków where, filtrujemy po obliczeniu średniej
        break;

      case 'free':
        whereConditions.price = 0;
        break;

      case 'category':
        // Kategoria - wymaga podania parametru category
        if (category) {
          const categoryMapping: Record<string, string> = {
            Obiady: 'obiad',
            Desery: 'deser',
            Śniadania: 'sniadanie',
            Przekąski: 'przekąska',
            Napoje: 'napój',
            Dodatki: 'dodatek',
          };

          const dbCategory =
            categoryMapping[category] || category.toLowerCase();

          whereConditions.category = {
            equals: dbCategory,
            mode: 'insensitive', // Case-insensitive comparison
          };
        } else {
          return res.status(400).json({
            error: 'Parametr category jest wymagany dla typu category',
          });
        }
        break;
    }

    // Wyszukiwanie po tytule
    if (search) {
      whereConditions.OR = [
        { title: { contains: search, mode: 'insensitive' } },
        { description: { contains: search, mode: 'insensitive' } },
      ];
    }

    // Pobierz przepisy z wszystkimi potrzebnymi danymi
    const recipes = await prisma.recipe.findMany({
      where: whereConditions,
      select: {
        id: true,
        title: true,
        description: true,
        category: true,
        price: true,
        image: true,
        createdAt: true,
        author: {
          select: {
            id: true,
            name: true,
            surname: true,
          },
        },
        reviews: {
          select: {
            rating: true,
          },
        },
        steps: {
          select: {
            stepType: true,
            time: true,
            ingredient: {
              select: {
                id: true,
              },
            },
          },
        },
        purchasers: userId
          ? {
              where: {
                id: userId,
              },
              select: {
                id: true,
              },
            }
          : false,
      },
      orderBy: { createdAt: 'desc' }, // Domyślnie sortuj po dacie utworzenia
    });

    // Przetwórz dane dla każdego przepisu
    const processedRecipes = recipes.map((recipe) => {
      // Oblicz średnią ocenę
      const averageRating =
        recipe.reviews.length > 0
          ? recipe.reviews.reduce((sum, review) => sum + review.rating, 0) /
            recipe.reviews.length
          : 0;

      // Oblicz przybliżony czas gotowania na podstawie kroków
      const cookingTimes = recipe.steps
        .filter((step) => step.stepType === 'COOKING' && step.time)
        .map((step) => {
          // Załóżmy że czas jest w formacie "15 min" lub "1h 30min"
          const timeStr = step.time!;
          const minutesMatch = timeStr.match(/(\d+)\s*min/);
          const hoursMatch = timeStr.match(/(\d+)\s*h/);

          let totalMinutes = 0;
          if (hoursMatch) totalMinutes += parseInt(hoursMatch[1]) * 60;
          if (minutesMatch) totalMinutes += parseInt(minutesMatch[1]);

          return totalMinutes;
        });

      const totalCookingTime = cookingTimes.reduce(
        (sum, time) => sum + time,
        0,
      );
      const cookingTime =
        totalCookingTime > 0
          ? totalCookingTime >= 60
            ? `${Math.floor(totalCookingTime / 60)}h ${
                totalCookingTime % 60
              }min`
            : `${totalCookingTime} min`
          : 'Brak danych';

      // Policz unikalne składniki
      const uniqueIngredients = new Set(
        recipe.steps
          .filter((step) => step.ingredient)
          .map((step) => step.ingredient!.id),
      );

      // Formatuj datę utworzenia do DD.MM.YYYY
      const createdDate = new Date(recipe.createdAt);
      const formattedDate = createdDate.toLocaleDateString('pl-PL', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
      });

      // Sprawdź status dla zalogowanego użytkownika
      const isPurchased = userId ? recipe.purchasers.length > 0 : false;
      const isOwned = userId ? recipe.author.id === userId : false;

      return {
        id: recipe.id,
        title: recipe.title,
        description: recipe.description,
        category: recipe.category,
        price: Number(recipe.price), // Konwersja Decimal na number
        image: recipe.image,
        author: recipe.author,
        createdAt: formattedDate, // Format DD.MM.YYYY
        averageRating: Math.round(averageRating * 10) / 10, // Zaokrąglij do 1 miejsca po przecinku
        reviewsCount: recipe.reviews.length,
        cookingTime,
        ingredientsCount: uniqueIngredients.size,
        ...(userId && {
          isPurchased,
          isOwned,
        }),
      };
    });

    // Filtruj według typu po obliczeniu średnich ocen
    let filteredRecipes = processedRecipes;

    switch (type) {
      case 'featured':
        // Wyróżnione: średnia ocena > 3 oraz czas dodania < 2 tygodnie
        filteredRecipes = processedRecipes.filter(
          (recipe) =>
            recipe.averageRating > 3 &&
            new Date(recipe.createdAt.split('.').reverse().join('-')) >=
              twoWeeksAgo,
        );
        // Sortuj według oceny malejąco
        filteredRecipes.sort((a, b) => b.averageRating - a.averageRating);
        break;

      case 'popular':
        // Popularne: ocena >= 4
        filteredRecipes = processedRecipes.filter(
          (recipe) => recipe.averageRating >= 4,
        );
        // Sortuj według oceny malejąco, potem według liczby recenzji
        filteredRecipes.sort((a, b) => {
          if (b.averageRating !== a.averageRating) {
            return b.averageRating - a.averageRating;
          }
          return b.reviewsCount - a.reviewsCount;
        });
        break;

      case 'new':
        // Nowe: sortuj według daty utworzenia malejąco (najnowsze pierwsze)
        filteredRecipes.sort((a, b) => {
          const dateA = new Date(a.createdAt.split('.').reverse().join('-'));
          const dateB = new Date(b.createdAt.split('.').reverse().join('-'));
          return dateB.getTime() - dateA.getTime();
        });
        break;

      case 'category':
        // Kategoria: sortuj według oceny malejąco
        filteredRecipes.sort((a, b) => b.averageRating - a.averageRating);
        break;

      default:
        // Domyślnie sortuj według daty utworzenia malejąco
        filteredRecipes.sort((a, b) => {
          const dateA = new Date(a.createdAt.split('.').reverse().join('-'));
          const dateB = new Date(b.createdAt.split('.').reverse().join('-'));
          return dateB.getTime() - dateA.getTime();
        });
        break;
    }

    // Zastosuj paginację do przefiltrowanych wyników
    const totalFiltered = filteredRecipes.length;
    const paginatedRecipes = filteredRecipes.slice(offset, offset + limit);

    // Oblicz dane paginacji
    const totalPages = Math.ceil(totalFiltered / limit);

    res.json({
      recipes: paginatedRecipes,
      pagination: {
        total: totalFiltered,
        page,
        limit,
        totalPages,
      },
      type: type || 'all',
    });
  } catch (error) {
    console.error('[GET /recipe/covers]', error);
    res.status(500).json({ error: 'Internal server error' });
  }
};

export const getRecipe = async (
  req: Request,
  res: Response,
  next: NextFunction,
) => {
  const recipeId = parseInt(req.params.id);
  if (isNaN(recipeId))
    return res.status(400).json({ error: 'Invalid recipe ID' });

  try {
    const recipe = await prisma.recipe.findUniqueOrThrow({
      where: { id: recipeId },
      select: {
        id: true,
        createdAt: true,
        author: {
          select: {
            id: true,
            name: true,
            surname: true,
          },
        },
        title: true,
        description: true,
        category: true,
        price: true,
        image: true,
        steps: {
          select: {
            id: true,
            title: true,
            stepType: true,
            description: true,
            ingredient: {
              select: {
                title: true,
                unit: true,
              },
            },
            amount: true,
            time: true,
            temperature: true,
            mixSpeed: true,
          },
        },
        reviews: true,
        purchasers: {
          select: {
            id: true,
          },
        },
      },
    });

    if (!recipe) {
      return res.status(404).json({ error: 'Recipe not found' });
    }

    const userId = (req as any).user.id;

    const isAuthor = recipe.author.id === userId;
    const hasPurchased = recipe.purchasers.some((p) => p.id === userId);
    const isFree = Number(recipe.price) === 0;

    const baseRecipeData = {
      id: recipe.id,
      createdAt: recipe.createdAt,
      author: recipe.author,
      title: recipe.title,
      category: recipe.category,
      price: recipe.price,
      image: recipe.image,
    };

    if (isAuthor || hasPurchased || isFree) {
      return res.json({
        ...recipe,
        permission: true,
      });
    }

    return res.json({
      ...baseRecipeData,
      permission: false,
    });
  } catch (error) {
    console.error('[GET /recipe/:id]', error);
    res.status(500).json({ error: 'Internal server error' });
  }
};

export const createRecipe = async (
  req: Request,
  res: Response,
  next: NextFunction,
) => {
  try {
    const userId = (req as any).user.id;

    // Parsowanie danych przepisu z JSON
    const recipeData: NewRecipeInfo = req.body.recipeData
      ? JSON.parse(req.body.recipeData)
      : req.body;

    const { title, description, category, visible, price, steps } = recipeData;

    // Sprawdź czy wszystkie składniki istnieją
    for (const step of steps) {
      if (step.stepType !== 'ADD_INGREDIENT') continue;
      const ingredient = await prisma.ingredient.findUnique({
        where: { id: step.ingredientId },
      });
      if (!ingredient) {
        throw new ValidationError(
          `Składnik o ID ${step.ingredientId} nie istnieje`,
          400,
        );
      }
    }

    let imageUrl = '';

    // Obsługa pliku obrazu, jeśli istnieje
    if (req.file) {
      // Utwórz FormData do wysłania do Cloudinary
      const fileFormData = new FormData();

      // Konwersja bufora pliku na Blob
      const fileBlob = new Blob([new Uint8Array(req.file.buffer)], { type: req.file.mimetype });

      fileFormData.append('file', fileBlob, req.file.originalname);
      fileFormData.append('upload_preset', 'dreamFoodX-images');
      fileFormData.append('api_key', process.env.CLOUDINARY_API_KEY || '123');

      const imgRes = await fetch(
        'https://api.cloudinary.com/v1_1/dco9zum8l/image/upload',
        {
          method: 'POST',
          body: fileFormData,
        },
      ).then((r) => r.json());

      if (imgRes.secure_url) {
        imageUrl = imgRes.secure_url;
      } else {
        throw new Error('Failed to upload image to Cloudinary');
      }
    }

    // Utwórz przepis z krokami
    const newRecipe = await prisma.recipe.create({
      data: {
        title,
        description,
        category,
        price: visible ? price : 0.0,
        visible,
        image: imageUrl,
        userId,
        steps: {
          create: steps,
        },
      },
      include: {
        steps: true,
      },
    });

    res.status(201).json({
      message: 'Przepis utworzony pomyślnie',
      recipe: {
        ...newRecipe,
        price: Number(newRecipe.price), // Konwersja Decimal na number
      },
    });
  } catch (error) {
    next(error);
  }
};


export const createRecipeReviews = async (
  req: Request,
  res: Response,
  next: NextFunction,
) => {
  try {
    const { recipeId, rating, opinion, userId } = req.body;

    // Walidacja podstawowa
    if (!recipeId || !userId || !rating) {
      throw new ValidationError('Brakuje wymaganych pól: recipeId, userId lub rating', 400);
    }

    if (rating < 1 || rating > 5) {
      throw new ValidationError('Ocena musi być w zakresie 1–5', 400);
    }

    // Sprawdź, czy przepis istnieje
    const recipe = await prisma.recipe.findUnique({
      where: { id: recipeId },
    });

    if (!recipe) {
      throw new ValidationError(`Przepis o ID ${recipeId} nie istnieje`, 404);
    }

    // Sprawdź, czy użytkownik już dodał opinię
    const existingReview = await prisma.review.findFirst({
      where: { recipeId, userId },
    });

    let review;

    if (existingReview) {
      // aktualizuj istniejącą opinię
      review = await prisma.review.update({
        where: { id: existingReview.id },
        data: {
          rating,
          opinion,
        },
      });
    } else {
      // utwórz nową opinię
      review = await prisma.review.create({
        data: {
          recipeId,
          userId,
          rating,
          opinion,
        },
      });
    }

    res.status(existingReview ? 200 : 201).json({
      message: existingReview
        ? 'Opinia zaktualizowana pomyślnie'
        : 'Opinia dodana pomyślnie',
      review,
    });
  } catch (error) {
    next(error);
  }
};

export const getRecipeReview = async (
  req: Request,
  res: Response,
  next: NextFunction,
) => {
  try {
    console.log("getRecipeReview: ")
    const recipeId = Number(req.query.recipeId);
    const userId = Number(req.query.userId);
  console.log("getRecipeReview: " + recipeId + "   " + userId)
    if (!recipeId || !userId) {
      throw new ValidationError('Brakuje wymaganych pól: recipeId lub userId', 400);
    }

    // Sprawdź, czy istnieje opinia użytkownika dla tego przepisu
    const review = await prisma.review.findFirst({
      where: { recipeId, userId },
    });

    if (!review) {
      // Jeśli brak opinii — zwróć puste dane lub rating = 0
      return res.status(200).json({
        recipeId,
        userId,
        rating: 0,
        opinion: "",
      });
    }

    // Zwróć ocenę
    const response = {
      recipeId: review.recipeId,
      userId: review.userId,
      rating: review.rating,
      opinion:  review.opinion
    };

    res.status(200).json(response);
  } catch (error) {
    next(error);
  }
};


export const hasAccesToRecipe = async (
  req: Request,
  res: Response,
  next: NextFunction,
) => {
  try {
    const recipeId = parseInt(req.params.id);
    const userId = (req as any).user.id;

    const recipe = await prisma.recipe.findUniqueOrThrow({
      where: { id: recipeId },
      select: {
        author: {
          select: {
            id: true,
          },
        },
        price: true,
        purchasers: {
          select: {
            id: true,
          },
        },
      },
    });

    const isAuthor = recipe.author.id === userId;
    const hasPurchased = recipe.purchasers.some((p) => p.id === userId);
    const isFree = Number(recipe.price) === 0.0;

    (req as any).hasPermission = isAuthor || hasPurchased || isFree;
    next();
  } catch (error) {
    next(error);
  }
};

export const getPlayRecipeSteps = async (
  req: Request,
  res: Response,
  next: NextFunction,
) => {
  const recipeId = parseInt(req.params.id);
  if (isNaN(recipeId))
    return res.status(400).json({ error: 'Invalid recipe ID' });

  try {
    const recipe = await prisma.recipe.findUniqueOrThrow({
      where: { id: recipeId },
      select: {
        steps: {
          select: {
            id: true,
            title: true,
            stepType: true,
            description: true,
            ingredient: {
              select: {
                title: true,
                unit: true,
              },
            },
            amount: true,
            time: true,
            temperature: true,
            mixSpeed: true,
          },
        },
      },
    });

    return res.json(recipe);
  } catch (error) {
    console.error('[GET /recipe/play/:id]', error);
    res.status(500).json({ error: 'Internal server error' });
  }
};
