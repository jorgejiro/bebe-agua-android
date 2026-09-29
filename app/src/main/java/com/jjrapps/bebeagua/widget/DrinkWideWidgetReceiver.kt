package com.jjrapps.bebeagua.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * Entry point the system uses to render [DrinkWideWidget]. It also owns the lifetime of the
 * midnight refresh alarm: on while at least one 2x1 widget is placed.
 */
class DrinkWideWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DrinkWideWidget

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        WideWidgetMidnightAlarm.schedule(context)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        WideWidgetMidnightAlarm.schedule(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        WideWidgetMidnightAlarm.cancel(context)
    }
}
