/*
  Warnings:

  - Added the required column `cookingHours` to the `User` table without a default value. This is not possible if the table is not empty.

*/
-- AlterTable
ALTER TABLE "User" ADD COLUMN     "cookingHours" DOUBLE PRECISION NOT NULL;

-- CreateTable
CREATE TABLE "Recipe" (
    "id" SERIAL NOT NULL,
    "hidden" BOOLEAN NOT NULL,
    "creatorId" INTEGER NOT NULL,
    "rating" INTEGER NOT NULL,
    "price" DOUBLE PRECISION NOT NULL,
    "image" TEXT NOT NULL,
    "categories" TEXT[],

    CONSTRAINT "Recipe_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Review" (
    "id" SERIAL NOT NULL,
    "recipeId" INTEGER NOT NULL,
    "rating" INTEGER NOT NULL,
    "opinion" TEXT NOT NULL,

    CONSTRAINT "Review_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "RecipeStep" (
    "id" SERIAL NOT NULL,
    "description" TEXT NOT NULL,
    "recipeId" INTEGER NOT NULL,

    CONSTRAINT "RecipeStep_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "WeightStep" (
    "id" SERIAL NOT NULL,
    "recipeStepId" INTEGER NOT NULL,
    "ingredientId" INTEGER NOT NULL,

    CONSTRAINT "WeightStep_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "MixStep" (
    "id" SERIAL NOT NULL,
    "recipeStepId" INTEGER NOT NULL,
    "speed" INTEGER NOT NULL,
    "temperature" INTEGER NOT NULL,
    "duration" INTEGER NOT NULL,

    CONSTRAINT "MixStep_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Ingredient" (
    "id" SERIAL NOT NULL,
    "title" TEXT NOT NULL,
    "amount" DOUBLE PRECISION NOT NULL,
    "unit" TEXT NOT NULL,
    "category" TEXT NOT NULL,

    CONSTRAINT "Ingredient_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "WeightStep_recipeStepId_key" ON "WeightStep"("recipeStepId");

-- CreateIndex
CREATE UNIQUE INDEX "MixStep_recipeStepId_key" ON "MixStep"("recipeStepId");

-- AddForeignKey
ALTER TABLE "Recipe" ADD CONSTRAINT "Recipe_creatorId_fkey" FOREIGN KEY ("creatorId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Review" ADD CONSTRAINT "Review_recipeId_fkey" FOREIGN KEY ("recipeId") REFERENCES "Recipe"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "RecipeStep" ADD CONSTRAINT "RecipeStep_recipeId_fkey" FOREIGN KEY ("recipeId") REFERENCES "Recipe"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "WeightStep" ADD CONSTRAINT "WeightStep_recipeStepId_fkey" FOREIGN KEY ("recipeStepId") REFERENCES "RecipeStep"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "WeightStep" ADD CONSTRAINT "WeightStep_ingredientId_fkey" FOREIGN KEY ("ingredientId") REFERENCES "Ingredient"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "MixStep" ADD CONSTRAINT "MixStep_recipeStepId_fkey" FOREIGN KEY ("recipeStepId") REFERENCES "RecipeStep"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
