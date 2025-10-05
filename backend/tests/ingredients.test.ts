import request from "supertest";
import mongoose from "mongoose";
import { describe, it, expect, beforeAll, afterAll } from "@jest/globals";
import { app } from "../src/app";
import { Ingredient, RecipeStep, User } from "../src/mongo/models";
import { generateToken } from "../src/utils/authenticate";
import { emitWarning } from "process";

const base_path = "/api/ingredients";

describe(`GET ${base_path}/public - success`, () => {
    beforeAll(async () => {
        if (mongoose.connection.readyState === 0)
            await mongoose.connect(process.env.MONGO_URI || "mongodb://localhost:27017/dreamfood_test");

        await Ingredient.create([
            { category: "Warzywa", title: "Pomidor", unit: "szt.", owner: null },
            { category: "Owoce", title: "Jabłko", unit: "kg", owner: null },
        ]);

        await Ingredient.create({
            category: "Mięso",
            title: "Kurczak",
            unit: "kg",
            owner: new mongoose.Types.ObjectId(),
        });
    });

    afterAll(async () => {
        if (mongoose.connection.db)
            await mongoose.connection.db.dropDatabase();
        await mongoose.disconnect();
    });

    it("should return 200", async () => {
        const response = await request(app)
            .get(`${base_path}/public`)

        expect(response.status).toBe(200)
        expect(Array.isArray(response.body)).toBe(true);
        expect(response.body.length).toBe(2);
        const titles = response.body.map((i: any) => i.title);
        expect(titles).toContain("Pomidor");
        expect(titles).toContain("Jabłko");
        expect(titles).not.toContain("Kurczak");
    })
})

describe(`GET ${base_path}/user - success`, () => {
  let token: string;
  let userId: string;

  beforeAll(async () => {
    if (mongoose.connection.readyState === 0) {
      await mongoose.connect(process.env.MONGO_URI || "mongodb://localhost:27017/dreamfood_test");
    }

    // Tworzymy użytkownika
    const user = await User.create({
      name: "Test",
      surname: "User",
      email: "test@example.com",
      password: "password123",
      cookingHours: 5,
    });
    userId = (user._id as string).toString();

    token = generateToken({id: userId, email: user.email});

    // Tworzymy składniki dla tego użytkownika
    await Ingredient.create([
      { category: "Warzywa", title: "Pomidor", unit: "szt.", owner: user._id },
      { category: "Owoce", title: "Jabłko", unit: "kg", owner: user._id },
    ]);

    // Składnik użyty w RecipeStep (nie do usunięcia)
    const usedIngredient = await Ingredient.create({ category: "Mięso", title: "Kurczak", unit: "kg", owner: user._id });
    await RecipeStep.create({ title: "Step1", stepType: "ADD_INGREDIENT", recipe: new mongoose.Types.ObjectId(), ingredient: usedIngredient._id });
  });

  afterAll(async () => {
    if (mongoose.connection.db) await mongoose.connection.db.dropDatabase();
    await mongoose.disconnect();
  });

  it("should return all user ingredients with deletable flag", async () => {
    const res = await request(app)
      .get(`${base_path}/user`)
      .set("Authorization", `Bearer ${token}`);

    expect(res.status).toBe(200);
    expect(Array.isArray(res.body)).toBe(true);
    expect(res.body.length).toBe(3);

    const deletableFlags = res.body.map((ing: any) => ing.deletable);
    expect(deletableFlags).toEqual(expect.arrayContaining([true, true, false]));
  });
});