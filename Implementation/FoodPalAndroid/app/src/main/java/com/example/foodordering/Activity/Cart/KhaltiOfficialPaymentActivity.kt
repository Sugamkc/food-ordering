@file:Suppress("PackageName")

package com.example.foodordering.Activity.Cart

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.setContent
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
import com.example.foodordering.Activity.BaseActivity
import com.example.foodordering.Activity.Helper.ManagmentCart
import com.example.foodordering.Activity.Order.OrderActivity
import com.example.foodordering.Helper.NotificationManager
import com.example.foodordering.Helper.OrderManager
import com.example.foodordering.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class KhaltiOfficialPaymentActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val totalAmount = intent.getDoubleExtra("totalAmount", 0.0)
        val deliveryAddress = intent.getStringExtra("deliveryAddress") ?: "Kathmandu, Nepal"

        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF4F6F9)) {
                KhaltiOfficialPaymentScreen(
                    totalAmount = totalAmount,
                    deliveryAddress = deliveryAddress,
                    onBack = { finish() },
                    onSuccess = {
                        val successIntent = Intent(this, OrderActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(successIntent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun KhaltiOfficialPaymentScreen(
    totalAmount: Double,
    deliveryAddress: String,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    var mobile by remember { mutableStateOf("") }
    var mpin by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) } // 1: Login & MPIN, 2: OTP
    var isLoading by remember { mutableStateOf(false) }

    val orderId = remember { "NP-${System.currentTimeMillis().toString().takeLast(6)}" }
    val pidx = remember { "FOOD-NP-${System.currentTimeMillis()}" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F9))
    ) {
        // Top Khalti Official Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF5C2D91),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color.White, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Kh", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5C2D91))
                    }
                    Column {
                        Text("Khalti ePayment", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Merchant: FoodPal Nepal", fontSize = 12.sp, color = Color(0xFFE2D6F5))
                    }
                }

                TextButton(onClick = onBack) {
                    Text("Cancel", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Amount Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "Payable Amount", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                    Text(
                        text = "Rs. ${String.format(Locale.getDefault(), "%.2f", totalAmount)}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5C2D91)
                    )
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                    Text(text = "Order ID: #$orderId", fontSize = 12.sp, color = Color.DarkGray)
                    Text(text = "Destination: $deliveryAddress", fontSize = 12.sp, color = Color.Gray)
                }
            }

            if (step == 1) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Khalti Mobile & MPIN Login", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF5C2D91))

                        OutlinedTextField(
                            value = mobile,
                            onValueChange = { mobile = it },
                            label = { Text("Khalti Mobile Number") },
                            placeholder = { Text("e.g. 98XXXXXXXX") },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = mpin,
                            onValueChange = { mpin = it },
                            label = { Text("4-digit MPIN") },
                            visualTransformation = PasswordVisualTransformation(),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Enter Verification Code (OTP)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF5C2D91))
                        Text("A 4-digit SMS verification code has been sent to $mobile.", fontSize = 13.sp, color = Color.Gray)

                        OutlinedTextField(
                            value = otp,
                            onValueChange = { otp = it },
                            label = { Text("Verification OTP Code") },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF5C2D91))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (step == 1) {
                        if (mobile.length < 10 || mpin.length < 4) {
                            Toast.makeText(context, "Please enter valid mobile number and MPIN", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                        Handler(Looper.getMainLooper()).postDelayed({
                            isLoading = false
                            step = 2
                            Toast.makeText(context, "Verification code sent to $mobile", Toast.LENGTH_SHORT).show()
                        }, 1000)
                    } else {
                        if (otp.length < 4) {
                            Toast.makeText(context, "Please enter 4-digit verification code", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isLoading = true
                        Handler(Looper.getMainLooper()).postDelayed({
                            isLoading = false
                            val txnId = "KHALTI-TXN-${System.currentTimeMillis().toString().takeLast(8)}"
                            val uid = try { FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous_user" } catch (e: Exception) { "anonymous_user" }

                            val managementCart = ManagmentCart(context)
                            val cartItems = managementCart.getListCart()

                            val orderData = hashMapOf(
                                "orderId" to orderId,
                                "userId" to uid,
                                "totalAmount" to totalAmount,
                                "totalPaisa" to (totalAmount * 100).toLong(),
                                "address" to deliveryAddress,
                                "paymentMethod" to "KHALTI",
                                "paymentStatus" to "PAID",
                                "orderStatus" to "CONFIRMED",
                                "transactionId" to txnId,
                                "pidx" to pidx,
                                "items" to cartItems.map { mapOf("Id" to it.Id, "Title" to it.Title, "Price" to it.Price, "numberInCart" to it.numberInCart) },
                                "timestamp" to System.currentTimeMillis()
                            )

                            // Save to Firestore
                            FirebaseFirestore.getInstance().collection("orders").document(orderId)
                                .set(orderData)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Saved order #$orderId to Firestore ✓", Toast.LENGTH_SHORT).show()
                                }

                            // Save to Realtime Database
                            FirebaseDatabase.getInstance().getReference("Orders").child(orderId).setValue(orderData)

                            OrderManager.addOrder(cartItems.toList(), totalAmount, "$deliveryAddress [Paid via: Khalti (Txn: $txnId)]")
                            managementCart.clearCart()

                            NotificationManager.addNotification("Khalti Payment Confirmed 🎉", "Rs. ${String.format(Locale.getDefault(), "%.2f", totalAmount)} paid via Khalti. Delivery on the way 🛵")
                            Toast.makeText(context, "Khalti Payment Successful!", Toast.LENGTH_SHORT).show()
                            onSuccess()
                        }, 1200)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C2D91)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    text = if (step == 1) "Proceed to Verification" else "Confirm & Pay Rs. ${String.format(Locale.getDefault(), "%.2f", totalAmount)}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
