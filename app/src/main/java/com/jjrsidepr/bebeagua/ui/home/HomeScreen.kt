package com.jjrsidepr.bebeagua.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material.icons.outlined.WatchLater
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jjrsidepr.bebeagua.R
import com.jjrsidepr.bebeagua.domain.model.Intake
import com.jjrsidepr.bebeagua.ui.common.IntakeRecordItem
import com.jjrsidepr.bebeagua.ui.common.ProgressRing
import com.jjrsidepr.bebeagua.ui.theme.AccentGlow
import com.jjrsidepr.bebeagua.ui.theme.AccentLight
import com.jjrsidepr.bebeagua.ui.theme.AccentPrimary
import com.jjrsidepr.bebeagua.ui.theme.BackgroundCard
import com.jjrsidepr.bebeagua.ui.theme.BackgroundElement
import com.jjrsidepr.bebeagua.ui.theme.BackgroundMain
import com.jjrsidepr.bebeagua.ui.theme.BorderDefault
import com.jjrsidepr.bebeagua.ui.theme.BorderSubtle
import com.jjrsidepr.bebeagua.ui.theme.DmMonoFontFamily
import com.jjrsidepr.bebeagua.ui.theme.DmSansFontFamily
import com.jjrsidepr.bebeagua.ui.theme.TextMuted
import com.jjrsidepr.bebeagua.ui.theme.TextOnAccent
import com.jjrsidepr.bebeagua.ui.theme.TextOnAccentSoft
import com.jjrsidepr.bebeagua.ui.theme.TextPrimary
import com.jjrsidepr.bebeagua.ui.theme.TextSecondary
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is HomeEvent.Error) {
                snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            HomeUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AccentLight
                )
            }
            is HomeUiState.Error -> {
                Text(
                    text = state.message,
                    color = AccentLight,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is HomeUiState.Success -> {
                HomeContent(
                    state = state,
                    onAddIntake = viewModel::onAddIntake,
                    onDeleteIntake = viewModel::onDeleteIntake
                )
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    state: HomeUiState.Success,
    onAddIntake: (Int) -> Unit,
    onDeleteIntake: (Long) -> Unit
) {
    var showSizeSelector by rememberSaveable { mutableStateOf(false) }
    var showCustomDialog by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundMain),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            ProgressRing(
                consumedMl = state.summary.consumedMl,
                goalMl = state.summary.goalMl
            )
            Spacer(Modifier.height(10.dp))
            NextReminderPill(nextTime = state.nextReminderTime)
            Spacer(Modifier.height(28.dp))
            // FAB row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.size(52.dp))
                Spacer(Modifier.width(16.dp))
                AddIntakeFab(
                    amountMl = state.defaultIntakeSizeMl,
                    onClick = { onAddIntake(state.defaultIntakeSizeMl) }
                )
                Spacer(Modifier.width(16.dp))
                ChangeSizeButton(onClick = { showSizeSelector = true })
            }
            Spacer(Modifier.height(28.dp))
            // Records header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.home_today_intakes),
                    fontFamily = DmSansFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        if (state.summary.intakes.isEmpty()) {
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
                        text = stringResource(R.string.home_no_intakes),
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
                        // Next reminder row (always first)
                        state.nextReminderTime?.let { nextTime ->
                            NextReminderListItem(time = nextTime)
                            HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)
                        }
                        state.summary.intakes.forEachIndexed { index, intake ->
                            IntakeRecordItem(
                                intake = intake,
                                onDelete = { onDeleteIntake(intake.id) }
                            )
                            if (index < state.summary.intakes.lastIndex) {
                                HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }

    // Size selector bottom sheet
    if (showSizeSelector) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showSizeSelector = false },
            sheetState = sheetState,
            containerColor = BackgroundCard
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                Text(
                    text = stringResource(R.string.home_select_size),
                    fontFamily = DmSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                state.availableSizesMl.forEach { size ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onAddIntake(size)
                                showSizeSelector = false
                            }
                            .padding(vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.home_ml_format, size),
                            fontFamily = DmMonoFontFamily,
                            fontSize = 16.sp,
                            color = if (size == state.defaultIntakeSizeMl) AccentLight else TextPrimary
                        )
                    }
                    HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)
                }
                TextButton(
                    onClick = {
                        showSizeSelector = false
                        showCustomDialog = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.home_add_custom),
                        color = TextMuted
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    // Custom amount dialog
    if (showCustomDialog) {
        var input by rememberSaveable { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCustomDialog = false },
            title = { Text(stringResource(R.string.home_custom_amount_title)) },
            text = {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it.filter(Char::isDigit) },
                    label = { Text(stringResource(R.string.home_custom_amount_hint)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    suffix = { Text("ml") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    input.toIntOrNull()?.takeIf { it > 0 }?.let { onAddIntake(it) }
                    showCustomDialog = false
                }) {
                    Text(stringResource(R.string.add))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            containerColor = BackgroundCard
        )
    }
}

@Composable
private fun NextReminderPill(
    nextTime: LocalTime?,
    modifier: Modifier = Modifier
) {
    val timeStr = remember(nextTime) {
        nextTime?.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    Row(
        modifier = modifier
            .background(BackgroundElement, RoundedCornerShape(20.dp))
            .border(0.5.dp, BorderDefault, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(AccentGlow, CircleShape)
        )
        Text(
            text = stringResource(R.string.home_next_reminder),
            fontFamily = DmSansFontFamily,
            fontSize = 11.sp,
            color = TextMuted
        )
        Text(
            text = timeStr ?: stringResource(R.string.home_no_reminder),
            fontFamily = DmSansFontFamily,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
        )
    }
}

@Composable
private fun AddIntakeFab(
    amountMl: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(104.dp)
            .border(2.dp, AccentPrimary, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(92.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF1A7FD4), Color(0xFF0E4A8A))
                    )
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "+",
                    fontFamily = DmSansFontFamily,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Light,
                    color = TextOnAccent,
                    lineHeight = 38.sp
                )
                Text(
                    text = stringResource(R.string.home_ml_format, amountMl),
                    fontFamily = DmMonoFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextOnAccentSoft
                )
            }
        }
    }
}

@Composable
private fun ChangeSizeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .size(52.dp)
            .background(BackgroundElement, RoundedCornerShape(16.dp))
            .border(0.5.dp, BorderDefault, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.SwapVert,
            contentDescription = stringResource(R.string.home_measure),
            tint = TextMuted,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = stringResource(R.string.home_measure),
            fontFamily = DmSansFontFamily,
            fontSize = 9.sp,
            color = TextMuted
        )
    }
}

@Composable
private fun NextReminderListItem(
    time: LocalTime,
    modifier: Modifier = Modifier
) {
    val timeStr = remember(time) { time.format(DateTimeFormatter.ofPattern("HH:mm")) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0B2540))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(BackgroundCard, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.WatchLater,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = timeStr,
                fontFamily = DmSansFontFamily,
                fontSize = 13.sp,
                color = TextMuted
            )
            Text(
                text = stringResource(R.string.home_upcoming_reminder),
                fontFamily = DmSansFontFamily,
                fontSize = 11.sp,
                color = TextMuted
            )
        }
        Text(
            text = "— ml",
            fontFamily = DmMonoFontFamily,
            fontSize = 13.sp,
            color = TextMuted.copy(alpha = 0.5f)
        )
    }
}
