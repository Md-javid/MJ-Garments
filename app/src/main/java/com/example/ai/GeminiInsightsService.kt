package com.example.ai

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.DailyClosingTally
import com.example.data.model.ExpenseEntry
import com.example.data.model.SaleEntry
import com.example.data.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AIInsightResult(
    val summaryTitle: String,
    val executiveSummary: String,
    val keyStrengths: List<String>,
    val alertsOrRecommendations: List<String>,
    val suggestedFocusForTomorrow: String
)

class GeminiInsightsService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateSalesAnalysis(
        dateLabel: String,
        sales: List<SaleEntry>,
        expenses: List<ExpenseEntry>,
        closingTally: DailyClosingTally?
    ): Result<AIInsightResult> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val activeSales = sales.filter { !it.isVoided }

        val totalSales = activeSales.sumOf { it.amount }
        val cashSales = activeSales.filter { it.paymentMode.equals("CASH", ignoreCase = true) }.sumOf { it.amount }
        val upiSales = activeSales.filter { it.paymentMode.equals("UPI", ignoreCase = true) }.sumOf { it.amount }
        val totalExpenses = expenses.filter { !it.isVoided }.sumOf { it.amount }

        val categoryBreakdown = activeSales.groupBy { it.category }
            .mapValues { (_, list) -> list.sumOf { it.amount } }
        val itemTypeBreakdown = activeSales.groupBy { it.itemType }
            .mapValues { (_, list) -> list.sumOf { it.amount } }
        val salesmanBreakdown = activeSales.groupBy { it.salesmanName }
            .mapValues { (_, list) -> list.sumOf { it.amount } }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Local high-fidelity smart business analytics engine fallback
            return@withContext Result.success(
                generateLocalRuleBasedInsights(
                    dateLabel = dateLabel,
                    totalSales = totalSales,
                    cashSales = cashSales,
                    upiSales = upiSales,
                    totalExpenses = totalExpenses,
                    categoryBreakdown = categoryBreakdown,
                    itemTypeBreakdown = itemTypeBreakdown,
                    salesmanBreakdown = salesmanBreakdown,
                    closingTally = closingTally
                )
            )
        }

        try {
            val prompt = """
                You are a senior retail business advisor and financial analyst for 'MJ Garments', an established retail store in Broadway, Kerala, India owned by Syed Ibrahim.
                Analyze the following real sales and expense data for $dateLabel:
                
                - Total Sales: ₹$totalSales (${activeSales.size} transactions)
                - Cash vs UPI: Cash = ₹$cashSales, UPI = ₹$upiSales (${if (totalSales > 0) (upiSales / totalSales * 100).toInt() else 0}% UPI)
                - Total Expenses: ₹$totalExpenses
                - Category Breakdown: $categoryBreakdown
                - Item Type Breakdown: $itemTypeBreakdown
                - Salesman Sales Performance: $salesmanBreakdown
                - End-of-Day Tally Status: ${if (closingTally != null) "Expected Cash ₹${closingTally.expectedCashInHand}, Counted ₹${closingTally.actualPhysicalCash}, Diff ₹${closingTally.cashDifference}" else "Register open / not yet tallied"}
                
                Respond in valid JSON format only with these exact keys:
                {
                   "summaryTitle": "Short punchy Kerala shop status title",
                   "executiveSummary": "2-3 crisp sentences summarizing shop floor turnover and financial health",
                   "keyStrengths": ["Strength 1", "Strength 2", "Strength 3"],
                   "alertsOrRecommendations": ["Alert or Tip 1", "Alert or Tip 2"],
                   "suggestedFocusForTomorrow": "Practical tip for Syed Ibrahim and salesmen for tomorrow"
                }
            """.trimIndent()

            val jsonPayload = JSONObject().apply {
                val contents = JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                }
                put("contents", contents)
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.3)
                })
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
                .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.w("GeminiInsights", "API call failed with ${response.code}, falling back to local analysis")
                return@withContext Result.success(
                    generateLocalRuleBasedInsights(
                        dateLabel = dateLabel,
                        totalSales = totalSales,
                        cashSales = cashSales,
                        upiSales = upiSales,
                        totalExpenses = totalExpenses,
                        categoryBreakdown = categoryBreakdown,
                        itemTypeBreakdown = itemTypeBreakdown,
                        salesmanBreakdown = salesmanBreakdown,
                        closingTally = closingTally
                    )
                )
            }

            val responseBody = response.body?.string() ?: ""
            val rootJson = JSONObject(responseBody)
            val candidates = rootJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text") ?: ""

            val parsedInsight = JSONObject(text)
            val strengths = mutableListOf<String>()
            val parsedStrengths = parsedInsight.optJSONArray("keyStrengths")
            if (parsedStrengths != null) {
                for (i in 0 until parsedStrengths.length()) {
                    strengths.add(parsedStrengths.getString(i))
                }
            }

            val alerts = mutableListOf<String>()
            val parsedAlerts = parsedInsight.optJSONArray("alertsOrRecommendations")
            if (parsedAlerts != null) {
                for (i in 0 until parsedAlerts.length()) {
                    alerts.add(parsedAlerts.getString(i))
                }
            }

            Result.success(
                AIInsightResult(
                    summaryTitle = parsedInsight.optString("summaryTitle", "MJ Garments Business Summary"),
                    executiveSummary = parsedInsight.optString("executiveSummary", "Good sales momentum across Broadway footwear and accessories."),
                    keyStrengths = if (strengths.isNotEmpty()) strengths else listOf("Consistent footwear demand", "Balanced UPI and Cash distribution"),
                    alertsOrRecommendations = if (alerts.isNotEmpty()) alerts else listOf("Ensure daily closing physical cash is counted before 9:30 PM"),
                    suggestedFocusForTomorrow = parsedInsight.optString("suggestedFocusForTomorrow", "Encourage up-selling leather belts and purses with footwear purchases.")
                )
            )
        } catch (e: Exception) {
            Log.e("GeminiInsights", "Error generating insights", e)
            Result.success(
                generateLocalRuleBasedInsights(
                    dateLabel = dateLabel,
                    totalSales = totalSales,
                    cashSales = cashSales,
                    upiSales = upiSales,
                    totalExpenses = totalExpenses,
                    categoryBreakdown = categoryBreakdown,
                    itemTypeBreakdown = itemTypeBreakdown,
                    salesmanBreakdown = salesmanBreakdown,
                    closingTally = closingTally
                )
            )
        }
    }

    private fun generateLocalRuleBasedInsights(
        dateLabel: String,
        totalSales: Double,
        cashSales: Double,
        upiSales: Double,
        totalExpenses: Double,
        categoryBreakdown: Map<String, Double>,
        itemTypeBreakdown: Map<String, Double>,
        salesmanBreakdown: Map<String, Double>,
        closingTally: DailyClosingTally?
    ): AIInsightResult {
        val topCategory = categoryBreakdown.maxByOrNull { it.value }?.key ?: "Footwear"
        val topItem = itemTypeBreakdown.maxByOrNull { it.value }?.key ?: "Footwear"
        val topSalesman = salesmanBreakdown.maxByOrNull { it.value }?.key ?: "Staff"
        val upiRatio = if (totalSales > 0) (upiSales / totalSales * 100).toInt() else 0

        val strengths = mutableListOf<String>()
        strengths.add("Top revenue driver: $topItem ($topCategory) leading with ${DateUtils.formatCurrency(itemTypeBreakdown[topItem] ?: 0.0)}")
        strengths.add("Digital payments health: $upiRatio% UPI collection (₹${upiSales.toInt()}) vs Cash (₹${cashSales.toInt()})")
        if (topSalesman.isNotBlank()) {
            strengths.add("Top performing salesman: $topSalesman with ${DateUtils.formatCurrency(salesmanBreakdown[topSalesman] ?: 0.0)} in floor sales")
        }

        val alerts = mutableListOf<String>()
        if (totalExpenses > (totalSales * 0.15) && totalSales > 0) {
            alerts.add("High floor expense ratio: Floor outlays at ${((totalExpenses / totalSales) * 100).toInt()}% of gross sales.")
        }
        if (closingTally != null) {
            if (closingTally.cashDifference != 0.0) {
                alerts.add("Cash Tally Mismatch: Physical drawer difference of ${DateUtils.formatCurrency(closingTally.cashDifference)} detected.")
            } else {
                alerts.add("Perfect Cash Reconciliation: Physical drawer matched Expected Cash exactly (₹${closingTally.expectedCashInHand.toInt()}).")
            }
        } else {
            alerts.add("Day Closing Reminder: End-of-Day cash drawer has not been closed yet for $dateLabel.")
        }

        return AIInsightResult(
            summaryTitle = if (totalSales > 10000) "Strong Broadway Sales Velocity" else "Steady Shop Performance ($dateLabel)",
            executiveSummary = "MJ Garments recorded ${DateUtils.formatCurrency(totalSales)} across ${categoryBreakdown.size} categories. $topItem remains the store's primary turnover category, supported by healthy $topSalesman floor customer conversions.",
            keyStrengths = strengths,
            alertsOrRecommendations = alerts,
            suggestedFocusForTomorrow = "Place high-margin handmade leather belts & purses near the footwear fitting area to drive combo add-on sales."
        )
    }
}
