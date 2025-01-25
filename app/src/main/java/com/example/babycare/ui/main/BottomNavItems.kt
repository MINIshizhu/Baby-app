object BottomNavItems {
    val items = listOf(
        BottomNavItem(
            label = "首页",
            icon = Icons.Default.Home,
            route = Screen.Home.route
        ),
        BottomNavItem(
            label = "记录",
            icon = Icons.Default.Edit,
            route = Screen.Record.route
        ),
        BottomNavItem(
            label = "统计",
            icon = Icons.Default.Analytics,
            route = Screen.Statistics.route
        ),
        BottomNavItem(
            label = "设置",
            icon = Icons.Default.Settings,
            route = Screen.Settings.route
        )
    )
} 