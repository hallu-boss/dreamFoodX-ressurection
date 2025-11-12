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
    var isPurchased: Boolean, // czy dodano do ulubionych
    val isOwned: Boolean
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

data class RecipeResponse(
    val id: Int,
    val createdAt: String,
    val author: Author,
    val title: String,
    val description: String?,
    val category: String,
    val price: Double,
    val image: String?,
    val steps: List<RecipeStep>?,
    val reviews: List<Review>?,
    val permission: Boolean
)

data class RecipeStep(
    val id: Int,
    val title: String,
    val stepType: String,
    val description: String,
    val ingredient: Ingredient?,
    val amount: Double?,
    val time: String?,
    val temperature: Int?,
    val mixSpeed: Int?
)

data class Ingredient(
    val title: String,
    val unit: String
)

data class Review (
    val recipeId: Int,
    val rating:   Int,
    val opinion:  String,
    val userId:   Int,
)

data class ReviewRequest (
    val recipeId: Int,
    val userId:   Int,
)

data class ReviewResponse (
    val recipeId: Int,
    val userId:   Int,
    val rating:   Int,
    val opinion:  String,
)

