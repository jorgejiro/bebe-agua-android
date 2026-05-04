package com.jjrsidepr.bebeagua.reminder

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.jjrsidepr.bebeagua.BebeAguaApplication.Companion.REMINDER_CHANNEL_ID
import com.jjrsidepr.bebeagua.MainActivity
import com.jjrsidepr.bebeagua.R

object NotificationFactory {

    const val NOTIFICATION_ID = 1001
    const val EXTRA_AMOUNT_ML = "extra_amount_ml"
    const val ACTION_DRINK = "com.jjrsidepr.bebeagua.ACTION_DRINK"
    const val ACTION_SNOOZE = "com.jjrsidepr.bebeagua.ACTION_SNOOZE"

    fun build(
        context: Context,
        consumedMl: Int,
        goalMl: Int,
        suggestedAmountMl: Int
    ): Notification {
        val openIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val drinkIntent = PendingIntent.getBroadcast(
            context,
            1,
            Intent(ACTION_DRINK, null, context, NotificationActionReceiver::class.java).apply {
                putExtra(EXTRA_AMOUNT_ML, suggestedAmountMl)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = PendingIntent.getBroadcast(
            context,
            2,
            Intent(ACTION_SNOOZE, null, context, NotificationActionReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water_drop)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_text, consumedMl, goalMl))
            .setContentIntent(openIntent)
            .setAutoCancel(true)
            .addAction(
                0,
                context.getString(R.string.notification_action_drink, suggestedAmountMl),
                drinkIntent
            )
            .addAction(
                0,
                context.getString(R.string.notification_action_snooze),
                snoozeIntent
            )
            .build()
    }
}
