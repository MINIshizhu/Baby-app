@AndroidEntryPoint
class NotificationService : Service() {
    @Inject lateinit var repository: UserPreferencesRepository
    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SHOW_FEEDING_REMINDER -> showFeedingReminder()
            ACTION_SHOW_SLEEP_REMINDER -> showSleepReminder()
            // ... 其他通知类型
        }
        return START_STICKY
    }
    
    private fun showFeedingReminder() {
        // 实现喂奶提醒通知
    }
} 