package com.jjrapps.bebeagua.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import com.jjrapps.bebeagua.R

/**
 * 1x1 home screen widget: the app icon artwork with an "add" badge. A tap logs the default intake
 * amount, exactly like the main button on the home screen.
 *
 * It renders no data on purpose, so there is nothing to keep in sync and no periodic update:
 * [provideGlance] runs once when the widget is placed.
 *
 * Everything is sized as a fraction of the cell instead of in fixed dp: on dense launcher grids
 * (e.g. 8x6 in Nova Launcher) a 1x1 cell can be little more than 40dp, and a fixed badge ends up
 * dwarfing the drop. [SizeMode.Exact] gives us the real cell size to scale against.
 *
 * The artwork is laid out inside a square of the cell's shorter side and scaled with
 * [ContentScale.Fit], so it is never cropped: a 1x1 cell is not necessarily square (in a 8x6 grid
 * it is clearly taller than wide) and filling the longer side would cut the drop off.
 */
object DrinkWidget : GlanceAppWidget() {

    /** We need the actual cell size to keep the badge proportional to the artwork. */
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { Content() }
    }

    @Composable
    private fun Content() {
        val context = LocalContext.current
        val size = LocalSize.current
        val side = minOf(size.width, size.height)
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .clickable(actionRunCallback<AddDefaultIntakeAction>()),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = GlanceModifier
                    .size(side)
                    .cornerRadius(side * CORNER_RADIUS_RATIO),
                contentAlignment = Alignment.Center
            ) {
                // The artwork is a crop of the launcher icon without the adaptive-icon safe zone,
                // so the drop is as big here as in the app icon the launcher draws next to it.
                Image(
                    provider = ImageProvider(R.drawable.ic_widget_icon),
                    contentDescription = context
                        .getString(R.string.widget_drink_content_description),
                    contentScale = ContentScale.Fit,
                    modifier = GlanceModifier.fillMaxSize()
                )
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .padding(side * BADGE_INSET_RATIO),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.ic_widget_add_badge),
                        contentDescription = null,
                        modifier = GlanceModifier.size(badgeSize(side))
                    )
                }
            }
        }
    }
}

/** Badge diameter for a cell of [side], clamped so it stays legible without hiding the drop. */
internal fun badgeSize(side: Dp): Dp = (side * BADGE_RATIO).coerceIn(BADGE_MIN, BADGE_MAX)

private const val BADGE_RATIO = 0.36f
private const val BADGE_INSET_RATIO = 0.02f
private const val CORNER_RADIUS_RATIO = 0.22f
private val BADGE_MIN = 15.dp
private val BADGE_MAX = 28.dp
