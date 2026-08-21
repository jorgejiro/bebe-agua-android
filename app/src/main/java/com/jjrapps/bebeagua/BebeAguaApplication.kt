package com.jjrapps.bebeagua

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BebeAguaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        createNotificationChannels()
    }

    /**
     * A channel's sound, vibration and importance are frozen the first time it is created:
     * calling [NotificationManager.createNotificationChannel] again with different values is a
     * no-op on every device that already has the channel, because from then on those settings
     * belong to the user. Turning the reminders silent-but-vibrating therefore needs a *new*
     * channel id, and the old one has to be deleted so the system settings do not end up listing
     * two rows of reminders.
     */
    private fun createNotificationChannels() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.deleteNotificationChannel(LEGACY_REMINDER_CHANNEL_ID)
        val channel = NotificationChannel(
            REMINDER_CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = getString(R.string.notification_channel_desc)
            // The whole point: vibrate so the reminder is noticeable with the phone in ring mode,
            // but never make a sound. The user can put the sound back from the system settings,
            // reachable from Settings → Permissions → Notification settings.
            setSound(null, null)
            enableVibration(true)
        }
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val REMINDER_CHANNEL_ID = "reminders_vibrate"

        /** Channel used up to 1.3.0: default sound, no vibration. Deleted on startup. */
        private const val LEGACY_REMINDER_CHANNEL_ID = "reminders"
    }
}
