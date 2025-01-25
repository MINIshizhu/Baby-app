package com.example.babycare.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.babycare.ui.components.QuickActionButton
import com.example.babycare.ui.components.StatisticsCard

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "首页",
            style = MaterialTheme.typography.headlineMedium
        )
        // 临时占位，后续实现具体功能
    }
}

@Composable
private fun QuickActions() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "快速记录",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton(
                    text = "喂奶",
                    onClick = { /* TODO */ }
                )
                QuickActionButton(
                    text = "睡眠",
                    onClick = { /* TODO */ }
                )
                QuickActionButton(
                    text = "换尿布",
                    onClick = { /* TODO */ }
                )
            }
        }
    }
}

@Composable
private fun DailySummary() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "今日概览",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatisticsCard(
                    title = "喂奶次数",
                    value = "6次",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                StatisticsCard(
                    title = "睡眠时长",
                    value = "8小时",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
} 