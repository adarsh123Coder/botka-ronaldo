package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// 1. Glacial Titanium (Classy Cold Dark)
val GlacialBlue = Color(0xFF38BDF8)
val GlacialIce = Color(0xFFBAE6FD)
val GlacialPlatinum = Color(0xFFE2E8F0)
val GlacialBg = Color(0xFF080A0F)
val GlacialSurface = Color(0xFF10141E)
val GlacialSurfaceVariant = Color(0xFF1B2232)

// 2. Nordic Ice (Classy Cold Light - Crisp & Ultra-Clean)
val NordicIceBlue = Color(0xFF1D4ED8)
val NordicIceCyan = Color(0xFF0284C7)
val NordicIceTeal = Color(0xFF0D9488)
val NordicIceBg = Color(0xFFF1F5F9)
val NordicIceSurface = Color(0xFFFFFFFF)
val NordicIceSurfaceVariant = Color(0xFFE2E8F0)

// 3. Onyx Diamond (Classy Cold Luxury Dark)
val DiamondWhite = Color(0xFFFFFFFF)
val DiamondPlatinum = Color(0xFFCBD5E1)
val DiamondSilver = Color(0xFF94A3B8)
val DiamondBg = Color(0xFF050608)
val DiamondSurface = Color(0xFF121419)
val DiamondSurfaceVariant = Color(0xFF1F222A)

// 4. Beryl Glacier (Classy Cold Mint/Cyan)
val BerylMint = Color(0xFF2DD4BF)
val BerylCyan = Color(0xFF22D3EE)
val BerylEmerald = Color(0xFF34D399)
val BerylBg = Color(0xFF040D13)
val BerylSurface = Color(0xFF0A1924)
val BerylSurfaceVariant = Color(0xFF132635)

// 5. Aurora Frost (Classy Cold Violet/Ice)
val AuroraLavender = Color(0xFFA78BFA)
val AuroraIceCyan = Color(0xFF38BDF8)
val AuroraPink = Color(0xFFF472B6)
val AuroraFrostBg = Color(0xFF080918)
val AuroraFrostSurface = Color(0xFF12142E)
val AuroraFrostSurfaceVariant = Color(0xFF1C2045)

enum class AppThemeMode(
    val id: String,
    val title: String,
    val subtitle: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val surfaceColor: Color,
    val isDark: Boolean,
    val gradientColors: List<Color>,
    val bevelHighlight: Color,
    val bevelShadow: Color
) {
    GLACIAL_TITANIUM(
        id = "glacial_titanium",
        title = "Glacial Titanium",
        subtitle = "Cold Arctic Blue & Brushed Titanium",
        primaryColor = GlacialBlue,
        secondaryColor = GlacialIce,
        surfaceColor = GlacialSurface,
        isDark = true,
        gradientColors = listOf(GlacialBlue, GlacialIce),
        bevelHighlight = Color(0x60BAE6FD),
        bevelShadow = Color(0x90030508)
    ),
    NORDIC_ICE(
        id = "nordic_ice",
        title = "Nordic Ice Light",
        subtitle = "Crisp Diamond Porcelain & Royal Glacial",
        primaryColor = NordicIceBlue,
        secondaryColor = NordicIceCyan,
        surfaceColor = NordicIceSurface,
        isDark = false,
        gradientColors = listOf(NordicIceBlue, NordicIceCyan),
        bevelHighlight = Color(0xFFFFFFFF),
        bevelShadow = Color(0x3064748B)
    ),
    ONYX_DIAMOND(
        id = "onyx_diamond",
        title = "Onyx Diamond",
        subtitle = "Deep Void & Brilliant Cold Diamond",
        primaryColor = DiamondWhite,
        secondaryColor = DiamondPlatinum,
        surfaceColor = DiamondSurface,
        isDark = true,
        gradientColors = listOf(DiamondWhite, DiamondSilver),
        bevelHighlight = Color(0x50FFFFFF),
        bevelShadow = Color(0x90000000)
    ),
    BERYL_GLACIER(
        id = "beryl_glacier",
        title = "Beryl Glacier",
        subtitle = "Sub-Zero Mint & Polar Ice Trench",
        primaryColor = BerylMint,
        secondaryColor = BerylCyan,
        surfaceColor = BerylSurface,
        isDark = true,
        gradientColors = listOf(BerylMint, BerylCyan),
        bevelHighlight = Color(0x6067E8F9),
        bevelShadow = Color(0x9002060A)
    ),
    AURORA_FROST(
        id = "aurora_frost",
        title = "Aurora Frost",
        subtitle = "Crystal Violet & Icy Lavender Glow",
        primaryColor = AuroraLavender,
        secondaryColor = AuroraIceCyan,
        surfaceColor = AuroraFrostSurface,
        isDark = true,
        gradientColors = listOf(AuroraLavender, AuroraIceCyan),
        bevelHighlight = Color(0x60DDD6FE),
        bevelShadow = Color(0x9004050E)
    )
}
