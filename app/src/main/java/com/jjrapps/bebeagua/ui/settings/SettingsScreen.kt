package com.jjrapps.bebeagua.ui.settings

import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.provider.Settings
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jjrapps.bebeagua.R
import com.jjrapps.bebeagua.domain.model.AppSettings
import com.jjrapps.bebeagua.ui.theme.AccentLight
import com.jjrapps.bebeagua.ui.theme.BackgroundCard
import com.jjrapps.bebeagua.ui.theme.BackgroundElement
import com.jjrapps.bebeagua.ui.theme.BackgroundMain
import com.jjrapps.bebeagua.ui.theme.BorderDefault
import com.jjrapps.bebeagua.ui.theme.BorderSubtle
import com.jjrapps.bebeagua.ui.theme.DmMonoFontFamily
import com.jjrapps.bebeagua.ui.theme.DmSansFontFamily
import com.jjrapps.bebeagua.ui.theme.SuccessGreen
import com.jjrapps.bebeagua.ui.theme.TextMuted
import com.jjrapps.bebeagua.ui.theme.TextPrimary
import com.jjrapps.bebeagua.ui.theme.TextSecondary
import com.jjrapps.bebeagua.ui.theme.WarnYellow
import java.time.LocalTime

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is SettingsEvent.Error) snackbarHostState.showSnackbar(event.message)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundMain)
    ) {
        when (val state = uiState) {
            SettingsUiState.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = AccentLight
            )
            is SettingsUiState.Error -> Text(
                text = state.message,
                color = AccentLight,
                modifier = Modifier.align(Alignment.Center)
            )
            is SettingsUiState.Success -> SettingsContent(
                state = state,
                onUpdateGoal = viewModel::updateDailyGoal,
                onUpdateWindow = viewModel::updateDayWindow,
                onUpdateReminders = viewModel::updateRemindersPerDay,
                onUpdateSizes = viewModel::updateIntakeSizes,
                onUpdateLanguage = viewModel::updateLanguage
            )
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsContent(
    state: SettingsUiState.Success,
    onUpdateGoal: (Int) -> Unit,
    onUpdateWindow: (Int, Int) -> Unit,
    onUpdateReminders: (Int) -> Unit,
    onUpdateSizes: (List<Int>) -> Unit,
    onUpdateLanguage: (String) -> Unit
) {
    val context = LocalContext.current
    val settings = state.settings

    var showGoalDialog by rememberSaveable { mutableStateOf(false) }
    var showStartTimePicker by rememberSaveable { mutableStateOf(false) }
    var showEndTimePicker by rememberSaveable { mutableStateOf(false) }
    var showAddSizeDialog by rememberSaveable { mutableStateOf(false) }
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Goal section
        item {
            SectionHeader(stringResource(R.string.settings_section_goal))
            Spacer(Modifier.height(8.dp))
            SettingsCard {
                SettingRow(
                    label = stringResource(R.string.settings_daily_goal),
                    subtitle = stringResource(R.string.settings_daily_goal_subtitle),
                    value = "${settings.dailyGoalMl} ml",
                    onClick = { showGoalDialog = true }
                )
            }
        }

        // Reminders section
        item {
            SectionHeader(stringResource(R.string.settings_section_reminders))
            Spacer(Modifier.height(8.dp))
            SettingsCard {
                SettingRow(
                    label = stringResource(R.string.settings_start_time),
                    value = settings.dayStart.toHhMm(),
                    onClick = { showStartTimePicker = true }
                )
                HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)
                SettingRow(
                    label = stringResource(R.string.settings_end_time),
                    value = settings.dayEnd.toHhMm(),
                    onClick = { showEndTimePicker = true }
                )
                HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)
                RemindersPerDayRow(
                    count = settings.remindersPerDay,
                    onDecrement = { if (settings.remindersPerDay > 1) onUpdateReminders(settings.remindersPerDay - 1) },
                    onIncrement = { if (settings.remindersPerDay < 24) onUpdateReminders(settings.remindersPerDay + 1) }
                )
                if (state.calculatedTimes.isNotEmpty()) {
                    HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)
                    SchedulePreview(
                        times = state.calculatedTimes,
                        suggestedAmountMl = settings.dailyGoalMl / settings.remindersPerDay
                    )
                }
            }
        }

        // Intake sizes section
        item {
            SectionHeader(stringResource(R.string.settings_section_sizes))
            Spacer(Modifier.height(8.dp))
            SettingsCard {
                settings.intakeSizesMl.forEachIndexed { index, size ->
                    IntakeSizeRow(
                        size = size,
                        canDelete = settings.intakeSizesMl.size > 1,
                        onDelete = { onUpdateSizes(settings.intakeSizesMl.filter { it != size }) }
                    )
                    if (index < settings.intakeSizesMl.lastIndex) {
                        HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)
                    }
                }
                HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)
                TextButton(
                    onClick = { showAddSizeDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.settings_add_size),
                        color = AccentLight,
                        fontFamily = DmSansFontFamily,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Appearance section
        item {
            SectionHeader(stringResource(R.string.settings_section_appearance))
            Spacer(Modifier.height(8.dp))
            SettingsCard {
                SettingRow(
                    label = stringResource(R.string.settings_language),
                    value = languageLabel(settings.language),
                    onClick = { showLanguageDialog = true }
                )
            }
        }

        // Permissions section
        item {
            SectionHeader(stringResource(R.string.settings_section_permissions))
            Spacer(Modifier.height(8.dp))
            SettingsCard {
                PermissionRow(
                    label = stringResource(R.string.settings_notifications),
                    granted = state.notificationsGranted,
                    onClick = {
                        if (!state.notificationsGranted) {
                            context.startActivity(
                                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                            )
                        }
                    }
                )
                HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)
                PermissionRow(
                    label = stringResource(R.string.settings_exact_alarms),
                    subtitle = stringResource(R.string.settings_exact_alarms_subtitle),
                    granted = state.exactAlarmsGranted,
                    onClick = {
                        if (!state.exactAlarmsGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            context.startActivity(
                                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                    data = android.net.Uri.fromParts("package", context.packageName, null)
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                            )
                        }
                    }
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }

    // Dialogs
    if (showGoalDialog) {
        GoalDialog(
            current = settings.dailyGoalMl,
            onConfirm = { onUpdateGoal(it); showGoalDialog = false },
            onDismiss = { showGoalDialog = false }
        )
    }

    if (showStartTimePicker) {
        val state2 = rememberTimePickerState(
            initialHour = settings.dayStart.hour,
            initialMinute = settings.dayStart.minute,
            is24Hour = true
        )
        TimePickerDialog(
            onConfirm = {
                val startMin = state2.hour * 60 + state2.minute
                onUpdateWindow(startMin, settings.dayEndMinutes)
                showStartTimePicker = false
            },
            onDismiss = { showStartTimePicker = false }
        ) { TimePicker(state = state2) }
    }

    if (showEndTimePicker) {
        val state2 = rememberTimePickerState(
            initialHour = settings.dayEnd.hour,
            initialMinute = settings.dayEnd.minute,
            is24Hour = true
        )
        TimePickerDialog(
            onConfirm = {
                val endMin = state2.hour * 60 + state2.minute
                onUpdateWindow(settings.dayStartMinutes, endMin)
                showEndTimePicker = false
            },
            onDismiss = { showEndTimePicker = false }
        ) { TimePicker(state = state2) }
    }

    if (showAddSizeDialog) {
        SizeInputDialog(
            title = stringResource(R.string.settings_add_size_title),
            onConfirm = { newSize ->
                if (!settings.intakeSizesMl.contains(newSize)) {
                    onUpdateSizes((settings.intakeSizesMl + newSize).sorted())
                }
                showAddSizeDialog = false
            },
            onDismiss = { showAddSizeDialog = false }
        )
    }

    if (showLanguageDialog) {
        ChoiceDialog(
            title = stringResource(R.string.settings_language),
            options = listOf(
                "es" to stringResource(R.string.language_es),
                "en" to stringResource(R.string.language_en)
            ),
            selected = settings.language,
            onSelect = { onUpdateLanguage(it); showLanguageDialog = false },
            onDismiss = { showLanguageDialog = false }
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text.uppercase(),
        fontFamily = DmSansFontFamily,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 1.sp,
        color = TextMuted
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundCard, RoundedCornerShape(14.dp))
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(14.dp))
    ) {
        content()
    }
}

