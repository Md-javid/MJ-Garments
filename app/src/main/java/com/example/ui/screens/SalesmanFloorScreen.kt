package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.cloud.CloudSyncStatus
import com.example.data.model.ExpenseEntry
import com.example.data.model.ItemCategory
import com.example.data.model.SaleEntry
import com.example.data.util.DateUtils
import com.example.ui.SalesmanViewModel
import com.example.ui.components.BrandHeader
import com.example.ui.components.NumericKeypad
import com.example.ui.theme.GlassBorderGold
import com.example.ui.theme.GlassCard
import com.example.ui.theme.GlassCategoryPill
import com.example.ui.theme.GoldDeep
import com.example.ui.theme.GoldGlassBrush
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LargeActionConfirmationBanner
import com.example.ui.theme.LiquidGlassBackground
import com.example.ui.theme.LiquidGlassButton
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RoseLiquid
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.PlusJakartaSans
import kotlinx.coroutines.delay

@Composable
fun SalesmanFloorScreen(
    salesmanViewModel: SalesmanViewModel,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by salesmanViewModel.uiState.collectAsState()
    val todayTally by salesmanViewModel.todayClosingTally.collectAsState()
    val recentSales by salesmanViewModel.recentOneHourSales.collectAsState()
    val recentExpenses by salesmanViewModel.recentOneHourExpenses.collectAsState()

    val isDayLocked = todayTally?.isClosed == true
    val snackbarHostState = remember { SnackbarHostState() }

    // Quick Entry State
    var selectedCategory by remember { mutableStateOf(ItemCategory.COM.code) }
    var selectedPaymentMode by remember { mutableStateOf("Cash") }
    var customWriteUp by remember { mutableStateOf("") }
    var rawAmountString by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }
    var saleToVoid by remember { mutableStateOf<SaleEntry?>(null) }

    // Large Confirmation Banner Banner State
    var confirmationMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            confirmationMessage = msg
            snackbarHostState.showSnackbar(msg)
            salesmanViewModel.clearSnackbar()
            delay(3000)
            confirmationMessage = null
        }
    }

    LiquidGlassBackground(modifier = modifier.testTag("salesman_floor_screen")) {
        Column(modifier = Modifier.fillMaxSize()) {
            // White & Glowing Yellow Brand Header
            BrandHeader(
                currentUser = uiState.currentSalesman,
                isOwnerView = false,
                syncStatus = CloudSyncStatus.SYNCED,
                onLogoutClick = onLogoutClick
            )

            // Large Animated Action Confirmation Banner
            LargeActionConfirmationBanner(
                visible = confirmationMessage != null,
                message = confirmationMessage ?: ""
            )

            Box(modifier = Modifier.weight(1f)) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Day Closed Banner (if locked by Owner)
                    if (isDayLocked) {
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                backgroundColor = GoldLight,
                                borderColor = GoldPrimary
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = TextPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Today's sales register is closed by admin.",
                                        color = TextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Main Quick Sale Card in White & Gold Liquid Glass
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("quick_sale_card"),
                            contentPadding = 18.dp,
                            backgroundColor = PureWhite,
                            borderColor = GlassBorderGold,
                            elevation = 3.dp
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // 1. Large 4-Category Pill Buttons (COM, CHN, HM, OT)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("COM", "CHN", "HM", "OT").forEach { cat ->
                                        val isSelected = selectedCategory == cat
                                        GlassCategoryPill(
                                            code = cat,
                                            title = cat,
                                            isSelected = isSelected,
                                            accentColor = GoldPrimary,
                                            onClick = {
                                                if (!isDayLocked) selectedCategory = cat
                                            },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("cat_button_$cat")
                                        )
                                    }
                                }

                                // Optional write-up field (shown if OT is chosen)
                                AnimatedVisibility(visible = selectedCategory == "OT") {
                                    Column(modifier = Modifier.padding(top = 10.dp)) {
                                        OutlinedTextField(
                                            value = customWriteUp,
                                            onValueChange = { customWriteUp = it },
                                            placeholder = {
                                                Text(
                                                    "Item details (Optional)",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = TextMuted
                                                )
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(58.dp)
                                                .testTag("custom_writeup_input"),
                                            singleLine = true,
                                            shape = RoundedCornerShape(14.dp),
                                            textStyle = androidx.compose.ui.text.TextStyle(
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            ),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = GoldDeep,
                                                unfocusedBorderColor = Color(0x33000000),
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                focusedContainerColor = PureWhite,
                                                unfocusedContainerColor = PureWhite
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // 2. Large High-Contrast Amount Display Field
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(76.dp)
                                        .shadow(3.dp, RoundedCornerShape(18.dp))
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(GoldLight)
                                        .border(BorderStroke(2.dp, GoldPrimary), RoundedCornerShape(18.dp))
                                        .padding(horizontal = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (rawAmountString.isEmpty()) "₹0" else "₹$rawAmountString",
                                        fontSize = 42.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TextPrimary,
                                        letterSpacing = 1.sp
                                    )
                                }

                                if (validationError != null) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = validationError!!,
                                        fontSize = 15.sp,
                                        color = RoseLiquid,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // 3. Large Numeric Keypad
                                NumericKeypad(
                                    onDigitClick = { digit ->
                                        if (!isDayLocked && rawAmountString.length < 6) {
                                            rawAmountString += digit
                                            validationError = null
                                        }
                                    },
                                    onBackspace = {
                                        if (rawAmountString.isNotEmpty()) {
                                            rawAmountString = rawAmountString.dropLast(1)
                                            validationError = null
                                        }
                                    },
                                    onClear = {
                                        rawAmountString = ""
                                        validationError = null
                                    }
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // 4. Large Action Buttons (Minimum 58-62dp height)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Shop Expense Button (Frosted Crimson Tint)
                                    OutlinedButton(
                                        onClick = { salesmanViewModel.openAddExpenseDialog() },
                                        enabled = !isDayLocked,
                                        modifier = Modifier
                                            .weight(0.36f)
                                            .height(60.dp)
                                            .testTag("shop_expense_btn"),
                                        shape = RoundedCornerShape(18.dp),
                                        border = BorderStroke(1.5.dp, RoseLiquid.copy(alpha = 0.6f)),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = Color(0xFFFFF1F2),
                                            contentColor = RoseLiquid
                                        )
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Outbox,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp),
                                                tint = RoseLiquid
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                "Expense",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }

                                    // Save Sale Button with Rich Gold Gradient & Bold Black Text
                                    LiquidGlassButton(
                                        text = if (rawAmountString.isNotBlank()) "SAVE ₹$rawAmountString" else "SAVE SALE",
                                        onClick = {
                                            val amt = rawAmountString.toDoubleOrNull() ?: 0.0
                                            if (amt <= 0) {
                                                validationError = "Please enter an amount greater than ₹0"
                                                return@LiquidGlassButton
                                            }
                                            salesmanViewModel.quickSaveSale(
                                                category = selectedCategory,
                                                customWriteUp = customWriteUp,
                                                amount = amt,
                                                paymentMode = selectedPaymentMode
                                            )
                                            rawAmountString = ""
                                            customWriteUp = ""
                                            validationError = null
                                        },
                                        enabled = !isDayLocked,
                                        gradientBrush = GoldGlassBrush,
                                        icon = Icons.Default.Add,
                                        height = 60.dp,
                                        fontSize = 19,
                                        modifier = Modifier
                                            .weight(0.64f)
                                            .testTag("save_quick_sale_button")
                                    )
                                }
                            }
                        }
                    }

                    // Recent 1-Hour Entries Header
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Recent Sales",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(GoldLight)
                                        .border(BorderStroke(1.dp, GoldPrimary), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Last 2 Hours",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldDeep
                                    )
                                }
                            }

                            Text(
                                text = "${recentSales.size} sales",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldDeep
                            )
                        }
                    }

                    // 2-Hour Rolling List with Stacked Cards
                    if (recentSales.isEmpty() && recentExpenses.isEmpty()) {
                        item {
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                backgroundColor = PureWhite,
                                borderColor = GlassBorderGold
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No sales in the last 2 hours.\nReady for today's entries.",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextMuted,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        lineHeight = 22.sp
                                    )
                                }
                            }
                        }
                    } else {
                        items(recentSales, key = { "sale_${it.id}" }) { sale ->
                            RecentSaleRowItem(
                                sale = sale,
                                onCancelClick = { saleToVoid = sale }
                            )
                        }

                        items(recentExpenses, key = { "expense_${it.id}" }) { expense ->
                            RecentExpenseRowItem(expense = expense)
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }

    // Add Shop Expense Dialog
    if (uiState.showAddExpenseDialog) {
        AddExpenseDialog(
            salesmanName = uiState.currentSalesman?.name ?: "Salesman",
            onDismiss = { salesmanViewModel.closeAddExpenseDialog() },
            onSaveExpense = { type, custom, amt, recipient ->
                salesmanViewModel.saveShopExpense(type, custom, amt, recipient)
            }
        )
    }

    // Cancel / Delete Confirm Dialog (Clear, simple text)
    saleToVoid?.let { sale ->
        AlertDialog(
            onDismissRequest = { saleToVoid = null },
            title = {
                Text(
                    "Delete ₹${sale.amount.toInt()} (${sale.category})?",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    "This will cancel the entry made at ${DateUtils.formatDisplayTime(sale.timestamp)} (allowed within 1.5 hours).",
                    fontSize = 16.sp,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        salesmanViewModel.voidRecentSale(sale)
                        saleToVoid = null
                    },
                    modifier = Modifier.height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoseLiquid)
                ) {
                    Text("Delete Entry", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { saleToVoid = null },
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("Keep", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            },
            containerColor = PureWhite,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun RecentSaleRowItem(
    sale: SaleEntry,
    onCancelClick: () -> Unit
) {
    val isVoided = sale.isVoided
    val canCancel = DateUtils.isWithin1Point5Hours(sale.timestamp) && !isVoided

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("recent_sale_${sale.id}"),
        contentPadding = 14.dp,
        backgroundColor = if (isVoided) Color(0xFFF1F5F9) else PureWhite,
        borderColor = if (isVoided) Color(0x1F000000) else GlassBorderGold,
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Category Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(GoldPrimary)
                        .border(BorderStroke(1.dp, GoldDeep), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = sale.category,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (!sale.customItemName.isNullOrBlank()) "${sale.category} • ${sale.customItemName}" else sale.category,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isVoided) TextMuted else TextPrimary,
                        textDecoration = if (isVoided) TextDecoration.LineThrough else null
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = DateUtils.formatDisplayTime(sale.timestamp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = DateUtils.formatCurrency(sale.amount),
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isVoided) TextMuted else TextPrimary,
                    textDecoration = if (isVoided) TextDecoration.LineThrough else null
                )

                if (canCancel) {
                    Spacer(modifier = Modifier.width(10.dp))
                    IconButton(
                        onClick = onCancelClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = RoseLiquid,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentExpenseRowItem(expense: ExpenseEntry) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = 14.dp,
        backgroundColor = Color(0xFFFFF1F2),
        borderColor = RoseLiquid.copy(alpha = 0.4f),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(RoseLiquid.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "EXPENSE",
                        color = RoseLiquid,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = expense.effectiveType,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoseLiquid
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${expense.recipientNote} • ${DateUtils.formatDisplayTime(expense.timestamp)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
            }

            Text(
                text = "-${DateUtils.formatCurrency(expense.amount)}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = RoseLiquid
            )
        }
    }
}
