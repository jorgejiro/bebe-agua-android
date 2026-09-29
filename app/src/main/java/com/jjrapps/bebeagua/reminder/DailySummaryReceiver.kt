package com.jjrapps.bebeagua.reminder

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.jjrapps.bebeagua.domain.repository.IntakeRepository
import com.jjrapps.bebeagua.domain.repository.SettingsRepository
import com.jjrapps.bebeagua.domain.usecase.ScheduleDailySummaryUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class DailySummaryReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_DAILY_SUMMARY_ALARM = "com.jjrapps.bebeagua.ACTION_DAILY_SUMMARY_ALARM"
    }

    @Inject lateinit var settingsRepository: SettingsRepository
    @Inject lateinit var intakeRepository: IntakeRepository
    @Inject lateinit var scheduleDailySummaryUseCase: ScheduleDailySummaryUseCase
    @Inject lateinit var clock: Clock

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_DAILY_SUMMARY_ALARM) return

        val pendingResult = goAsync()
        scope.launch {
            try {
                // Date at firing time: tapping the notification after midnight still shows this day.
                val date = LocalDate.now(clock)
                val consumedMl = intakeRepository.observeTotalForDate(date).first()
                val goalMl = settingsRepository.observeSettings().first().dailyGoalMl

                // Posted even when the goal was reached: it is a summary, not a reminder.
                if (canPostNotifications(context)) {
                    val notification = DailySummaryNotificationFactory.build(
                        context = context,
                        date = date,
                        consumedMl = consumedMl,
                        goalMl = goalMl
                    )
                    try {
                        NotificationManagerCompat.from(context)
                            .notify(DailySummaryNotificationFactory.NOTIFICATION_ID, notification)
                    } catch (e: SecurityException) {
                        Timber.w(e, "Notification permission was revoked before posting")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "DailySummaryReceiver error")
            } finally {
                try {
                    scheduleDailySummaryUseCase()
                } catch (e: Exception) {
                    Timber.e(e, "DailySummaryReceiver could not reschedule")
                }
                pendingResult.finish()
            }
        }
    }

    private fun canPostNotifications(context: Context): Boolean {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return false
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
