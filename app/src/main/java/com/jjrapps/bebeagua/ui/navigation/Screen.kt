package com.jjrapps.bebeagua.ui.navigation

import java.time.LocalDate

sealed class Screen(val route: String) {
    data object Home     : Screen("home")
    data object History  : Screen("history")
    data object Settings : Screen("settings")

    /** Not a top-level tab: opened from Settings. */
    data object Changelog : Screen("changelog")

    /** Not a top-level tab: opened from History rows and from the daily summary notification. */
    data object DayDetail : Screen("day/{date}") {
        const val ARG_DATE = "date"

        fun createRoute(date: LocalDate): String = "day/$date"
    }
}
