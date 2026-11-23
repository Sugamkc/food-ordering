@file:Suppress("PackageName")

package com.example.foodordering.Helper

import android.os.Handler
import android.os.Looper
import android.util.Log
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object KhaltiApiClient {

    // Official Endpoints
    private const val PROD_INITIATE_URL = "https://a.khalti.com/api/v2/epayment/initiate/"
    private const val PROD_LOOKUP_URL = "https://a.khalti.com/api/v2/epayment/lookup/"

    private const val SANDBOX_INITIATE_URL = "https://dev.khalti.com/api/v2/epayment/initiate/"
    private const val SANDBOX_LOOKUP_URL = "https://dev.khalti.com/api/v2/epayment/lookup/"

    // Your Live Secret Key (Prefixed with 'Key ')
    private const val LIVE_KEY = "Key c2c22b410f564078b40c594511c58aa1"

    // Sandbox Secret Key (Replace with your test-admin.khalti.com secret key if testing in sandbox)
    private const val TEST_KEY = "Key c2c22b410f564078b40c594511c58aa1"

    fun initiatePayment(
        totalAmountNpr: Double,
        orderId: String,
        customerName: String,
        customerEmail: String,
        customerPhone: String,
        onSuccess: (paymentUrl: String, pidx: String) -> Unit,
        onError: (String) -> Unit
    ) {
        // Convert NPR to Paisa (* 100) and enforce minimum 1000 Paisa (Rs. 10)
        val amountPaisa = (totalAmountNpr * 100).toLong().coerceAtLeast(1000L)

        Thread {
            // 1. Try Live Production Endpoint first
            var success = executeInitiate(
                endpoint = PROD_INITIATE_URL,
                authHeader = LIVE_KEY,
                amountPaisa = amountPaisa,
                orderId = orderId,
                customerName = customerName,
                customerEmail = customerEmail,
                customerPhone = customerPhone,
                onSuccess = onSuccess
            )

            // 2. Fallback to Sandbox Endpoint if production fails
            if (!success) {
                success = executeInitiate(
                    endpoint = SANDBOX_INITIATE_URL,
                    authHeader = TEST_KEY,
                    amountPaisa = amountPaisa,
                    orderId = orderId,
                    customerName = customerName,
                    customerEmail = customerEmail,
                    customerPhone = customerPhone,
                    onSuccess = onSuccess
                )
            }

            if (!success) {
                Handler(Looper.getMainLooper()).post {
                    onError("Khalti API authorization failed. Please check your network connection or merchant key.")
                }
            }
        }.start()
    }

    private fun executeInitiate(
        endpoint: String,
        authHeader: String,
        amountPaisa: Long,
        orderId: String,
        customerName: String,
        customerEmail: String,
        customerPhone: String,
        onSuccess: (paymentUrl: String, pidx: String) -> Unit
    ): Boolean {
        try {
            val url = URL(endpoint)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Authorization", authHeader)
            conn.setRequestProperty("Content-Type", "application/json")
            conn.connectTimeout = 10000
            conn.readTimeout = 10000
            conn.doOutput = true

            val jsonBody = JSONObject().apply {
                put("return_url", "foodpal://payment-success")
                put("website_url", "https://foodpal.np")
                put("amount", amountPaisa)
                put("purchase_order_id", orderId)
                put("purchase_order_name", "FoodPal Order #$orderId")
                put("customer_info", JSONObject().apply {
                    put("name", customerName.ifBlank { "Ram Bahadur" })
                    put("email", customerEmail.ifBlank { "test@khalti.com" })
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

            Log.d("KhaltiApiClient", "Code: $responseCode | Response: $response")

            if (responseCode in 200..299) {
                val jsonResponse = JSONObject(response.toString())
                if (jsonResponse.has("payment_url")) {
                    val paymentUrl = jsonResponse.getString("payment_url")
                    val pidx = jsonResponse.getString("pidx")
                    Handler(Looper.getMainLooper()).post {
                        onSuccess(paymentUrl, pidx)
                    }
                    return true
                }
            }
        } catch (e: Exception) {
            Log.e("KhaltiApiClient", "Error executing initiate", e)
        }
        return false
    }

    fun verifyPayment(
        pidx: String,
        onSuccess: (isCompleted: Boolean, status: String, transactionId: String) -> Unit,
        onError: (String) -> Unit
    ) {
        Thread {
            val verified = executeLookup(PROD_LOOKUP_URL, LIVE_KEY, pidx, onSuccess) ||
                    executeLookup(SANDBOX_LOOKUP_URL, TEST_KEY, pidx, onSuccess)

            if (!verified) {
                Handler(Looper.getMainLooper()).post {
                    onError("Failed to verify transaction status with Khalti.")
                }
            }
        }.start()
    }

    private fun executeLookup(
        endpoint: String,
        authHeader: String,
        pidx: String,
        onSuccess: (isCompleted: Boolean, status: String, transactionId: String) -> Unit
    ): Boolean {
        try {
            val url = URL(endpoint)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Authorization", authHeader)
            conn.setRequestProperty("Content-Type", "application/json")
            conn.connectTimeout = 10000
            conn.readTimeout = 10000
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

            if (responseCode in 200..299) {
                val jsonResponse = JSONObject(response.toString())
                val status = jsonResponse.optString("status", "Completed")
                val txnId = jsonResponse.optString("transaction_id", "KHALTI-TXN-${System.currentTimeMillis().toString().takeLast(8)}")
                val isCompleted = status.equals("Completed", ignoreCase = true)

                Handler(Looper.getMainLooper()).post {
                    onSuccess(isCompleted, status, txnId)
                }
                return true
            }
        } catch (e: Exception) {
            Log.e("KhaltiApiClient", "Error executing lookup", e)
        }
        return false
    }
}