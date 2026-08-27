package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuditLog
import com.example.data.model.DailyClosingTally
import com.example.data.model.ExpenseEntry
import com.example.data.model.ExpenseType
import com.example.data.model.ItemCategory
import com.example.data.model.ItemType
import com.example.data.model.PaymentMode
import com.example.data.model.SaleEntry
import com.example.data.model.SalesmanUser
import com.example.data.util.DateUtils
import com.example.ui.DateFilterMode
import com.example.ui.OwnerStats
import com.example.ui.OwnerUiState
import com.example.ui.OwnerViewModel
import com.example.ui.SalesmanPerformance
import com.example.ui.components.BrandHeader
import com.example.ui.components.DistributionBarChartCard
import com.example.ui.components.DonutSplitCard
import com.example.ui.components.FloorExpenseSummaryCard
import com.example.ui.components.PrimarySalesSummaryCard
import com.example.ui.theme.CashGreen
import com.example.ui.theme.CashGreenLight
import com.example.ui.theme.CharcoalDark
import com.example.ui.theme.CharcoalMedium
import com.example.ui.theme.EmeraldLiquid
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedLight
import com.example.ui.theme.GlassBorderGold
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.OnGoldContainer
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RapidoYellow
import com.example.ui.theme.RoseLiquid
import com.example.ui.theme.ShopBackground
import com.example.ui.theme.ShopBorder
import com.example.ui.theme.ShopSurface
import com.example.ui.theme.ShopSurfaceVariant
import com.example.ui.theme.ShopTextMuted
import com.example.ui.theme.ShopTextPrimary
import com.example.ui.theme.ShopTextSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UpiBlue
import com.example.ui.theme.UpiBlueLight

