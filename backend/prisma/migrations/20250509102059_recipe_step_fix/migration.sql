/*
  Warnings:

  - Changed the type of `stepType` on the `RecipeStep` table. No cast exists, the column would be dropped and recreated, which cannot be done if there is data, since the column is required.

*/
-- CreateEnum
CREATE TYPE "StepType" AS ENUM ('ADD_INGREDIENT', 'COOKING', 'DESCRIPTION');

-- DropForeignKey
ALTER TABLE "RecipeStep" DROP CONSTRAINT "RecipeStep_ingredientId_fkey";

-- AlterTable
ALTER TABLE "RecipeStep" ALTER COLUMN "czas" DROP NOT NULL,
ALTER COLUMN "ingredientId" DROP NOT NULL,
ALTER COLUMN "opis" DROP NOT NULL,
ALTER COLUMN "predkoscOstrzy" DROP NOT NULL,
DROP COLUMN "stepType",
ADD COLUMN     "stepType" "StepType" NOT NULL,
ALTER COLUMN "temperatura" DROP NOT NULL;

-- AddForeignKey
ALTER TABLE "RecipeStep" ADD CONSTRAINT "RecipeStep_ingredientId_fkey" FOREIGN KEY ("ingredientId") REFERENCES "Ingredient"("id") ON DELETE SET NULL ON UPDATE CASCADE;
