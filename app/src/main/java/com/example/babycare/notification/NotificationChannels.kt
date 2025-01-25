object NotificationChannels {
    const val CHANNEL_REMINDER = "reminder"
    const val CHANNEL_RECORD = "record"
    
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = 
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                
            // 提醒通道
            NotificationChannel(
                CHANNEL_REMINDER,
                "提醒",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "包含喂奶、睡眠等定时提醒"
                enableLights(true)
                lightColor = Color.YELLOW
                enableVibration(true)
                setShowBadge(true)
            }.also { channel ->
                notificationManager.createNotificationChannel(channel)
            }
            
            // 记录通道
            NotificationChannel(
                CHANNEL_RECORD,
                "记录",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "包含记录完成、统计等通知"
                setShowBadge(false)
            }.also { channel ->
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
} 