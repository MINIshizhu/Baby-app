// 宝宝管理页面，支持添加/编辑宝宝信息
package com.example.babycare.ui.baby

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.babycare.R
import com.example.babycare.data.entity.Baby
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun BabyManageScreen(
    navController: NavController,
    viewModel: BabyManageViewModel = hiltViewModel()
) {
    val viewState by viewModel.viewState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("宝宝管理") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        viewModel.handleEvent(BabyManageEvent.AddBaby)
                    }) {
                        Icon(Icons.Default.Add, "添加宝宝")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (viewState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            
            BabyList(
                babies = viewState.babies,
                currentBaby = viewState.currentBaby,
                onSelect = {
                    viewModel.handleEvent(BabyManageEvent.SelectBaby(it))
                },
                onEdit = {
                    viewModel.handleEvent(BabyManageEvent.EditBaby(it))
                },
                onDelete = {
                    viewModel.handleEvent(BabyManageEvent.DeleteBaby(it))
                }
            )
            
            // 错误提示
            viewState.error?.let { error ->
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Text(error)
                }
            }
        }
    }
    
    // 添加/编辑对话框
    if (viewState.showAddDialog || viewState.showEditDialog) {
        BabyEditDialog(
            baby = viewState.editingBaby,
            onDismiss = {
                viewModel.handleEvent(BabyManageEvent.DismissDialog)
            },
            onSave = { name, gender, birthday, avatarUri ->
                viewModel.handleEvent(
                    BabyManageEvent.SaveBaby(
                        name = name,
                        gender = gender,
                        birthday = birthday,
                        avatarUri = avatarUri
                    )
                )
            }
        )
    }
}

@Composable
private fun BabyList(
    babies: List<Baby>,
    currentBaby: Baby?,
    onSelect: (Baby) -> Unit,
    onEdit: (Baby) -> Unit,
    onDelete: (Baby) -> Unit
) {
    val groupedBabies = remember(babies) {
        babies.groupBy { it.name.first().toUpperCase() }
            .toSortedMap()
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        groupedBabies.forEach { (initial, groupBabies) ->
            stickyHeader {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Text(
                        text = initial.toString(),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            items(
                items = groupBabies,
                key = { it.id }
            ) { baby ->
                BabyItem(
                    baby = baby,
                    isSelected = baby.id == currentBaby?.id,
                    onSelect = { onSelect(baby) },
                    onEdit = { onEdit(baby) },
                    onDelete = { onDelete(baby) }
                )
            }
        }
    }
}

@Composable
private fun BabyItem(
    baby: Baby,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像
            Image(
                painter = baby.avatar?.let { 
                    rememberAsyncImagePainter(it)
                } ?: painterResource(R.drawable.ic_baby_default),
                contentDescription = "头像",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // 信息
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = baby.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${if (baby.gender == 0) "女" else "男"} · ${
                        baby.birthday.toDateString()
                    }",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // 操作按钮
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "编辑")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "删除")
                }
            }
        }
    }
}

@Composable
private fun BabyEditDialog(
    baby: Baby?,
    onDismiss: () -> Unit,
    onSave: (String, Int, Long, Uri?) -> Unit
) {
    var name by remember { mutableStateOf(baby?.name ?: "") }
    var gender by remember { mutableStateOf(baby?.gender ?: 0) }
    var birthday by remember { mutableStateOf(baby?.birthday ?: System.currentTimeMillis()) }
    var avatarUri by remember { mutableStateOf<Uri?>(null) }
    
    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { avatarUri = it }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (baby == null) "添加宝宝" else "编辑宝宝") },
        text = {
            Column {
                // 头像选择
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable { imagePicker.launch("image/*") }
                        .align(Alignment.CenterHorizontally)
                ) {
                    Image(
                        painter = when {
                            avatarUri != null -> rememberAsyncImagePainter(avatarUri)
                            baby?.avatar != null -> rememberAsyncImagePainter(baby.avatar)
                            else -> painterResource(R.drawable.ic_baby_default)
                        },
                        contentDescription = "头像",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 姓名输入
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("姓名") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 性别选择
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    RadioButton(
                        selected = gender == 0,
                        onClick = { gender = 0 }
                    )
                    Text("女")
                    RadioButton(
                        selected = gender == 1,
                        onClick = { gender = 1 }
                    )
                    Text("男")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 出生日期选择
                DatePicker(
                    value = birthday,
                    onValueChange = { birthday = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(name, gender, birthday, avatarUri)
                },
                enabled = name.isNotBlank()
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
} 