@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val darkMode: Flow<Boolean> = dataStore.data
        .map { it[DARK_MODE] ?: false }
        
    val notificationsEnabled: Flow<Boolean> = dataStore.data
        .map { it[NOTIFICATIONS_ENABLED] ?: true }
        
    val quietHours: Flow<Pair<LocalTime, LocalTime>> = dataStore.data
        .map {
            Pair(
                it[QUIET_HOURS_START]?.let { time -> LocalTime.ofSecondOfDay(time) }
                    ?: LocalTime.of(22, 0),
                it[QUIET_HOURS_END]?.let { time -> LocalTime.ofSecondOfDay(time) }
                    ?: LocalTime.of(6, 0)
            )
        }
        
    suspend fun updateDarkMode(enabled: Boolean) {
        dataStore.edit { it[DARK_MODE] = enabled }
    }
    
    // ... 其他设置项
} 