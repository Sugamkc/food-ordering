@file:Suppress("PackageName")

package com.example.foodordering.Activity.DashBoard

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.foodordering.Activity.Auth.LoginActivity
import com.example.foodordering.Helper.NotificationManager
import com.example.foodordering.R

@Composable
@Preview(showBackground = true)
fun TopBar() {
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    ConstraintLayout(
        modifier = Modifier
            .padding(top = 48.dp)
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        val (name, settings, notification) = createRefs()

        Image(
            painter = painterResource(R.drawable.settings_icon),
            contentDescription = "Settings",
            modifier = Modifier
                .constrainAs(settings) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
                .clickable { showSettingsDialog = true }
        )

        Column(
            modifier = Modifier
                .constrainAs(name) {
                    top.linkTo(parent.top)
                    start.linkTo(settings.end)
                    end.linkTo(notification.start)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🇳🇵 ", fontSize = 18.sp)
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = colorResource(R.color.orange))) {
                            append("Food")
                        }
                        withStyle(style = SpanStyle(color = colorResource(R.color.darkPurple))) {
                            append("Pal")
                        }
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            }
            Text(
                text = "Authentic Nepali Food",
                color = Color.DarkGray,
                fontSize = 12.sp
            )
        }

        Image(
            painter = painterResource(R.drawable.bell_icon),
            contentDescription = "Notifications",
            modifier = Modifier
                .constrainAs(notification) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
                .clickable { showNotificationDialog = true }
        )
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("⚙️ FoodPal Settings") },
            text = {
                Column {
                    Text(text = "• Notifications: Enabled", modifier = Modifier.padding(vertical = 4.dp))
                    Text(text = "• Language: English / Nepali", modifier = Modifier.padding(vertical = 4.dp))
                    Text(text = "• Theme: Classic Orange", modifier = Modifier.padding(vertical = 4.dp))
                    Text(text = "• App Version: FoodPal v1.0.0", modifier = Modifier.padding(vertical = 4.dp))
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSettingsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.orange))
                ) {
                    Text("Close", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showSettingsDialog = false
                        val intent = Intent(context, LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Logout", color = Color.White)
                }
            }
        )
    }

    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            title = { Text("🔔 FoodPal Notifications & Recommendations") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val notifications = NotificationManager.getNotifications()
                    notifications.forEach { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = colorResource(R.color.grey))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colorResource(R.color.darkPurple))
                                    Text(text = item.time, fontSize = 10.sp, color = Color.Gray)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = item.message, fontSize = 12.sp, color = Color.DarkGray)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showNotificationDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.orange))
                ) {
                    Text("Close", color = Color.White)
                }
            }
        )
    }
}
