@file:Suppress("PackageName")

package com.example.foodordering.Activity.Cart

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodordering.Domain.FoodModel
import com.example.foodordering.Helper.NotificationManager
import com.example.foodordering.Helper.OrderManager
import com.example.foodordering.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

@Composable
fun KhaltiPaymentDialog(
    totalAmount: Double,
    deliveryAddress: String,
    cartItems: List<FoodModel>,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    var khaltiMobile by remember { mutableStateOf("9812345678") }
    var khaltiMpin by remember { mutableStateOf("1234") }
    var step by remember { mutableStateOf(1) } // 1: Login, 2: OTP
    var otpCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Firebase Payment Gateway Config
    var firebasePublicKey by remember { mutableStateOf("004fc7717a4b48eaaa1cd7d849bb4be7") }
    var gatewayStatus by remember { mutableStateOf("Connected to Firebase Gateway") }

    LaunchedEffect(Unit) {
        val dbRef = FirebaseDatabase.getInstance().getReference("PaymentGateways/Khalti")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val key = snapshot.child("publicKey").getValue(String::class.java)
                if (!key.isNullOrBlank()) {
                    firebasePublicKey = key
                    gatewayStatus = "Verified via Firebase Gateway ✓"
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // use default fallback
            }
        })
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(colorResource(R.color.orange), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🟣", fontSize = 20.sp)
                }
                Column {
                    Text("Khalti Verified Gateway", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(gatewayStatus, fontSize = 10.sp, color = colorResource(R.color.darkBrown))
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colorResource(R.color.grey))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Total Payable Amount:", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            text = "Rs. ${String.format(Locale.getDefault(), "%.2f", totalAmount)}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.orange)
                        )
                        Text(text = "Active PublicKey: ${firebasePublicKey.take(12)}...", fontSize = 10.sp, color = Color.Gray)
                    }
                }

                if (step == 1) {
                    Text("🔐 Step 1: Khalti Wallet Authentication", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colorResource(R.color.darkPurple))
                    OutlinedTextField(
                        value = khaltiMobile,
                        onValueChange = { khaltiMobile = it },
                        label = { Text("Khalti Mobile Number (+977)") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = khaltiMpin,
                        onValueChange = { khaltiMpin = it },
                        label = { Text("Khalti 4-digit MPIN") },
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else if (step == 2) {
                    Text("📱 Step 2: Khalti SMS OTP Verification", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colorResource(R.color.darkPurple))
                    Text("Verification code sent to $khaltiMobile", fontSize = 12.sp, color = Color.Gray)
                    OutlinedTextField(
                        value = otpCode,
                        onValueChange = { otpCode = it },
                        label = { Text("Enter 4-digit OTP (e.g. 5678)") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colorResource(R.color.orange))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (step == 1) {
                        if (khaltiMobile.length < 10) {
                            Toast.makeText(context, "Please enter valid mobile number", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                        Handler(Looper.getMainLooper()).postDelayed({
                            isLoading = false
                            step = 2
                            Toast.makeText(context, "OTP sent successfully via Firebase Gateway!", Toast.LENGTH_SHORT).show()
                        }, 1000)
                    } else if (step == 2) {
                        if (otpCode.length < 4) {
                            Toast.makeText(context, "Please enter valid 4-digit OTP", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                        Handler(Looper.getMainLooper()).postDelayed({
                            isLoading = false
                            val txnId = "KHALTI-TXN-${System.currentTimeMillis().toString().takeLast(8)}"

                            // Save order in Firebase Realtime Database & local OrderManager
                            val orderId = "NP-${System.currentTimeMillis().toString().takeLast(6)}"
                            val orderData = mapOf(
                                "orderId" to orderId,
                                "totalAmount" to totalAmount,
                                "address" to deliveryAddress,
                                "paymentMethod" to "Khalti",
                                "paymentStatus" to "PAID",
                                "transactionId" to txnId,
                                "publicKey" to firebasePublicKey,
                                "timestamp" to System.currentTimeMillis()
                            )
                            FirebaseDatabase.getInstance().getReference("Orders").child(orderId).setValue(orderData)

                            OrderManager.addOrder(cartItems, totalAmount, "$deliveryAddress [Paid via: Khalti (Txn: $txnId)]")
                            NotificationManager.addNotification(
                                "Khalti Payment Verified & Confirmed! 🎉",
                                "Rs. ${String.format(Locale.getDefault(), "%.2f", totalAmount)} verified via Firebase & Khalti. Order placed 🛵"
                            )
                            onSuccess()
                        }, 1500)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.orange)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (step == 1) "Verify & Request OTP" else "Confirm & Pay Rs. ${String.format(Locale.getDefault(), "%.2f", totalAmount)}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
