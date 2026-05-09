package com.jjrapps.bebeagua.ui.onboarding

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.jjrapps.bebeagua.R
import com.jjrapps.bebeagua.ui.theme.AccentLight
import com.jjrapps.bebeagua.ui.theme.AccentPrimary
import com.jjrapps.bebeagua.ui.theme.BackgroundCard
import com.jjrapps.bebeagua.ui.theme.BackgroundDeep
import com.jjrapps.bebeagua.ui.theme.BackgroundMain
import com.jjrapps.bebeagua.ui.theme.BorderDefault
import com.jjrapps.bebeagua.ui.theme.DmMonoFontFamily
import com.jjrapps.bebeagua.ui.theme.DmSansFontFamily
import com.jjrapps.bebeagua.ui.theme.SuccessGreen
import com.jjrapps.bebeagua.ui.theme.TextMuted
import com.jjrapps.bebeagua.ui.theme.TextPrimary
import com.jjrapps.bebeagua.ui.theme.TextSecondary
import com.jjrapps.bebeagua.ui.theme.WarnYellow
import kotlinx.coroutines.launch
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundMain)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> WelcomePage()
                1 -> SetupPage(viewModel)
                else -> PermissionsPage()
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { index ->
                    val isActive = pagerState.currentPage == index
                    val dotColor by animateColorAsState(
                        if (isActive) AccentLight else BorderDefault,
                        label = "dot$index"
                    )
                    Box(
                        modifier = Modifier
                            .size(if (isActive) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                }
            }

            val isLast = pagerState.currentPage == 2
            Button(
                onClick = {
                    if (isLast) {
                        viewModel.finish()
                        onFinished()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentLight,
                    contentColor = BackgroundDeep
                )
            ) {
                Text(
                    text = stringResource(if (isLast) R.string.onboarding_finish else R.string.onboarding_next),
                    fontFamily = DmSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun WelcomePage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(AccentPrimary.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.WaterDrop,
                contentDescription = null,
                modifier = Modifier.size(52.dp),
                tint = AccentLight
            )
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.onboarding_welcome_title),
            fontFamily = DmSansFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.onboarding_welcome_body),
            fontFamily = DmSansFontFamily,
            fontSize = 15.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(160.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetupPage(viewModel: OnboardingViewModel) {
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    val startTime = LocalTime.ofSecondOfDay(viewModel.dayStartMinutes * 60L)
    val endTime = LocalTime.ofSecondOfDay(viewModel.dayEndMinutes * 60L)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 48.dp, bottom = 160.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = stringResource(R.string.onboarding_setup_title),
            fontFamily = DmSansFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = TextPrimary
        )

        Text(
            text = stringResource(R.string.onboarding_setup_subtitle),
            fontFamily = DmSansFontFamily,
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.onboarding_goal_label),
            fontFamily = DmSansFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = TextMuted
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundCard, RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.setGoal(viewModel.goalMl - 100) }) {
                Icon(Icons.Outlined.Remove, contentDescription = null, tint = AccentLight)
            }
            Text(
                text = "${viewModel.goalMl} ml",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontFamily = DmMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = TextPrimary
            )
            IconButton(onClick = { viewModel.setGoal(viewModel.goalMl + 100) }) {
                Icon(Icons.Outlined.Add, contentDescription = null, tint = AccentLight)
            }
        }

        Text(
            text = stringResource(R.string.onboarding_time_label),
            fontFamily = DmSansFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = TextMuted
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TimeChip(
                label = stringResource(R.string.settings_start_time),
                time = startTime,
                modifier = Modifier.weight(1f),
                onClick = { showStartPicker = true }
            )
            TimeChip(
                label = stringResource(R.string.settings_end_time),
                time = endTime,
                modifier = Modifier.weight(1f),
                onClick = { showEndPicker = true }
            )
        }

        Text(
            text = stringResource(R.string.onboarding_reminders_label),
            fontFamily = DmSansFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = TextMuted
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundCard, RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.changeRemindersPerDay(viewModel.remindersPerDay - 1) }) {
                Icon(Icons.Outlined.Remove, contentDescription = null, tint = AccentLight)
            }
            Text(
                text = "${viewModel.remindersPerDay}",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontFamily = DmMonoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = TextPrimary
            )
            IconButton(onClick = { viewModel.changeRemindersPerDay(viewModel.remindersPerDay + 1) }) {
                Icon(Icons.Outlined.Add, contentDescription = null, tint = AccentLight)
            }
        }
    }

    if (showStartPicker) {
        val state = rememberTimePickerState(
            initialHour = startTime.hour,
            initialMinute = startTime.minute,
            is24Hour = true
        )
        TimePickerDialog(
            onDismiss = { showStartPicker = false },
            onConfirm = {
                viewModel.setStartMinutes(state.hour * 60 + state.minute)
                showStartPicker = false
            }
        ) { TimePicker(state = state) }
    }

    if (showEndPicker) {
        val state = rememberTimePickerState(
            initialHour = endTime.hour,
            initialMinute = endTime.minute,
            is24Hour = true
        )
        TimePickerDialog(
            onDismiss = { showEndPicker = false },
            onConfirm = {
                viewModel.setEndMinutes(state.hour * 60 + state.minute)
                showEndPicker = false
            }
        ) { TimePicker(state = state) }
    }
}

