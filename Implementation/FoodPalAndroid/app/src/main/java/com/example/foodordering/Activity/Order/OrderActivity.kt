@file:Suppress("PackageName")

package com.example.foodordering.Activity.Order

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodordering.Activity.BaseActivity
import com.example.foodordering.Activity.Chat.ChatActivity
import com.example.foodordering.Helper.OrderManager
import com.example.foodordering.Helper.OrderModel
import com.example.foodordering.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                var selectedOrder by remember { mutableStateOf<OrderModel?>(null) }
                var currentTimeMs by remember { mutableStateOf(System.currentTimeMillis()) }

                // 1-second ticker for live cancellation timer
                LaunchedEffect(Unit) {
                    while (true) {
                        delay(1000L)
                        currentTimeMs = System.currentTimeMillis()
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Top back button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { finish() }) {
                            Icon(
                                painter = painterResource(id = R.drawable.back),
                                contentDescription = "Back",
                                tint = colorResource(R.color.darkPurple)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "My Orders & Live Tracking",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.darkPurple)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (OrderManager.orders.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No orders placed yet 🍛",
                                color = colorResource(R.color.darkPurple),
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(OrderManager.orders) { order ->
                                val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                                val placedTimeStr = timeFormat.format(Date(order.timestamp))
                                val deliveryTimeStr = timeFormat.format(Date(order.timestamp + 25 * 60 * 1000))

                                val timePassedMs = currentTimeMs - order.timestamp
                                val fiveMinsMs = 5 * 60 * 1000L
                                val remainingMs = (fiveMinsMs - timePassedMs).coerceAtLeast(0L)
                                val canCancel = remainingMs > 0 && order.orderStatus != "CANCELLED"

                                val remainingSecsTotal = remainingMs / 1000
                                val mins = remainingSecsTotal / 60
                                val secs = remainingSecsTotal % 60
                                val timerStr = String.format(Locale.getDefault(), "%d:%02d", mins, secs)

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedOrder = order },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = colorResource(R.color.grey))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Order #${order.orderId}",
                                                fontWeight = FontWeight.Bold,
                                                color = colorResource(R.color.darkPurple)
                                            )
                                            Text(
                                                text = "Rs. ${String.format(Locale.getDefault(), "%.2f", order.totalAmount)}",
                                                fontWeight = FontWeight.Bold,
                                                color = colorResource(R.color.orange)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = "Deliver to: ${order.address}", fontSize = 14.sp, color = Color.DarkGray)
                                        Text(text = "Items: ${order.items.joinToString { it.Title }}", fontSize = 12.sp, color = Color.Gray)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "🕐 Placed at: $placedTimeStr  |  🚚 Est. Delivery: $deliveryTimeStr",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colorResource(R.color.darkPurple)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (order.orderStatus == "CANCELLED") {
                                                Text(
                                                    text = "❌ Order Cancelled",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.Red
                                                )
                                            } else {
                                                Text(
                                                    text = "Status: 🛵 Out for Delivery",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = colorResource(R.color.orange)
                                                )
                                            }

                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                // 5-Minute Cancellation Button
                                                if (canCancel) {
                                                    Button(
                                                        onClick = {
                                                            // Cancel order in Firestore & Realtime DB
                                                            FirebaseFirestore.getInstance().collection("orders")
                                                                .document(order.orderId)
                                                                .update("orderStatus", "CANCELLED")

                                                            FirebaseDatabase.getInstance().getReference("Orders")
                                                                .child(order.orderId)
                                                                .child("orderStatus")
                                                                .setValue("CANCELLED")

                                                            order.orderStatus = "CANCELLED"
                                                            Toast.makeText(context, "Order #${order.orderId} Cancelled ✓", Toast.LENGTH_LONG).show()
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                                    ) {
                                                        Text("Cancel ($timerStr)", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                                    }
                                                }

                                                Button(
                                                    onClick = {
                                                        context.startActivity(Intent(context, ChatActivity::class.java))
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.orange)),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Text("Chat Rider 💬", fontSize = 11.sp, color = Color.White)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (selectedOrder != null) {
                    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    val placedTimeStr = timeFormat.format(Date(selectedOrder!!.timestamp))
                    val deliveryTimeStr = timeFormat.format(Date(selectedOrder!!.timestamp + 25 * 60 * 1000))

                    AlertDialog(
                        onDismissRequest = { selectedOrder = null },
                        title = { Text("Live Delivery Tracking 🛵") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Order #${selectedOrder!!.orderId}", fontWeight = FontWeight.Bold)
                                Text("Destination: ${selectedOrder!!.address}")
                                Divider()
                                Text("🕐 Placed at: $placedTimeStr", fontSize = 13.sp, color = Color.DarkGray)
                                Text("🚚 Estimated Delivery: $deliveryTimeStr", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colorResource(R.color.orange))
                                Spacer(modifier = Modifier.height(4.dp))
                                if (selectedOrder!!.orderStatus == "CANCELLED") {
                                    Text("❌ This Order Was Cancelled Within 5 Mins", fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 13.sp)
                                } else {
                                    Text("✅ Order Confirmed by Restaurant", color = Color.DarkGray, fontSize = 12.sp)
                                    Text("✅ Kitchen Preparing Your Food", color = Color.DarkGray, fontSize = 12.sp)
                                    Text("🛵 Rider Picked Up & On the Way!", fontWeight = FontWeight.Bold, color = colorResource(R.color.orange), fontSize = 12.sp)
                                }
                            }
                        },
                        confirmButton = {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        selectedOrder = null
                                        context.startActivity(Intent(context, ChatActivity::class.java))
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.orange))
                                ) {
                                    Text("Chat Rider 💬", color = Color.White)
                                }
                                Button(
                                    onClick = { selectedOrder = null },
                                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.darkBrown))
                                ) {
                                    Text("Close", color = Color.White)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
