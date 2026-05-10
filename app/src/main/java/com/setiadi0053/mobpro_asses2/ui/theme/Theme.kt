package com.setiadi0053.mobpro_asses2.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Tf2Red,
    secondary = Tf2Blu,
    tertiary = Pink80,
    background = Color(0xFF1A1A1A),
    surface = Color(0xFF2D2D2D)
)

private val LightColorScheme = lightColorScheme(
    primary = Tf2Red,
    secondary = Tf2Blu,
    tertiary = Pink40,
    background = Tf2Background,
    surface = Tf2Surface
)

@Composable
fun MobProAsses2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    teamTheme: String = "RED",
    content: @Composable () -> Unit
) {
    val primaryColor = if (teamTheme == "RED") Tf2Red else Tf2Blu
    val secondaryColor = if (teamTheme == "RED") Tf2Blu else Tf2Red

    val colorScheme = if (darkTheme) {
        DarkColorScheme.copy(primary = primaryColor, secondary = secondaryColor)
    } else {
        LightColorScheme.copy(primary = primaryColor, secondary = secondaryColor)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
