object NavAnimation {
    fun enterTransition(): EnterTransition {
        return fadeIn(
            animationSpec = tween(300)
        ) + slideIntoContainer(
            animationSpec = tween(300),
            towards = AnimatedContentTransitionScope.SlideDirection.Left
        )
    }

    fun exitTransition(): ExitTransition {
        return fadeOut(
            animationSpec = tween(300)
        ) + slideOutOfContainer(
            animationSpec = tween(300),
            towards = AnimatedContentTransitionScope.SlideDirection.Left
        )
    }

    fun popEnterTransition(): EnterTransition {
        return fadeIn(
            animationSpec = tween(300)
        ) + slideIntoContainer(
            animationSpec = tween(300),
            towards = AnimatedContentTransitionScope.SlideDirection.Right
        )
    }

    fun popExitTransition(): ExitTransition {
        return fadeOut(
            animationSpec = tween(300)
        ) + slideOutOfContainer(
            animationSpec = tween(300),
            towards = AnimatedContentTransitionScope.SlideDirection.Right
        )
    }
} 