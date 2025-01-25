@Composable
fun RecordScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("记录页面")
    }
}

@Composable
fun StatisticsScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("统计页面")
    }
}

@Composable
fun SettingsScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("设置页面")
    }
}

@Composable
fun RecordDetailScreen(
    type: String?,
    id: Long?,
    navController: NavController
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("记录详情页面: $type - $id")
    }
} 