data class RegisterRequest(
    val name: String,
    val surname: String,
    val email: String,
    val password: String,
    val cookingHours: Int = 0
)

data class User(
    val id: Int,
    val name: String,
    val surname: String,
    val email: String,
    val cookingHours: Float
)

data class RegisterResponse(
    val message: String,
    val token: String,
    val user: User
)