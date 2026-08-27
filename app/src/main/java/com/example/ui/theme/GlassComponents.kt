package com.example.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Warm Cream & Soft White Liquid Glass Background (No dark mode)
 */
@Composable
fun LiquidGlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8FAFC), // Crisp cool white top
                        Color(0xFFFFFBF0), // Warm cream bottom - matches browser
                    )
                )
            )
    ) {
        content()
    }
}

/**
 * Reusable Frosted White "Liquid Glass" Card with Gold Border & Gentle Depth
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = PureWhite,
    borderColor: Color? = null,
    borderBrush: Brush = GlassBorderBrush,
    contentPadding: Dp = 16.dp,
    elevation: Dp = 2.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val borderModifier = if (borderColor != null) {
        Modifier.border(BorderStroke(1.5.dp, borderColor), shape)
    } else {
        Modifier.border(BorderStroke(1.5.dp, borderBrush), shape)
    }

    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = GoldPrimary.copy(alpha = 0.3f)),
            onClick = onClick
        )
    } else Modifier

    Box(
        modifier = modifier
            .shadow(elevation, shape, clip = false)
            .clip(shape)
            .background(backgroundColor)
            .then(borderModifier)
            .then(clickModifier)
            .padding(contentPadding),
        content = content
    )
}

/**
 * Large, Accessible Liquid Glass Primary Action Button
 * Minimum 56-64dp height, high contrast dark charcoal text on rich gold
 */
@Composable
fun LiquidGlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = GoldPrimary,
    gradientBrush: Brush? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    height: Dp = 58.dp,
    fontSize: Int = 18
) {
    val shape = RoundedCornerShape(18.dp)
    val backgroundBrush = gradientBrush ?: GoldGlassBrush

    Box(
        modifier = modifier
            .height(height)
            .shadow(if (enabled) 3.dp else 0.dp, shape)
            .clip(shape)
            .background(
                if (enabled) backgroundBrush
                else Brush.linearGradient(listOf(Color(0xFFE5E5E5), Color(0xFFD4D4D4)))
            )
            .border(
                BorderStroke(
                    1.5.dp,
                    if (enabled) Color(0xFF9E7B1A) else Color(0x1F000000)
                ),
                shape
            )
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color.White.copy(alpha = 0.4f)),
                onClick = onClick
            )
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) TextPrimary else TextMuted,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text,
                color = if (enabled) TextPrimary else TextMuted,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Black,
                fontFamily = PlusJakartaSans,
                letterSpacing = 0.5.sp
            )
        }
    }
}

/**
 * Large, Accessible Category Pill with High Contrast
 * Minimum 56dp height touch target for error-free tapping
 */
@Composable
fun GlassCategoryPill(
    code: String,
    title: String,
    isSelected: Boolean,
    accentColor: Color = GoldPrimary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)

    val backgroundColor = if (isSelected) GoldPrimary else PureWhite
    val borderStroke = if (isSelected) {
        BorderStroke(2.dp, TextPrimary)
    } else {
        BorderStroke(1.5.dp, GlassBorderGold)
    }

    Box(
        modifier = modifier
            .height(56.dp)
            .shadow(if (isSelected) 3.dp else 1.dp, shape)
            .clip(shape)
            .background(backgroundColor)
            .border(borderStroke, shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = GoldPrimary.copy(alpha = 0.4f)),
                onClick = onClick
            )
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) TextPrimary else GoldDeep)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = code,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                fontFamily = PlusJakartaSans,
                color = if (isSelected) TextPrimary else TextSecondary,
                letterSpacing = 0.5.sp
            )
        }
    }
}

/**
 * Large, Clear Confirmation Banner
 * Displays an unmistakable confirmation banner when a sale or expense is saved
 */
@Composable
fun LargeActionConfirmationBanner(
    visible: Boolean,
    message: String,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { -it },
        exit = fadeOut() + slideOutVertically { -it },
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .shadow(4.dp, RoundedCornerShape(18.dp))
                .clip(RoundedCornerShape(18.dp))
                .background(GoldPrimary)
                .border(BorderStroke(2.dp, TextPrimary), RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = message,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = PlusJakartaSans,
                    color = TextPrimary,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
