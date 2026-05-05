package com.jjrapps.bebeagua.data.local.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "intake",
    indices = [Index(value = ["local_date"])]
)
data class IntakeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "amount_ml") val amountMl: Int,
    @ColumnInfo(name = "timestamp_epoch_ms") val timestampEpochMs: Long,
    @ColumnInfo(name = "timezone_id") val timezoneId: String,
    @ColumnInfo(name = "local_date") val localDate: String
)
