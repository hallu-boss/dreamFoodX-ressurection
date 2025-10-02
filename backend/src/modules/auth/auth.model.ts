import mongoose, { Schema, Document, Model } from "mongoose";
import bcrypt from "bcrypt";

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

    // Relacje
    recipes: [{ type: Schema.Types.ObjectId, ref: "Recipe" }],
    purchasedRecipes: [{ type: Schema.Types.ObjectId, ref: "Recipe" }],
    ingredients: [{ type: Schema.Types.ObjectId, ref: "Ingredient" }],
    cart: { type: Schema.Types.ObjectId, ref: "Cart" },
    orders: [{ type: Schema.Types.ObjectId, ref: "Order" }],
  },
  {
    timestamps: true,
  }
);

// pre-save hook do hash'owania hasła
UserSchema.pre<IUser>("save", async function (next) {
  if (!this.isModified("password")) return next();
  const salt = await bcrypt.genSalt(10);
  this.password = await bcrypt.hash(this.password, salt);
  next();
});

// metoda instancyjna do porównywania hasła
UserSchema.methods.comparePassword = function (candidatePassword: string) {
  return bcrypt.compare(candidatePassword, this.password);
};

const User: Model<IUser> = mongoose.model<IUser>("User", UserSchema);

export default User;
