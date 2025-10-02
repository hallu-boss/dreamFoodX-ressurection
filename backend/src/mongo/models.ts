import mongoose, { Schema, Document, Model } from "mongoose";
import bcrypt from "bcrypt";

/* =========================
   USER
========================= */
export interface IUser extends Document {
  name: string;
  surname: string;
  email: string;
  password: string;
  cookingHours: number;
  newsletter: boolean;
  recipes: mongoose.Types.ObjectId[];
  purchasedRecipes: mongoose.Types.ObjectId[];
  ingredients: mongoose.Types.ObjectId[];
  cart?: mongoose.Types.ObjectId;
  orders: mongoose.Types.ObjectId[];
  comparePassword(candidatePassword: string): Promise<boolean>;
}

const UserSchema: Schema<IUser> = new Schema(
  {
    name: { type: String, required: true },
    surname: { type: String, required: true },
    email: { type: String, required: true, unique: true },
    password: { type: String, required: true },
    cookingHours: { type: Number, default: 0 },
    newsletter: { type: Boolean, default: false },
    recipes: [{ type: Schema.Types.ObjectId, ref: "Recipe" }],
    purchasedRecipes: [{ type: Schema.Types.ObjectId, ref: "Recipe" }],
    ingredients: [{ type: Schema.Types.ObjectId, ref: "Ingredient" }],
    cart: { type: Schema.Types.ObjectId, ref: "Cart" },
    orders: [{ type: Schema.Types.ObjectId, ref: "Order" }],
  },
  { timestamps: true }
);

UserSchema.pre<IUser>("save", async function (next) {
  if (!this.isModified("password")) return next();
  const salt = await bcrypt.genSalt(10);
  this.password = await bcrypt.hash(this.password, salt);
  next();
});

UserSchema.methods.comparePassword = function (candidatePassword: string) {
  return bcrypt.compare(candidatePassword, this.password);
};

export const User: Model<IUser> = mongoose.model<IUser>("User", UserSchema);

/* =========================
   RECIPE
========================= */
export interface IRecipe extends Document {
  title: string;
  description: string;
  category: string;
  price: number;
  visible: boolean;
  image?: string;
  user: mongoose.Types.ObjectId;
  steps: mongoose.Types.ObjectId[];
  reviews: mongoose.Types.ObjectId[];
  purchasers: mongoose.Types.ObjectId[];
}

const RecipeSchema: Schema<IRecipe> = new Schema(
  {
    title: { type: String, required: true },
    description: { type: String, required: true },
    category: { type: String, required: true },
    price: { type: Number, required: true },
    visible: { type: Boolean, default: true },
    image: { type: String },
    user: { type: Schema.Types.ObjectId, ref: "User", required: true },
    steps: [{ type: Schema.Types.ObjectId, ref: "RecipeStep" }],
    reviews: [{ type: Schema.Types.ObjectId, ref: "Review" }],
    purchasers: [{ type: Schema.Types.ObjectId, ref: "User" }],
  },
  { timestamps: true }
);

export const Recipe: Model<IRecipe> = mongoose.model<IRecipe>("Recipe", RecipeSchema);

/* =========================
   RECIPE STEP
========================= */
export enum StepType {
  ADD_INGREDIENT = "ADD_INGREDIENT",
  COOKING = "COOKING",
  DESCRIPTION = "DESCRIPTION",
}

export interface IRecipeStep extends Document {
  title: string;
  stepType: StepType;
  recipe: mongoose.Types.ObjectId;
  ingredient?: mongoose.Types.ObjectId;
  amount?: number;
  time?: string;
  temperature?: number;
  mixSpeed?: number;
  description?: string;
}

const RecipeStepSchema: Schema<IRecipeStep> = new Schema(
  {
    title: { type: String, required: true },
    stepType: { type: String, enum: Object.values(StepType), required: true },
    recipe: { type: Schema.Types.ObjectId, ref: "Recipe", required: true },
    ingredient: { type: Schema.Types.ObjectId, ref: "Ingredient" },
    amount: { type: Number },
    time: { type: String },
    temperature: { type: Number },
    mixSpeed: { type: Number },
    description: { type: String },
  },
  { timestamps: true }
);

export const RecipeStep: Model<IRecipeStep> = mongoose.model<IRecipeStep>("RecipeStep", RecipeStepSchema);

