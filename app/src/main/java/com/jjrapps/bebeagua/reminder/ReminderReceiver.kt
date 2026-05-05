package com.jjrapps.bebeagua.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
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

                if (consumedMl < settings.dailyGoalMl) {
                    val notification = NotificationFactory.build(
                        context = context,
                        consumedMl = consumedMl,
                        goalMl = settings.dailyGoalMl,
                        suggestedAmountMl = suggestedAmountMl
                    )
                    NotificationManagerCompat.from(context)
                        .notify(NotificationFactory.NOTIFICATION_ID, notification)
                }

                scheduleRemindersUseCase()
            } catch (e: Exception) {
                Timber.e(e, "ReminderReceiver error")
            } finally {
                pendingResult.finish()
            }
        }
    }
}
