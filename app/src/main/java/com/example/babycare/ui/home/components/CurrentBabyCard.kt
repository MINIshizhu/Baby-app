@Composable
fun CurrentBabyCard(
    baby: Baby?,
    onSwitchBaby: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像
            AsyncImage(
                model = baby?.avatar,
                contentDescription = "头像",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
            
            // 信息
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = baby?.name ?: "添加宝宝",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = baby?.let { "年龄：${it.getAge()}" } ?: "开始记录宝宝的成长",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // 切换按钮
            IconButton(onClick = onSwitchBaby) {
                Icon(Icons.Default.SwapHoriz, "切换宝宝")
            }
        }
    }
} 