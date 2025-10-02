import request from "supertest";
import app from "../src/server";

describe("GET / - status message", () => {
    it("should return 201", async () => {
    const response = await request(app).get("/")

    expect(response.status).toBe(200);
    expect(response.body).toHaveProperty("message"); // ValidationError message
    expect(response.body.message).toContain("Backend serwisu przepisów działa 🚀");
  });
});
