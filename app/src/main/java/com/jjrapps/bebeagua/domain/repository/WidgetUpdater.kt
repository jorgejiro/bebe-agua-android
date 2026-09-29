package com.jjrapps.bebeagua.domain.repository

/**
 * Redraws the home screen widgets that render data (today's total and goal). Implementations must
 * never throw: a widget failure must not break logging an intake or saving a setting.
 */
interface WidgetUpdater {
    suspend fun refresh()
}
