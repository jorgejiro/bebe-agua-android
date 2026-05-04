package com.jjrsidepr.bebeagua.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.jjrsidepr.bebeagua.domain.repository.ReminderScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmManagerReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) : ReminderScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun scheduleNext(triggerAtMs: Long, suggestedAmountMl: Int) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S &&
            alarmManager?.canScheduleExactAlarms() == false
        ) {
            Timber.w("Cannot schedule exact alarms — permission not granted")
            return
        }
        alarmManager?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMs,
            buildPendingIntent(suggestedAmountMl)
        )
        Timber.d("Scheduled reminder at $triggerAtMs for $suggestedAmountMl ml")
    }

    override fun cancel() {
        alarmManager?.cancel(buildPendingIntent(0))
        Timber.d("Reminder cancelled")
    }

    private fun buildPendingIntent(suggestedAmountMl: Int): PendingIntent {
        val intent = Intent(
            ReminderReceiver.ACTION_REMINDER_ALARM,
            null,
            context,
            ReminderReceiver::class.java
        ).apply {
            putExtra(NotificationFactory.EXTRA_AMOUNT_ML, suggestedAmountMl)
        }
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
