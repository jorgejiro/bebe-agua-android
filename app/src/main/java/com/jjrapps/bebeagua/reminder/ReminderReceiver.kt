package com.jjrapps.bebeagua.reminder

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.jjrapps.bebeagua.domain.model.Intake
import com.jjrapps.bebeagua.domain.usecase.GetTodaySummaryUseCase
import com.jjrapps.bebeagua.domain.usecase.ObserveSettingsUseCase
import com.jjrapps.bebeagua.domain.usecase.ScheduleRemindersUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_REMINDER_ALARM = "com.jjrapps.bebeagua.ACTION_REMINDER_ALARM"
    }

    @Inject lateinit var observeSettingsUseCase: ObserveSettingsUseCase
    @Inject lateinit var getTodaySummaryUseCase: GetTodaySummaryUseCase
    @Inject lateinit var scheduleRemindersUseCase: ScheduleRemindersUseCase

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_REMINDER_ALARM) return

        val suggestedAmountMl = intent.getIntExtra(NotificationFactory.EXTRA_AMOUNT_ML, 200)
        val pendingResult = goAsync()

        scope.launch {
            try {
                val settings = observeSettingsUseCase().first()
                val summary = getTodaySummaryUseCase().first()
                val consumedMl = summary.consumedMl

                // Safety net: an alarm already in flight when the user drank must stay silent
                // if it lands inside the grace window.
                val withinGraceWindow = settings.skipImminentReminder &&
                    isWithinGraceWindow(summary.intakes, settings.skipImminentWindowMinutes)

                if (consumedMl < settings.dailyGoalMl &&
                    !withinGraceWindow &&
                    canPostNotifications(context)
                ) {
                    val notification = NotificationFactory.build(
                        context = context,
                        consumedMl = consumedMl,
                        goalMl = settings.dailyGoalMl,
                        suggestedAmountMl = suggestedAmountMl
                    )
                    try {
                        NotificationManagerCompat.from(context)
                            .notify(NotificationFactory.NOTIFICATION_ID, notification)
                    } catch (e: SecurityException) {
                        Timber.w(e, "Notification permission was revoked before posting")
                    }
                }

                scheduleRemindersUseCase()
            } catch (e: Exception) {
                Timber.e(e, "ReminderReceiver error")
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun isWithinGraceWindow(intakes: List<Intake>, windowMinutes: Int): Boolean {
        if (windowMinutes <= 0) return false
        val lastIntakeMs = intakes.maxOfOrNull { it.timestampEpochMs } ?: return false
        val elapsedMs = System.currentTimeMillis() - lastIntakeMs
        return elapsedMs in 0 until windowMinutes * 60_000L
    }

    private fun canPostNotifications(context: Context): Boolean {
        val notificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
        if (!notificationsEnabled) return false

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
