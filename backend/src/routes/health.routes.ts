import express from "express"
import { PrismaClient } from "@prisma/client";


const router = express.Router();
const prisma = new PrismaClient();

router.get('/health', async (req, res) => {
  try {
    await prisma.$queryRaw`SELECT 1`;

    res.status(200).json({
      status: 'ok',
      timestamp: new Date().toISOString(),
      uptime: process.uptime(),
      database: 'connected',
      message: 'Server is running correctly'
    });
  } catch (error) {
    res.status(500).json({
      status: 'error',
      timestamp: new Date().toISOString(),
      uptime: process.uptime(),
      database: 'disconnected',
      message: 'Server is running but database connection failed'
    });
  }
})

export default router;
