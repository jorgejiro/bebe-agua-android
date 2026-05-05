package com.jjrapps.bebeagua.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jjrapps.bebeagua.R
import com.jjrapps.bebeagua.domain.model.Intake
import com.jjrapps.bebeagua.ui.theme.AccentLight
import com.jjrapps.bebeagua.ui.theme.BackgroundElement
import com.jjrapps.bebeagua.ui.theme.DmMonoFontFamily
import com.jjrapps.bebeagua.ui.theme.DmSansFontFamily
import com.jjrapps.bebeagua.ui.theme.TextDim
import com.jjrapps.bebeagua.ui.theme.TextMuted
import com.jjrapps.bebeagua.ui.theme.TextPrimary
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

@Composable
fun IntakeRecordItem(
    intake: Intake,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val time = remember(intake.timestampEpochMs, intake.timezoneId) {
        Instant.ofEpochMilli(intake.timestampEpochMs)
            .atZone(ZoneId.of(intake.timezoneId))
            .format(timeFormatter)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(BackgroundElement, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.WaterDrop,
                contentDescription = null,
                tint = AccentLight,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = time,
                fontFamily = DmSansFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Text(
                text = stringResource(R.string.home_intake_label),
                fontFamily = DmSansFontFamily,
                fontSize = 11.sp,
                color = TextMuted
            )
        }

        Text(
            text = stringResource(R.string.home_ml_format, intake.amountMl),
            fontFamily = DmMonoFontFamily,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = AccentLight
        )

        Spacer(Modifier.width(4.dp))

        var menuExpanded by remember { mutableStateOf(false) }
        Box {
            IconButton(onClick = { menuExpanded = true }) {
                Text("⋮", fontSize = 18.sp, color = TextDim)
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.home_delete)) },
                    onClick = { menuExpanded = false; onDelete() }
                )
            }
        }
    }
}
