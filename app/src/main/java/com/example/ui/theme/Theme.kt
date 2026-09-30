package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val LocalAppTheme = staticCompositionLocalOf { AppThemeMode.GLACIAL_TITANIUM }

fun getThemeColorScheme(themeMode: AppThemeMode): ColorScheme {
    return when (themeMode) {
        AppThemeMode.GLACIAL_TITANIUM -> darkColorScheme(
            primary = GlacialBlue,
            onPrimary = Color(0xFF03141F),
            primaryContainer = Color(0xFF0C2B42),
            onPrimaryContainer = Color(0xFFBAE6FD),
            secondary = GlacialIce,
            onSecondary = Color(0xFF082F49),
            secondaryContainer = Color(0xFF0F3B57),
            onSecondaryContainer = Color(0xFFE0F2FE),
            tertiary = GlacialPlatinum,
            background = GlacialBg,
            onBackground = Color(0xFFF1F5F9),
            surface = GlacialSurface,
            onSurface = Color(0xFFE2E8F0),
            surfaceVariant = GlacialSurfaceVariant,
            onSurfaceVariant = Color(0xFF94A3B8),
            outline = Color(0xFF2E3E56),
            outlineVariant = Color(0xFF1B273A)
        )
        AppThemeMode.NORDIC_ICE -> lightColorScheme(
            primary = NordicIceBlue,
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFDBEAFE),
            onPrimaryContainer = Color(0xFF1E3A8A),
            secondary = NordicIceCyan,
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFE0F2FE),
            onSecondaryContainer = Color(0xFF075985),
            tertiary = NordicIceTeal,
            background = NordicIceBg,
            onBackground = Color(0xFF09111E),
            surface = NordicIceSurface,
            onSurface = Color(0xFF0F172A),
            surfaceVariant = NordicIceSurfaceVariant,
            onSurfaceVariant = Color(0xFF64748B),
            outline = Color(0xFFCBD5E1),
            outlineVariant = Color(0xFFE2E8F0)
        )
        AppThemeMode.ONYX_DIAMOND -> darkColorScheme(
            primary = DiamondWhite,
            onPrimary = Color(0xFF000000),
            primaryContainer = Color(0xFF272A32),
            onPrimaryContainer = Color(0xFFFFFFFF),
            secondary = DiamondPlatinum,
            onSecondary = Color(0xFF0B0D11),
            secondaryContainer = Color(0xFF333842),
            onSecondaryContainer = Color(0xFFF1F5F9),
            tertiary = DiamondSilver,
            background = DiamondBg,
            onBackground = Color(0xFFF8FAFC),
            surface = DiamondSurface,
            onSurface = Color(0xFFF1F5F9),
            surfaceVariant = DiamondSurfaceVariant,
            onSurfaceVariant = Color(0xFFA1A8B8),
            outline = Color(0xFF333945),
            outlineVariant = Color(0xFF20252F)
        )
        AppThemeMode.BERYL_GLACIER -> darkColorScheme(
            primary = BerylMint,
            onPrimary = Color(0xFF00201A),
            primaryContainer = Color(0xFF063B34),
            onPrimaryContainer = Color(0xFF99F6E4),
            secondary = BerylCyan,
            onSecondary = Color(0xFF032830),
            secondaryContainer = Color(0xFF0E434F),
            onSecondaryContainer = Color(0xFFA5F3FC),
            tertiary = BerylEmerald,
            background = BerylBg,
            onBackground = Color(0xFFF0FDF4),
            surface = BerylSurface,
            onSurface = Color(0xFFE6FAF4),
            surfaceVariant = BerylSurfaceVariant,
            onSurfaceVariant = Color(0xFF86A3A0),
            outline = Color(0xFF254B4E),
            outlineVariant = Color(0xFF142F33)
        )
        AppThemeMode.AURORA_FROST -> darkColorScheme(
            primary = AuroraLavender,
            onPrimary = Color(0xFF1E1338),
            primaryContainer = Color(0xFF3B2A68),
            onPrimaryContainer = Color(0xFFEDE9FE),
            secondary = AuroraIceCyan,
            onSecondary = Color(0xFF042030),
            secondaryContainer = Color(0xFF103952),
            onSecondaryContainer = Color(0xFFBAE6FD),
            tertiary = AuroraPink,
            background = AuroraFrostBg,
            onBackground = Color(0xFFF5F3FF),
            surface = AuroraFrostSurface,
            onSurface = Color(0xFFECEAF8),
            surfaceVariant = AuroraFrostSurfaceVariant,
            onSurfaceVariant = Color(0xFF9896B8),
            outline = Color(0xFF3D3866),
            outlineVariant = Color(0xFF231F45)
        )
    }
}

@Composable
fun NovaTaskTheme(
    themeMode: AppThemeMode = AppThemeMode.GLACIAL_TITANIUM,
    content: @Composable () -> Unit
) {
    val colorScheme = getThemeColorScheme(themeMode)

    CompositionLocalProvider(LocalAppTheme provides themeMode) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

// Convenient helper extension for linear gradient
val AppThemeMode.brush: Brush
    get() = Brush.horizontalGradient(gradientColors)

// 3D Bevel vertical gradient for borders and cards
val AppThemeMode.bevelBorderBrush: Brush
    get() = Brush.verticalGradient(
        colors = listOf(
            bevelHighlight,
            primaryColor.copy(alpha = 0.2f),
            bevelShadow
        )
    )
