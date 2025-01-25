val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 添加新字段
        database.execSQL(
            "ALTER TABLE babies ADD COLUMN isSelected INTEGER NOT NULL DEFAULT 0"
        )
    }
} 