data class RecipeCoversResponse(
    val recipes: List<RecipeCover>,
    val pagination: Pagination,
    val type: String
)

data class RecipeCover(
    val id: Int,
    val title: String,
    val image: String?,
    val category: String,
    val price: Double,
    val author: Author,
    val createdAt: String,
    val averageRating: Double,
    val reviewsCount: Int,
    val cookingTime: String,
    val ingredientsCount: Int,
    val isPurchased: Boolean? = null,
    val isOwned: Boolean? = null
)

data class Author(
    val id: Int,
    val name: String,
    val surname: String
)

data class Pagination(
    val total: Int,
    val page: Int,
    val limit: Int,
    val totalPages: Int
)
