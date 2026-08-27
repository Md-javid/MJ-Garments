package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.util.DateUtils
import com.example.ui.theme.CashGreen
import com.example.ui.theme.GlassBorderGold
import com.example.ui.theme.GlassCard
import com.example.ui.theme.GoldDeep
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UpiBlue
import com.example.ui.theme.WarmCreamSurface

val ChartPalette = listOf(
    GoldPrimary,
    TextPrimary,
    UpiBlue,
    Color(0xFFD97706),
    CashGreen,
    Color(0xFF8B5CF6)
)

@Composable
fun DistributionBarChartCard(
    title: String,
    data: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    val total = data.values.sum()

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("distribution_bar_chart"),
        contentPadding = 18.dp,
        backgroundColor = PureWhite,
        borderColor = GlassBorderGold,
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = DateUtils.formatCurrency(total),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldDeep
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (total == 0.0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No sales data recorded for this period",
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                }
            } else {
                // Multi-segment progress bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(WarmCreamSurface)
                ) {
                    data.entries.filter { it.value > 0 }.forEachIndexed { index, entry ->
                        val weight = (entry.value / total).toFloat().coerceIn(0.01f, 1f)
                        val color = ChartPalette[index % ChartPalette.size]
                        Box(
                            modifier = Modifier
                                .weight(weight)
                                .fillMaxHeight()
                                .background(color)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Breakdown Legend items
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    data.entries.sortedByDescending { it.value }.forEachIndexed { index, (key, value) ->
                        val percent = if (total > 0) ((value / total) * 100).toInt() else 0
                        val color = ChartPalette[index % ChartPalette.size]

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(if (value > 0) color else TextMuted)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = key,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$percent%",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Text(
                                    text = DateUtils.formatCurrency(value),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DonutSplitCard(
    title: String,
    cashAmount: Double,
    upiAmount: Double,
    modifier: Modifier = Modifier
) {
    val total = cashAmount + upiAmount
    val cashRatio = if (total > 0) (cashAmount / total).toFloat() else 0.5f

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("donut_split_card"),
        contentPadding = 18.dp,
        backgroundColor = PureWhite,
        borderColor = GlassBorderGold,
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Donut Canvas
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(110.dp)) {
                        val strokeWidth = 16.dp.toPx()
                        val diameter = size.minDimension - strokeWidth
                        val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                        val arcSize = Size(diameter, diameter)

                        // Draw Cash Arc (Green)
                        val cashSweep = cashRatio * 360f
                        drawArc(
                            color = CashGreen,
                            startAngle = -90f,
                            sweepAngle = cashSweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // Draw UPI Arc (Blue)
                        val upiSweep = (1f - cashRatio) * 360f
                        drawArc(
                            color = UpiBlue,
                            startAngle = -90f + cashSweep,
                            sweepAngle = upiSweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TOTAL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        Text(
                            text = DateUtils.formatCurrency(total),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                    }
                }

                // Legend
                Column(
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Cash
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(CashGreen)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Cash: ${if (total > 0) ((cashAmount / total) * 100).toInt() else 0}%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = DateUtils.formatCurrency(cashAmount),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = CashGreen
                            )
                        }
                    }

                    // UPI
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(UpiBlue)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "UPI: ${if (total > 0) ((upiAmount / total) * 100).toInt() else 0}%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = DateUtils.formatCurrency(upiAmount),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = UpiBlue
                            )
                        }
                    }
                }
            }
        }
    }
}
