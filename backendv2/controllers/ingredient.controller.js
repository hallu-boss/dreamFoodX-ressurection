import mongoose from "mongoose";
import Ingredient from "../models/ingredient.model.js";

export const createIngredient = async (req, res, next) => {
    const session = await mongoose.startSession();
    session.startTransaction();
    try {
        const { title, unit, category } = req.body;
        if (!title || !unit || !category) {
            const error = new Error('Title, unit and category are required');
            error.statusCode = 400;
            throw error;
        }

        const newIngredient = await Ingredient.create([{ title, unit, category, user: req.user.id }], { session });

        await session.commitTransaction();
        res.status(201).json({ 
            success: true, 
            message: 'Ingredient created successfully',
            data: newIngredient[0] 
        });
    } catch (error) {
        await session.abortTransaction();
        next(error);
    } finally {
        session.endSession();
    }
}

export const getPublicIngredients = async (req, res, next) => {
    try {
        const ingredients = await Ingredient.find({ user: null });
        res.status(200).json({
            success: true,
            message: 'Public ingredients fetched successfully',
            data: ingredients
        });
    } catch (error) {
        next(error);
    }
}

export const getUserIngredients = async (req, res, next) => {
    try {
        const { id } = req.params;
        if (id !== req.user.id) {
            const error = new Error('Forbidden: You can only access your own user data');
            error.statusCode = 403;
            throw error;
        }

        const ingredients = await Ingredient.find({ user: id });
        res.status(200).json({
            success: true,
            message: 'User ingredients fetched successfully',
            data: ingredients
        });
    } catch (error) {
        next(error);
    }
}