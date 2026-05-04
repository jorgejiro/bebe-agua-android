package com.jjrsidepr.bebeagua

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jjrsidepr.bebeagua.ui.navigation.BebeAguaNavGraph
import com.jjrsidepr.bebeagua.ui.theme.BebeAguaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BebeAguaTheme {
                BebeAguaNavGraph()
            }
        }
    }
}
