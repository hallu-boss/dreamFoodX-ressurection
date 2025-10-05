import { Router } from "express";
import authorize from "../middlewares/auth.middleware.js";
import { createIngredient, getPublicIngredients, getUserIngredients } from "../controllers/ingredient.controller.js";

const ingredientRouter = Router();

ingredientRouter.get('/', getPublicIngredients);

ingredientRouter.post('/', authorize, createIngredient);

ingredientRouter.get('/user/:id', authorize, getUserIngredients);

ingredientRouter.put('/:id', (req, res) => res.send({title: "UPDATE ingredient"}));

ingredientRouter.delete('/:id', (req, res) => res.send({title: "DELETE ingredient"}));

export default ingredientRouter;