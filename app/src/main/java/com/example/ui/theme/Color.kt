package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// =========================================================
// VIBRANT GLOWING YELLOW & CLEAN WHITE LUXURY PALETTE
// =========================================================

// Base Backgrounds: Ultra-clean Titanium White & Luminous Warm Tones
val WarmCreamBg = Color(0xFFF8FAFC)        // Crisp light modern app titanium base
val WarmCreamSurface = Color(0xFFF1F5F9)   // Subtle slate-white container
val PureWhite = Color(0xFFFFFFFF)          // Pure white elevated card surfaces

// Vibrant Glowing Yellow (High-energy, non-dull, modern gold glow)
val GoldPrimary = Color(0xFFFFC700)        // Vibrant Luminous Yellow (#FFC700)
val GoldRich = Color(0xFFFFB800)           // Deep Warm Golden Amber
val GoldDeep = Color(0xFFB45309)           // High-contrast Amber for text/icons on yellow
val GoldLight = Color(0xFFFEF9C3)          // Luminous soft yellow glow background (#FEF9C3)
val GoldContainer = Color(0xFFFEF08A)      // Rich yellow pill container
val OnGoldContainer = Color(0xFF451A03)    // Deep contrast dark text on yellow

// Glowing Yellow Shadows & Borders
val YellowGlow = Color(0x4DFFC700)         // Ambient neon-yellow glow
val YellowGlowSubtle = Color(0x26FFC700)   // Soft background glow
val GlassBorderGold = Color(0x66FFC700)    // High-visibility glowing yellow border
val GlassBorder = Color(0x33FFC700)
val GlassBorderSubtle = Color(0x0F0F172A)  // Crisp structural edge

// High-Contrast Titanium Typography (Zero dullness, maximum legibility)
val TextPrimary = Color(0xFF0F172A)        // Deep titanium charcoal (#0F172A)
val TextSecondary = Color(0xFF334155)      // Crisp slate
val TextMuted = Color(0xFF64748B)          // Secondary muted slate

// Accent Status Colors
val EmeraldLiquid = Color(0xFF059669)      // Vibrant Emerald (Cash)
val EmeraldLiquidBg = Color(0xFFECFDF5)
val SapphireLiquid = Color(0xFF2563EB)     // Vibrant Royal Blue (UPI)
val SapphireLiquidBg = Color(0xFFEFF6FF)
val AmberLiquid = Color(0xFFD97706)        // Amber Warning
val AmberLiquidBg = Color(0xFFFFFBEB)
val RoseLiquid = Color(0xFFE11D48)         // Vibrant Rose/Crimson (Expense)
val RoseLiquidBg = Color(0xFFFFF1F2)
val PurpleLiquid = Color(0xFF7C3AED)
val PurpleLiquidBg = Color(0xFFF5F3FF)

// Semantic Names & Aliases
val RapidoYellow = GoldPrimary
val YellowPrimary = GoldPrimary
val YellowDark = GoldDeep
val YellowLight = GoldLight
val YellowContainer = GoldContainer
val OnYellowContainer = OnGoldContainer
val GoldDark = GoldDeep

val CharcoalDark = Color(0xFF0F172A)
val CharcoalMedium = Color(0xFF1E293B)
val CharcoalSurface = WarmCreamSurface
val ShopBackground = WarmCreamBg
val ShopSurface = PureWhite
val ShopSurfaceVariant = WarmCreamSurface
val ShopBorder = GlassBorder
val ShopTextPrimary = TextPrimary
val ShopTextSecondary = TextSecondary
val ShopTextMuted = TextMuted

val CashGreen = EmeraldLiquid
val CashGreenLight = EmeraldLiquidBg
val UpiBlue = SapphireLiquid
val UpiBlueLight = SapphireLiquidBg
val ExpenseRed = RoseLiquid
val ExpenseRedLight = RoseLiquidBg
val WarningAmber = AmberLiquid
val WarningAmberLight = AmberLiquidBg

// =========================================================
// VIBRANT GLOWING GRADIENT BRUSHES
// =========================================================

// Glowing Yellow Gold Gradient (Primary Actions)
val GoldGlassBrush = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFFFFD54F),
        Color(0xFFFFC700),
        Color(0xFFFFA000)
    )
)

val YellowGlassBrush = GoldGlassBrush

// Crisp White Elevated Card Gradient
val GlassCardBrush = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFFFFF),
        Color(0xFFFCFBF7)
    )
)

// Glowing Border Gradient
val GlassBorderBrush = Brush.verticalGradient(
    colors = listOf(
        Color(0x99FFC700),
        Color(0x33FFC700)
    )
)

// Primary Action Button Brush
val PrimaryActionButtonBrush = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFD700),
        Color(0xFFFFB300)
    )
)

// Rose Expense Action Brush
val RoseActionBrush = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFFF43F5E),
        Color(0xFFE11D48)
    )
)
