package com.fadhil.taba.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = GreenDarkPrimary,
    secondary = GreenSecondary,
    tertiary = GoldAccent,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,        // Hijau Gelap Utama TABA
    secondary = GreenSecondary,    // Hijau Muda Lembut untuk Banner
    tertiary = GoldAccent,         // Aksen Emas/Krem untuk Badge
    background = BackgroundLight,  // Background Krem Pucat bersih
    surface = SurfaceLight,        // Card Putih Bersih

    onPrimary = Color.White,
    onSecondary = GreenPrimary,
    onTertiary = NavyDark,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun TabaTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}