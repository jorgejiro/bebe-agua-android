package com.jjrapps.bebeagua.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jjrapps.bebeagua.R
import com.jjrapps.bebeagua.ui.theme.AccentLight
import com.jjrapps.bebeagua.ui.theme.AccentPrimary
import com.jjrapps.bebeagua.ui.theme.BackgroundCard
import com.jjrapps.bebeagua.ui.theme.BackgroundElement
import com.jjrapps.bebeagua.ui.theme.DmMonoFontFamily
import com.jjrapps.bebeagua.ui.theme.DmSansFontFamily
import com.jjrapps.bebeagua.ui.theme.TextDim
import com.jjrapps.bebeagua.ui.theme.TextMuted
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ProgressRing(
    consumedMl: Int,
    goalMl: Int,
    modifier: Modifier = Modifier
) {
    val rawProgress = if (goalMl > 0) consumedMl / goalMl.toFloat() else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress.coerceIn(0f, 1f),
        animationSpec = spring(dampingRatio = 0.8f),
        label = "ring_progress"
    )

    Box(
        modifier = modifier.size(210.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = 9.dp.toPx()
            val radius = (size.minDimension - strokePx) / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            val topLeft = Offset(center.x - radius, center.y - radius)
            val arcSize = Size(radius * 2f, radius * 2f)

            // Background track
            drawCircle(
                color = BackgroundElement,
                radius = radius,
                center = center,
                style = Stroke(width = strokePx)
            )

            // Start dot (dim)
            drawCircle(
                color = AccentPrimary.copy(alpha = 0.5f),
                radius = 5.5.dp.toPx(),
                center = Offset(center.x, center.y - radius)
            )

            if (animatedProgress > 0f) {
                val sweepAngle = 360f * animatedProgress
                drawArc(
                    color = AccentPrimary,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round),
                    topLeft = topLeft,
                    size = arcSize
                )

                // End dot (bright)
                val endRad = Math.toRadians((-90.0 + sweepAngle))
                drawCircle(
                    color = AccentLight,
                    radius = 5.5.dp.toPx(),
                    center = Offset(
                        x = center.x + radius * cos(endRad).toFloat(),
                        y = center.y + radius * sin(endRad).toFloat()
                    )
                )
            }
        }

        // Center info
        Box(
            modifier = Modifier
                .size(166.dp)
                .background(BackgroundCard, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = consumedMl.toString(),
                    fontFamily = DmMonoFontFamily,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium,
                    color = AccentLight
                )
                Text(
                    text = stringResource(R.string.home_goal_format, goalMl),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.home_daily_goal_label).uppercase(),
                    fontFamily = DmSansFontFamily,
                    fontSize = 9.sp,
                    letterSpacing = 1.2.sp,
                    color = TextDim
                )
            }
        }
    }
}
