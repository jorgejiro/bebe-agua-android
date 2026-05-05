package com.jjrapps.bebeagua.data.local.db

import androidx.room.ColumnInfo

data class DayTotal(
    @ColumnInfo(name = "local_date") val localDate: String,
    @ColumnInfo(name = "total_ml") val totalMl: Int
)
