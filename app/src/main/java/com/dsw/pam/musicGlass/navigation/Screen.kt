sealed class Screen(private val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main/{email}") {
        fun createRoute(email: String) = "main/$email"
    }
    object Playlists : Screen("playlists")

    fun route() = route
} 