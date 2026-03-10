package com.example.encuentratumanitas.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Brand colours
val PrimaryDarkGreen  = Color(0xFF155E38)
val PrimaryContainer  = Color(0xFFB7F0D0)
val SecondaryAmber    = Color(0xFFF59E0B)
val SecondaryContainer= Color(0xFFFEF3C7)
val TertiaryGreen     = Color(0xFF16A34A)
val ErrorRed          = Color(0xFFDC2626)

private val LightColorScheme = lightColorScheme(
    primary              = PrimaryDarkGreen,
    onPrimary            = Color.White,
    primaryContainer     = PrimaryContainer,
    onPrimaryContainer   = Color(0xFF002112),
    secondary            = SecondaryAmber,
    onSecondary          = Color.White,
    secondaryContainer   = SecondaryContainer,
    onSecondaryContainer = Color(0xFF3D1A00),
    tertiary             = TertiaryGreen,
    onTertiary           = Color.White,
    background           = Color(0xFFFAFAFA),
    onBackground         = Color(0xFF111827),
    surface              = Color.White,
    onSurface            = Color(0xFF111827),
    error                = ErrorRed,
    onError              = Color.White,
)

@Composable
fun EncuentraTuManitasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,        // keep brand colours consistent
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = PrimaryDarkGreen.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography   = Typography,
        content      = content
    )
}