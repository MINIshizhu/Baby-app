package com.example.babycare.ui.baby

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.babycare.ui.components.DatePicker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun BabyEditDialog(
    baby: Baby?,
    onDismiss: () -> Unit,
    onSave: (String, Int, Long, Uri?) -> Unit
) {
    var name by rememberSaveable { mutableStateOf(baby?.name ?: "") }
    var gender by rememberSaveable { mutableStateOf(baby?.gender ?: 0) }
    var birthday by rememberSaveable { mutableStateOf(baby?.birthday ?: System.currentTimeMillis()) }
    var avatarUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var showExitConfirm by remember { mutableStateOf(false) }
    val hasChanges = name != baby?.name || 
                    gender != baby?.gender || 
                    birthday != baby?.birthday ||
                    avatarUri != null

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { avatarUri = it }
    }

    AlertDialog(
        onDismissRequest = {
            if (hasChanges) {
                showExitConfirm = true
            } else {
                onDismiss()
            }
        },
        title = { Text(if (baby == null) "添加宝宝" else "编辑宝宝") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                // 头像选择
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterHorizontally)
                ) {
                    AsyncImage(
                        model = when {
                            avatarUri != null -> avatarUri
                            baby?.avatar != null -> baby.avatar
                            else -> R.drawable.ic_baby_default
                        },
                        contentDescription = "头像",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    IconButton(
                        onClick = { imagePicker.launch("image/*") },
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Camera,
                            contentDescription = "更换头像"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 姓名输入
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("姓名") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = name.isBlank(),
                    supportingText = {
                        if (name.isBlank()) {
                            Text("请输入宝宝的名字")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 性别选择
                Text(
                    text = "性别",
                    style = MaterialTheme.typography.titleSmall
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterChip(
                        selected = gender == 0,
                        onClick = { gender = 0 },
                        label = { Text("女") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Female,
                                contentDescription = null,
                                tint = Color(0xFFE91E63)
                            )
                        }
                    )
                    FilterChip(
                        selected = gender == 1,
                        onClick = { gender = 1 },
                        label = { Text("男") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Male,
                                contentDescription = null,
                                tint = Color(0xFF2196F3)
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

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

    // 退出确认对话框
    if (showExitConfirm) {
        AlertDialog(
            onDismissRequest = { showExitConfirm = false },
            title = { Text("放弃更改？") },
            text = { Text("您有未保存的更改，确定要放弃吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitConfirm = false
                        onDismiss()
                    }
                ) {
                    Text("放弃")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExitConfirm = false }
                ) {
                    Text("继续编辑")
                }
            }
        )
    }

    // Snackbar主机
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.padding(16.dp)
    )
} 