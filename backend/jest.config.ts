import type { Config } from "jest";

const config: Config = {
  preset: "ts-jest",
  testEnvironment: "node",
  testTimeout: 10000, // zwiększony timeout na wypadek DB
};

export default config;
