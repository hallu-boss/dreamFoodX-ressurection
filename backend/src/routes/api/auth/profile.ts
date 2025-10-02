import { Router, Request, Response, NextFunction } from "express";
import { AuthError } from "../../../utils/errors";
import { authenticate } from "../../../utils/authenticate";
import { User } from "../../../mongo/models";

const router = Router();

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

router.get("/", authenticate, getProfile);

export default router;