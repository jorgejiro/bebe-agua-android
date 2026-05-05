package com.jjrapps.bebeagua

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.jjrapps.bebeagua.ui.navigation.BebeAguaNavGraph
import com.jjrapps.bebeagua.ui.theme.BebeAguaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
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
