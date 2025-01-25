package com.example.babycare.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val viewState by viewModel.viewState.collectAsState()
    var showQuietHoursDialog by remember { mutableStateOf(false) }

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

        // 主题设置
        SettingsSection(title = "显示设置") {
            SettingsSwitch(
                title = "夜间模式",
                checked = viewState.darkMode,
                onCheckedChange = { 
                    viewModel.handleEvent(SettingsEvent.UpdateDarkMode(it))
                }
            )
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // 通知设置
        SettingsSection(title = "通知设置") {
            SettingsSwitch(
                title = "启用通知",
                checked = viewState.notificationsEnabled,
                onCheckedChange = {
                    viewModel.handleEvent(SettingsEvent.UpdateNotifications(it))
                }
            )
            if (viewState.notificationsEnabled) {
                ReminderSettingsContent(
                    settings = viewState.reminderSettings,
                    onReminderEnabled = { type, enabled ->
                        viewModel.handleEvent(SettingsEvent.UpdateReminderEnabled(type, enabled))
                    },
                    onIntervalChanged = {
                        viewModel.handleEvent(SettingsEvent.UpdateFeedingInterval(it))
                    },
                    onQuietHoursClick = { showQuietHoursDialog = true }
                )
            }
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // 数据管理
        SettingsSection(title = "数据管理") {
            SettingsButton(
                title = "导出数据",
                onClick = { viewModel.handleEvent(SettingsEvent.ExportData) }
            )
            SettingsButton(
                title = "导入数据",
                onClick = { viewModel.handleEvent(SettingsEvent.ImportData) }
            )
            SettingsButton(
                title = "清除数据",
                onClick = { viewModel.handleEvent(SettingsEvent.ClearData) }
            )
        }
    }

    if (showQuietHoursDialog) {
        QuietHoursDialog(
            currentStart = viewState.reminderSettings.quietHoursStart,
            currentEnd = viewState.reminderSettings.quietHoursEnd,
            onDismiss = { showQuietHoursDialog = false },
            onConfirm = { start, end ->
                viewModel.handleEvent(SettingsEvent.UpdateQuietHours(start, end))
                showQuietHoursDialog = false
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsSwitch(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SettingsButton(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ReminderSettingsContent(
    settings: ReminderSettings,
    onReminderEnabled: (ReminderType, Boolean) -> Unit,
    onIntervalChanged: (Int) -> Unit,
    onQuietHoursClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(start = 16.dp)
    ) {
        SettingsSwitch(
            title = "喂奶提醒",
            checked = settings.feedingEnabled,
            onCheckedChange = { onReminderEnabled(ReminderType.FEEDING, it) }
        )
        if (settings.feedingEnabled) {
            IntervalSelector(
                currentInterval = settings.feedingInterval,
                onIntervalSelected = onIntervalChanged
            )
        }
        
        SettingsSwitch(
            title = "睡眠提醒",
            checked = settings.sleepEnabled,
            onCheckedChange = { onReminderEnabled(ReminderType.SLEEP, it) }
        )
        
        SettingsSwitch(
            title = "换尿布提醒",
            checked = settings.diaperEnabled,
            onCheckedChange = { onReminderEnabled(ReminderType.DIAPER, it) }
        )
        
        SettingsSwitch(
            title = "喂药提醒",
            checked = settings.medicineEnabled,
            onCheckedChange = { onReminderEnabled(ReminderType.MEDICINE, it) }
        )

        SettingsButton(
            title = "免打扰时间",
            subtitle = "${settings.quietHoursStart.format(timeFormatter)} - ${settings.quietHoursEnd.format(timeFormatter)}",
            onClick = onQuietHoursClick
        )
    }
}

@Composable
private fun IntervalSelector(
    currentInterval: Int,
    onIntervalSelected: (Int) -> Unit
) {
    val intervals = listOf(120, 180, 240, 300) // 分钟
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        intervals.forEach { interval ->
            FilterChip(
                selected = currentInterval == interval,
                onClick = { onIntervalSelected(interval) },
                label = { Text("${interval/60}小时") }
            )
        }
    }
}

@Composable
private fun QuietHoursDialog(
    currentStart: LocalTime,
    currentEnd: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime, LocalTime) -> Unit
) {
    var startTime by remember { mutableStateOf(currentStart) }
    var endTime by remember { mutableStateOf(currentEnd) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("设置免打扰时间") },
        text = {
            Column {
                // TODO: 实现时间选择器
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(startTime, endTime) }
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

@Composable
private fun ExportOptionsDialog(
    onDismiss: () -> Unit,
    onConfirm: (Set<RecordType>, Boolean) -> Unit
) {
    var selectedTypes by remember { mutableStateOf(setOf<RecordType>()) }
    var includeCharts by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("导出选项") },
        text = {
            Column {
                Text(
                    text = "选择要导出的记录类型",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                RecordType.values().forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedTypes = if (type in selectedTypes) {
                                    selectedTypes - type
                                } else {
                                    selectedTypes + type
                                }
                            }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (type) {
                                RecordType.FEEDING -> "喂奶记录"
                                RecordType.SLEEP -> "睡眠记录"
                                RecordType.DIAPER -> "换尿布记录"
                                RecordType.MEDICINE -> "喂药记录"
                                RecordType.WATER -> "喂水记录"
                                RecordType.GROWTH -> "成长记录"
                            }
                        )
                        Checkbox(
                            checked = type in selectedTypes,
                            onCheckedChange = { checked ->
                                selectedTypes = if (checked) {
                                    selectedTypes + type
                                } else {
                                    selectedTypes - type
                                }
                            }
                        )
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { includeCharts = !includeCharts }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("包含统计图表")
                    Switch(
                        checked = includeCharts,
                        onCheckedChange = { includeCharts = it }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selectedTypes, includeCharts) },
                enabled = selectedTypes.isNotEmpty()
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

@Composable
private fun ProgressDialog(
    progress: Float,
    title: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {}
    )
}

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm") 