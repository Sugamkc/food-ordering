package com.example.foodordering.Helper

import androidx.compose.runtime.mutableStateListOf

object UserSession {
    var name: String = "John Doe"
    var email: String = "john.doe@example.com"
    val savedAddresses = mutableStateListOf(
        "House #12, Room #3, Thamel, Kathmandu, Zip: 44600",
        "House #45, Lakeside, Pokhara, Zip: 33700"
    )
}
