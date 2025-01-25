sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Record : Screen("record")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")
    object BabyManage : Screen("baby_manage")
    
    // 带参数的路由
    object RecordDetail : Screen("record/{type}/{id}") {
        fun createRoute(type: String, id: Long) = "record/$type/$id"
    }
} 