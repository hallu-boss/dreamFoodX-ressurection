import request from "supertest";
import { describe, it, expect, beforeAll, afterAll } from "@jest/globals";
import { app } from "../src/app";

describe("GET / - status message", () => {
    it("should return 201", async () => {
    const response = await request(app).get("/")

    expect(response.status).toBe(200);
    expect(response.body).toHaveProperty("message"); // ValidationError message
    expect(response.body.message).toContain("Server is ready 🚀");
  });
});
