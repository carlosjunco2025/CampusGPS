package com.junco.campusgps.ui.theme

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
    primary = PetrolPrimaryDark,
    onPrimary = PetrolOnPrimaryDark,
    primaryContainer = PetrolPrimaryContainerDark,
    onPrimaryContainer = PetrolOnPrimaryContainerDark,
    secondary = PetrolSecondaryDark,
    onSecondary = PetrolOnSecondaryDark,
    secondaryContainer = PetrolSecondaryContainerDark,
    onSecondaryContainer = PetrolOnSecondaryContainerDark,
    tertiary = GreenAccentDark,
    onTertiary = GreenOnAccentDark,
    tertiaryContainer = GreenAccentContainerDark,
    onTertiaryContainer = GreenOnAccentContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = PetrolPrimaryLight,
    onPrimary = PetrolOnPrimaryLight,
    primaryContainer = PetrolPrimaryContainerLight,
    onPrimaryContainer = PetrolOnPrimaryContainerLight,
    secondary = PetrolSecondaryLight,
    onSecondary = PetrolOnSecondaryLight,
    secondaryContainer = PetrolSecondaryContainerLight,
    onSecondaryContainer = PetrolOnSecondaryContainerLight,
    tertiary = GreenAccentLight,
    onTertiary = GreenOnAccentLight,
    tertiaryContainer = GreenAccentContainerLight,
    onTertiaryContainer = GreenOnAccentContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight
)

@Composable
fun CampusGPSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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
