package com.example.babycare.ui.settings

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.babycare.data.repository.UserPreferencesRepository
import com.example.babycare.data.repository.FeedingRecordRepository
import com.example.babycare.data.repository.SleepRecordRepository
import com.example.babycare.data.repository.DiaperRecordRepository
import com.example.babycare.data.repository.MedicineRecordRepository
import com.example.babycare.data.repository.WaterRecordRepository
import com.example.babycare.data.repository.GrowthRecordRepository
import com.example.babycare.ui.base.BaseViewModel
import com.example.babycare.utils.DataExportImport
import com.example.babycare.utils.PdfExporter
import com.example.babycare.utils.ChartGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

/**
 * 设置页面视图状态
 */
data class SettingsViewState(
    val darkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val reminderSettings: ReminderSettings = ReminderSettings(),
    val exportProgress: Float? = null,
    val importProgress: Float? = null,
    val showExportTypeDialog: Boolean = false,
    val showImportDialog: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * 提醒设置数据
 */
data class ReminderSettings(
    val feedingInterval: Int = 180,  // 喂奶提醒间隔(分钟)
    val feedingEnabled: Boolean = true,
    val sleepEnabled: Boolean = true,
    val diaperEnabled: Boolean = true,
    val medicineEnabled: Boolean = true,
    val quietHoursEnabled: Boolean = true,
    val quietHoursStart: LocalTime = LocalTime.of(22, 0),
    val quietHoursEnd: LocalTime = LocalTime.of(6, 0)
)

/**
 * 设置页面事件
 */
sealed class SettingsEvent {
    data class UpdateDarkMode(val enabled: Boolean) : SettingsEvent()
    data class UpdateNotifications(val enabled: Boolean) : SettingsEvent()
    data class UpdateFeedingInterval(val minutes: Int) : SettingsEvent()
    data class UpdateReminderEnabled(
        val type: ReminderType,
        val enabled: Boolean
    ) : SettingsEvent()
    data class UpdateQuietHours(
        val start: LocalTime,
        val end: LocalTime
    ) : SettingsEvent()
    object ExportData : SettingsEvent()
    object ImportData : SettingsEvent()
    object ClearData : SettingsEvent()
    data class SelectExportTypes(val types: Set<RecordType>) : SettingsEvent()
    data class ExportToFile(val uri: Uri) : SettingsEvent()
    data class ImportFromFile(val uri: Uri) : SettingsEvent()
    data class ExportToPdf(
        val uri: Uri,
        val types: Set<RecordType>,
        val includeCharts: Boolean
    ) : SettingsEvent()
}

enum class ReminderType {
    FEEDING, SLEEP, DIAPER, MEDICINE
}

enum class RecordType {
    FEEDING, SLEEP, DIAPER, MEDICINE, WATER, GROWTH
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    private val feedingRepository: FeedingRecordRepository,
    private val sleepRepository: SleepRecordRepository,
    private val diaperRepository: DiaperRecordRepository,
    private val medicineRepository: MedicineRecordRepository,
    private val waterRepository: WaterRecordRepository,
    private val growthRepository: GrowthRecordRepository,
    private val dataExportImport: DataExportImport,
    private val pdfExporter: PdfExporter,
    private val chartGenerator: ChartGenerator
) : BaseViewModel<SettingsViewState, SettingsEvent>() {

    override fun initViewState() = SettingsViewState()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            
            try {
                combine(
                    preferencesRepository.getDarkMode(),
                    preferencesRepository.getNotificationsEnabled(),
                    preferencesRepository.getReminderSettings()
                ) { darkMode, notifications, reminderSettings ->
                    updateState { state ->
                        state.copy(
                            darkMode = darkMode,
                            notificationsEnabled = notifications,
                            reminderSettings = reminderSettings,
                            isLoading = false
                        )
                    }
                }.catch { error ->
                    updateState { it.copy(error = error.message, isLoading = false) }
                }.collect()
            } catch (e: Exception) {
                updateState { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    override fun handleEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.UpdateDarkMode -> updateDarkMode(event.enabled)
            is SettingsEvent.UpdateNotifications -> updateNotifications(event.enabled)
            is SettingsEvent.UpdateFeedingInterval -> updateFeedingInterval(event.minutes)
            is SettingsEvent.UpdateReminderEnabled -> updateReminderEnabled(event.type, event.enabled)
            is SettingsEvent.UpdateQuietHours -> updateQuietHours(event.start, event.end)
            is SettingsEvent.ExportData -> exportData()
            is SettingsEvent.ImportData -> importData()
            is SettingsEvent.ClearData -> clearData()
            is SettingsEvent.SelectExportTypes -> selectExportTypes(event.types)
            is SettingsEvent.ExportToFile -> exportData(event.uri)
            is SettingsEvent.ImportFromFile -> importData(event.uri)
            is SettingsEvent.ExportToPdf -> exportToPdf(event.uri, event.types, event.includeCharts)
        }
    }

    private fun updateDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesRepository.setDarkMode(enabled)
                updateState { it.copy(darkMode = enabled) }
            } catch (e: Exception) {
                updateState { it.copy(error = e.message) }
            }
        }
    }

    private fun updateNotifications(enabled: Boolean) {
        viewModelScope.launch {
            try {
                preferencesRepository.setNotificationsEnabled(enabled)
                updateState { it.copy(notificationsEnabled = enabled) }
            } catch (e: Exception) {
                updateState { it.copy(error = e.message) }
            }
        }
    }

    private fun updateFeedingInterval(minutes: Int) {
        viewModelScope.launch {
            try {
                val currentSettings = viewState.value.reminderSettings
                val newSettings = currentSettings.copy(feedingInterval = minutes)
                preferencesRepository.setReminderSettings(newSettings)
                updateState { it.copy(reminderSettings = newSettings) }
            } catch (e: Exception) {
                updateState { it.copy(error = e.message) }
            }
        }
    }

    private fun updateReminderEnabled(type: ReminderType, enabled: Boolean) {
        viewModelScope.launch {
            try {
                val currentSettings = viewState.value.reminderSettings
                val newSettings = when (type) {
                    ReminderType.FEEDING -> currentSettings.copy(feedingEnabled = enabled)
                    ReminderType.SLEEP -> currentSettings.copy(sleepEnabled = enabled)
                    ReminderType.DIAPER -> currentSettings.copy(diaperEnabled = enabled)
                    ReminderType.MEDICINE -> currentSettings.copy(medicineEnabled = enabled)
                }
                preferencesRepository.setReminderSettings(newSettings)
                updateState { it.copy(reminderSettings = newSettings) }
            } catch (e: Exception) {
                updateState { it.copy(error = e.message) }
            }
        }
    }

    private fun updateQuietHours(start: LocalTime, end: LocalTime) {
        viewModelScope.launch {
            try {
                val currentSettings = viewState.value.reminderSettings
                val newSettings = currentSettings.copy(
                    quietHoursEnabled = true,
                    quietHoursStart = start,
                    quietHoursEnd = end
                )
                preferencesRepository.setReminderSettings(newSettings)
                updateState { it.copy(reminderSettings = newSettings) }
            } catch (e: Exception) {
                updateState { it.copy(error = e.message) }
            }
        }
    }

    private fun exportData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                // TODO: 实现数据导出逻辑
                // 1. 收集所有数据
                // 2. 转换为JSON或其他格式
                // 3. 保存到文件
                updateState { it.copy(isLoading = false) }
            } catch (e: Exception) {
                updateState { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun importData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                // TODO: 实现数据导入逻辑
                // 1. 读取文件
                // 2. 解析数据
                // 3. 保存到数据库
                updateState { it.copy(isLoading = false) }
            } catch (e: Exception) {
                updateState { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun clearData() {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                // TODO: 实现数据清除逻辑
                // 1. 清除所有记录数据
                // 2. 保留用户设置
                updateState { it.copy(isLoading = false) }
            } catch (e: Exception) {
                updateState { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun selectExportTypes(types: Set<RecordType>) {
        // Implementation needed
    }

    private fun exportData(uri: Uri) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                // 收集选中类型的数据
                val exportData = ExportData(
                    feedingRecords = if (RecordType.FEEDING in types) 
                        feedingRepository.getAllFeedingRecords().first() else emptyList(),
                    sleepRecords = if (RecordType.SLEEP in types)
                        sleepRepository.getAllSleepRecords().first() else emptyList(),
                    // ... 其他记录类型 ...
                )

                // 导出到CSV
                dataExportImport.exportToCSV(uri, exportData) { progress ->
                    updateState { it.copy(exportProgress = progress) }
                }.collect { result ->
                    result.onSuccess {
                        updateState { it.copy(
                            isLoading = false,
                            exportProgress = null,
                            error = null
                        ) }
                    }.onFailure { error ->
                        updateState { it.copy(
                            isLoading = false,
                            exportProgress = null,
                            error = error.message
                        ) }
                    }
                }
            } catch (e: Exception) {
                updateState { it.copy(
                    isLoading = false,
                    exportProgress = null,
                    error = e.message
                ) }
            }
        }
    }

    private fun importData(uri: Uri) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                dataExportImport.importFromCSV(uri) { progress ->
                    updateState { it.copy(importProgress = progress) }
                }.collect { result ->
                    result.onSuccess { importData ->
                        // 保存导入的数据
                        importData.feedingRecords.forEach { feedingRepository.insert(it) }
                        importData.sleepRecords.forEach { sleepRepository.insert(it) }
                        // ... 其他记录类型 ...

                        updateState { it.copy(
                            isLoading = false,
                            importProgress = null,
                            error = null
                        ) }
                    }.onFailure { error ->
                        updateState { it.copy(
                            isLoading = false,
                            importProgress = null,
                            error = error.message
                        ) }
                    }
                }
            } catch (e: Exception) {
                updateState { it.copy(
                    isLoading = false,
                    importProgress = null,
                    error = e.message
                ) }
            }
        }
    }

    private fun exportToPdf(uri: Uri, types: Set<RecordType>, includeCharts: Boolean) {
        viewModelScope.launch {
            updateState { it.copy(isLoading = true) }
            try {
                // 收集选中类型的数据
                val exportData = ExportData(
                    feedingRecords = if (RecordType.FEEDING in types) 
                        feedingRepository.getAllFeedingRecords().first() else emptyList(),
                    sleepRecords = if (RecordType.SLEEP in types)
                        sleepRepository.getAllSleepRecords().first() else emptyList(),
                    // ... 其他记录类型 ...
                )

                // 生成统计图表
                val charts = if (includeCharts) {
                    chartGenerator.generateCharts(exportData)
                } else null

                // 导出到PDF
                pdfExporter.exportToPdf(uri, exportData, charts) { progress ->
                    updateState { it.copy(exportProgress = progress) }
                }.collect { result ->
                    result.onSuccess {
                        updateState { it.copy(
                            isLoading = false,
                            exportProgress = null,
                            error = null
                        ) }
                    }.onFailure { error ->
                        updateState { it.copy(
                            isLoading = false,
                            exportProgress = null,
                            error = error.message
                        ) }
                    }
                }
            } catch (e: Exception) {
                updateState { it.copy(
                    isLoading = false,
                    exportProgress = null,
                    error = e.message
                ) }
            }
        }
    }
} 