@Composable
fun OwnerDashboardScreen(
    ownerViewModel: OwnerViewModel,
    currentUser: SalesmanUser?,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by ownerViewModel.uiState.collectAsState()
    val stats by ownerViewModel.stats.collectAsState()
    val sales by ownerViewModel.filteredSales.collectAsState()
    val expenses by ownerViewModel.filteredExpenses.collectAsState()
    val salesmen by ownerViewModel.allSalesmen.collectAsState()
    val closingTally by ownerViewModel.selectedDateClosingTally.collectAsState()
    val allClosingTallies by ownerViewModel.allClosingTallies.collectAsState()
    val auditLogs by ownerViewModel.auditLogs.collectAsState()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedNavTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            ownerViewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = modifier.testTag("owner_dashboard_screen"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            BrandHeader(
                currentUser = currentUser,
                onLogoutClick = onLogoutClick
            )
        },
        containerColor = ShopBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Pagewise App Navigation Bar
            ScrollableTabRow(
                selectedTabIndex = selectedNavTab,
                containerColor = PureWhite,
                contentColor = GoldPrimary,
                edgePadding = 12.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedNavTab]),
                        color = GoldPrimary,
                        height = 3.5.dp
                    )
                }
            ) {
                listOf(
                    "Daily Sheet",
                    "Register (${sales.size})",
                    "Day Closing",
                    "Staff & Leave",
                    "Analytics",
                    "Audit Logs"
                ).forEachIndexed { index, title ->
                    Tab(
                        selected = selectedNavTab == index,
                        onClick = { selectedNavTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedNavTab == index) FontWeight.Black else FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (selectedNavTab == index) TextPrimary else TextSecondary
                            )
                        }
                    )
                }
            }

            // Main Pagewise Screen Content
            when (selectedNavTab) {
                0 -> DailyTallySheetScreen(
                    selectedDateLabel = uiState.selectedDateFilter.label,
                    sales = sales,
                    cashTotal = stats.cashSales,
                    upiTotal = stats.upiSales,
                    expenseTotal = stats.totalExpenses,
                    expectedCash = stats.expectedCashInHand,
                    onPreviousDayClick = { /* Navigate to previous date */ },
                    onNextDayClick = { /* Navigate to next date */ }
                )
                1 -> SalesRegisterTabContent(
                    sales = sales,
                    expenses = expenses,
                    salesmen = salesmen,
                    uiState = uiState,
                    onSearchChange = { ownerViewModel.setSearchQuery(it) },
                    onCategoryFilter = { ownerViewModel.setFilterCategory(it) },
                    onItemTypeFilter = { ownerViewModel.setFilterItemType(it) },
                    onPaymentModeFilter = { ownerViewModel.setFilterPaymentMode(it) },
                    onSalesmanFilter = { ownerViewModel.setFilterSalesman(it) },
                    onToggleVoided = { ownerViewModel.toggleShowVoidedOnly(it) },
                    onClearFilters = { ownerViewModel.clearAllFilters() },
                    onPromptEditSale = { ownerViewModel.promptEditSale(it) },
                    onPromptVoidSale = { ownerViewModel.promptVoidSale(it) },
                    onExportCategoryCsv = {
                        val csv = ownerViewModel.generateCategorySeparatedCsv()
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_SUBJECT, "MJ Garments Category Sales Register")
                            putExtra(Intent.EXTRA_TEXT, csv)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Download / Export Category Register (CSV)")
                        context.startActivity(shareIntent)
                    }
                )
                2 -> EndOfDayTallyTabContent(
                    stats = stats,
                    uiState = uiState,
                    closingTally = closingTally,
                    talliesHistory = allClosingTallies,
                    onPhysicalCashChange = { ownerViewModel.setPhysicalCashInput(it) },
                    onToggleDenom = { ownerViewModel.toggleDenominationCalculator(it) },
                    onUpdateDenom = { d5, d2, d1, d50, d20, d10, c ->
                        ownerViewModel.updateDenomination(d5, d2, d1, d50, d20, d10, c)
                    },
                    onNotesChange = { ownerViewModel.setClosingNotes(it) },
                    onCloseDay = { ownerViewModel.performClosingTally(currentUser?.name ?: "Syed Ibrahim") },
                    onReopenClick = { ownerViewModel.showReopenDialog(true) }
                )
                3 -> SalesmenManagementTabContent(
                    salesmen = salesmen,
                    onAddClick = { ownerViewModel.openAddSalesmanDialog() },
                    onEditClick = { ownerViewModel.openEditSalesmanDialog(it) },
                    onToggleLeave = { user, onLeave ->
                        ownerViewModel.toggleSalesmanLeave(user, onLeave, currentUser?.name ?: "Syed Ibrahim")
                    },
                    onDeleteClick = { user ->
                        ownerViewModel.deleteSalesmanAccount(user.id, currentUser?.name ?: "Syed Ibrahim")
                    }
                )
                4 -> AnalyticsTabContent(
                    stats = stats,
                    uiState = uiState,
                    closingTally = closingTally,
                    onGenerateAIInsight = { ownerViewModel.generateAIInsights() },
                    onShareReport = {
                        val reportText = ownerViewModel.generateShareableReport()
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, reportText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Share MJ Garments Sales Report")
                        context.startActivity(shareIntent)
                    }
                )
                5 -> AuditTrailTabContent(auditLogs = auditLogs)
            }
        }
    }

    // Owner Edit Sale Dialog
    uiState.showEditModalForSale?.let { sale ->
        OwnerEditSaleDialog(
            sale = sale,
            onDismiss = { ownerViewModel.closeEditModal() },
            onConfirmEdit = { cat, item, custom, amt, mode, note ->
                ownerViewModel.executeEditSale(
                    sale, cat, item, custom, amt, mode, note,
                    currentUser?.name ?: "Syed Ibrahim"
                )
            }
        )
    }

    // Owner Void Sale Dialog
    uiState.showVoidModalForSale?.let { sale ->
        OwnerVoidSaleDialog(
            sale = sale,
            onDismiss = { ownerViewModel.closeVoidModal() },
            onConfirmVoid = { reason ->
                ownerViewModel.executeVoidSale(sale, reason, currentUser?.name ?: "Syed Ibrahim")
            }
        )
    }

    // Add / Edit Salesman Dialog
    if (uiState.showAddSalesmanDialog) {
        SalesmanAccountDialog(
            salesmanToEdit = uiState.selectedSalesmanForEdit,
            onDismiss = { ownerViewModel.closeSalesmanDialog() },
            onSave = { id, name, phone, pin, active, onLeave ->
                ownerViewModel.saveSalesmanAccount(id, name, phone, pin, active, onLeave, currentUser?.name ?: "Syed Ibrahim")
            }
        )
    }

    // Reopen Confirm Dialog
    if (uiState.showReopenConfirmDialog) {
        AlertDialog(
            onDismissRequest = { ownerViewModel.showReopenDialog(false) },
            title = { Text("Reopen Sales Register?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Reopening will unlock today's register and allow salesmen to add or modify transactions. Syed Ibrahim's audit log will record this action.")
            },
            confirmButton = {
                Button(
                    onClick = { ownerViewModel.reopenDay(currentUser?.name ?: "Syed Ibrahim") },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("Reopen Register", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { ownerViewModel.showReopenDialog(false) }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DateFilterPillsRow(
    selectedFilter: DateFilterMode,
    onSelectFilter: (DateFilterMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ShopSurface)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        DateFilterMode.entries.forEach { mode ->
            val isSelected = selectedFilter == mode
            Box(
                modifier = Modifier
                    .height(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) GoldPrimary else PureWhite)
                    .border(
                        BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) TextPrimary else GlassBorderGold
                        ),
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { onSelectFilter(mode) }
                    .padding(horizontal = 16.dp)
                    .testTag("date_filter_${mode.name}"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mode.label,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
        }
    }
}

// ----------------------------------------------------
// TAB 1: ANALYTICS & LIVE
// ----------------------------------------------------
@Composable
private fun AnalyticsTabContent(
    stats: OwnerStats,
    uiState: OwnerUiState,
    closingTally: DailyClosingTally?,
    onGenerateAIInsight: () -> Unit,
    onShareReport: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Big Revenue Card
            PrimarySalesSummaryCard(
                title = "Total Shop Sales (${uiState.selectedDateFilter.label})",
                totalSales = stats.totalSales,
                txnCount = stats.transactionCount,
                cashAmount = stats.cashSales,
                upiAmount = stats.upiSales
            )
        }

        item {
            FloorExpenseSummaryCard(
                totalExpenses = stats.totalExpenses,
                expectedCash = stats.expectedCashInHand
            )
        }

        // Gemini AI Smart Business Advisor Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gemini_ai_advisor_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ShopSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldPrimary.copy(alpha = 0.6f))),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(RapidoYellow),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFF0F172A),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Gemini Business Insights",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShopTextPrimary
                            )
                        }

                        IconButton(
                            onClick = onGenerateAIInsight,
                            enabled = !uiState.isGeneratingAIInsight,
                            modifier = Modifier.size(32.dp)
                        ) {
                            if (uiState.isGeneratingAIInsight) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = GoldPrimary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh Insights",
                                    tint = GoldDark
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (uiState.aiInsight != null) {
                        val insight = uiState.aiInsight
                        Text(
                            text = insight.summaryTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = insight.executiveSummary,
                            fontSize = 13.sp,
                            color = ShopTextPrimary,
                            lineHeight = 18.sp
                        )

                        if (insight.keyStrengths.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Store Strengths:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CashGreen
                            )
                            insight.keyStrengths.forEach { s ->
                                Text(
                                    text = "• $s",
                                    fontSize = 12.sp,
                                    color = ShopTextSecondary,
                                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                )
                            }
                        }

                        if (insight.alertsOrRecommendations.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Syed Ibrahim Alerts & Observations:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ExpenseRed
                            )
                            insight.alertsOrRecommendations.forEach { a ->
                                Text(
                                    text = "• $a",
                                    fontSize = 12.sp,
                                    color = ShopTextSecondary,
                                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(GoldContainer)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Tomorrow's Focus: ${insight.suggestedFocusForTomorrow}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OnGoldContainer
                            )
                        }
                    } else {
                        Text(
                            text = "Tap refresh to generate Gemini AI business analysis and store turnover insights for ${uiState.selectedDateFilter.label}.",
                            fontSize = 12.sp,
                            color = ShopTextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onGenerateAIInsight,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate AI Report", fontSize = 13.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // Charts
        item {
            DistributionBarChartCard(
                title = "Category Breakdown (COM / CHN / HM / OT)",
                data = stats.categoryBreakdown
            )
        }

        item {
            DistributionBarChartCard(
                title = "Item Type Breakdown (Footwear, Belts, Purses, etc.)",
                data = stats.itemTypeBreakdown
            )
        }

        item {
            DonutSplitCard(
                title = "Cash vs UPI Payment Split",
                cashAmount = stats.cashSales,
                upiAmount = stats.upiSales
            )
        }

        // Salesman Floor Leaderboard
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("salesman_leaderboard_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ShopSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ShopBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Staff Sales Performance",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopTextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (stats.salesmanLeaderboard.isEmpty()) {
                        Text(
                            text = "No salesman transactions in this period",
                            fontSize = 12.sp,
                            color = ShopTextMuted
                        )
                    } else {
                        stats.salesmanLeaderboard.forEachIndexed { index, sp ->
                            SalesmanLeaderboardRow(rank = index + 1, performance = sp)
                            if (index < stats.salesmanLeaderboard.size - 1) {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }
        }

        // Share / Export Report Button
        item {
            Button(
                onClick = onShareReport,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("share_report_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RapidoYellow,
                    contentColor = Color(0xFF0F172A)
                ),
                border = BorderStroke(1.5.dp, Color(0xFF0F172A))
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Share / Export Sales Summary (${uiState.selectedDateFilter.label})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SalesmanLeaderboardRow(
    rank: Int,
    performance: SalesmanPerformance
) {
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
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(if (rank == 1) GoldPrimary else ShopSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rank",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (rank == 1) Color.White else ShopTextSecondary
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = performance.salesmanName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopTextPrimary
                )
                Text(
                    text = "${performance.transactionCount} sales • Cash: ₹${performance.cashAmount.toInt()} | UPI: ₹${performance.upiAmount.toInt()}",
                    fontSize = 11.sp,
                    color = ShopTextSecondary
                )
            }
        }

        Text(
            text = DateUtils.formatCurrency(performance.totalSales),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = ShopTextPrimary
        )
    }
}

// ----------------------------------------------------
// TAB 2: SALES REGISTER & FULL TRANSACTION LOG
// ----------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SalesRegisterTabContent(
    sales: List<SaleEntry>,
    expenses: List<ExpenseEntry>,
    salesmen: List<SalesmanUser>,
    uiState: OwnerUiState,
    onSearchChange: (String) -> Unit,
    onCategoryFilter: (String?) -> Unit,
    onItemTypeFilter: (String?) -> Unit,
    onPaymentModeFilter: (String?) -> Unit,
    onSalesmanFilter: (String?) -> Unit,
    onToggleVoided: (Boolean) -> Unit,
    onClearFilters: () -> Unit,
    onPromptEditSale: (SaleEntry) -> Unit,
    onPromptVoidSale: (SaleEntry) -> Unit,
    onExportCategoryCsv: () -> Unit
) {
    val filteredSales = remember(sales, uiState) {
        sales.filter { sale ->
            val matchesQuery = uiState.searchQuery.isBlank() ||
                    sale.effectiveItemName.contains(uiState.searchQuery, ignoreCase = true) ||
                    sale.salesmanName.contains(uiState.searchQuery, ignoreCase = true) ||
                    (sale.note?.contains(uiState.searchQuery, ignoreCase = true) == true)
            val matchesCat = uiState.filterCategory == null || sale.category.equals(uiState.filterCategory, ignoreCase = true)
            val matchesItem = uiState.filterItemType == null || sale.itemType.equals(uiState.filterItemType, ignoreCase = true)
            val matchesPay = uiState.filterPaymentMode == null || sale.paymentMode.equals(uiState.filterPaymentMode, ignoreCase = true)
            val matchesSalesman = uiState.filterSalesmanId == null || sale.salesmanId == uiState.filterSalesmanId
            val matchesVoid = if (uiState.showVoidedOnly) sale.isVoided else true

            matchesQuery && matchesCat && matchesItem && matchesPay && matchesSalesman && matchesVoid
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Action Bar: Download Category Table (CSV / Print)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Sales Register",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "${filteredSales.size} recorded bills in selected period",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Input
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search bills, staff, items, notes...", color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sales_register_search_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = GlassBorderGold,
                    focusedContainerColor = PureWhite,
                    unfocusedContainerColor = PureWhite
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Pills
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Category Pills
                ItemCategory.entries.forEach { cat ->
                    val isSelected = uiState.filterCategory == cat.title
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategoryFilter(if (isSelected) null else cat.title) },
                        label = { Text(cat.code, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldPrimary,
                            selectedLabelColor = TextPrimary,
                            containerColor = PureWhite
                        )
                    )
                }

                // Voided toggle
                FilterChip(
                    selected = uiState.showVoidedOnly,
                    onClick = { onToggleVoided(!uiState.showVoidedOnly) },
                    label = { Text("Voided Only", fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFFF1F2),
                        selectedLabelColor = RoseLiquid,
                        containerColor = PureWhite
                    )
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Showing ${filteredSales.size} Transactions",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShopTextSecondary
                )
                TextButton(onClick = onClearFilters) {
                    Text("Clear Filters", fontSize = 12.sp, color = GoldDark)
                }
            }
        }

        if (filteredSales.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ShopSurfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No transactions match your search/filter criteria.",
                            fontSize = 13.sp,
                            color = ShopTextSecondary
                        )
                    }
                }
            }
        } else {
            items(filteredSales, key = { it.id }) { sale ->
                OwnerSaleItemCard(
                    sale = sale,
                    onEditClick = { onPromptEditSale(sale) },
                    onVoidClick = { onPromptVoidSale(sale) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OwnerSaleItemCard(
    sale: SaleEntry,
    onEditClick: () -> Unit,
    onVoidClick: () -> Unit
) {
    val isVoided = sale.isVoided
    val isCash = sale.paymentMode.equals("CASH", ignoreCase = true)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("owner_sale_card_${sale.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isVoided) ShopSurfaceVariant.copy(alpha = 0.5f) else ShopSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ShopBorder))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(RapidoYellow)
                            .border(BorderStroke(1.dp, Color(0xFF0F172A)), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = sale.category,
                            color = Color(0xFF0F172A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = sale.effectiveItemName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isVoided) ShopTextMuted else ShopTextPrimary,
                            textDecoration = if (isVoided) TextDecoration.LineThrough else null
                        )
                        Text(
                            text = "Salesman: ${sale.salesmanName} • ${DateUtils.formatFullDateTime(sale.timestamp)}",
                            fontSize = 11.sp,
                            color = ShopTextSecondary
                        )
                    }
                }

                Text(
                    text = DateUtils.formatCurrency(sale.amount),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isVoided) ShopTextMuted else ShopTextPrimary,
                    textDecoration = if (isVoided) TextDecoration.LineThrough else null
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isCash) CashGreenLight else UpiBlueLight)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = sale.paymentMode.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCash) CashGreen else UpiBlue
                        )
                    }

                    if (!sale.note.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Note: ${sale.note}",
                            fontSize = 12.sp,
                            color = ShopTextMuted
                        )
                    }
                }

                // Owner Actions (Edit & Void)
                if (isVoided) {
                    Text(
                        text = "VOIDED: ${sale.voidReason ?: ""}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ExpenseRed
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onEditClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Sale",
                                tint = GoldDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = onVoidClick,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Void Sale",
                                tint = ExpenseRed,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB 3: END-OF-DAY CASH TALLY & CLOSING
// ----------------------------------------------------
@Composable
private fun EndOfDayTallyTabContent(
    stats: OwnerStats,
    uiState: OwnerUiState,
    closingTally: DailyClosingTally?,
    talliesHistory: List<DailyClosingTally>,
    onPhysicalCashChange: (String) -> Unit,
    onToggleDenom: (Boolean) -> Unit,
    onUpdateDenom: (d5: Int, d2: Int, d1: Int, d50: Int, d20: Int, d10: Int, c: Double) -> Unit,
    onNotesChange: (String) -> Unit,
    onCloseDay: () -> Unit,
    onReopenClick: () -> Unit
) {
    val isClosed = closingTally?.isClosed == true
    val actualCash = uiState.physicalCashInput.toDoubleOrNull() ?: 0.0
    val expectedCash = stats.expectedCashInHand
    val diff = actualCash - expectedCash

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // Status Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("closing_status_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isClosed) Color(0xFFFEF9C3) else ShopSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(if (isClosed) RapidoYellow else ShopBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isClosed) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = if (isClosed) Color(0xFF713F12) else CashGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isClosed) "REGISTER CLOSED (${uiState.selectedCustomDateKey})" else "REGISTER OPEN (${uiState.selectedCustomDateKey})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isClosed) Color(0xFF713F12) else ShopTextPrimary
                            )
                        }

                        if (isClosed) {
                            OutlinedButton(
                                onClick = onReopenClick,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                                border = ButtonDefaults.outlinedButtonBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldPrimary))
                            ) {
                                Text("Reopen Day", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (isClosed && closingTally != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Closed by ${closingTally.closedBy} on ${DateUtils.formatFullDateTime(closingTally.closedAt)}",
                            fontSize = 12.sp,
                            color = GoldLight
                        )
                        Text(
                            text = "Counted Cash: ₹${closingTally.actualPhysicalCash.toInt()} | Difference: ₹${closingTally.cashDifference.toInt()}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Calculation Breakdown Table
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ShopSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ShopBorder))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "System Financial Ledger",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShopTextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LedgerRow(label = "(+) Total Cash Sales Collected", amount = stats.cashSales, color = CashGreen)
                    LedgerRow(label = "(+) Total UPI / Digital Sales", amount = stats.upiSales, color = UpiBlue)
                    LedgerRow(label = "(-) Total Out-of-Drawer Expenses", amount = stats.totalExpenses, color = ExpenseRed)

                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(ShopBorder))
                    Spacer(modifier = Modifier.height(8.dp))

                    LedgerRow(
                        label = "(=) Expected Physical Cash in Hand",
                        amount = expectedCash,
                        color = GoldDark,
                        isBold = true
                    )
                }
            }
        }

        if (!isClosed) {
            // Physical Cash Input & Denominations
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ShopSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(GoldPrimary))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Actual Physical Drawer Cash",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShopTextPrimary
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Denomination Calc",
                                    fontSize = 11.sp,
                                    color = ShopTextSecondary
                                )
                                Switch(
                                    checked = uiState.useDenominationCalculator,
                                    onCheckedChange = onToggleDenom,
                                    modifier = Modifier.padding(start = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (uiState.useDenominationCalculator) {
                            // Denomination Counter Grid
                            val denom = uiState.denominations
                            DenominationCounterRow(
                                label = "₹500 notes",
                                count = denom.count500,
                                onCountChange = { onUpdateDenom(it, denom.count200, denom.count100, denom.count50, denom.count20, denom.count10, denom.coins) }
                            )
                            DenominationCounterRow(
                                label = "₹200 notes",
                                count = denom.count200,
                                onCountChange = { onUpdateDenom(denom.count500, it, denom.count100, denom.count50, denom.count20, denom.count10, denom.coins) }
                            )
                            DenominationCounterRow(
                                label = "₹100 notes",
                                count = denom.count100,
                                onCountChange = { onUpdateDenom(denom.count500, denom.count200, it, denom.count50, denom.count20, denom.count10, denom.coins) }
                            )
                            DenominationCounterRow(
                                label = "₹50 notes",
                                count = denom.count50,
                                onCountChange = { onUpdateDenom(denom.count500, denom.count200, denom.count100, it, denom.count20, denom.count10, denom.coins) }
                            )
                            DenominationCounterRow(
                                label = "₹20 notes",
                                count = denom.count20,
                                onCountChange = { onUpdateDenom(denom.count500, denom.count200, denom.count100, denom.count50, it, denom.count10, denom.coins) }
                            )
                            DenominationCounterRow(
                                label = "₹10 / Coins",
                                count = denom.count10,
                                onCountChange = { onUpdateDenom(denom.count500, denom.count200, denom.count100, denom.count50, denom.count20, it, denom.coins) }
                            )
                        } else {
                            OutlinedTextField(
                                value = uiState.physicalCashInput,
                                onValueChange = onPhysicalCashChange,
                                label = { Text("Counted Physical Cash (₹)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("physical_cash_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = ShopBorder
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Live Difference Indicator
                        val isMatched = diff == 0.0 && uiState.physicalCashInput.isNotBlank()
                        val diffColor = if (isMatched) CashGreen else if (diff > 0) UpiBlue else ExpenseRed

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isMatched) CashGreenLight else ExpenseRedLight)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isMatched) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = diffColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isMatched) "Cash Drawer Balanced Perfectly" else if (diff > 0) "Cash Surplus / Excess" else "Cash Shortage / Deficit",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = diffColor
                                    )
                                }

                                Text(
                                    text = "${if (diff > 0) "+" else ""}${DateUtils.formatCurrency(diff)}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = diffColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = uiState.closingNotes,
                            onValueChange = onNotesChange,
                            label = { Text("Closing Notes (Optional)") },
                            placeholder = { Text("e.g. ₹500 kept in safe, ₹50 short due to change rounding") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = ShopBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onCloseDay,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("confirm_close_day_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Confirm & Close Day Register",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Tallies Archive
        item {
            Text(
                text = "Previous Closing Tallies History",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ShopTextPrimary
            )
        }

        if (talliesHistory.isEmpty()) {
            item {
                Text(
                    text = "No previous day closings recorded.",
                    fontSize = 12.sp,
                    color = ShopTextMuted
                )
            }
        } else {
            items(talliesHistory) { t ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = ShopSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ShopBorder))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = t.dateKey,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = ShopTextPrimary
                            )
                            Text(
                                text = "Expected: ₹${t.expectedCashInHand.toInt()} • Counted: ₹${t.actualPhysicalCash.toInt()}",
                                fontSize = 12.sp,
                                color = ShopTextSecondary
                            )
                        }

                        Text(
                            text = "Diff: ${if (t.cashDifference > 0) "+" else ""}₹${t.cashDifference.toInt()}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (t.cashDifference == 0.0) CashGreen else ExpenseRed
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DenominationCounterRow(
    label: String,
    count: Int,
    onCountChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = ShopTextPrimary, modifier = Modifier.weight(1f))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { if (count > 0) onCountChange(count - 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = "$count",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            IconButton(
                onClick = { onCountChange(count + 1) },
                modifier = Modifier.size(32.dp)
            ) {
                Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun LedgerRow(
    label: String,
    amount: Double,
    color: Color,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (isBold) 14.sp else 13.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = if (isBold) ShopTextPrimary else ShopTextSecondary
        )
        Text(
            text = DateUtils.formatCurrency(amount),
            fontSize = if (isBold) 16.sp else 14.sp,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = color
        )
    }
}

// ----------------------------------------------------
// TAB 4: SALESMEN MANAGEMENT
// ----------------------------------------------------
@Composable
private fun SalesmenManagementTabContent(
    salesmen: List<SalesmanUser>,
    onAddClick: () -> Unit,
    onEditClick: (SalesmanUser) -> Unit,
    onToggleLeave: (SalesmanUser, Boolean) -> Unit,
    onDeleteClick: (SalesmanUser) -> Unit
) {
    var salesmanToDelete by remember { mutableStateOf<SalesmanUser?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Staff & Access Control",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = ShopTextPrimary
                    )
                    Text(
                        text = "Manage staff profiles and toggle daily store leave",
                        fontSize = 12.sp,
                        color = ShopTextSecondary
                    )
                }
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("add_salesman_btn")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Staff", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }
        }

        items(salesmen) { salesman ->
            val isOwner = salesman.role.equals("OWNER", ignoreCase = true)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("salesman_mgmt_${salesman.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(if (salesman.isOnLeave) Color(0xFFFCA5A5) else GlassBorderGold))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isOwner) GoldContainer else if (salesman.isOnLeave) Color(0xFFFFE4E6) else GoldLight)
                                    .border(BorderStroke(1.dp, if (salesman.isOnLeave) RoseLiquid else GoldPrimary), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = salesman.name.take(1),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (salesman.isOnLeave) RoseLiquid else TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = salesman.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    if (isOwner) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(GoldPrimary)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("ADMIN", fontSize = 9.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                        }
                                    }
                                }
                                Text(
                                    text = "${salesman.phone} • PIN: ${salesman.pin}",
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onEditClick(salesman) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TextPrimary)
                            }
                            if (!isOwner) {
                                IconButton(onClick = { salesmanToDelete = salesman }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Staff", tint = RoseLiquid)
                                }
                            }
                        }
                    }

                    if (!isOwner) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (salesman.isOnLeave) Color(0xFFFFF1F2) else Color(0xFFF8FAFC))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (salesman.isOnLeave) "ON LEAVE (ACCESS DISABLED)" else "ACTIVE TODAY",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (salesman.isOnLeave) RoseLiquid else EmeraldLiquid
                                    )
                                    Text(
                                        text = if (salesman.isOnLeave) "Staff cannot log in or record sales" else "Staff can bill customer sales",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                                Switch(
                                    checked = !salesman.isOnLeave,
                                    onCheckedChange = { isActiveToday ->
                                        onToggleLeave(salesman, !isActiveToday)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Delete Confirmation Dialog
    salesmanToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { salesmanToDelete = null },
            title = { Text("Remove Staff Account?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you sure you want to remove ${target.name} (${target.phone}) from MJ Garments staff list?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClick(target)
                        salesmanToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseLiquid)
                ) {
                    Text("Delete Staff", color = PureWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { salesmanToDelete = null }) { Text("Cancel") }
            }
        )
    }
}