@Composable
private fun TimeChip(
    label: String,
    time: LocalTime,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .background(BackgroundCard, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label, fontFamily = DmSansFontFamily, fontSize = 11.sp, color = TextMuted)
        Text(
            text = "%02d:%02d".format(time.hour, time.minute),
            fontFamily = DmMonoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = AccentLight
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = BackgroundCard,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.ok), color = AccentLight)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = TextMuted)
            }
        },
        text = { content() }
    )
}

@Composable
private fun PermissionsPage() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var notificationsGranted by remember { mutableStateOf(false) }
    var exactAlarmsGranted by remember { mutableStateOf(false) }

    fun checkPermissions() {
        val nm = context.getSystemService(NotificationManager::class.java)
        notificationsGranted = nm?.areNotificationsEnabled() ?: false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val am = context.getSystemService(AlarmManager::class.java)
            exactAlarmsGranted = am?.canScheduleExactAlarms() ?: false
        } else {
            exactAlarmsGranted = true
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) checkPermissions()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) { checkPermissions() }

    val notificationLauncher = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            checkPermissions()
        }
    } else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 48.dp, bottom = 160.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = stringResource(R.string.onboarding_permissions_title),
            fontFamily = DmSansFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = TextPrimary
        )

        Text(
            text = stringResource(R.string.onboarding_permissions_subtitle),
            fontFamily = DmSansFontFamily,
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(Modifier.height(8.dp))

        PermissionRow(
            icon = Icons.Outlined.Notifications,
            title = stringResource(R.string.settings_notifications),
            granted = notificationsGranted,
            onRequest = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationLauncher?.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    context.startActivity(intent)
                }
            }
        )

        PermissionRow(
            icon = Icons.Outlined.Schedule,
            title = stringResource(R.string.settings_exact_alarms),
            granted = exactAlarmsGranted,
            onRequest = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = android.net.Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }
            }
        )
    }
}

@Composable
private fun PermissionRow(
    icon: ImageVector,
    title: String,
    granted: Boolean,
    onRequest: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundCard, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (granted) SuccessGreen else AccentLight,
            modifier = Modifier.size(22.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = DmSansFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Text(
                text = stringResource(
                    if (granted) R.string.settings_permission_granted
                    else R.string.settings_permission_denied
                ),
                fontFamily = DmSansFontFamily,
                fontSize = 12.sp,
                color = if (granted) SuccessGreen else WarnYellow
            )
        }
        if (!granted) {
            TextButton(onClick = onRequest) {
                Text(
                    text = stringResource(R.string.onboarding_grant),
                    fontFamily = DmSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = AccentLight
                )
            }
        } else {
            Icon(
                Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
