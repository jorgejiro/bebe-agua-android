package com.jjrapps.bebeagua.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.jjrapps.bebeagua.domain.repository.DailySummaryScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmManagerDailySummaryScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : DailySummaryScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(triggerAtMs: Long) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S &&
            alarmManager?.canScheduleExactAlarms() == false
        ) {
            Timber.w("Cannot schedule the daily summary — exact alarm permission not granted")
            return
        }
        alarmManager?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMs,
            buildPendingIntent()
        )
        Timber.d("Scheduled daily summary at $triggerAtMs")
    }

    override fun cancel() {
        alarmManager?.cancel(buildPendingIntent())
        Timber.d("Daily summary cancelled")
    }

    private fun buildPendingIntent(): PendingIntent {
        val intent = Intent(
            DailySummaryReceiver.ACTION_DAILY_SUMMARY_ALARM,
            null,
            context,
            DailySummaryReceiver::class.java
        )
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private companion object {
        // 0 is the reminder alarm; 1-2 are the reminder notification actions.
        const val REQUEST_CODE = 3
    }
}
