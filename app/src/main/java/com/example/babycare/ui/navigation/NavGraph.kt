package com.example.babycare.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.Records.route) {
            RecordsScreen(navController)
        }
        composable(Screen.Statistics.route) {
            StatisticsScreen(navController)
        }
        composable(Screen.Growth.route) {
            GrowthScreen(navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
    }
}

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "首页")
    object Records : Screen("records", "记录")
    object Statistics : Screen("statistics", "统计")
    object Growth : Screen("growth", "成长")
    object Settings : Screen("settings", "设置")
} 