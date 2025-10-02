import { Router } from "express";
import fs from "fs";
import path from "path";

export function loadRoutes(baseDir: string, basePath = ""): Router {
  const router = Router();

  const files = fs.readdirSync(baseDir);
  for (const file of files) {
    const fullPath = path.join(baseDir, file);
    const stat = fs.statSync(fullPath);

    if (stat.isDirectory()) {
      router.use(`/${file}`, loadRoutes(fullPath));
    } else if (file.endsWith(".ts") || file.endsWith(".js")) {
      const routePath = "/" + path.basename(file, path.extname(file));
      const routeModule = require(fullPath);

      if (routeModule.default) {
        router.use(routePath, routeModule.default);
      }
    }
  }

  return router;
}
