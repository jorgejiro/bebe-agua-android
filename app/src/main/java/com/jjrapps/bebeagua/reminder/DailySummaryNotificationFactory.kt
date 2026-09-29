package com.jjrapps.bebeagua.reminder

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.jjrapps.bebeagua.BebeAguaApplication.Companion.DAILY_SUMMARY_CHANNEL_ID
import com.jjrapps.bebeagua.MainActivity
import com.jjrapps.bebeagua.R
import java.time.LocalDate

object DailySummaryNotificationFactory {

    const val NOTIFICATION_ID = 1002

    /** ISO `yyyy-MM-dd` date of the summarized day, read by the activity to open its detail. */
    const val EXTRA_SUMMARY_DATE = "extra_summary_date"

    // 0 open / 1 drink / 2 snooze are taken by the reminder notification; 3 is the alarm.
    private const val OPEN_REQUEST_CODE = 4

    fun build(
        context: Context,
        date: LocalDate,
        consumedMl: Int,
        goalMl: Int
    ): Notification {
        val openIntent = PendingIntent.getActivity(
            context,
            OPEN_REQUEST_CODE,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_SUMMARY_DATE, date.toString())
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val text = if (goalMl in 1..consumedMl) {
            context.getString(R.string.daily_summary_text_goal_reached, consumedMl, goalMl)
        } else {
            val percent = if (goalMl > 0) consumedMl * 100 / goalMl else 0
            context.getString(R.string.daily_summary_text, consumedMl, goalMl, percent)
        }

        return NotificationCompat.Builder(context, DAILY_SUMMARY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water_drop)
            .setContentTitle(context.getString(R.string.daily_summary_title))
            .setContentText(text)
            .setContentIntent(openIntent)
            .setAutoCancel(true)
            .build()
    }
}
