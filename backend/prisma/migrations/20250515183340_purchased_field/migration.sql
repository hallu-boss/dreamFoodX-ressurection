-- CreateTable
CREATE TABLE "_PurchasedRecipes" (
    "A" INTEGER NOT NULL,
    "B" INTEGER NOT NULL
);

-- CreateIndex
CREATE UNIQUE INDEX "_PurchasedRecipes_AB_unique" ON "_PurchasedRecipes"("A", "B");

-- CreateIndex
CREATE INDEX "_PurchasedRecipes_B_index" ON "_PurchasedRecipes"("B");

-- AddForeignKey
ALTER TABLE "_PurchasedRecipes" ADD CONSTRAINT "_PurchasedRecipes_A_fkey" FOREIGN KEY ("A") REFERENCES "Recipe"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "_PurchasedRecipes" ADD CONSTRAINT "_PurchasedRecipes_B_fkey" FOREIGN KEY ("B") REFERENCES "User"("id") ON DELETE CASCADE ON UPDATE CASCADE;
