package com.example.foodordering.Helper

import android.text.format.DateFormat
import com.example.foodordering.Domain.FoodModel
import java.util.Date

data class OrderModel(
    val orderId: String,
    val items: List<FoodModel>,
    val totalAmount: Double,
    val address: String,
    val date: String,
    var orderStatus: String = "CONFIRMED",
    val timestamp: Long = System.currentTimeMillis()
)

object OrderManager {
    val orders = mutableListOf<OrderModel>()

    fun addOrder(items: List<FoodModel>, total: Double, address: String) {
        val currentTime = System.currentTimeMillis()
        val formattedDate = DateFormat.format("yyyy-MM-dd HH:mm:ss", Date(currentTime)).toString()
        val order = OrderModel(
            orderId = "NP-${currentTime.toString().takeLast(6)}",
            items = items.toList(),
            totalAmount = total,
            address = address,
            date = formattedDate,
            orderStatus = "CONFIRMED",
            timestamp = currentTime
        )
        orders.add(0, order)
    }
}
