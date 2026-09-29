package com.jjrapps.bebeagua.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.text.TextAlign
import androidx.glance.unit.ColorProvider
import com.jjrapps.bebeagua.R
import com.jjrapps.bebeagua.domain.model.DaySummary
import com.jjrapps.bebeagua.domain.usecase.GetTodaySummaryUseCase
import com.jjrapps.bebeagua.ui.theme.BackgroundMain
import com.jjrapps.bebeagua.ui.theme.SuccessGreen
import com.jjrapps.bebeagua.ui.theme.TextPrimary
import com.jjrapps.bebeagua.ui.theme.TextSecondary
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import timber.log.Timber

/**
 * 2x1 home screen widget: the same icon + "add" badge as [DrinkWidget] (a tap logs the default
 * amount) next to today's progress (`1250 / 2400 ml`).
 *
 * Unlike the 1x1 it renders data, so it must be invalidated whenever today's total or the goal
 * change: see `WidgetUpdater` and the midnight alarm. Everything is proportional to the cell size
 * ([SizeMode.Exact]), never fixed dp.
 */
object DrinkWideWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface Dependencies {
        fun getTodaySummaryUseCase(): GetTodaySummaryUseCase
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Read before provideContent: the content lambda must stay free of I/O.
        val summary = runCatching {
            EntryPointAccessors
                .fromApplication(context.applicationContext, Dependencies::class.java)
                .getTodaySummaryUseCase()()
                .first()
        }.onFailure { Timber.e(it, "Wide widget could not read today's summary") }.getOrNull()
        provideContent { Content(summary) }
    }

    @Composable
    private fun Content(summary: DaySummary?) {
        val context = LocalContext.current
        val size = LocalSize.current
        val layout = wideLayout(size.width, size.height)
        val consumed = summary?.consumedMl ?: 0
        val goal = summary?.goalMl ?: 0
        val reached = summary != null && isGoalReached(consumed, goal)

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(BackgroundMain))
                .cornerRadius(size.height * CORNER_RADIUS_RATIO)
                .clickable(actionRunCallback<AddDefaultIntakeAction>())
                .padding(layout.padding),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DrinkIconWithBadge(layout.iconSide)
                Spacer(GlanceModifier.width(layout.padding))
                Column(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (summary == null) {
                            context.getString(R.string.widget_wide_no_data)
                        } else {
                            context.getString(R.string.widget_wide_progress, consumed, goal)
                        },
                        maxLines = 1,
                        style = TextStyle(
                            color = ColorProvider(TextPrimary),
                            fontSize = layout.primaryTextSize,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start
                        )
                    )
                    if (summary != null) {
                        Text(
                            text = if (reached) {
                                context.getString(R.string.widget_wide_goal_reached)
                            } else {
                                context.getString(
                                    R.string.widget_wide_percent,
                                    progressPercent(consumed, goal)
                                )
                            },
                            maxLines = 1,
                            style = TextStyle(
                                color = ColorProvider(if (reached) SuccessGreen else TextSecondary),
                                fontSize = layout.secondaryTextSize
                            )
                        )
                    }
                }
            }
        }
    }
}

/** Sizes of the 2x1 widget parts, all derived from the cell so nothing is a fixed dp. */
internal data class WideLayout(
    val padding: Dp,
    val iconSide: Dp,
    val primaryTextSize: TextUnit,
    val secondaryTextSize: TextUnit
)

/**
 * Icon square = cell height minus padding, but never more than [ICON_MAX_WIDTH_RATIO] of the width
 * so the text keeps room on dense launcher grids. Text sizes follow the height and shrink with the
 * width left for the text, within sane clamps.
 */
internal fun wideLayout(width: Dp, height: Dp): WideLayout {
    val padding = height * PADDING_RATIO
    val iconSide = minOf(height - padding * 2, width * ICON_MAX_WIDTH_RATIO)
    val textWidth = (width - iconSide - padding * 3).coerceAtLeast(0.dp)
    val primary = minOf(height.value * PRIMARY_HEIGHT_RATIO, textWidth.value / PRIMARY_CHARS_WIDTH)
        .coerceIn(PRIMARY_MIN_SP, PRIMARY_MAX_SP)
    val secondary = (primary * SECONDARY_RATIO).coerceIn(SECONDARY_MIN_SP, SECONDARY_MAX_SP)
    return WideLayout(padding, iconSide, primary.sp, secondary.sp)
}

/** Whole-number percentage of the goal, clamped to 0-100; 0 when there is no valid goal. */
internal fun progressPercent(consumedMl: Int, goalMl: Int): Int =
    if (goalMl <= 0) 0 else (consumedMl * 100L / goalMl).toInt().coerceIn(0, 100)

internal fun isGoalReached(consumedMl: Int, goalMl: Int): Boolean =
    goalMl > 0 && consumedMl >= goalMl

private const val PADDING_RATIO = 0.10f
private const val ICON_MAX_WIDTH_RATIO = 0.38f
private const val PRIMARY_HEIGHT_RATIO = 0.28f
private const val PRIMARY_CHARS_WIDTH = 8.2f
private const val PRIMARY_MIN_SP = 10f
private const val PRIMARY_MAX_SP = 22f
private const val SECONDARY_RATIO = 0.75f
private const val SECONDARY_MIN_SP = 9f
private const val SECONDARY_MAX_SP = 15f
