// 应用导航图
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.Records.route) {
            RecordListScreen(navController)
        }
        composable(Screen.Statistics.route) {
            StatisticsScreen(navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
        composable(Screen.BabyManage.route) {
            BabyManageScreen(navController)
        }
    }
} 