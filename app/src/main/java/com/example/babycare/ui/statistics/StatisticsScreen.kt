package com.example.babycare.ui.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.babycare.ui.components.StatisticsCard

@Composable
fun StatisticsScreen(
    navController: NavController,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val viewState by viewModel.viewState.collectAsState()
    val periods = listOf(
        StatisticsPeriod.TODAY to "今日",
        StatisticsPeriod.WEEK to "本周",
        StatisticsPeriod.MONTH to "本月"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 错误提示
        viewState.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // 加载指示器
        if (viewState.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 时间段选择
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            periods.forEach { (period, title) ->
                FilterChip(
                    selected = viewState.selectedPeriod == period,
                    onClick = { viewModel.handleEvent(StatisticsEvent.SelectPeriod(period)) },
                    label = { Text(title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 统计卡片
        LazyColumn {
            item {
                StatisticsSection(
                    title = "喂奶统计",
                    stats = with(viewState.feedingStats) {
                        listOf(
                            "总次数" to "$totalCount次",
                            "总奶量" to "${totalAmount}ml",
                            "平均时长" to "${averageDuration}分钟"
                        )
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                StatisticsSection(
                    title = "睡眠统计",
                    stats = with(viewState.sleepStats) {
                        listOf(
                            "总时长" to "${totalDuration/60}小时",
                            "次数" to "$count次",
                            "平均时长" to "${"%.1f".format(averageDuration/60)}小时"
                        )
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 其他统计部分...
        }
    }
}

@Composable
private fun StatisticsSection(
    title: String,
    stats: List<Pair<String, String>>
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                stats.forEach { (title, value) ->
                    StatisticsCard(
                        title = title,
                        value = value,
                        modifier = Modifier.weight(1f)
                    )
                    if (title != stats.last().first) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }
    }
} 