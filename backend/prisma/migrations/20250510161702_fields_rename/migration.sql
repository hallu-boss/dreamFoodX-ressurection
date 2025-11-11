/*
  Warnings:

  - You are about to drop the column `cena` on the `Recipe` table. All the data in the column will be lost.
  - You are about to drop the column `kategoria` on the `Recipe` table. All the data in the column will be lost.
  - You are about to drop the column `nazwa` on the `Recipe` table. All the data in the column will be lost.
  - You are about to drop the column `obraz` on the `Recipe` table. All the data in the column will be lost.
  - You are about to drop the column `opis` on the `Recipe` table. All the data in the column will be lost.
  - You are about to drop the column `czas` on the `RecipeStep` table. All the data in the column will be lost.
  - You are about to drop the column `ilosc` on the `RecipeStep` table. All the data in the column will be lost.
  - You are about to drop the column `nazwa` on the `RecipeStep` table. All the data in the column will be lost.
  - You are about to drop the column `opis` on the `RecipeStep` table. All the data in the column will be lost.
  - You are about to drop the column `predkoscOstrzy` on the `RecipeStep` table. All the data in the column will be lost.
  - You are about to drop the column `temperatura` on the `RecipeStep` table. All the data in the column will be lost.
  - Added the required column `category` to the `Recipe` table without a default value. This is not possible if the table is not empty.
  - Added the required column `description` to the `Recipe` table without a default value. This is not possible if the table is not empty.
  - Added the required column `price` to the `Recipe` table without a default value. This is not possible if the table is not empty.
  - Added the required column `title` to the `Recipe` table without a default value. This is not possible if the table is not empty.
  - Added the required column `title` to the `RecipeStep` table without a default value. This is not possible if the table is not empty.

*/
-- AlterTable
ALTER TABLE "Recipe" DROP COLUMN "cena",
DROP COLUMN "kategoria",
DROP COLUMN "nazwa",
DROP COLUMN "obraz",
DROP COLUMN "opis",
ADD COLUMN     "category" TEXT NOT NULL,
ADD COLUMN     "description" TEXT NOT NULL,
ADD COLUMN     "image" TEXT,
ADD COLUMN     "price" DECIMAL(10,2) NOT NULL,
ADD COLUMN     "title" TEXT NOT NULL;

-- AlterTable
ALTER TABLE "RecipeStep" DROP COLUMN "czas",
DROP COLUMN "ilosc",
DROP COLUMN "nazwa",
DROP COLUMN "opis",
DROP COLUMN "predkoscOstrzy",
DROP COLUMN "temperatura",
ADD COLUMN     "amount" DECIMAL(10,2),
ADD COLUMN     "description" TEXT,
ADD COLUMN     "mixSpeed" INTEGER,
ADD COLUMN     "temperature" INTEGER,
ADD COLUMN     "time" TEXT,
ADD COLUMN     "title" TEXT NOT NULL;
