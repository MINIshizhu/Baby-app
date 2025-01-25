package com.example.babycare.ui.records

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun RecordsScreen(
    navController: NavController,
    viewModel: RecordsViewModel = hiltViewModel()
) {
    val viewState by viewModel.viewState.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("喂奶", "睡眠", "换尿布", "喂药", "喂水")

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 错误提示
        viewState.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }

        // 加载指示器
        if (viewState.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 顶部标签栏
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index }
                )
            }
        }

        // 记录内容
        when (selectedTabIndex) {
            0 -> FeedingRecordsList(
                records = viewState.feedingRecords,
                onDelete = { recordId ->
                    viewModel.handleEvent(RecordsEvent.DeleteRecord(RecordType.FEEDING, recordId))
                }
            )
            1 -> SleepRecordsList()
            2 -> DiaperRecordsList()
            3 -> MedicineRecordsList()
            4 -> WaterRecordsList()
        }
    }
}

@Composable
private fun FeedingRecordsList(
    records: List<FeedingRecordUI>,
    onDelete: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(records) { record ->
            FeedingRecordItem(
                record = record,
                onDelete = { onDelete(record.id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedingRecordItem(
    record: FeedingRecordUI,
    onDelete: () -> Unit
) {
    SwipeToDismiss(
        state = rememberDismissState(
            confirmValueChange = {
                if (it == DismissValue.DismissedToEnd || it == DismissValue.DismissedToStart) {
                    onDelete()
                    true
                } else false
            }
        ),
        background = { /* 滑动背景 */ },
        dismissContent = {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = record.time,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "类型: ${if (record.isBottleFeeding) "瓶喂" else "亲喂"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (record.amount != null) {
                        Text(
                            text = "奶量: ${record.amount}ml",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    if (record.duration != null) {
                        Text(
                            text = "时长: ${record.duration}分钟",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    )
}

// 临时数据模型
data class FeedingRecordUI(
    val id: Long,
    val time: String,
    val isBottleFeeding: Boolean,
    val amount: Int? = null,
    val duration: Int? = null
)

// 生成测试数据
private fun generateDummyFeedingRecords(): List<FeedingRecordUI> {
    return listOf(
        FeedingRecordUI(1, "今天 14:30", true, amount = 120),
        FeedingRecordUI(2, "今天 11:00", false, duration = 25),
        FeedingRecordUI(3, "今天 07:30", true, amount = 150)
    )
} 