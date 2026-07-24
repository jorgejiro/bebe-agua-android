package com.jjrapps.bebeagua.ui.changelog

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jjrapps.bebeagua.R
import com.jjrapps.bebeagua.ui.theme.AccentLight
import com.jjrapps.bebeagua.ui.theme.AccentPrimary
import com.jjrapps.bebeagua.ui.theme.BackgroundCard
import com.jjrapps.bebeagua.ui.theme.BackgroundElement
import com.jjrapps.bebeagua.ui.theme.BackgroundMain
import com.jjrapps.bebeagua.ui.theme.BebeAguaTheme
import com.jjrapps.bebeagua.ui.theme.BorderDefault
import com.jjrapps.bebeagua.ui.theme.BorderSubtle
import com.jjrapps.bebeagua.ui.theme.DmMonoFontFamily
import com.jjrapps.bebeagua.ui.theme.DmSansFontFamily
import com.jjrapps.bebeagua.ui.theme.TextMuted
import com.jjrapps.bebeagua.ui.theme.TextPrimary
import com.jjrapps.bebeagua.ui.theme.TextSecondary
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun ChangelogScreen(
    onBack: () -> Unit,
    viewModel: ChangelogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundMain)
    ) {
        ChangelogHeader(onBack = onBack)

        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                ChangelogUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AccentLight
                )
                is ChangelogUiState.Error -> Text(
                    text = state.message,
                    color = AccentLight,
                    modifier = Modifier.align(Alignment.Center)
                )
                is ChangelogUiState.Success -> ChangelogContent(state)
            }
        }
    }
}

@Composable
private fun ChangelogHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 20.dp, top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.changelog_back),
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = stringResource(R.string.changelog_title),
            fontFamily = DmSansFontFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}

@Composable
private fun ChangelogContent(state: ChangelogUiState.Success) {
    if (state.releases.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.changelog_empty),
                fontFamily = DmSansFontFamily,
                fontSize = 13.sp,
                color = TextMuted
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(state.releases, key = { it.versionCode }) { release ->
            ReleaseCard(
                release = release,
                isCurrent = release.versionCode == state.currentVersionCode
            )
        }
    }
}

@Composable
private fun ReleaseCard(release: ChangelogRelease, isCurrent: Boolean) {
    val locale = LocalLocale.current.platformLocale
    val formatter = remember(locale) {
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
    }
    val highlights = stringArrayResource(release.highlightsRes)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundCard, RoundedCornerShape(14.dp))
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = release.versionName,
                fontFamily = DmMonoFontFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = AccentLight
            )
            if (isCurrent) {
                Spacer(Modifier.width(8.dp))
                CurrentBadge()
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = release.releaseDate.format(formatter),
                fontFamily = DmSansFontFamily,
                fontSize = 11.sp,
                color = TextMuted
            )
        }
        Spacer(Modifier.height(10.dp))
        highlights.forEachIndexed { index, highlight ->
            if (index > 0) Spacer(Modifier.height(8.dp))
            HighlightRow(highlight)
        }
    }
}

@Composable
private fun CurrentBadge() {
    Text(
        text = stringResource(R.string.changelog_current).uppercase(),
        fontFamily = DmSansFontFamily,
        fontSize = 9.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.8.sp,
        color = AccentLight,
        modifier = Modifier
            .background(BackgroundElement, RoundedCornerShape(6.dp))
            .border(0.5.dp, BorderDefault, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    )
}

@Composable
private fun HighlightRow(text: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp, end = 10.dp)
                .size(4.dp)
                .background(AccentPrimary, CircleShape)
        )
        Text(
            text = text,
            fontFamily = DmSansFontFamily,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = TextSecondary
        )
    }
}

@PreviewLightDark
@Composable
private fun ChangelogContentPreview() {
    BebeAguaTheme {
        Column(modifier = Modifier.background(BackgroundMain)) {
            ChangelogHeader(onBack = {})
            ChangelogContent(
                ChangelogUiState.Success(
                    releases = listOf(
                        ChangelogRelease(
                            versionName = "1.1.0",
                            versionCode = 6,
                            releaseDate = LocalDate.of(2026, 7, 24),
                            highlightsRes = R.array.changelog_1_1_0
                        ),
                        ChangelogRelease(
                            versionName = "1.0",
                            versionCode = 5,
                            releaseDate = LocalDate.of(2026, 5, 26),
                            highlightsRes = R.array.changelog_1_0
                        )
                    ),
                    currentVersionCode = 6
                )
            )
        }
    }
}