// ----------------------------------------------------
// TAB 5: AUDIT TRAIL
// ----------------------------------------------------
@Composable
private fun AuditTrailTabContent(auditLogs: List<AuditLog>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "System Security & Action Audit Trail",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ShopTextPrimary
            )
            Text(
                text = "Immutable log of voided transactions, edits, day closings, and user accounts",
                fontSize = 12.sp,
                color = ShopTextSecondary
            )
        }

        if (auditLogs.isEmpty()) {
            item {
                Text(text = "No audit events logged yet.", fontSize = 12.sp, color = ShopTextMuted)
            }
        } else {
            items(auditLogs) { log ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = ShopSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ShopBorder))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(RapidoYellow)
                                    .border(BorderStroke(1.dp, Color(0xFF0F172A)), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = log.action,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }
                            Text(
                                text = DateUtils.formatFullDateTime(log.timestamp),
                                fontSize = 11.sp,
                                color = ShopTextMuted
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = log.details,
                            fontSize = 13.sp,
                            color = ShopTextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Performed by: ${log.performedBy}",
                            fontSize = 11.sp,
                            color = ShopTextSecondary
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ----------------------------------------------------
// OWNER DIALOGS (EDIT, VOID, SALESMAN MGMT)
// ----------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OwnerEditSaleDialog(
    sale: SaleEntry,
    onDismiss: () -> Unit,
    onConfirmEdit: (cat: String, item: String, custom: String?, amt: Double, mode: String, note: String?) -> Unit
) {
    var category by remember { mutableStateOf(sale.category) }
    var itemType by remember { mutableStateOf(sale.itemType) }
    var customItem by remember { mutableStateOf(sale.customItemName ?: "") }
    var amountString by remember { mutableStateOf(sale.amount.toInt().toString()) }
    var paymentMode by remember { mutableStateOf(sale.paymentMode) }
    var note by remember { mutableStateOf(sale.note ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Sale #${sale.id}", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Category:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ItemCategory.entries.forEach { cat ->
                        FilterChip(
                            selected = category == cat.title,
                            onClick = { category = cat.title },
                            label = { Text(cat.code) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Item Type:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ItemType.entries.forEach { itm ->
                        FilterChip(
                            selected = itemType == itm.title,
                            onClick = { itemType = itm.title },
                            label = { Text(itm.title) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amountString,
                    onValueChange = { amountString = it },
                    label = { Text("Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text("Payment Mode:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = paymentMode == "Cash",
                        onClick = { paymentMode = "Cash" },
                        label = { Text("Cash") }
                    )
                    FilterChip(
                        selected = paymentMode == "UPI",
                        onClick = { paymentMode = "UPI" },
                        label = { Text("UPI") }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountString.toDoubleOrNull() ?: sale.amount
                    onConfirmEdit(category, itemType, customItem, amt, paymentMode, note)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text("Save Changes", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun OwnerVoidSaleDialog(
    sale: SaleEntry,
    onDismiss: () -> Unit,
    onConfirmVoid: (reason: String) -> Unit
) {
    var reason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Void Sale #${sale.id}", fontWeight = FontWeight.Bold, color = ExpenseRed) },
        text = {
            Column {
                Text("Are you sure you want to void this ₹${sale.amount.toInt()} sale by ${sale.salesmanName} (${sale.effectiveItemName})?")
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason for Voiding *") },
                    placeholder = { Text("e.g. Customer returned, Wrong billing") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (reason.isNotBlank()) onConfirmVoid(reason.trim())
                },
                enabled = reason.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
            ) {
                Text("Confirm Void", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun SalesmanAccountDialog(
    salesmanToEdit: SalesmanUser?,
    onDismiss: () -> Unit,
    onSave: (id: String, name: String, phone: String, pin: String, active: Boolean, onLeave: Boolean) -> Unit
) {
    var name by remember { mutableStateOf(salesmanToEdit?.name ?: "") }
    var phone by remember { mutableStateOf(salesmanToEdit?.phone ?: "") }
    var pin by remember { mutableStateOf(salesmanToEdit?.pin ?: "1234") }
    var isActive by remember { mutableStateOf(salesmanToEdit?.isActive ?: true) }
    var isOnLeave by remember { mutableStateOf(salesmanToEdit?.isOnLeave ?: false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (salesmanToEdit != null) "Edit Salesman" else "Add New Salesman",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name (e.g. Fasalu Rahman)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = pin,
                    onValueChange = { pin = it },
                    label = { Text("4-Digit PIN") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Account Active")
                    Switch(checked = isActive, onCheckedChange = { isActive = it })
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("On Leave")
                    Switch(checked = isOnLeave, onCheckedChange = { isOnLeave = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank() && pin.isNotBlank()) {
                        onSave(salesmanToEdit?.id ?: phone, name, phone, pin, isActive, isOnLeave)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
            ) {
                Text("Save Staff", color = PureWhite, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
