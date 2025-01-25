// 后台提醒服务
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: ReminderRepository
) : CoroutineWorker(context, params) {
    // 实现提醒逻辑
} 