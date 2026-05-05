package com.jjrapps.bebeagua

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jjrapps.bebeagua.ui.navigation.BebeAguaNavGraph
import com.jjrapps.bebeagua.ui.theme.BebeAguaTheme
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
