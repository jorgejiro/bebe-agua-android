package com.jjrsidepr.bebeagua.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jjrsidepr.bebeagua.R
import com.jjrsidepr.bebeagua.domain.model.DayHistory
import com.jjrsidepr.bebeagua.ui.theme.AccentLight
import com.jjrsidepr.bebeagua.ui.theme.AccentPrimary
import com.jjrsidepr.bebeagua.ui.theme.BackgroundCard
import com.jjrsidepr.bebeagua.ui.theme.BackgroundElement
import com.jjrsidepr.bebeagua.ui.theme.BackgroundMain
import com.jjrsidepr.bebeagua.ui.theme.BorderSubtle
import com.jjrsidepr.bebeagua.ui.theme.DmMonoFontFamily
import com.jjrsidepr.bebeagua.ui.theme.DmSansFontFamily
import com.jjrsidepr.bebeagua.ui.theme.SuccessBorder
import com.jjrsidepr.bebeagua.ui.theme.SuccessGreen
import com.jjrsidepr.bebeagua.ui.theme.TextMuted
import com.jjrsidepr.bebeagua.ui.theme.TextPrimary
import com.jjrsidepr.bebeagua.ui.theme.TextSecondary
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundMain)
    ) {
        when (val state = uiState) {
            HistoryUiState.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = AccentLight
            )
            is HistoryUiState.Error -> Text(
                text = state.message,
                color = AccentLight,
                modifier = Modifier.align(Alignment.Center)
            )
            is HistoryUiState.Success -> HistoryContent(state)
        }
    }
}

@Composable
private fun HistoryContent(state: HistoryUiState.Success) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            StatsRow(
                averageMl = state.averageMl,
                bestDayMl = state.bestDayMl,
                streakDays = state.streakDays
            )
            Spacer(Modifier.height(4.dp))
        }

        if (state.days.all { it.totalMl == 0 }) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundCard, RoundedCornerShape(14.dp))
                        .border(0.5.dp, BorderSubtle, RoundedCornerShape(14.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.history_no_data),
                        color = TextMuted,
                        fontFamily = DmSansFontFamily,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundCard, RoundedCornerShape(14.dp))
                        .border(0.5.dp, BorderSubtle, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                ) {
                    Column {
                        state.days.filter { it.totalMl > 0 }.forEachIndexed { index, day ->
                            DayHistoryItem(day)
                            if (index < state.days.filter { it.totalMl > 0 }.lastIndex) {
                                HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsRow(averageMl: Int, bestDayMl: Int, streakDays: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            label = stringResource(R.string.history_average_label),
            value = "$averageMl ml",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = stringResource(R.string.history_best_label),
            value = "$bestDayMl ml",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = stringResource(R.string.history_streak_label),
            value = "$streakDays ${stringResource(R.string.history_days_suffix)}",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(BackgroundCard, RoundedCornerShape(12.dp))
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontFamily = DmMonoFontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = AccentLight
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            fontFamily = DmSansFontFamily,
            fontSize = 10.sp,
            color = TextMuted
        )
    }
}

private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)

@Composable
private fun DayHistoryItem(day: DayHistory) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = day.date.format(dateFormatter),
                fontFamily = DmSansFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { day.progressFraction.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = if (day.isGoalReached) SuccessGreen else AccentPrimary,
                trackColor = BackgroundElement,
                strokeCap = StrokeCap.Round
            )
        }
        Spacer(Modifier.width(12.dp))
        if (day.isGoalReached) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = stringResource(R.string.history_goal_reached),
                tint = SuccessGreen,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = "${day.totalMl} ml",
            fontFamily = DmMonoFontFamily,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (day.isGoalReached) SuccessGreen else TextSecondary
        )
    }
}
