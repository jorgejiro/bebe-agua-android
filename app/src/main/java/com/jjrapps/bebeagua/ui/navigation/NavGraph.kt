package com.jjrapps.bebeagua.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jjrapps.bebeagua.R
import com.jjrapps.bebeagua.ui.changelog.ChangelogScreen
import com.jjrapps.bebeagua.ui.daydetail.DayDetailScreen
import com.jjrapps.bebeagua.ui.history.HistoryScreen
import com.jjrapps.bebeagua.ui.home.HomeScreen
import com.jjrapps.bebeagua.ui.main.MainViewModel
import com.jjrapps.bebeagua.ui.main.PendingNavigation
import com.jjrapps.bebeagua.ui.onboarding.OnboardingScreen
import com.jjrapps.bebeagua.ui.settings.SettingsScreen
import com.jjrapps.bebeagua.ui.theme.AccentLight
import com.jjrapps.bebeagua.ui.theme.BackgroundMain
import com.jjrapps.bebeagua.ui.theme.BackgroundNav
import com.jjrapps.bebeagua.ui.theme.BorderStrong
import com.jjrapps.bebeagua.ui.theme.DmSansFontFamily
import com.jjrapps.bebeagua.ui.theme.TextMuted

@Composable
fun BebeAguaNavGraph(mainViewModel: MainViewModel = hiltViewModel()) {
    val isOnboardingDone by mainViewModel.isOnboardingDone.collectAsStateWithLifecycle()
    val pendingNavigation by mainViewModel.pendingNavigation.collectAsStateWithLifecycle()

    when (isOnboardingDone) {
        null -> Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundMain)
        )
        false -> OnboardingScreen(onFinished = {})
        true -> MainScaffold(
            pendingNavigation = pendingNavigation,
            onNavigationConsumed = mainViewModel::consumePendingNavigation
        )
    }
}

@Composable
private fun MainScaffold(
    pendingNavigation: PendingNavigation?,
    onNavigationConsumed: () -> Unit
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Open the destination requested by a notification tap, once per tap. Wait for the
    // NavHost (composed later, inside the Scaffold content) to set the graph: a non-null back stack
    // entry means it has. Any day detail already open is replaced, since launchSingleTop would
    // match on the route pattern and keep showing the previous date. Home pops everything above the
    // start destination, so a reminder tap never leaves a stale detail screen on top.
    val graphReady = backStackEntry != null
    LaunchedEffect(pendingNavigation, graphReady) {
        if (!graphReady) return@LaunchedEffect
        when (val request = pendingNavigation) {
            null -> return@LaunchedEffect
            PendingNavigation.Home -> navController.popBackStack(
                navController.graph.findStartDestination().id,
                inclusive = false
            )
            is PendingNavigation.DayDetail ->
                navController.navigate(Screen.DayDetail.createRoute(request.date)) {
                    popUpTo(Screen.DayDetail.route) { inclusive = true }
                }
        }
        onNavigationConsumed()
    }

    // Changelog is reached from Settings, and day detail from History: keep those tabs highlighted.
    val activeRoute = when (currentRoute) {
        Screen.Changelog.route -> Screen.Settings.route
        Screen.DayDetail.route -> Screen.History.route
        else -> currentRoute
    }

    Scaffold(
        containerColor = BackgroundMain,
        topBar = {
            BebeAguaTopNav(
                currentRoute = activeRoute,
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
            composable(Screen.History.route)  {
                HistoryScreen(
                    onDayClick = { date ->
                        navController.navigate(Screen.DayDetail.createRoute(date))
                    }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onOpenChangelog = { navController.navigate(Screen.Changelog.route) }
                )
            }
            composable(Screen.Changelog.route) {
                ChangelogScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = Screen.DayDetail.route,
                arguments = listOf(navArgument(Screen.DayDetail.ARG_DATE) { type = NavType.StringType })
            ) {
                DayDetailScreen(onBack = { navController.popBackStack() })
            }
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
