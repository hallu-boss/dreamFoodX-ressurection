import { Router, Request, Response, NextFunction } from "express";
import { AuthError } from "../../../utils/errors";
import { authenticate } from "../../../utils/authenticate";
import { User } from "../../../mongo/models";

const router = Router();

/**
 * @swagger
 * components:
 *   schemas:
 *     ProfileSuccess:
 *       type: object
 *       properties:
 *         id:
 *           type: string
 *           example: "652f1a0e3a6f2b001f43c1a7"
 *         name:
 *           type: string
 *           example: "Jan"
 *         surname:
 *           type: string
 *           example: "Kowalski"
 *         email:
 *           type: string
 *           example: "jan.kowalski@example.com"
 *         cookingHours:
 *           type: integer
 *           example: 10
 *         recipes:
 *           type: array
 *           items:
 *             type: object
 *             example: []
 *         purchasedRecipes:
 *           type: array
 *           example: []
 *         ingredients:
 *           type: array
 *           example: []
 */


export const getProfile = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const userId = (req as any).user.id;

    const user = await User.findById(userId)
      .select("name surname email cookingHours recipes purchasedRecipes ingredients")
      .populate({
        path: "recipes",
        select: "title visible category price image",
      })
      .populate({
        path: "purchasedRecipes",
        select: "title category image",
      })
      .populate({
        path: "ingredients",
        select: "category title unit",
      });

    if (!user) {
      throw new AuthError("User not found", 404);
    }

    res.status(200).json(user);
  } catch (error) {
    next(error);
  }
};

/**
 * @swagger
 * /api/auth/profile:
 *   get:
 *     summary: Gets profile of user
 *     tags: [Auth]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Returns user profile data
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/ProfileSuccess'
 *       401:
 *         description: No or wrong token send
 *       404:
 *         description: User not found
 *       500:
 *         description: Internal server error
 */
router.get("/", authenticate, getProfile);

export default router;