@Composable
private fun SettingRow(
    label: String,
    value: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontFamily = DmSansFontFamily, fontSize = 14.sp, color = TextPrimary)
            if (subtitle != null) {
                Text(subtitle, fontFamily = DmSansFontFamily, fontSize = 11.sp, color = TextMuted)
            }
        }
        Text(value, fontFamily = DmMonoFontFamily, fontSize = 13.sp, color = TextSecondary)
        Spacer(Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun RemindersPerDayRow(count: Int, onDecrement: () -> Unit, onIncrement: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.settings_reminders_per_day),
            fontFamily = DmSansFontFamily,
            fontSize = 14.sp,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDecrement, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Outlined.Remove, contentDescription = stringResource(R.string.settings_reminders_less), tint = TextMuted)
        }
        Text(
            text = count.toString(),
            fontFamily = DmMonoFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = AccentLight,
            modifier = Modifier.width(32.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        IconButton(onClick = onIncrement, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Outlined.Add, contentDescription = stringResource(R.string.settings_reminders_more), tint = TextMuted)
        }
    }
}

@Composable
private fun SchedulePreview(times: List<LocalTime>, suggestedAmountMl: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_schedule_preview),
            fontFamily = DmSansFontFamily,
            fontSize = 11.sp,
            color = TextMuted,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        times.forEach { time ->
            Text(
                text = stringResource(
                    R.string.settings_reminder_at_format,
                    time.toHhMm(),
                    suggestedAmountMl
                ),
                fontFamily = DmMonoFontFamily,
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun IntakeSizeRow(size: Int, canDelete: Boolean, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.settings_ml_format, size),
            fontFamily = DmMonoFontFamily,
            fontSize = 14.sp,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        if (canDelete) {
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.settings_delete_size),
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun PermissionRow(
    label: String,
    granted: Boolean,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontFamily = DmSansFontFamily, fontSize = 14.sp, color = TextPrimary)
            if (subtitle != null) {
                Text(subtitle, fontFamily = DmSansFontFamily, fontSize = 11.sp, color = TextMuted)
            }
        }
        Text(
            text = if (granted) stringResource(R.string.settings_permission_granted)
            else stringResource(R.string.settings_permission_denied),
            fontFamily = DmSansFontFamily,
            fontSize = 12.sp,
            color = if (granted) SuccessGreen else WarnYellow
        )
    }
}

