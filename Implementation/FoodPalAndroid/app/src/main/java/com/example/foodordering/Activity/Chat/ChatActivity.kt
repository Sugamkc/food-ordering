@file:Suppress("PackageName")

package com.example.foodordering.Activity.Chat

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodordering.Activity.BaseActivity
import com.example.foodordering.Helper.UserSession
import com.example.foodordering.R

data class ChatMessage(
    val message: String,
    val isCustomer: Boolean,
    val time: String = "Just now"
)

class ChatActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
                ChatScreen(onBackClick = { finish() })
            }
        }
    }
}

@Composable
fun ChatScreen(onBackClick: () -> Unit) {
    val userName = if (UserSession.name.isNotBlank()) UserSession.name else "Customer"

    val messages = remember {
        mutableStateListOf(
            ChatMessage("Hello! My name is FoodPal Delivery Courier 🛵. Before we start, what is your name?", isCustomer = false),
            ChatMessage("Hello! $userName. I can converse in your preferred language. How may I help you with your order today?", isCustomer = false)
        )
    }

    var textInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Bar matching reference image
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(colorResource(R.color.grey), CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back",
                    tint = colorResource(R.color.darkPurple)
                )
            }

            Text(
                text = "Chat with Rider 🛵",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.darkPurple)
            )

            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(40.dp)
                    .background(colorResource(R.color.grey), CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.bell_icon),
                    contentDescription = "Notification",
                    tint = colorResource(R.color.darkPurple)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chat Message List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                if (msg.isCustomer) {
                    // Customer Right Message Bubble (Orange/Red)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp),
                            color = colorResource(R.color.orange),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Text(
                                text = msg.message,
                                color = Color.White,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                } else {
                    // Rider Left Message Bubble (Grey)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(colorResource(R.color.darkPurple)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🛵", fontSize = 18.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                            color = colorResource(R.color.grey),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Text(
                                text = msg.message,
                                color = Color.DarkGray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input Field Bar matching reference image
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { },
                shape = RoundedCornerShape(12.dp),
                color = colorResource(R.color.grey)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🔗", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Type a message to rider...", fontSize = 14.sp, color = Color.Gray) },
                shape = RoundedCornerShape(24.dp),
                trailingIcon = {
                    IconButton(onClick = {
                        if (textInput.isNotBlank()) {
                            val userMsg = textInput
                            messages.add(ChatMessage(userMsg, isCustomer = true))
                            textInput = ""

                            // Rider auto reply simulation
                            messages.add(ChatMessage("Got it! I am currently bringing your order. I will reach your location shortly 🛵💨", isCustomer = false))
                        }
                    }) {
                        Text("➔", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = colorResource(R.color.orange))
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
