package com.jjrapps.bebeagua.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jjrapps.bebeagua.domain.usecase.ScheduleDailySummaryUseCase
import com.jjrapps.bebeagua.domain.usecase.ScheduleRemindersUseCase
import com.jjrapps.bebeagua.widget.WideWidgetMidnightAlarm
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Clock
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var scheduleRemindersUseCase: ScheduleRemindersUseCase
    @Inject lateinit var scheduleDailySummaryUseCase: ScheduleDailySummaryUseCase
    @Inject lateinit var clock: Clock

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != "android.intent.action.LOCKED_BOOT_COMPLETED"
        ) return

        val pendingResult = goAsync()
        scope.launch {
            // Independent alarms: one failing must not leave the others unscheduled after a reboot.
            try {
                runCatching { scheduleRemindersUseCase() }
                    .onFailure { Timber.e(it, "BootReceiver: reminders not rescheduled") }
                runCatching { scheduleDailySummaryUseCase() }
                    .onFailure { Timber.e(it, "BootReceiver: daily summary not rescheduled") }
                runCatching { WideWidgetMidnightAlarm.scheduleIfPlaced(context.applicationContext, clock) }
                    .onFailure { Timber.e(it, "BootReceiver: widget midnight refresh not rescheduled") }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
