package com.jjrapps.bebeagua.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.jjrapps.bebeagua.domain.usecase.AddIntakeUseCase
import com.jjrapps.bebeagua.domain.usecase.ScheduleRemindersUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActionReceiver : BroadcastReceiver() {

    @Inject lateinit var addIntakeUseCase: AddIntakeUseCase
    @Inject lateinit var scheduleRemindersUseCase: ScheduleRemindersUseCase

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        scope.launch {
            try {
                when (intent.action) {
                    NotificationFactory.ACTION_DRINK -> {
                        val amountMl = intent.getIntExtra(NotificationFactory.EXTRA_AMOUNT_ML, 200)
                        addIntakeUseCase(amountMl)
                        scheduleRemindersUseCase()
                        NotificationManagerCompat.from(context)
                            .cancel(NotificationFactory.NOTIFICATION_ID)
                    }
                    NotificationFactory.ACTION_SNOOZE -> {
                        scheduleRemindersUseCase(postponeMinutes = 15)
                        NotificationManagerCompat.from(context)
                            .cancel(NotificationFactory.NOTIFICATION_ID)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "NotificationActionReceiver error")
            } finally {
                pendingResult.finish()
            }
        }
    }
}
