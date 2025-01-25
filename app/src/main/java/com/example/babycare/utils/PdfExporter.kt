package com.example.babycare.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.babycare.data.entity.*
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.*
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * PDF导出工具类
 */
class PdfExporter(private val context: Context) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val primaryColor = DeviceRgb(255, 152, 0) // 主题色

    /**
     * 导出数据到PDF
     */
    fun exportToPdf(
        uri: Uri,
        data: ExportData,
        charts: List<Bitmap>? = null,
        progressCallback: (Float) -> Unit
    ): Flow<Result<Unit>> = flow {
        try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                PdfWriter(outputStream).use { writer ->
                    PdfDocument(writer).use { pdf ->
                        Document(pdf, PageSize.A4).use { document ->
                            // 添加标题
                            addTitle(document, "宝宝成长记录")
                            
                            var progress = 0f
                            val totalSections = data.getTotalCount() + (charts?.size ?: 0)

                            // 添加统计图表
                            charts?.forEach { chart ->
                                addChart(document, chart)
                                progress += 1
                                progressCallback(progress / totalSections)
                            }

                            // 添加喂奶记录
                            if (data.feedingRecords.isNotEmpty()) {
                                addSection(document, "喂奶记录")
                                addFeedingRecords(document, data.feedingRecords) {
                                    progress += 1
                                    progressCallback(progress / totalSections)
                                }
                            }

                            // 添加睡眠记录
                            if (data.sleepRecords.isNotEmpty()) {
                                addSection(document, "睡眠记录")
                                addSleepRecords(document, data.sleepRecords) {
                                    progress += 1
                                    progressCallback(progress / totalSections)
                                }
                            }

                            // 添加其他记录...
                        }
                    }
                }
            }
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    private fun addTitle(document: Document, title: String) {
        val paragraph = Paragraph(title)
            .setFontSize(24f)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontColor(primaryColor)
            .setMarginBottom(20f)
        document.add(paragraph)
    }

    private fun addSection(document: Document, title: String) {
        val paragraph = Paragraph(title)
            .setFontSize(18f)
            .setFontColor(primaryColor)
            .setMarginTop(15f)
            .setMarginBottom(10f)
        document.add(paragraph)
    }

    private fun addChart(document: Document, chart: Bitmap) {
        val image = com.itextpdf.layout.element.Image(
            com.itextpdf.io.image.ImageDataFactory.create(
                chart.toByteArray()
            )
        ).setWidth(UnitValue.createPercentValue(100f))
        document.add(image)
    }

    private fun addFeedingRecords(
        document: Document,
        records: List<FeedingRecord>,
        onProgress: () -> Unit
    ) {
        val table = Table(floatArrayOf(2f, 1f, 1f, 1f, 2f))
            .setWidth(UnitValue.createPercentValue(100f))

        // 添加表头
        arrayOf("时间", "类型", "奶量(ml)", "时长(分钟)", "备注").forEach { header ->
            table.addHeaderCell(
                Cell().add(Paragraph(header))
                    .setBackgroundColor(primaryColor)
                    .setFontColor(DeviceRgb(255, 255, 255))
            )
        }

        // 添加数据行
        records.forEach { record ->
            val time = LocalDateTime.ofEpochSecond(record.startTime / 1000, 0, java.time.ZoneOffset.UTC)
            table.addCell(Cell().add(Paragraph(time.format(dateFormatter))))
            table.addCell(Cell().add(Paragraph(if (record.feedingType == 1) "瓶喂" else "亲喂")))
            table.addCell(Cell().add(Paragraph(record.amount?.toString() ?: "-")))
            table.addCell(Cell().add(Paragraph(
                if (record.endTime != null) 
                    ((record.endTime - record.startTime) / 60000).toString() 
                else "-"
            )))
            table.addCell(Cell().add(Paragraph(record.note ?: "-")))
            onProgress()
        }

        document.add(table)
    }

    private fun addSleepRecords(
        document: Document,
        records: List<SleepRecord>,
        onProgress: () -> Unit
    ) {
        // 类似的表格实现...
    }

    // 其他记录类型的添加方法...

    private fun Bitmap.toByteArray(): ByteArray {
        val stream = java.io.ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
} 