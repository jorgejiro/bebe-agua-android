package com.jjrapps.bebeagua.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import timber.log.Timber
import java.time.Clock

/**
 * Owns the single inexact alarm that redraws [DrinkWideWidget] after midnight so its text resets
 * for the new day. Inexact on purpose: a few minutes of delay are harmless and it needs no
 * exact-alarm permission. No WorkManager and no `updatePeriodMillis` (see ADR 007).
 */
object WideWidgetMidnightAlarm {

    const val ACTION_MIDNIGHT_REFRESH = "com.jjrapps.bebeagua.ACTION_WIDGET_MIDNIGHT_REFRESH"

    // 0 is the reminder alarm; 1-2 notification actions; 3 daily summary alarm; 4 its content intent.
    private const val REQUEST_CODE = 5

    fun schedule(context: Context, clock: Clock = Clock.systemDefaultZone()) {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val triggerAt = nextMidnightMillis(clock)
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC, triggerAt, pendingIntent(context))
        Timber.d("Scheduled wide widget midnight refresh at $triggerAt")
    }

    /** Schedules only if a 2x1 widget is placed; otherwise makes sure no alarm is left behind. */
    fun scheduleIfPlaced(context: Context, clock: Clock = Clock.systemDefaultZone()) {
        if (isPlaced(context)) schedule(context, clock) else cancel(context)
    }

    fun cancel(context: Context) {
        context.getSystemService(AlarmManager::class.java)?.cancel(pendingIntent(context))
        Timber.d("Wide widget midnight refresh cancelled")
    }

    fun isPlaced(context: Context): Boolean =
        AppWidgetManager.getInstance(context)
            .getAppWidgetIds(ComponentName(context, DrinkWideWidgetReceiver::class.java))
            .isNotEmpty()

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(
            ACTION_MIDNIGHT_REFRESH,
            null,
            context,
            WidgetMidnightReceiver::class.java
        )
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
