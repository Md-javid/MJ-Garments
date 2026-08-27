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
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ExpenseType
import com.example.ui.components.NumericKeypad
import com.example.ui.theme.GlassBorderGold
import com.example.ui.theme.LiquidGlassButton
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RoseLiquid
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddExpenseDialog(
    salesmanName: String,
    onDismiss: () -> Unit,
    onSaveExpense: (type: String, customType: String?, amount: Double, recipientNote: String) -> Unit
) {
    var selectedType by remember { mutableStateOf(ExpenseType.TEA_SNACKS.title) }
    var customType by remember { mutableStateOf("") }
    var rawAmountString by remember { mutableStateOf("") }
    var recipientNote by remember { mutableStateOf("") }
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
                .testTag("add_expense_dialog"),
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
                                .background(Color(0xFFFFF1F2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Outbox,
                                contentDescription = null,
                                tint = RoseLiquid,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Shop Expense",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = "Paid from cash drawer by $salesmanName",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("close_add_expense_dialog")
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
                        .background(Color(0xFFFFF1F2))
                        .border(BorderStroke(2.dp, RoseLiquid.copy(alpha = 0.5f)), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp)
                        .testTag("expense_amount_display"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (rawAmountString.isEmpty()) "₹0" else "₹$rawAmountString",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Black,
                        color = RoseLiquid
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Expense Type Selection
                Text(
                    text = "EXPENSE TYPE",
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
                    ExpenseType.entries.forEach { exp ->
                        val isSelected = selectedType == exp.title
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFFFFF1F2) else PureWhite)
                                .border(
                                    BorderStroke(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) RoseLiquid else GlassBorderGold
                                    ),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedType = exp.title }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .testTag("expense_type_${exp.title}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = exp.title,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                color = if (isSelected) RoseLiquid else TextPrimary
                            )
                        }
                    }
                }

                // Custom Expense Name
                AnimatedVisibility(visible = selectedType == ExpenseType.OTHER.title) {
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        OutlinedTextField(
                            value = customType,
                            onValueChange = { customType = it },
                            label = { Text("Specify (e.g. Repair, Maintenance)", fontSize = 14.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp)
                                .testTag("custom_expense_type_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RoseLiquid,
                                unfocusedBorderColor = GlassBorderGold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Recipient / Vendor field
                OutlinedTextField(
                    value = recipientNote,
                    onValueChange = { recipientNote = it },
                    label = { Text("Vendor / Person / Reason *", fontSize = 15.sp) },
                    placeholder = { Text("e.g. Tea Boy, Metro Cargo, Footwear Supply", fontSize = 14.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .testTag("expense_note_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoseLiquid,
                        unfocusedBorderColor = GlassBorderGold
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Keypad
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
                    text = if (currentAmount > 0) "LOG EXPENSE (₹${currentAmount.toInt()})" else "LOG EXPENSE",
                    onClick = {
                        if (currentAmount <= 0) {
                            validationError = "Please enter an amount greater than ₹0"
                            return@LiquidGlassButton
                        }
                        if (recipientNote.isBlank()) {
                            validationError = "Please specify who it was for or the reason"
                            return@LiquidGlassButton
                        }
                        onSaveExpense(
                            selectedType,
                            customType,
                            currentAmount,
                            recipientNote.trim()
                        )
                    },
                    gradientBrush = Brush.horizontalGradient(
                        listOf(Color(0xFFE11D48), Color(0xFFBE123C))
                    ),
                    height = 60.dp,
                    fontSize = 18,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_add_expense_button")
                )
            }
        }
    }
}
