package com.jjrapps.bebeagua.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jjrapps.bebeagua.domain.repository.WidgetUpdater
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Clock
import javax.inject.Inject

/**
 * Redraws the 2x1 widget at midnight and reschedules the next one. Also reacts to clock and time
 * zone changes, which move "midnight" and may leave the widget showing another day.
 */
@AndroidEntryPoint
class WidgetMidnightReceiver : BroadcastReceiver() {

    @Inject lateinit var widgetUpdater: WidgetUpdater
    @Inject lateinit var clock: Clock

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WideWidgetMidnightAlarm.ACTION_MIDNIGHT_REFRESH,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> Unit
            else -> return
        }

        val appContext = context.applicationContext
        val pendingResult = goAsync()
        scope.launch {
            try {
                widgetUpdater.refresh()
                WideWidgetMidnightAlarm.scheduleIfPlaced(appContext, clock)
            } catch (e: Exception) {
                Timber.e(e, "WidgetMidnightReceiver error")
            } finally {
                pendingResult.finish()
            }
        }
    }
}
