package com.jjrapps.bebeagua.ui.daydetail

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jjrapps.bebeagua.R
import com.jjrapps.bebeagua.domain.model.DaySummary
import com.jjrapps.bebeagua.domain.model.Intake
import com.jjrapps.bebeagua.ui.common.IntakeRecordItem
import com.jjrapps.bebeagua.ui.common.ProgressRing
import com.jjrapps.bebeagua.ui.theme.AccentLight
import com.jjrapps.bebeagua.ui.theme.BackgroundCard
import com.jjrapps.bebeagua.ui.theme.BackgroundMain
import com.jjrapps.bebeagua.ui.theme.BebeAguaTheme
import com.jjrapps.bebeagua.ui.theme.BorderSubtle
import com.jjrapps.bebeagua.ui.theme.DmSansFontFamily
import com.jjrapps.bebeagua.ui.theme.TextMuted
import com.jjrapps.bebeagua.ui.theme.TextPrimary
import com.jjrapps.bebeagua.ui.theme.TextSecondary
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun DayDetailScreen(
    onBack: () -> Unit,
    viewModel: DayDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundMain)
    ) {
        when (val state = uiState) {
            DayDetailUiState.Loading -> {
                DayDetailHeader(title = "", onBack = onBack)
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AccentLight
                    )
                }
            }
            is DayDetailUiState.Error -> {
                DayDetailHeader(title = "", onBack = onBack)
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = state.message,
                        color = AccentLight,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            is DayDetailUiState.Success -> DayDetailContent(
                summary = state.summary,
                onBack = onBack,
                onDeleteIntake = viewModel::onDeleteIntake
            )
        }
    }
}

@Composable
private fun DayDetailHeader(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 20.dp, top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.day_detail_back),
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = title,
            fontFamily = DmSansFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}

@Composable
internal fun DayDetailContent(
    summary: DaySummary,
    onBack: () -> Unit,
    onDeleteIntake: (Long) -> Unit
) {
    val title = remember(summary.date) {
        summary.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        DayDetailHeader(title = title, onBack = onBack)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                ProgressRing(consumedMl = summary.consumedMl, goalMl = summary.goalMl)
                Spacer(Modifier.height(24.dp))
            }

            item {
                Text(
                    text = stringResource(R.string.day_detail_intakes),
                    fontFamily = DmSansFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundCard, RoundedCornerShape(14.dp))
                        .border(0.5.dp, BorderSubtle, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                ) {
                    if (summary.intakes.isEmpty()) {
                        Text(
                            text = stringResource(R.string.day_detail_empty),
                            color = TextMuted,
                            fontFamily = DmSansFontFamily,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp)
                        )
                    } else {
                        Column {
                            summary.intakes.forEachIndexed { index, intake ->
                                IntakeRecordItem(
                                    intake = intake,
                                    onDelete = { onDeleteIntake(intake.id) }
                                )
                                if (index < summary.intakes.lastIndex) {
                                    HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun previewIntake(id: Long, hour: Int, amountMl: Int): Intake {
    val date = LocalDate.of(2026, 7, 24)
    val zone = ZoneId.of("Europe/Madrid")
    return Intake(
        id = id,
        amountMl = amountMl,
        timestampEpochMs = date.atTime(hour, 0).atZone(zone).toInstant().toEpochMilli(),
        timezoneId = zone.id,
        localDate = date
    )
}

@PreviewLightDark
@Composable
private fun DayDetailContentPreview() {
    BebeAguaTheme {
        Box(modifier = Modifier.background(BackgroundMain)) {
            DayDetailContent(
                summary = DaySummary(
                    date = LocalDate.of(2026, 7, 24),
                    consumedMl = 1800,
                    goalMl = 2400,
                    intakes = listOf(
                        previewIntake(3, 18, 300),
                        previewIntake(2, 13, 500),
                        previewIntake(1, 9, 1000)
                    )
                ),
                onBack = {},
                onDeleteIntake = {}
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun DayDetailEmptyPreview() {
    BebeAguaTheme {
        Box(modifier = Modifier.background(BackgroundMain)) {
            DayDetailContent(
                summary = DaySummary(
                    date = LocalDate.of(2026, 7, 24),
                    consumedMl = 0,
                    goalMl = 2400,
                    intakes = emptyList()
                ),
                onBack = {},
                onDeleteIntake = {}
            )
        }
    }
}
