package com.example.babycare.utils

import android.content.Context
import android.net.Uri
import com.example.babycare.data.entity.*
import com.opencsv.CSVReader
import com.opencsv.CSVWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 数据导出导入工具类
 */
class DataExportImport(private val context: Context) {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    /**
     * 导出数据到CSV文件
     */
    fun exportToCSV(
        uri: Uri,
        records: ExportData,
        progressCallback: (Float) -> Unit
    ): Flow<Result<Unit>> = flow {
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                CSVWriter(OutputStreamWriter(outputStream)).use { writer ->
                    var progress = 0f
                    val totalRecords = records.getTotalCount()

                    // 写入喂奶记录
                    if (records.feedingRecords.isNotEmpty()) {
                        writer.writeNext(arrayOf("=== 喂奶记录 ==="))
                        writer.writeNext(FEEDING_HEADERS)
                        records.feedingRecords.forEach { record ->
                            writer.writeNext(record.toCSVRow())
                            progress += 1
                            progressCallback(progress / totalRecords)
                        }
                    }

                    // 写入睡眠记录
                    if (records.sleepRecords.isNotEmpty()) {
                        writer.writeNext(arrayOf("=== 睡眠记录 ==="))
                        writer.writeNext(SLEEP_HEADERS)
                        records.sleepRecords.forEach { record ->
                            writer.writeNext(record.toCSVRow())
                            progress += 1
                            progressCallback(progress / totalRecords)
                        }
                    }

                    // 写入其他记录...
                }
            }
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * 从CSV文件导入数据
     */
    fun importFromCSV(
        uri: Uri,
        progressCallback: (Float) -> Unit
    ): Flow<Result<ImportData>> = flow {
        try {
            val importData = ImportData()
            var currentSection = ""
            var lineCount = 0f
            val totalLines = countLines(uri)

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                CSVReader(InputStreamReader(inputStream)).use { reader ->
                    var line: Array<String>?
                    while (reader.readNext().also { line = it } != null) {
                        line?.let { row ->
                            when {
                                row[0].startsWith("===") -> {
                                    currentSection = row[0]
                                }
                                row[0] == "时间" -> {
                                    // 跳过表头
                                }
                                else -> {
                                    when (currentSection) {
                                        "=== 喂奶记录 ===" -> importData.feedingRecords.add(row.toFeedingRecord())
                                        "=== 睡眠记录 ===" -> importData.sleepRecords.add(row.toSleepRecord())
                                        // 其他记录类型...
                                    }
                                }
                            }
                        }
                        lineCount++
                        progressCallback(lineCount / totalLines)
                    }
                }
            }
            emit(Result.success(importData))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    private fun countLines(uri: Uri): Int {
        var count = 0
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            CSVReader(InputStreamReader(inputStream)).use { reader ->
                while (reader.readNext() != null) count++
            }
        }
        return count
    }

    companion object {
        private val FEEDING_HEADERS = arrayOf("时间", "类型", "奶量(ml)", "时长(分钟)", "备注")
        private val SLEEP_HEADERS = arrayOf("开始时间", "结束时间", "时长(分钟)", "质量", "备注")
        // 其他记录类型的表头...
    }
}

/**
 * 要导出的数据
 */
data class ExportData(
    val feedingRecords: List<FeedingRecord> = emptyList(),
    val sleepRecords: List<SleepRecord> = emptyList(),
    val diaperRecords: List<DiaperRecord> = emptyList(),
    val medicineRecords: List<MedicineRecord> = emptyList(),
    val waterRecords: List<WaterRecord> = emptyList(),
    val growthRecords: List<GrowthRecord> = emptyList()
) {
    fun getTotalCount() = feedingRecords.size + sleepRecords.size + 
        diaperRecords.size + medicineRecords.size + waterRecords.size + growthRecords.size
}

/**
 * 导入的数据
 */
data class ImportData(
    val feedingRecords: MutableList<FeedingRecord> = mutableListOf(),
    val sleepRecords: MutableList<SleepRecord> = mutableListOf(),
    val diaperRecords: MutableList<DiaperRecord> = mutableListOf(),
    val medicineRecords: MutableList<MedicineRecord> = mutableListOf(),
    val waterRecords: MutableList<WaterRecord> = mutableListOf(),
    val growthRecords: MutableList<GrowthRecord> = mutableListOf()
)

// 扩展函数：实体转CSV行
private fun FeedingRecord.toCSVRow(): Array<String> = arrayOf(
    LocalDateTime.ofEpochSecond(startTime / 1000, 0, java.time.ZoneOffset.UTC)
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
    if (feedingType == 1) "瓶喂" else "亲喂",
    amount?.toString() ?: "",
    if (endTime != null) ((endTime - startTime) / 60000).toString() else "",
    note ?: ""
)

// 扩展函数：CSV行转实体
private fun Array<String>.toFeedingRecord(): FeedingRecord {
    val time = LocalDateTime.parse(this[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
    val duration = this[3].toIntOrNull()
    return FeedingRecord(
        babyId = 1, // 导入时需要指定babyId
        startTime = time.toEpochSecond(java.time.ZoneOffset.UTC) * 1000,
        endTime = if (duration != null) {
            time.plusMinutes(duration.toLong()).toEpochSecond(java.time.ZoneOffset.UTC) * 1000
        } else null,
        feedingType = if (this[1] == "瓶喂") 1 else 0,
        amount = this[2].toIntOrNull(),
        note = this[4].takeIf { it.isNotEmpty() }
    )
}

// 其他实体的转换函数... 