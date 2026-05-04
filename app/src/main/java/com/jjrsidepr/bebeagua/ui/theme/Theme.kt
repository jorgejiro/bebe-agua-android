package com.jjrsidepr.bebeagua.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

val BebeAguaDarkColorScheme = darkColorScheme(
    primary             = AccentPrimary,
    onPrimary           = TextOnAccent,
    primaryContainer    = BackgroundElement,
    onPrimaryContainer  = TextPrimary,
    secondary           = AccentLight,
    onSecondary         = TextOnAccent,
    secondaryContainer  = BackgroundElement,
    onSecondaryContainer = TextSecondary,
    background          = BackgroundMain,
    onBackground        = TextPrimary,
    surface             = BackgroundCard,
    onSurface           = TextPrimary,
    surfaceVariant      = BackgroundElement,
    onSurfaceVariant    = TextSecondary,
    outline             = BorderDefault,
    outlineVariant      = BorderSubtle,
    error               = WarnYellow,
    onError             = BackgroundDeep,
)

@Composable
fun BebeAguaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BebeAguaDarkColorScheme,
        typography  = Typography,
        shapes      = BebeAguaShapes,
        content     = content
    )
}