@Composable
private fun GoalDialog(current: Int, onConfirm: (Int) -> Unit, onDismiss: () -> Unit) {
    var input by rememberSaveable { mutableStateOf(current.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        title = { Text(stringResource(R.string.settings_daily_goal)) },
        text = {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it.filter(Char::isDigit) },
                label = { Text(stringResource(R.string.settings_daily_goal_subtitle)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                suffix = { Text("ml") }
            )
        },
        confirmButton = {
            TextButton(onClick = {
                input.toIntOrNull()?.takeIf { it in 100..10000 }?.let { onConfirm(it) }
                    ?: onDismiss()
            }) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        },
        containerColor = BackgroundCard
    )
}

@Composable
private fun SizeInputDialog(title: String, onConfirm: (Int) -> Unit, onDismiss: () -> Unit) {
    var input by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        title = { Text(title) },
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
                input.toIntOrNull()?.takeIf { it > 0 }?.let { onConfirm(it) } ?: onDismiss()
            }) { Text(stringResource(R.string.add)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        },
        containerColor = BackgroundCard
    )
}

@Composable
private fun ChoiceDialog(
    title: String,
    options: List<Pair<String, String>>,
    selected: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        title = { Text(title) },
        text = {
            Column {
                options.forEach { (key, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(key) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            fontFamily = DmSansFontFamily,
                            fontSize = 14.sp,
                            color = if (key == selected) AccentLight else TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        if (key == selected) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(AccentLight, androidx.compose.foundation.shape.CircleShape)
                            )
                        }
                    }
                    HorizontalDivider(thickness = 0.5.dp, color = BorderSubtle)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        },
        containerColor = BackgroundCard
    )
}

@Composable
private fun TimePickerDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(BackgroundCard, RoundedCornerShape(20.dp))
                .border(0.5.dp, BorderDefault, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = onConfirm) { Text(stringResource(R.string.ok)) }
            }
        }
    }
}

private fun LocalTime.toHhMm(): String =
    String.format("%02d:%02d", hour, minute)

@Composable
private fun languageLabel(language: String) = when (language) {
    "es" -> stringResource(R.string.language_es)
    "en" -> stringResource(R.string.language_en)
    else -> when (java.util.Locale.getDefault().language) {
        "es" -> stringResource(R.string.language_es)
        else -> stringResource(R.string.language_en)
    }
}
