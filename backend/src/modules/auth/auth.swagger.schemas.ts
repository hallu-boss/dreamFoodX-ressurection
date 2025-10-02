/**
 * @swagger
 * components:
 *   schemas:
 *     RegisterInput:
 *       type: object
 *       required:
 *         - name
 *         - surname
 *         - email
 *         - password
 *       properties:
 *         name:
 *           type: string
 *           example: "Jan"
 *         surname:
 *           type: string
 *           example: "Kowalski"
 *         email:
 *           type: string
 *           format: email
 *           example: "jan.kowalski@example.com"
 *         password:
 *           type: string
 *           format: password
 *           example: "SuperHaslo123"
 *         cookingHours:
 *           type: integer
 *           example: 0
 *
 *     RegisterSuccess:
 *       type: object
 *       properties:
 *         message:
 *           type: string
 *           example: "User registered successfully"
 *         token:
 *           type: string
 *           description: JWT token do autoryzacji
 *           example: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
 *         user:
 *           type: object
 *           properties:
 *             id:
 *               type: string
 *               example: "652f1a0e3a6f2b001f43c1a7"
 *             name:
 *               type: string
 *               example: "Jan"
 *             surname:
 *               type: string
 *               example: "Kowalski"
 *             email:
 *               type: string
 *               example: "jan.kowalski@example.com"
 *             cookingHours:
 *               type: integer
 *               example: 0
 */
