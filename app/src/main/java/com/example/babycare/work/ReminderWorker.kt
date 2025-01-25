@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: RecordRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // 检查是否需要发送提醒
        val shouldRemind = checkShouldRemind()
        if (shouldRemind) {
            sendReminder()
        }
        
        // 安排下一次检查
        scheduleNextCheck()
        
        return Result.success()
    }
} 