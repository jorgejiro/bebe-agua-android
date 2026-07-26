package com.jjrapps.bebeagua.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.size
import com.jjrapps.bebeagua.R

/**
 * 1x1 home screen widget: the launcher icon with an "add" badge. A tap logs the default intake
 * amount, exactly like the main button on the home screen.
 *
 * It renders no data on purpose, so there is nothing to keep in sync and no periodic update:
 * [provideGlance] runs once when the widget is placed.
 */
object DrinkWidget : GlanceAppWidget() {

    /** Same layout at every size: the widget is a fixed 1x1 button. */
    override val sizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { Content() }
    }

    @Composable
    private fun Content() {
        val context = LocalContext.current
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .clickable(actionRunCallback<AddDefaultIntakeAction>()),
            contentAlignment = Alignment.Center
        ) {
            Image(
                provider = ImageProvider(R.drawable.ic_launcher_fg),
                contentDescription = context.getString(R.string.widget_drink_content_description),
                contentScale = ContentScale.Fit,
                modifier = GlanceModifier.fillMaxSize()
            )
            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_widget_add_badge),
                    contentDescription = null,
                    modifier = GlanceModifier.size(BADGE_SIZE_DP)
                )
            }
        }
    }
}

private val BADGE_SIZE_DP = 22.dp
