package com.jjrapps.bebeagua.widget

import java.time.Clock
import java.time.Instant

/**
 * First instant of the next local day, as epoch millis. Computed with calendar arithmetic
 * (`plusDays(1).atStartOfDay(zone)`) instead of adding 24 h, so 23 h and 25 h DST days are right.
 */
internal fun nextMidnightMillis(clock: Clock): Long =
    Instant.now(clock).atZone(clock.zone)
        .toLocalDate()
        .plusDays(1)
        .atStartOfDay(clock.zone)
        .toInstant()
        .toEpochMilli()
