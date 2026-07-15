package com.example.taxflow.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = InkNavy,
    onPrimary = Color.White,
    primaryContainer = InkNavyContainerLight,
    onPrimaryContainer = InkNavy,

    secondary = ForestGreen,
    onSecondary = Color.White,
    secondaryContainer = ForestGreenContainerLight,
    onSecondaryContainer = ForestGreen,

    tertiary = ReserveGold,
    onTertiary = Color.White,
    tertiaryContainer = ReserveGoldContainerLight,
    onTertiaryContainer = Color(0xFF4A3A1A),

    error = Terracotta,
    onError = Color.White,
    errorContainer = TerracottaContainerLight,
    onErrorContainer = Color(0xFF522A1B),

    background = PaperBackground,
    onBackground = InkTextPrimary,
    surface = Color.White,
    onSurface = InkTextPrimary,
    surfaceVariant = PaperSurfaceVariant,
    onSurfaceVariant = InkTextSecondary,
    outline = WarmOutline
)

private val DarkColors = darkColorScheme(
    primary = InkNavyLight,
    onPrimary = DeepInkBackground,
    primaryContainer = InkNavyContainerDark,
    onPrimaryContainer = InkNavyContainerLight,

    secondary = ForestGreenLight,
    onSecondary = DeepInkBackground,
    secondaryContainer = ForestGreenContainerDark,
    onSecondaryContainer = ForestGreenContainerLight,

    tertiary = ReserveGoldLight,
    onTertiary = DeepInkBackground,
    tertiaryContainer = ReserveGoldContainerDark,
    onTertiaryContainer = ReserveGoldContainerLight,

    error = TerracottaLight,
    onError = DeepInkBackground,
    errorContainer = TerracottaContainerDark,
    onErrorContainer = TerracottaContainerLight,

    background = DeepInkBackground,
    onBackground = PaperTextPrimaryDark,
    surface = DeepInkSurface,
    onSurface = PaperTextPrimaryDark,
    surfaceVariant = DeepInkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC9C2B4),
    outline = WarmOutlineDark
)

@Composable
fun TaxFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = TaxFlowTypography,
        content = content
    )
}