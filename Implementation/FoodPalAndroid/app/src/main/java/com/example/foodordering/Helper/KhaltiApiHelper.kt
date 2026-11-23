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

object KhaltiApiHelper {

    // Official Production & Sandbox Endpoints
    private const val PROD_INITIATE_URL = "https://a.khalti.com/api/v2/epayment/initiate/"
    private const val SANDBOX_INITIATE_URL = "https://dev.khalti.com/api/v2/epayment/initiate/"

    // Live Secret Key (Secret key is required for initiate endpoint)
    private const val LIVE_SECRET_KEY = "Key c2c22b410f564078b40c594511c58aa1"

    fun initiateKhaltiPayment(
        totalAmountNpr: Double,
        orderId: String,
        customerName: String,
        customerEmail: String,
        customerPhone: String,
        onSuccess: (paymentUrl: String, pidx: String) -> Unit,
        onError: (errorMessage: String) -> Unit
    ) {
        // FIX 1: Multiply by 100 to get correct Paisa (minimum 1000 Paisa = Rs. 10)
        val amountPaisa = (totalAmountNpr * 100).toLong().coerceAtLeast(1000L)

        Thread {
            // Try Production URL first, fallback to Sandbox if production fails
            var success = executeRequest(
                endpoint = PROD_INITIATE_URL,
                amountPaisa = amountPaisa,
                orderId = orderId,
                customerName = customerName,
                customerEmail = customerEmail,
                customerPhone = customerPhone,
                onSuccess = onSuccess
            )

            if (!success) {
                success = executeRequest(
                    endpoint = SANDBOX_INITIATE_URL,
                    amountPaisa = amountPaisa,
                    orderId = orderId,
                    customerName = customerName,
                    customerEmail = customerEmail,
                    customerPhone = customerPhone,
                    onSuccess = onSuccess
                )
            }

            // FIX 4: Call onError if failed instead of generating fake fallback URLs
            if (!success) {
                Handler(Looper.getMainLooper()).post {
                    onError("Failed to initiate Khalti payment. Please verify your merchant secret key and network.")
                }
            }
        }.start()
    }

    private fun executeRequest(
        endpoint: String,
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

            // FIX 2: Using Live Secret Key header
            conn.setRequestProperty("Authorization", LIVE_SECRET_KEY)
            conn.setRequestProperty("Content-Type", "application/json")
            conn.connectTimeout = 12000
            conn.readTimeout = 12000
            conn.doOutput = true

            val jsonBody = JSONObject().apply {
                put("return_url", "foodpal://payment-success")
                put("website_url", "https://foodpal.np")
                put("amount", amountPaisa)
                put("purchase_order_id", orderId)
                put("purchase_order_name", "FoodPal Order #$orderId")
                put("customer_info", JSONObject().apply {
                    put("name", customerName.ifBlank { "Test Bahadur" })
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

            Log.d("KhaltiApiHelper", "Code: $responseCode | Response: $response")

            if (responseCode in 200..299) {
                val jsonResponse = JSONObject(response.toString())
                val paymentUrl = jsonResponse.optString("payment_url")
                val pidx = jsonResponse.optString("pidx")

                if (paymentUrl.isNotBlank() && pidx.isNotBlank()) {
                    Handler(Looper.getMainLooper()).post {
                        onSuccess(paymentUrl, pidx)
                    }
                    return true
                }
            }
        } catch (e: Exception) {
            Log.e("KhaltiApiHelper", "API Exception on $endpoint", e)
        }
        return false
    }
}