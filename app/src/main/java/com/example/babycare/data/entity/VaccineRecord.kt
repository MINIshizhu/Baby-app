@Entity(
    tableName = "vaccine_records",
    foreignKeys = [
        ForeignKey(
            entity = Baby::class,
            parentColumns = ["id"],
            childColumns = ["babyId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class VaccineRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val babyId: Long,
    val vaccineId: Long, // 关联疫苗信息
    val time: Long,
    val hospital: String,
    val doctor: String? = null,
    val reaction: Int? = null, // 0:无 1:轻微 2:一般 3:严重
    val note: String? = null,
    val nextTime: Long? = null, // 下次接种时间
    val createdAt: Long = System.currentTimeMillis()
) 