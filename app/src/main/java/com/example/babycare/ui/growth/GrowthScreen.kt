package com.example.babycare.ui.growth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun GrowthScreen(
    navController: NavController,
    viewModel: GrowthViewModel = hiltViewModel()
) {
    val viewState by viewModel.viewState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

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
        PeriodSelector(
            selectedPeriod = viewState.selectedPeriod,
            onPeriodSelected = { viewModel.handleEvent(GrowthEvent.SelectPeriod(it)) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 最新数据卡片
        viewState.latestRecord?.let { record ->
            LatestGrowthCard(record = record)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 成长趋势提示
        GrowthTrendCard(trends = viewState.growthTrends)

        Spacer(modifier = Modifier.height(16.dp))

        // 成长曲线图表
        GrowthChart(records = viewState.growthRecords)

        // 添加记录按钮
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.End)
        ) {
            Text("+")
        }
    }

    // 添加记录对话框
    if (showAddDialog) {
        AddGrowthRecordDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { height, weight, headCircumference ->
                viewModel.handleEvent(GrowthEvent.AddGrowthRecord(height, weight, headCircumference))
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun PeriodSelector(
    selectedPeriod: GrowthPeriod,
    onPeriodSelected: (GrowthPeriod) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        GrowthPeriod.values().forEach { period ->
            val text = when (period) {
                GrowthPeriod.MONTH_3 -> "近3个月"
                GrowthPeriod.MONTH_6 -> "近6个月"
                GrowthPeriod.YEAR_1 -> "近1年"
            }
            FilterChip(
                selected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) },
                label = { Text(text) }
            )
        }
    }
}

@Composable
private fun GrowthTrendCard(trends: GrowthTrends) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "成长趋势（${trends.period}个月）",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (trends.heightGrowth > 0) {
                Text("身高增长 ${String.format("%.1f", trends.heightGrowth)} 厘米")
            }
            if (trends.weightGain > 0) {
                Text("体重增加 ${String.format("%.1f", trends.weightGain)} 千克")
            }
            if (trends.headGrowth > 0) {
                Text("头围增长 ${String.format("%.1f", trends.headGrowth)} 厘米")
            }
        }
    }
}

@Composable
private fun LatestGrowthCard(record: GrowthRecordUI) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "最新记录 (${record.time})",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("身高")
                    Text("${record.height} cm")
                }
                Column {
                    Text("体重")
                    Text("${record.weight} kg")
                }
                Column {
                    Text("头围")
                    Text("${record.headCircumference} cm")
                }
            }
        }
    }
}

@Composable
private fun AddGrowthRecordDialog(
    onDismiss: () -> Unit,
    onConfirm: (Float?, Float?, Float?) -> Unit
) {
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var headCircumference by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加成长记录") },
        text = {
            Column {
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("身高 (cm)") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("体重 (kg)") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = headCircumference,
                    onValueChange = { headCircumference = it },
                    label = { Text("头围 (cm)") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        height.toFloatOrNull(),
                        weight.toFloatOrNull(),
                        headCircumference.toFloatOrNull()
                    )
                }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
} 