@file:Suppress("PackageName")

package com.example.foodordering.Helper

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.foodordering.Domain.FoodModel
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object KhaltiManager {

    private const val PROD_INIT_URL = "https://a.khalti.com/api/v2/epayment/initiate/"
    private const val PROD_LOOKUP_URL = "https://a.khalti.com/api/v2/epayment/lookup/"
    private const val AUTH_HEADER = "Key c2c22b410f564078b40c594511c58aa1"

    /**
     * Initiates a payment session on Khalti Live Production.
     */
    fun initiatePayment(
        totalAmountNpr: Double,
        deliveryFeeNpr: Double,
        orderId: String,
        customerName: String,
        customerEmail: String,
        customerPhone: String,
        cartItems: List<FoodModel>,
        onSuccess: (paymentUrl: String, pidx: String) -> Unit,
        onError: (String) -> Unit
    ) {
        val totalAmountPaisa = (totalAmountNpr * 100).toLong().coerceAtLeast(1000L)

        Thread {
            try {
                val url = URL(PROD_INIT_URL)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Authorization", AUTH_HEADER)
                conn.setRequestProperty("Content-Type", "application/json")
                conn.connectTimeout = 15000
                conn.readTimeout = 15000
                conn.doOutput = true

                // Clean core payload without breakdown arrays to prevent validation mismatches
                val jsonBody = JSONObject().apply {
                    put("return_url", "https://foodpal.np/payment-success")
                    put("website_url", "https://foodpal.np")
                    put("amount", totalAmountPaisa)
                    put("purchase_order_id", orderId)
                    put("purchase_order_name", "FoodPal Order #$orderId")
                    put("customer_info", JSONObject().apply {
                        put("name", customerName.ifBlank { "FoodPal Customer" })
                        put("email", customerEmail.ifBlank { "customer@foodpal.np" })
                        put("phone", if (customerPhone.length >= 10) customerPhone else "9800000001")
                    })
                }

                val writer = OutputStreamWriter(conn.outputStream)
                writer.write(jsonBody.toString())
                writer.flush()
                writer.close()

                val responseCode = conn.responseCode
                val stream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
                val reader = BufferedReader(InputStreamReader(stream))
                val response = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                val responseString = response.toString().trim()
                Log.d("KhaltiManager", "Initiate Code: $responseCode | Response: $responseString")

                if (responseCode in 200..299 && responseString.startsWith("{")) {
                    val jsonResponse = JSONObject(responseString)
                    val paymentUrl = jsonResponse.optString("payment_url")
                    val pidx = jsonResponse.optString("pidx")

                    Handler(Looper.getMainLooper()).post {
                        if (paymentUrl.isNotBlank() && pidx.isNotBlank()) {
                            onSuccess(paymentUrl, pidx)
                        } else {
                            onError("Missing payment_url or pidx in response: $responseString")
                        }
                    }
                } else {
                    Handler(Looper.getMainLooper()).post {
                        onError("Khalti API Error ($responseCode): $responseString")
                    }
                }
            } catch (e: Exception) {
                Log.e("KhaltiManager", "Exception during initiation", e)
                Handler(Looper.getMainLooper()).post {
                    onError("Network Exception: ${e.localizedMessage}")
                }
            }
        }.start()
    }

    /**
     * Verifies transaction status via Khalti Lookup API.
     */
    fun verifyPayment(
        pidx: String,
        onVerified: (isCompleted: Boolean, status: String, transactionId: String) -> Unit,
        onError: (String) -> Unit
    ) {
        Thread {
            try {
                val url = URL(PROD_LOOKUP_URL)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Authorization", AUTH_HEADER)
                conn.setRequestProperty("Content-Type", "application/json")
                conn.connectTimeout = 15000
                conn.readTimeout = 15000
                conn.doOutput = true

                val jsonBody = JSONObject().apply {
                    put("pidx", pidx)
                }

                val writer = OutputStreamWriter(conn.outputStream)
                writer.write(jsonBody.toString())
                writer.flush()
                writer.close()

                val responseCode = conn.responseCode
                val stream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
                val reader = BufferedReader(InputStreamReader(stream))
                val response = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                val responseString = response.toString().trim()
                Log.d("KhaltiManager", "Lookup Code: $responseCode | Response: $responseString")

                if (responseCode in 200..299 && responseString.startsWith("{")) {
                    val jsonResponse = JSONObject(responseString)
                    val status = jsonResponse.optString("status", "Pending")
                    val transactionId = jsonResponse.optString("transaction_id", "")
                    val isCompleted = status.equals("Completed", ignoreCase = true)

                    Handler(Looper.getMainLooper()).post {
                        onVerified(isCompleted, status, transactionId)
                    }
                } else {
                    Handler(Looper.getMainLooper()).post {
                        onError("Lookup Error ($responseCode): $responseString")
                    }
                }
            } catch (e: Exception) {
                Log.e("KhaltiManager", "Exception during lookup", e)
                Handler(Looper.getMainLooper()).post {
                    onError("Lookup Exception: ${e.localizedMessage}")
                }
            }
        }.start()
    }

    fun openPaymentGateway(context: Context, paymentUrl: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("KhaltiManager", "Failed to open browser", e)
        }
    }
}