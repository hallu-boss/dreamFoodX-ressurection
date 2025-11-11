import express from "express";
import { PrismaClient } from "@prisma/client";
import { authenticate } from "../middleware/authenticate";
import {
  addIngredient,
  addMultipleIngredients,
  deleteIngredient,
  getIngredients,
  getPublicIngredients,
  updateIngredient,
} from "../controllers/ingredients.controller";

const router = express.Router();
const prisma = new PrismaClient();

router.get("/all", getPublicIngredients);

router.post("/add", authenticate, addIngredient);

router.post("/add-multiple", authenticate, addMultipleIngredients);

router.get("/user", authenticate, getIngredients);

router.patch("/:id", authenticate, updateIngredient);
router.delete("/:id", authenticate, deleteIngredient);

export default router;
