@file:Suppress("PackageName")

package com.example.foodordering.Activity.Cart

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.foodordering.Domain.FoodModel
import com.example.foodordering.Repository.KhaltiPaymentRepository

@Composable
fun KhaltiPaymentScreen(
    orderId: String,
    totalAmount: Double,
    cartItems: List<FoodModel>
) {
    val context = LocalContext.current
    val repository = remember { KhaltiPaymentRepository() }

    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var paymentCreated by remember { mutableStateOf(false) }
    var paid by remember { mutableStateOf(false) }
    var lastPidx by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Khalti Secure Payment Gateway",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Order ID: $orderId")

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading && !paid,
            onClick = {
                loading = true
                message = "Creating secure backend payment session..."

                repository.createPayment(
                    orderId = orderId,
                    totalAmount = totalAmount,
                    items = cartItems,
                    onSuccess = { paymentUrl, pidx ->
                        loading = false
                        paymentCreated = true
                        lastPidx = pidx
                        message = "Opening Khalti checkout (Pidx: $pidx)..."

                        try {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(paymentUrl)
                            )
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            message = "Could not open Khalti checkout."
                        }
                    },
                    onError = { error ->
                        loading = false
                        message = error
                    }
                )
            }
        ) {
            Text("Pay with Khalti")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = paymentCreated && !loading && !paid,
            onClick = {
                loading = true
                message = "Verifying payment with Khalti lookup..."

                repository.verifyPayment(
                    pidx = lastPidx,
                    orderId = orderId,
                    onSuccess = { isPaid, status, txnId ->
                        loading = false
                        paid = isPaid

                        message = if (isPaid) {
                            "Payment verified successfully! Txn ID: $txnId"
                        } else {
                            "Payment status: $status. Please try again later if pending."
                        }
                    },
                    onError = { error ->
                        loading = false
                        message = error
                    }
                )
            }
        ) {
            Text("Verify Payment")
        }

        if (loading) {
            CircularProgressIndicator()
        }

        Text(message)

        if (paid) {
            Text(
                text = "Payment completed & Order Confirmed",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
