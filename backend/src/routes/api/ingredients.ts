import { Router, Request, Response, NextFunction } from "express";
import { Ingredient, RecipeStep } from "../../mongo/models";
import { authenticate } from "../../utils/authenticate";

const getPublicIngredients = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const ingredients = await Ingredient.find({ owner: null });
    if (ingredients.length === 0) {
        res.status(402).json({ message: "No public ingredients" });
    }
    res.json(ingredients);
  } catch (error) {
    res.status(401).json({ message: "Could not load public ingredients" });
  }
};

const getIngredients = async (req: Request, res: Response) => {
  try {
    const userId = (req as any).user.id;

    const ingredients = await Ingredient.find({ owner: userId});

    // RecipeStep ingredient protection
    const ingredientsWithDeletable = await Promise.all(
      ingredients.map(async (ingredient) => {
        const used = await RecipeStep.exists({ ingredient: ingredient._id });
        return {
          ...ingredient.toObject(),
          deletable: !used,
        };
      })
    );

    res.json(ingredientsWithDeletable);
  } catch {
    res.status(402).json({ error: "Could not load user ingredients" });
  }
};

const addIngredient = async (req: Request, res: Response) => {
  try {
    const userId = (req as any).user.id;
    const { category, title, unit } = req.body;

    const ingredient = await Ingredient.create({
      category,
      title,
      unit,
      owner: userId,
    });

    res.status(201).json(ingredient);
  } catch {
    res.status(402).json({ error: "Could not add ingredient" });
  }
};

const router = Router();

/**
 * @swagger
 * /api/ingredients/public:
 *  get:
 *      tags:
 *          - Ingredients
 *      description: Gets a list of all ingredients that are available to all users (owner is null)
 *      responses:
 *          201:
 *              description: Retrived data successfuly
 *              content:
 *                  application/json:
 *                      schema:
 *                          type: array
 *                          items:
 *                              $ref: "#/components/schemas/IngredientPublic"
 *          401:
 *              description: Could not load public ingredients
 *      
 */
router.get("/public", getPublicIngredients);
/**
 * @swagger
 * /api/ingredients/user:
 *  get:
 *      tags: [Ingredients]
 *      description: Gets a list of user ingredients
 *      security:
 *          - bearerAuth: []
 *      responses:
 *          201:
 *              description: Retrived data successfuly
 *              content:
 *                  application/json:
 *                      schema:
 *                          type: array
 *                          items:
 *                              $ref: "#/components/schemas/IngredientUser"
 *          401:
 *              description: Could not load user ingredients
 *      
 */
router.get("/user", authenticate, getIngredients);

/**
 * @swagger
 * /api/ingredients/add:
 *  get:
 *      tags: [Ingredients]
 *      description: Adds new ingredient to user list
 *      security:
 *          - bearerAuth: []
 *      responses:
 *          201:
 *              description: Added new ingredient
 *              content:
 *                  application/json:
 *                      schema:
 *                          $ref: "#/components/schemas/IngredientAdd"
 *          402:
 *              description: Could not add ingredient
 */
router.post("/add", authenticate, addIngredient);
// router.post("/add-multiple", authenticate, addMultipleIngredients);

export default router;