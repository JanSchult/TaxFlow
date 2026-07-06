package com.example.taxflow.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
private val Color_DarkSurface = androidx.compose.ui.graphics.Color(0xFF232838)
private val Color_White = androidx.compose.ui.graphics.Color.White

private val LightColors = lightColorScheme(
    primary = TrustBlue,
    onPrimary = Color_White,
    background = NeutralBackground,
    surface = NeutralSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    secondary = PositiveGreen,
    error = WarningOrange
)

private val DarkColors = darkColorScheme(
    primary = TrustBlue,
    background = TrustBlueDark,
    surface = Color_DarkSurface
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