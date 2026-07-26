package com.jjrapps.bebeagua.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/** Entry point the system uses to render [DrinkWidget]. */
class DrinkWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DrinkWidget
}
