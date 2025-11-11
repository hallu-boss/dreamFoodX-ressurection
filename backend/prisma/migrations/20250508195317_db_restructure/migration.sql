/*
  Warnings:

  - You are about to drop the column `categories` on the `Recipe` table. All the data in the column will be lost.
  - You are about to drop the column `creatorId` on the `Recipe` table. All the data in the column will be lost.
  - You are about to drop the column `hidden` on the `Recipe` table. All the data in the column will be lost.
  - You are about to drop the column `image` on the `Recipe` table. All the data in the column will be lost.
  - You are about to drop the column `price` on the `Recipe` table. All the data in the column will be lost.
  - You are about to drop the column `rating` on the `Recipe` table. All the data in the column will be lost.
  - You are about to drop the column `description` on the `RecipeStep` table. All the data in the column will be lost.
  - You are about to drop the `MixStep` table. If the table is not empty, all the data it contains will be lost.
  - You are about to drop the `WeightStep` table. If the table is not empty, all the data it contains will be lost.
  - Added the required column `cena` to the `Recipe` table without a default value. This is not possible if the table is not empty.
  - Added the required column `kategoria` to the `Recipe` table without a default value. This is not possible if the table is not empty.
  - Added the required column `nazwa` to the `Recipe` table without a default value. This is not possible if the table is not empty.
  - Added the required column `opis` to the `Recipe` table without a default value. This is not possible if the table is not empty.
  - Added the required column `updatedAt` to the `Recipe` table without a default value. This is not possible if the table is not empty.
  - Added the required column `userId` to the `Recipe` table without a default value. This is not possible if the table is not empty.
  - Added the required column `czas` to the `RecipeStep` table without a default value. This is not possible if the table is not empty.
  - Added the required column `ingredientId` to the `RecipeStep` table without a default value. This is not possible if the table is not empty.
  - Added the required column `nazwa` to the `RecipeStep` table without a default value. This is not possible if the table is not empty.
  - Added the required column `opis` to the `RecipeStep` table without a default value. This is not possible if the table is not empty.
  - Added the required column `predkoscOstrzy` to the `RecipeStep` table without a default value. This is not possible if the table is not empty.
  - Added the required column `stepType` to the `RecipeStep` table without a default value. This is not possible if the table is not empty.
  - Added the required column `temperatura` to the `RecipeStep` table without a default value. This is not possible if the table is not empty.
  - Added the required column `userId` to the `Review` table without a default value. This is not possible if the table is not empty.

*/
-- DropForeignKey
ALTER TABLE "MixStep" DROP CONSTRAINT "MixStep_recipeStepId_fkey";

-- DropForeignKey
ALTER TABLE "Recipe" DROP CONSTRAINT "Recipe_creatorId_fkey";

-- DropForeignKey
ALTER TABLE "WeightStep" DROP CONSTRAINT "WeightStep_ingredientId_fkey";

-- DropForeignKey
ALTER TABLE "WeightStep" DROP CONSTRAINT "WeightStep_recipeStepId_fkey";

-- AlterTable
ALTER TABLE "Recipe" DROP COLUMN "categories",
DROP COLUMN "creatorId",
DROP COLUMN "hidden",
DROP COLUMN "image",
DROP COLUMN "price",
DROP COLUMN "rating",
ADD COLUMN     "cena" DECIMAL(10,2) NOT NULL,
ADD COLUMN     "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN     "kategoria" TEXT NOT NULL,
ADD COLUMN     "nazwa" TEXT NOT NULL,
ADD COLUMN     "obraz" TEXT,
ADD COLUMN     "opis" TEXT NOT NULL,
ADD COLUMN     "updatedAt" TIMESTAMP(3) NOT NULL,
ADD COLUMN     "userId" INTEGER NOT NULL,
ADD COLUMN     "visible" BOOLEAN NOT NULL DEFAULT true;

-- AlterTable
ALTER TABLE "RecipeStep" DROP COLUMN "description",
ADD COLUMN     "czas" TEXT NOT NULL,
ADD COLUMN     "ilosc" TEXT,
ADD COLUMN     "ingredientId" INTEGER NOT NULL,
ADD COLUMN     "nazwa" TEXT NOT NULL,
ADD COLUMN     "opis" TEXT NOT NULL,
ADD COLUMN     "predkoscOstrzy" INTEGER NOT NULL,
ADD COLUMN     "stepType" TEXT NOT NULL,
ADD COLUMN     "temperatura" INTEGER NOT NULL;

-- AlterTable
ALTER TABLE "Review" ADD COLUMN     "userId" INTEGER NOT NULL;

-- DropTable
DROP TABLE "MixStep";

-- DropTable
DROP TABLE "WeightStep";

-- AddForeignKey
ALTER TABLE "Recipe" ADD CONSTRAINT "Recipe_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Review" ADD CONSTRAINT "Review_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "RecipeStep" ADD CONSTRAINT "RecipeStep_ingredientId_fkey" FOREIGN KEY ("ingredientId") REFERENCES "Ingredient"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
