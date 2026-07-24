package com.jjrapps.bebeagua.domain.usecase

import java.time.LocalTime

/**
 * Earliest local time a reminder is allowed to fire when [offsetMinutes] are added to [from].
 *
 * Returns null when the offset crosses midnight: in that case no slot is valid for the rest
 * of the day and the caller must fall back to tomorrow's schedule.
 */
fun reminderCutoff(from: LocalTime, offsetMinutes: Long): LocalTime? {
    if (offsetMinutes <= 0L) return from
    if (offsetMinutes >= 24 * 60) return null
    return from.plusMinutes(offsetMinutes).takeIf { it > from }
}

/**
 * Earliest local time a reminder is allowed to fire given the post-intake grace window.
 *
 * When the user drinks at [lastIntakeAt], any reminder scheduled within the next
 * [graceMinutes] is skipped. Returns [now] when the feature does not apply, or null when
 * the grace window crosses midnight.
 */
fun graceWindowCutoff(now: LocalTime, lastIntakeAt: LocalTime?, graceMinutes: Int): LocalTime? {
    if (lastIntakeAt == null || graceMinutes <= 0 || lastIntakeAt > now) return now
    val graceEnd = reminderCutoff(lastIntakeAt, graceMinutes.toLong()) ?: return null
    return if (graceEnd > now) graceEnd else now
}
