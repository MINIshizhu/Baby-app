package com.example.babycare.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DatePicker(
    value: Long,
    onValueChange: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val date = remember(value) {
        Instant.ofEpochMilli(value)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }
    
    OutlinedTextField(
        value = date.format(DateTimeFormatter.ISO_LOCAL_DATE),
        onValueChange = {},
        label = { Text("出生日期") },
        readOnly = true,
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
    )
    
    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(
                state = rememberDatePickerState(
                    initialSelectedDateMillis = value
                ),
                showModeToggle = false,
                title = null,
                headline = null,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
} 