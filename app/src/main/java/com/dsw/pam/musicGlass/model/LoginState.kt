data class LoginState(
    val email: String = "",
    val password: String = "",
    val loginError: String? = null,
    val isLoading: Boolean = false
)
