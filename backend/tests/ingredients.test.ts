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

describe(`POST ${base_path}/add - success`, () => {
  let token: string;
  let userId: string;

  beforeAll(async () => {
    if (mongoose.connection.readyState === 0) {
      await mongoose.connect(process.env.MONGO_URI || "mongodb://localhost:27017/dreamfood_test");
    }

    const user = await User.create({
      name: "Test",
      surname: "User",
      email: "test@example.com",
      password: "password123",
      cookingHours: 5,
    });

    userId = (user._id as string).toString();
    token = generateToken({id: userId, email: user.email});
  });

  afterAll(async () => {
    if (mongoose.connection.db) await mongoose.connection.db.dropDatabase();
    await mongoose.disconnect();
  });

  it("should add new ingredient to test user", async () => {
    const res = await request(app)
      .post(`${base_path}/add`)
      .set("Authorization", `Bearer ${token}`)
      .send({
        category: "Nabiał",
        title: "Mleko",
        unit: "ml"
      });

    expect(res.status).toBe(201);
    expect(res.body).toHaveProperty("_id");
    expect(res.body.category).toBe("Nabiał");
    expect(res.body.title).toBe("Mleko");
    expect(res.body.unit).toBe("ml");

    const ingredientInDb = await Ingredient.findById(res.body._id);
    expect(ingredientInDb).not.toBeNull();
  });
});

describe("POST /api/ingredients/add-multiple", () => {
    let token: string;
    let userId: string;

    beforeAll(async () => {
    if (mongoose.connection.readyState === 0) {
      await mongoose.connect(process.env.MONGO_URI || "mongodb://localhost:27017/dreamfood_test");
    }

    const user = await User.create({
      name: "Test",
      surname: "User",
      email: "test@example.com",
      password: "password123",
      cookingHours: 5,
    });

    userId = (user._id as string).toString();
    token = generateToken({id: userId, email: user.email});
  });

  afterAll(async () => {
    if (mongoose.connection.db) await mongoose.connection.db.dropDatabase();
    await mongoose.disconnect();
  });

  it("should add multiple ingredients successfully", async () => {
    const response = await request(app)
      .post("/api/ingredients/add-multiple")
      .set("Authorization", `Bearer ${token}`)
      .send({
        ingredients: [
          { title: "Sugar", category: "Sweet", unit: "g" },
          { title: "Salt", category: "Spice", unit: "g" },
        ],
      });

    expect(response.status).toBe(201);
    expect(response.body.message).toBe("Added 2 ingredients");
    expect(response.body.ingredients).toHaveLength(2);

    const dbIngredients = await Ingredient.find({ owner: userId });
    expect(dbIngredients).toHaveLength(2);
  });

  it("should fail if ingredients field is missing", async () => {
    const response = await request(app)
      .post("/api/ingredients/add-multiple")
      .set("Authorization", `Bearer ${token}`)
      .send({});

    expect(response.status).toBe(400);
    expect(response.body.error).toBe("Field 'ingredients' must be non empty array");
  });

  it("should fail if ingredients array is empty", async () => {
    const response = await request(app)
      .post("/api/ingredients/add-multiple")
      .set("Authorization", `Bearer ${token}`)
      .send({ ingredients: [] });

    expect(response.status).toBe(400);
    expect(response.body.error).toBe("Field 'ingredients' must be non empty array");
  });
});