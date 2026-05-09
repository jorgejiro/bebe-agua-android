package com.jjrapps.bebeagua.domain.usecase

import java.time.LocalTime
import javax.inject.Inject

class CalculateReminderTimesUseCase @Inject constructor() {
    /**
     * Distributes [count] reminder times across [startMinutes]..[endMinutes].
     * The first reminder is anchored at [startMinutes]; the rest are evenly spaced.
     *
     * Example: 08:00–23:00 (900 min), 6 reminders → interval 180 min
     * → 08:00, 11:00, 14:00, 17:00, 20:00, 23:00
     */
    operator fun invoke(startMinutes: Int, endMinutes: Int, count: Int): List<LocalTime> {
        if (count <= 0 || startMinutes >= endMinutes) return emptyList()
        if (count == 1) return listOf(LocalTime.of(startMinutes / 60, startMinutes % 60))
        val interval = (endMinutes - startMinutes) / (count - 1)
        return (0 until count).map { i ->
            val totalMinutes = startMinutes + i * interval
            LocalTime.of(totalMinutes / 60, totalMinutes % 60)
        }
    }
}
