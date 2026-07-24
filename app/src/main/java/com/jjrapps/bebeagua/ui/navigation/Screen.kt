package com.jjrapps.bebeagua.ui.navigation

sealed class Screen(val route: String) {
    data object Home     : Screen("home")
    data object History  : Screen("history")
    data object Settings : Screen("settings")

    /** Not a top-level tab: opened from Settings. */
    data object Changelog : Screen("changelog")
}
