package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SaleEntry
import com.example.data.util.DateUtils
import com.example.ui.theme.EmeraldLiquid
import com.example.ui.theme.GlassBorderGold
import com.example.ui.theme.GoldDeep
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RoseLiquid
import com.example.ui.theme.SapphireLiquid
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.YellowGlow

/**
 * Daily Tally Sheet Screen:
 * Digital Replica of the shop's physical handwritten column tally paper (COM, CHN, HM, OT).
 * Shows entries listed column by column with column subtotals and 1-click Print/Export.
 */
@Composable
fun DailyTallySheetScreen(
    selectedDateLabel: String,
    sales: List<SaleEntry>,
    cashTotal: Double,
    upiTotal: Double,
    expenseTotal: Double,
    expectedCash: Double,
    onPreviousDayClick: () -> Unit = {},
    onNextDayClick: () -> Unit = {},
    onCalendarSelectClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Categorize sales for this specific day into COM, CHN, HM, OT columns
    val activeSales = remember(sales) { sales.filter { !it.isVoided } }
    val comSales = remember(activeSales) { activeSales.filter { it.category.equals("COM", ignoreCase = true) } }
    val chnSales = remember(activeSales) { activeSales.filter { it.category.equals("CHN", ignoreCase = true) } }
    val hmSales = remember(activeSales) { activeSales.filter { it.category.equals("HM", ignoreCase = true) } }
    val otSales = remember(activeSales) { activeSales.filter { it.category.equals("OT", ignoreCase = true) } }

    val comTotal = remember(comSales) { comSales.sumOf { it.amount } }
    val chnTotal = remember(chnSales) { chnSales.sumOf { it.amount } }
    val hmTotal = remember(hmSales) { hmSales.sumOf { it.amount } }
    val otTotal = remember(otSales) { otSales.sumOf { it.amount } }
    val grandTotal = comTotal + chnTotal + hmTotal + otTotal

    // Find the maximum row count among columns
    val maxRows = maxOf(comSales.size, chnSales.size, hmSales.size, otSales.size).coerceAtLeast(1)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp)
    ) {
        // Date Selector Bar with Previous/Next Day controls & Calendar Picker
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(PureWhite)
                .border(BorderStroke(1.5.dp, GlassBorderGold), RoundedCornerShape(16.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousDayClick) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Day", tint = TextPrimary)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(
                        text = "DAILY TALLY SHEET",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldDeep,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = selectedDateLabel,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                }

                IconButton(onClick = onNextDayClick) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next Day", tint = TextPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Action Buttons: Print Daily Sheet (CSV Removed)
        Button(
            onClick = {
                val printText = generatePrintableDailySheet(
                    dateLabel = selectedDateLabel,
                    comSales = comSales,
                    chnSales = chnSales,
                    hmSales = hmSales,
                    otSales = otSales,
                    comTotal = comTotal,
                    chnTotal = chnTotal,
                    hmTotal = hmTotal,
                    otTotal = otTotal,
                    grandTotal = grandTotal,
                    cashTotal = cashTotal,
                    upiTotal = upiTotal,
                    expenseTotal = expenseTotal,
                    expectedCash = expectedCash
                )
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_SUBJECT, "MJ Garments Tally Sheet - $selectedDateLabel")
                    putExtra(Intent.EXTRA_TEXT, printText)
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Print / Share Daily Tally Sheet"))
            },
            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
        ) {
            Icon(Icons.Default.Print, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Print / Share Daily Sheet", fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Multi-Column Tally Sheet Card (Digital Replica of Shop Paper)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = YellowGlow)
                .clip(RoundedCornerShape(16.dp))
                .background(PureWhite)
                .border(BorderStroke(1.5.dp, GlassBorderGold), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Multi-Column Header Row (COM, CHN, HM, OT)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PureWhite)
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ColumnHeaderCell(title = "COM", sub = "Company", weight = 1f)
                    ColumnDivider()
                    ColumnHeaderCell(title = "CHN", sub = "China", weight = 1f)
                    ColumnDivider()
                    ColumnHeaderCell(title = "HM", sub = "Handmade", weight = 1f)
                    ColumnDivider()
                    ColumnHeaderCell(title = "OT", sub = "Other", weight = 1f)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFCBD5E1))
                )

                // Scrollable Rows Body
                val maxRows = maxOf(comSales.size, chnSales.size, hmSales.size, otSales.size, 1)
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(maxRows) { index ->
                        val com = comSales.getOrNull(index)
                        val chn = chnSales.getOrNull(index)
                        val hm = hmSales.getOrNull(index)
                        val ot = otSales.getOrNull(index)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (index % 2 == 0) PureWhite else Color(0xFFF8FAFC))
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ColumnDataCell(amount = com?.amount, weight = 1f)
                            ColumnDivider()
                            ColumnDataCell(amount = chn?.amount, weight = 1f)
                            ColumnDivider()
                            ColumnDataCell(amount = hm?.amount, weight = 1f)
                            ColumnDivider()
                            ColumnDataCell(amount = ot?.amount, weight = 1f)
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.5.dp)
                                .background(Color(0xFFE2E8F0))
                        )
                    }
                }

                // Column Totals Footer Row
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(GoldPrimary)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(GoldLight)
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ColumnTotalCell(title = "₹${comTotal.toInt()}", count = comSales.size, weight = 1f)
                    ColumnDivider()
                    ColumnTotalCell(title = "₹${chnTotal.toInt()}", count = chnSales.size, weight = 1f)
                    ColumnDivider()
                    ColumnTotalCell(title = "₹${hmTotal.toInt()}", count = hmSales.size, weight = 1f)
                    ColumnDivider()
                    ColumnTotalCell(title = "₹${otTotal.toInt()}", count = otSales.size, weight = 1f)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Executive Day Summary Strip (Zero Cash/UPI distinction)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(PureWhite)
                .border(BorderStroke(1.dp, Color(0xFFCBD5E1)), RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            val totalBills = comSales.size + chnSales.size + hmSales.size + otSales.size
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("GROSS SALES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text("₹${grandTotal.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("SHOP EXPENSES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RoseLiquid)
                    Text("-₹${expenseTotal.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = RoseLiquid)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("TOTAL ITEMS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldDeep)
                    Text("$totalBills items", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                }
            }
        }
    }
}

