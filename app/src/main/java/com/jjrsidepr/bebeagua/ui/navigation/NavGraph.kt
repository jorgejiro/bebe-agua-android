package com.jjrsidepr.bebeagua.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jjrsidepr.bebeagua.R
import com.jjrsidepr.bebeagua.ui.history.HistoryScreen
import com.jjrsidepr.bebeagua.ui.home.HomeScreen
import com.jjrsidepr.bebeagua.ui.settings.SettingsScreen
import com.jjrsidepr.bebeagua.ui.theme.AccentLight
import com.jjrsidepr.bebeagua.ui.theme.BackgroundMain
import com.jjrsidepr.bebeagua.ui.theme.BackgroundNav
import com.jjrsidepr.bebeagua.ui.theme.BorderStrong
import com.jjrsidepr.bebeagua.ui.theme.DmSansFontFamily
import com.jjrsidepr.bebeagua.ui.theme.TextMuted

@Composable
fun BebeAguaNavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        containerColor = BackgroundMain,
        topBar = {
            BebeAguaTopNav(
                currentRoute = currentRoute,
                onNavigate = { screen ->
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route)     { HomeScreen() }
            composable(Screen.History.route)  { HistoryScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}

@Composable
private fun BebeAguaTopNav(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit
) {
    val tabs = listOf(
        Screen.Home     to stringResource(R.string.nav_home),
        Screen.History  to stringResource(R.string.nav_history),
        Screen.Settings to stringResource(R.string.nav_settings),
    )

    Column(modifier = Modifier.fillMaxWidth().statusBarsPadding()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundNav),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEach { (screen, label) ->
                val isActive = currentRoute == screen.route
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(screen) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        if (isActive) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .background(AccentLight, CircleShape)
                            )
                        }
                        Text(
                            text = label,
                            fontFamily = DmSansFontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isActive) AccentLight else TextMuted
                        )
                    }
                    if (isActive) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth(0.6f)
                                .height(2.dp)
                                .background(AccentLight)
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(BorderStrong)
        )
    }
}
