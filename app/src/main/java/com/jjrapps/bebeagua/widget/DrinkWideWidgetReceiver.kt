package com.jjrapps.bebeagua.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/** Entry point the system uses to render [DrinkWideWidget]. */
class DrinkWideWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DrinkWideWidget
}