@Composable
private fun ColumnHeaderCell(title: String, sub: String, weight: Float) {
    Column(
        modifier = Modifier.width(76.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        Text(text = sub, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GoldDeep)
    }
}

@Composable
private fun ColumnDataCell(amount: Double?, weight: Float = 1f) {
    Box(
        modifier = Modifier.width(76.dp),
        contentAlignment = Alignment.Center
    ) {
        if (amount != null) {
            Text(
                text = "${amount.toInt()}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        } else {
            Text("-", fontSize = 14.sp, color = Color(0xFFCBD5E1))
        }
    }
}

@Composable
private fun ColumnTotalCell(title: String, count: Int, weight: Float) {
    Column(
        modifier = Modifier.width(76.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        Text(text = "$count bills", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldDeep)
    }
}

@Composable
private fun ColumnDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(26.dp)
            .background(Color(0xFFE2E8F0))
    )
}

// Generate formatted text matching the physical tally paper
private fun generatePrintableDailySheet(
    dateLabel: String,
    comSales: List<SaleEntry>,
    chnSales: List<SaleEntry>,
    hmSales: List<SaleEntry>,
    otSales: List<SaleEntry>,
    comTotal: Double,
    chnTotal: Double,
    hmTotal: Double,
    otTotal: Double,
    grandTotal: Double,
    cashTotal: Double,
    upiTotal: Double,
    expenseTotal: Double,
    expectedCash: Double
): String {
    val sb = StringBuilder()
    sb.append("=========================================\n")
    sb.append("      MJ GARMENTS — DAILY TALLY SHEET    \n")
    sb.append("          Broadway, Kerala Store         \n")
    sb.append("Date: $dateLabel\n")
    sb.append("=========================================\n\n")

    val max = maxOf(comSales.size, chnSales.size, hmSales.size, otSales.size)
    sb.append(String.format("%-8s | %-8s | %-8s | %-8s\n", "COM", "CHN", "HM", "OT"))
    sb.append("-----------------------------------------\n")

    for (i in 0 until max) {
        val c = comSales.getOrNull(i)?.amount?.toInt()?.toString() ?: "-"
        val ch = chnSales.getOrNull(i)?.amount?.toInt()?.toString() ?: "-"
        val h = hmSales.getOrNull(i)?.amount?.toInt()?.toString() ?: "-"
        val o = otSales.getOrNull(i)?.amount?.toInt()?.toString() ?: "-"
        sb.append(String.format("%-8s | %-8s | %-8s | %-8s\n", c, ch, h, o))
    }

    sb.append("=========================================\n")
    sb.append(String.format("%-8s | %-8s | %-8s | %-8s\n", "₹${comTotal.toInt()}", "₹${chnTotal.toInt()}", "₹${hmTotal.toInt()}", "₹${otTotal.toInt()}"))
    sb.append("=========================================\n\n")

    sb.append("TOTAL DAY SALES : ₹${grandTotal.toInt()}\n")
    sb.append("Cash Inflow     : ₹${cashTotal.toInt()}\n")
    sb.append("UPI Inflow      : ₹${upiTotal.toInt()}\n")
    sb.append("Shop Expenses   : -₹${expenseTotal.toInt()}\n")
    sb.append("NET DRAWER CASH : ₹${expectedCash.toInt()}\n\n")
    sb.append("Verified by Syed Ibrahim (Admin)")
    return sb.toString()
}

// Generate CSV formatted column-wise matching the sheet
private fun generateDailySheetCsv(
    dateLabel: String,
    comSales: List<SaleEntry>,
    chnSales: List<SaleEntry>,
    hmSales: List<SaleEntry>,
    otSales: List<SaleEntry>,
    comTotal: Double,
    chnTotal: Double,
    hmTotal: Double,
    otTotal: Double,
    grandTotal: Double
): String {
    val sb = StringBuilder()
    sb.append("MJ GARMENTS — DAILY TALLY SHEET\n")
    sb.append("Date:,$dateLabel\n\n")
    sb.append("Row No,COM (Rs),CHN (Rs),HM (Rs),OT (Rs)\n")

    val max = maxOf(comSales.size, chnSales.size, hmSales.size, otSales.size)
    for (i in 0 until max) {
        val c = comSales.getOrNull(i)?.amount?.toInt()?.toString() ?: ""
        val ch = chnSales.getOrNull(i)?.amount?.toInt()?.toString() ?: ""
        val h = hmSales.getOrNull(i)?.amount?.toInt()?.toString() ?: ""
        val o = otSales.getOrNull(i)?.amount?.toInt()?.toString() ?: ""
        sb.append("${i + 1},$c,$ch,$h,$o\n")
    }

    sb.append("\nTOTALS,${comTotal.toInt()},${chnTotal.toInt()},${hmTotal.toInt()},${otTotal.toInt()}\n")
    sb.append("GRAND TOTAL,,,,${grandTotal.toInt()}\n")
    return sb.toString()
}
