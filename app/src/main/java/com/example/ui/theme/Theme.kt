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

private val DarkColorScheme =
  darkColorScheme(
    primary = HospitalTealPrimaryDark,
    onPrimary = HospitalOnPrimaryDark,
    primaryContainer = HospitalPrimaryContainerDark,
    onPrimaryContainer = HospitalOnPrimaryContainerDark,
    secondary = HospitalSecondaryDark,
    onSecondary = HospitalOnSecondaryDark,
    secondaryContainer = HospitalSecondaryContainerDark,
    onSecondaryContainer = HospitalOnSecondaryContainerDark,
    tertiary = HospitalTertiaryDark,
    background = HospitalBackgroundDark,
    onBackground = HospitalOnBackgroundDark,
    surface = HospitalSurfaceDark,
    onSurface = HospitalOnSurfaceDark,
    surfaceVariant = HospitalSurfaceVariantDark,
    onSurfaceVariant = HospitalOnSurfaceVariantDark,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = HospitalTealPrimary,
    onPrimary = HospitalOnPrimary,
    primaryContainer = HospitalPrimaryContainer,
    onPrimaryContainer = HospitalOnPrimaryContainer,
    secondary = HospitalSecondary,
    onSecondary = HospitalOnSecondary,
    secondaryContainer = HospitalSecondaryContainer,
    onSecondaryContainer = HospitalOnSecondaryContainer,
    tertiary = HospitalTertiary,
    onTertiary = HospitalOnTertiary,
    tertiaryContainer = HospitalTertiaryContainer,
    onTertiaryContainer = HospitalOnTertiaryContainer,
    background = HospitalBackground,
    onBackground = HospitalOnBackground,
    surface = HospitalSurface,
    onSurface = HospitalOnSurface,
    surfaceVariant = HospitalSurfaceVariant,
    onSurfaceVariant = HospitalOnSurfaceVariant,
    outline = HospitalOutline,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false, // Use intentional hospital branding by default
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

