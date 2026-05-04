package com.jjrsidepr.bebeagua.domain.usecase

import java.time.LocalTime
import javax.inject.Inject

class CalculateReminderTimesUseCase @Inject constructor() {
    /**
     * Distributes [count] reminder times uniformly within [startMinutes]..[endMinutes]
     * using midpoint placement so the first reminder isn't right at the window edge.
     *
     * Example: 08:00–23:00 (900 min), 6 reminders → interval 150 min
     * → 09:15, 11:45, 14:15, 16:45, 19:15, 21:45
     */
    operator fun invoke(startMinutes: Int, endMinutes: Int, count: Int): List<LocalTime> {
        if (count <= 0 || startMinutes >= endMinutes) return emptyList()
        val interval = (endMinutes - startMinutes) / count
        return (0 until count).map { i ->
            val totalMinutes = startMinutes + interval / 2 + i * interval
            LocalTime.of(totalMinutes / 60, totalMinutes % 60)
        }
    }
}