/* =========================
   INGREDIENT
========================= */
export interface IIngredient extends Document {
  title: string;
  unit: string;
  category: string;
  owner?: mongoose.Types.ObjectId;
}

const IngredientSchema: Schema<IIngredient> = new Schema(
  {
    title: { type: String, required: true },
    unit: { type: String, required: true },
    category: { type: String, required: true },
    owner: { type: Schema.Types.ObjectId, ref: "User" },
  },
  { timestamps: true }
);

export const Ingredient: Model<IIngredient> = mongoose.model<IIngredient>("Ingredient", IngredientSchema);

/* =========================
   REVIEW
========================= */
export interface IReview extends Document {
  user: mongoose.Types.ObjectId;
  recipe: mongoose.Types.ObjectId;
  rating: number;
  opinion: string;
}

const ReviewSchema: Schema<IReview> = new Schema(
  {
    user: { type: Schema.Types.ObjectId, ref: "User", required: true },
    recipe: { type: Schema.Types.ObjectId, ref: "Recipe", required: true },
    rating: { type: Number, required: true },
    opinion: { type: String, required: true },
  },
  { timestamps: true }
);

export const Review: Model<IReview> = mongoose.model<IReview>("Review", ReviewSchema);

/* =========================
   CART
========================= */
export interface ICart extends Document {
  user: mongoose.Types.ObjectId;
  items: mongoose.Types.ObjectId[];
}

const CartSchema: Schema<ICart> = new Schema(
  {
    user: { type: Schema.Types.ObjectId, ref: "User", unique: true, required: true },
    items: [{ type: Schema.Types.ObjectId, ref: "CartItem" }],
  },
  { timestamps: true }
);

export const Cart: Model<ICart> = mongoose.model<ICart>("Cart", CartSchema);

/* =========================
   CART ITEM
========================= */
export interface ICartItem extends Document {
  cart: mongoose.Types.ObjectId;
  recipe: mongoose.Types.ObjectId;
  addedAt: Date;
}

const CartItemSchema: Schema<ICartItem> = new Schema({
  cart: { type: Schema.Types.ObjectId, ref: "Cart", required: true },
  recipe: { type: Schema.Types.ObjectId, ref: "Recipe", required: true },
  addedAt: { type: Date, default: Date.now },
});

export const CartItem: Model<ICartItem> = mongoose.model<ICartItem>("CartItem", CartItemSchema);

/* =========================
   ORDER
========================= */
export enum OrderStatus {
  PENDING = "PENDING",
  COMPLETED = "COMPLETED",
  FAILED = "FAILED",
  CANCELLED = "CANCELLED",
}

export enum PaymentMethod {
  STRIPE = "STRIPE",
  MOCK = "MOCK",
  PAYPAL = "PAYPAL",
}

export interface IOrder extends Document {
  user: mongoose.Types.ObjectId;
  status: OrderStatus;
  total: number;
  paymentMethod: PaymentMethod;
  paymentIntentId?: string;
  items: mongoose.Types.ObjectId[];
}

const OrderSchema: Schema<IOrder> = new Schema(
  {
    user: { type: Schema.Types.ObjectId, ref: "User", required: true },
    status: { type: String, enum: Object.values(OrderStatus), default: OrderStatus.PENDING },
    total: { type: Number, required: true },
    paymentMethod: { type: String, enum: Object.values(PaymentMethod), required: true },
    paymentIntentId: { type: String },
    items: [{ type: Schema.Types.ObjectId, ref: "OrderItem" }],
  },
  { timestamps: true }
);

export const Order: Model<IOrder> = mongoose.model<IOrder>("Order", OrderSchema);

/* =========================
   ORDER ITEM
========================= */
export interface IOrderItem extends Document {
  order: mongoose.Types.ObjectId;
  recipe: mongoose.Types.ObjectId;
  price: number;
  title: string;
}

const OrderItemSchema: Schema<IOrderItem> = new Schema({
  order: { type: Schema.Types.ObjectId, ref: "Order", required: true },
  recipe: { type: Schema.Types.ObjectId, ref: "Recipe", required: true },
  price: { type: Number, required: true },
  title: { type: String, required: true },
});

export const OrderItem: Model<IOrderItem> = mongoose.model<IOrderItem>("OrderItem", OrderItemSchema);
