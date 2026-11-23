package com.example.foodordering.Helper

data class NotificationItem(
    val title: String,
    val message: String,
    val time: String,
    val isRecommendation: Boolean = false
)

object NotificationManager {
    private val list = mutableListOf(
        NotificationItem("Welcome to FoodPal! 🇳🇵", "Enjoy authentic Nepali momos, dal bhat & more delivered fresh.", "Just now", true),
        NotificationItem("🔥 Special Offer", "Get 20% off on Thakali Dal Bhat set today!", "10m ago", true),
        NotificationItem("🥟 Chef Recommendation", "Try our new Jhol Momo with rich sesame-peanut soup!", "1h ago", true)
    )

    fun getNotifications(): List<NotificationItem> = list.toList()

    fun addNotification(title: String, message: String) {
        list.add(0, NotificationItem(title, message, "Just now", false))
    }
}
