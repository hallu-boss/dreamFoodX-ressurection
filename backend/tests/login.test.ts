import request from "supertest";
import mongoose from "mongoose";
import { describe, it, expect, beforeAll, afterAll } from "@jest/globals";
import { app } from "../src/app";
import { User } from "../src/mongo/models";

const login_path = "/api/auth/login"
describe(`POST ${login_path} - validation error`, () => {
    it("should return 400", async () => {
        const response = await request(app)
            .post(login_path)
            .send({
                email: "invalidemail",
                password: "1",
            })

        expect(response.status).toBe(400);
        expect(response.body).toHaveProperty("message");
        expect(response.body.message).toContain("Invalid email format");
    })
})

describe(`POST ${login_path} - invalid user error`, () => {
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
    it("should return 401", async () => {
        const response = await request(app)
            .post(login_path)
            .send({
                email: "nonexistent@example.com",
                password: "doesnotmatter123"
            });

        expect(response.status).toBe(401);
    })
});

describe(`POST ${login_path} - success`, () => {
    let testUserId: string;

    beforeAll(async () => {
        if (mongoose.connection.readyState === 0) {
            await mongoose.connect(process.env.MONGO_URI || "mongodb://localhost:27017/dreamfood_test");
        }

        const user = new User({
            name: "Test",
            surname: "User",
            email: "testuser@example.com",
            password: "Password123", // Mongoose pre-save zahashuje
            cookingHours: 5,
        });
        await user.save();
        testUserId = (user._id as string).toString();
    });

    afterAll(async () => {
        if (mongoose.connection.db)
            await mongoose.connection.db.dropDatabase(); // sprzątanie całej testowej bazy
        await mongoose.disconnect();
    });
    it("should return 200", async () => {
        const response = await request(app)
            .post(login_path)
            .send({
                email: "testuser@example.com",
                password: "Password123",
            });

        expect(response.status).toBe(200);
        expect(response.body).toHaveProperty("message", "Login successful");
        expect(response.body).toHaveProperty("token");
        expect(response.body).toHaveProperty("user");
        expect(response.body.user).toHaveProperty("id", testUserId);
        expect(response.body.user).toHaveProperty("email", "testuser@example.com");
        expect(response.body.user).toHaveProperty("name", "Test");
        expect(response.body.user).toHaveProperty("surname", "User");
    })
})