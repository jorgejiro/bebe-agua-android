package com.jjrapps.bebeagua.reminder

import android.app.NotificationManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.jjrapps.bebeagua.BebeAguaApplication.Companion.REMINDER_CHANNEL_ID
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The reminder channel must stay silent and vibrating. A channel's sound and vibration are frozen
 * on first creation, so a regression here cannot be fixed by an update on devices that already
 * installed it: it would need yet another channel id. Hence the test.
 */
@RunWith(AndroidJUnit4::class)
class ReminderChannelTest {

    private val notificationManager =
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSystemService(NotificationManager::class.java)

    @Test
    fun reminderChannelIsSilentAndVibrates() {
        val channel = requireNotNull(notificationManager.getNotificationChannel(REMINDER_CHANNEL_ID)) {
            "Channel $REMINDER_CHANNEL_ID was not created by the Application"
        }
        assertNull("The reminder channel must have no sound", channel.sound)
        assertTrue("The reminder channel must vibrate", channel.shouldVibrate())
    }

    @Test
    fun legacySoundingChannelIsGone() {
        // Left behind it would show up as a second, sounding row of reminders in system settings.
        assertNull(notificationManager.getNotificationChannel("reminders"))
    }
}
