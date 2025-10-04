import request from "supertest";
import mongoose from "mongoose";
import { describe, it, expect, beforeAll, afterAll } from "@jest/globals";
import { app } from "../src/app";
import { User } from "../src/mongo/models";
import { generateToken } from "../src/utils/authenticate";


const login_path = "/api/auth/login"
describe("GET /api/auth/profile", () => {
    let token: string;
    let userId: string;

    beforeAll(async () => {
        if (mongoose.connection.readyState === 0) {
            await mongoose.connect(process.env.MONGO_URI || "mongodb://localhost:27017/dreamfood_test");
        }

        const user = new User({
            name: "Test",
            surname: "User",
            email: "test@example.com",
            password: "password123",
            cookingHours: 10,
        });

        await user.save()

        userId = (user._id as string).toString();
        token = generateToken({id: userId, email: user.email});
    });

    afterAll(async () => {
        if (mongoose.connection.db)
            await mongoose.connection.db.dropDatabase();
        await mongoose.disconnect();
    });

    it("should return 401 if no token is provided", async () => {
        const res = await request(app).get("/api/auth/profile");
        expect(res.status).toBe(401);
        expect(res.body).toHaveProperty("message");
    });

    it("should return profile data with valid token", async () => {
        const res = await request(app)
            .get("/api/auth/profile")
            .set("Authorization", `Bearer ${token}`);

        expect(res.status).toBe(200);
        expect(res.body).toHaveProperty("_id", userId);
        expect(res.body).toHaveProperty("name", "Test");
        expect(res.body).toHaveProperty("surname", "User");
        expect(res.body).toHaveProperty("email", "test@example.com");
        expect(res.body).toHaveProperty("cookingHours", 10);
    });
});
