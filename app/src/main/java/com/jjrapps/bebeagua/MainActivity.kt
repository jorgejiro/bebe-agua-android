package com.jjrapps.bebeagua

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.jjrapps.bebeagua.reminder.DailySummaryNotificationFactory
import com.jjrapps.bebeagua.reminder.NotificationFactory
import com.jjrapps.bebeagua.ui.main.MainViewModel
import com.jjrapps.bebeagua.ui.navigation.BebeAguaNavGraph
import com.jjrapps.bebeagua.ui.theme.BebeAguaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    // Same instance the nav graph gets from hiltViewModel(): both are scoped to this activity.
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // After a configuration change the original intent is still attached: only a fresh
        // launch must forward it, otherwise rotating would reopen the detail screen.
        if (savedInstanceState == null) forwardNotificationIntent(intent)
        setContent {
            BebeAguaTheme {
                BebeAguaNavGraph()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        forwardNotificationIntent(intent)
    }

    private fun forwardNotificationIntent(intent: Intent?) {
        mainViewModel.onNotificationIntentReceived(
            summaryIsoDate = intent?.getStringExtra(DailySummaryNotificationFactory.EXTRA_SUMMARY_DATE),
            openHome = intent?.getBooleanExtra(NotificationFactory.EXTRA_OPEN_HOME, false) == true
        )
    }
}
