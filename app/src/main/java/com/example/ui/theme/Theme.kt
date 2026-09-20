package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SakuraPrimaryDark,
    onPrimary = SakuraOnPrimaryDark,
    primaryContainer = SakuraPrimaryContainerDark,
    onPrimaryContainer = SakuraOnPrimaryContainerDark,
    secondary = SakuraSecondaryDark,
    onSecondary = SakuraOnSecondaryDark,
    secondaryContainer = SakuraSecondaryContainerDark,
    onSecondaryContainer = SakuraOnSecondaryContainerDark,
    tertiary = SakuraTertiaryDark,
    background = SakuraBackgroundDark,
    surface = SakuraSurfaceDark,
    surfaceVariant = SakuraSurfaceVariantDark,
    outline = SakuraOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = SakuraPrimaryLight,
    onPrimary = SakuraOnPrimaryLight,
    primaryContainer = SakuraPrimaryContainerLight,
    onPrimaryContainer = SakuraOnPrimaryContainerLight,
    secondary = SakuraSecondaryLight,
    onSecondary = SakuraOnSecondaryLight,
    secondaryContainer = SakuraSecondaryContainerLight,
    onSecondaryContainer = SakuraOnSecondaryContainerLight,
    tertiary = SakuraTertiaryLight,
    background = SakuraBackgroundLight,
    surface = SakuraSurfaceLight,
    surfaceVariant = SakuraSurfaceVariantLight,
    outline = SakuraOutlineLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep SaRa's signature theme by default
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

