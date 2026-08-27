package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassBorderGold
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RoseLiquid
import com.example.ui.theme.RoseLiquidBg
import com.example.ui.theme.TextPrimary

@Composable
fun NumericKeypad(
    onDigitClick: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Grid 1, 2, 3
        KeypadRow(
            keys = listOf("1", "2", "3"),
            onKeyClick = onDigitClick
        )

        // Grid 4, 5, 6
        KeypadRow(
            keys = listOf("4", "5", "6"),
            onKeyClick = onDigitClick
        )

        // Grid 7, 8, 9
        KeypadRow(
            keys = listOf("7", "8", "9"),
            onKeyClick = onDigitClick
        )

        // Bottom row: Clear, 0, 00, Backspace
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Clear Key (C)
            GlassKeyButton(
                modifier = Modifier
                    .weight(1f)
                    .testTag("keypad_clear"),
                onClick = onClear,
                backgroundColor = Color(0xFFFFF1F2),
                borderColor = Color(0x66F43F5E)
            ) {
                Text(
                    text = "CLEAR",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = RoseLiquid,
                    letterSpacing = 0.5.sp
                )
            }

            // '0' Key
            GlassKeyButton(
                modifier = Modifier
                    .weight(1f)
                    .testTag("keypad_digit_0"),
                onClick = { onDigitClick("0") }
            ) {
                Text(
                    text = "0",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
            }

            // '00' Key for rapid retail entry
            GlassKeyButton(
                modifier = Modifier
                    .weight(1f)
                    .testTag("keypad_digit_00"),
                onClick = { onDigitClick("00") }
            ) {
                Text(
                    text = "00",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
            }

            // Backspace Key (⌫)
            GlassKeyButton(
                modifier = Modifier
                    .weight(1f)
                    .testTag("keypad_backspace"),
                onClick = onBackspace,
                backgroundColor = Color(0xFFFAF5E8),
                borderColor = GlassBorderGold
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Delete last digit",
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun KeypadRow(
    keys: List<String>,
    onKeyClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        keys.forEach { digit ->
            GlassKeyButton(
                modifier = Modifier
                    .weight(1f)
                    .testTag("keypad_digit_$digit"),
                onClick = { onKeyClick(digit) }
            ) {
                Text(
                    text = digit,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun GlassKeyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = PureWhite,
    borderColor: Color = GlassBorderGold,
    content: @Composable RowScope.() -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .height(58.dp) // Accessible 58dp height
            .shadow(2.dp, shape)
            .clip(shape)
            .background(backgroundColor)
            .border(BorderStroke(1.5.dp, borderColor), shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = GoldPrimary.copy(alpha = 0.35f)),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}
