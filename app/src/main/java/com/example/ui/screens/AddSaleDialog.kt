package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ItemCategory
import com.example.data.model.ItemType
import com.example.data.model.PaymentMode
import com.example.ui.components.NumericKeypad
import com.example.ui.theme.CashGreen
import com.example.ui.theme.CashGreenLight
import com.example.ui.theme.GlassBorderGold
import com.example.ui.theme.GoldDeep
import com.example.ui.theme.GoldGlassBrush
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.LiquidGlassButton
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RoseLiquid
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UpiBlue
import com.example.ui.theme.UpiBlueLight

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddSaleDialog(
    salesmanName: String,
    onDismiss: () -> Unit,
    onSaveSale: (category: String, itemType: String, customItem: String?, amount: Double, paymentMode: String, note: String?) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(ItemCategory.COM.title) }
    var selectedItemType by remember { mutableStateOf(ItemType.FOOTWEAR.title) }
    var customItemName by remember { mutableStateOf("") }
    var rawAmountString by remember { mutableStateOf("") }
    var selectedPaymentMode by remember { mutableStateOf(PaymentMode.CASH.title) }
    var noteText by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    val currentAmount = rawAmountString.toDoubleOrNull() ?: 0.0

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(22.dp))
                .border(BorderStroke(1.5.dp, GlassBorderGold), RoundedCornerShape(22.dp))
                .testTag("add_sale_dialog"),
            color = PureWhite,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(GoldLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = GoldDeep,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "New Sale Entry",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = "Salesman: $salesmanName",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("close_add_sale_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Amount Display Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(GoldLight)
                        .border(BorderStroke(2.dp, GoldPrimary), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp)
                        .testTag("sale_amount_display"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (rawAmountString.isEmpty()) "₹0" else "₹$rawAmountString",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Category Selection (COM, CHN, HM, OT)
                Text(
                    text = "CATEGORY",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ItemCategory.entries.forEach { cat ->
                        val isSelected = selectedCategory == cat.title
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) GoldPrimary else PureWhite)
                                .border(
                                    BorderStroke(
                                        if (isSelected) 2.dp else 1.5.dp,
                                        if (isSelected) TextPrimary else GlassBorderGold
                                    ),
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedCategory = cat.title }
                                .testTag("cat_chip_${cat.code}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = cat.code,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Text(
                                    text = cat.title,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) TextPrimary else TextMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Item Type Selection
                Text(
                    text = "ITEM TYPE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ItemType.entries.forEach { item ->
                        val isSelected = selectedItemType == item.title
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) GoldLight else PureWhite)
                                .border(
                                    BorderStroke(
                                        if (isSelected) 1.5.dp else 1.dp,
                                        if (isSelected) GoldPrimary else GlassBorderGold
                                    ),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedItemType = item.title }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .testTag("item_chip_${item.title}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.title,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                    }
                }

                // Custom item name field if "Other" is selected
                AnimatedVisibility(visible = selectedItemType == ItemType.OTHER.title) {
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        OutlinedTextField(
                            value = customItemName,
                            onValueChange = { customItemName = it },
                            label = { Text("Specify Item Name (e.g. Socks, Cap, Wallet)", fontSize = 14.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp)
                                .testTag("custom_item_name_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldDeep,
                                unfocusedBorderColor = GlassBorderGold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Mode Selection (Cash vs UPI)
                Text(
                    text = "PAYMENT MODE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // CASH
                    val isCash = selectedPaymentMode == PaymentMode.CASH.title
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isCash) CashGreenLight else PureWhite)
                            .border(
                                BorderStroke(
                                    if (isCash) 2.dp else 1.dp,
                                    if (isCash) CashGreen else GlassBorderGold
                                ),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedPaymentMode = PaymentMode.CASH.title }
                            .testTag("pay_mode_cash"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = null,
                                tint = if (isCash) CashGreen else TextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cash",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isCash) CashGreen else TextPrimary
                            )
                        }
                    }

                    // UPI
                    val isUpi = selectedPaymentMode == PaymentMode.UPI.title
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isUpi) UpiBlueLight else PureWhite)
                            .border(
                                BorderStroke(
                                    if (isUpi) 2.dp else 1.dp,
                                    if (isUpi) UpiBlue else GlassBorderGold
                                ),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedPaymentMode = PaymentMode.UPI.title }
                            .testTag("pay_mode_upi"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = null,
                                tint = if (isUpi) UpiBlue else TextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "UPI / QR",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isUpi) UpiBlue else TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Integrated Numeric Keypad
                NumericKeypad(
                    onDigitClick = { digit ->
                        if (rawAmountString.length < 7) {
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

                if (validationError != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = validationError!!,
                        fontSize = 14.sp,
                        color = RoseLiquid,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Large Save Button
                LiquidGlassButton(
                    text = if (currentAmount > 0) "SAVE SALE (₹${currentAmount.toInt()})" else "SAVE SALE",
                    onClick = {
                        if (currentAmount <= 0) {
                            validationError = "Please enter a valid amount greater than ₹0"
                            return@LiquidGlassButton
                        }
                        onSaveSale(
                            selectedCategory,
                            selectedItemType,
                            customItemName,
                            currentAmount,
                            selectedPaymentMode,
                            noteText
                        )
                    },
                    gradientBrush = GoldGlassBrush,
                    height = 60.dp,
                    fontSize = 18,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_add_sale_button")
                )
            }
        }
    }
}
