import request from "supertest";
import mongoose from "mongoose";
import { describe, it, expect, beforeAll, afterAll } from "@jest/globals";
import app from "../src/server";

describe("POST /api/auth/register - validation errors", () => {
    it("should return 400 if required fields are missing", async () => {
        const response = await request(app)
            .post("/api/auth/register")
            .send({
                name: "P",
                surname: "K",
                email: "invalidemail",
                password: "123",
                cookingHours: -5
            });

        expect(response.status).toBe(400);
        expect(response.body).toHaveProperty("message");
        expect(response.body.message).toContain("Name must be at least 2 characters");
        expect(response.body.message).toContain("Surname must be at least 2 characters");
        expect(response.body.message).toContain("Password must be at least 6 characters");
        expect(response.body.message).toContain("Invalid email format");
        expect(response.body.message).toContain("Too small: expected number to be >=0");
    });
});

describe("POST /api/auth/register - duplicate email", () => {
    beforeAll(async () => {
        if (mongoose.connection.readyState === 0) {
            await mongoose.connect(process.env.MONGO_URI || "mongodb://localhost:27017/dreamfood_test");
        }
    });

    afterAll(async () => {
        if (mongoose.connection.db)
            await mongoose.connection.db.dropDatabase(); // sprzątanie całej testowej bazy
        await mongoose.disconnect();
    });

    it("should return 409 if email is already exists", async () => {
        const testUser = {
            name: "John",
            surname: "Doe",
            email: "duplicate@example.com",
            password: "password123",
            cookingHours: 5,
        }

        const first = await request(app)
            .post("/api/auth/register")
            .send(testUser);

        expect(first.status).toBe(201);

        const second = await request(app)
            .post("/api/auth/register")
            .send(testUser);

        expect(second.status).toBe(409);
        expect(second.body).toHaveProperty("message");
        expect(second.body.message).toMatch(/already exists/i);
    });
});

describe("POST /api/auth/register - success", () => {
    beforeAll(async () => {
        if (mongoose.connection.readyState === 0) {
            await mongoose.connect(process.env.MONGO_URI || "mongodb://localhost:27017/dreamfood_test");
        }
    });

    afterAll(async () => {
        await mongoose.connection.db?.dropDatabase(); // czyścimy testową bazę
        await mongoose.disconnect();
    });

    it("should register user", async () => {
        const newUser = {
            name: "Alice",
            surname: "Wonderland",
            email: "alice@example.com",
            password: "superSecret123",
            cookingHours: 10,
        };

        const response = await request(app)
            .post("/api/auth/register")
            .send(newUser);

        // sprawdzamy kod odpowiedzi
        expect(response.status).toBe(201);

        // sprawdzamy, czy API zwraca token
        expect(response.body).toHaveProperty("token");

        // sprawdzamy, czy user został zwrócony z odpowiednimi polami
        expect(response.body.user).toMatchObject({
            name: newUser.name,
            surname: newUser.surname,
            email: newUser.email,
            cookingHours: newUser.cookingHours,
        });
    })
})
