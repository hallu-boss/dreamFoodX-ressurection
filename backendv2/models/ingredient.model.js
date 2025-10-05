import mongoose from "mongoose";

const IngredientSchema = mongoose.Schema({
    title: {
        type: String,
        required: [true, 'Ingredient title is required']
    },
    unit: {
        type: String,
        required: [true, 'Ingredient unit is required']
    },
    category: {
        type: String,
        required: [true, 'Ingredient category is required']
    },
    user: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        index: true
    }
}, {timestamps: true});

const Ingredient = mongoose.model('Ingredient', IngredientSchema);

export default Ingredient;