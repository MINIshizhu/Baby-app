fun NavGraphBuilder.animatedComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        enterTransition = { NavAnimation.enterTransition() },
        exitTransition = { NavAnimation.exitTransition() },
        popEnterTransition = { NavAnimation.popEnterTransition() },
        popExitTransition = { NavAnimation.popExitTransition() }
    ) {
        content(it)
    }
}

fun NavController.navigateSafely(
    route: String,
    builder: NavOptionsBuilder.() -> Unit = {
        launchSingleTop = true
    }
) {
    try {
        navigate(route, builder)
    } catch (e: Exception) {
        e.printStackTrace()
    }
} 