package com.jjrsidepr.bebeagua.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jjrsidepr.bebeagua.domain.usecase.ScheduleRemindersUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var scheduleRemindersUseCase: ScheduleRemindersUseCase

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != "android.intent.action.LOCKED_BOOT_COMPLETED"
        ) return

        val pendingResult = goAsync()
        scope.launch {
            try {
                scheduleRemindersUseCase()
            } catch (e: Exception) {
                Timber.e(e, "BootReceiver error")
            } finally {
                pendingResult.finish()
            }
        }
    }
}
