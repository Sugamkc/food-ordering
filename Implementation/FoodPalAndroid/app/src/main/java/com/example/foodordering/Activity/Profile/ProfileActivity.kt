@file:Suppress("PackageName")

package com.example.foodordering.Activity.Profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.foodordering.Activity.Auth.LoginActivity
import com.example.foodordering.Activity.BaseActivity
import com.example.foodordering.Activity.Favorite.FavoriteActivity
import com.example.foodordering.Activity.Order.OrderActivity
import com.example.foodordering.Helper.UserSession
import com.example.foodordering.R

enum class ProfileScreenView {
    MAIN, PERSONAL_INFO, ADDRESSES, PAYMENT_METHODS, SETTINGS, HELP_SUPPORT
}

class ProfileActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (UserSession.name.isBlank() || UserSession.name == "John Doe") {
            UserSession.name = UserSession.email.substringBefore("@").replaceFirstChar { it.uppercase() }
        }

        setContent {
            ProfileManagerScreen(onBackClick = { finish() })
        }
    }
}

@Composable
fun ProfileManagerScreen(onBackClick: () -> Unit) {
    var currentView by remember { mutableStateOf(ProfileScreenView.MAIN) }

    when (currentView) {
        ProfileScreenView.MAIN -> MainProfileView(onBackClick = onBackClick, onViewChange = { currentView = it })
        ProfileScreenView.PERSONAL_INFO -> PersonalInfoView(onBack = { currentView = ProfileScreenView.MAIN })
        ProfileScreenView.ADDRESSES -> AddressManagementView(onBack = { currentView = ProfileScreenView.MAIN })
        ProfileScreenView.PAYMENT_METHODS -> PaymentMethodsView(onBack = { currentView = ProfileScreenView.MAIN })
        ProfileScreenView.SETTINGS -> SettingsView(onBack = { currentView = ProfileScreenView.MAIN })
        ProfileScreenView.HELP_SUPPORT -> HelpSupportView(onBack = { currentView = ProfileScreenView.MAIN })
    }
}

@Composable
fun MainProfileView(onBackClick: () -> Unit, onViewChange: (ProfileScreenView) -> Unit) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top back button & title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Back",
                        tint = colorResource(R.color.darkPurple)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Profile & Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.darkPurple)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Profile Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(colorResource(R.color.orange))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = UserSession.name.take(2).uppercase(),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = UserSession.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = UserSession.email,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 2.dp, bottom = 24.dp)
            )

            // Menu Items List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    ProfileMenuRow(title = "Personal Information") {
                        onViewChange(ProfileScreenView.PERSONAL_INFO)
                    }
                }
                item {
                    ProfileMenuRow(title = "My Orders") {
                        context.startActivity(Intent(context, OrderActivity::class.java))
                    }
                }
                item {
                    ProfileMenuRow(title = "Addresses") {
                        onViewChange(ProfileScreenView.ADDRESSES)
                    }
                }
                item {
                    ProfileMenuRow(title = "Payment Methods") {
                        onViewChange(ProfileScreenView.PAYMENT_METHODS)
                    }
                }
                item {
                    ProfileMenuRow(title = "Settings") {
                        onViewChange(ProfileScreenView.SETTINGS)
                    }
                }
                item {
                    ProfileMenuRow(title = "Help & Support") {
                        onViewChange(ProfileScreenView.HELP_SUPPORT)
                    }
                }
                item {
                    ProfileMenuRow(title = "Logout", isLogout = true) {
                        val intent = Intent(context, LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        context.startActivity(intent)
                    }
                }
            }
        }
    }
}

@Composable
fun PersonalInfoView(onBack: () -> Unit) {
    var name by remember { mutableStateOf(UserSession.name) }
    var email by remember { mutableStateOf(UserSession.email) }
    var phone by remember { mutableStateOf("+977 9812345678") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        HeaderRow("Personal Information", onBack)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                UserSession.name = name
                UserSession.email = email
                Toast.makeText(context, "Personal Info Saved!", Toast.LENGTH_SHORT).show()
                onBack()
            },
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.orange)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AddressManagementView(onBack: () -> Unit) {
    var houseNo by remember { mutableStateOf("") }
    var roomNo by remember { mutableStateOf("") }
    var streetArea by remember { mutableStateOf("") }
    var postalCode by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Kathmandu") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        HeaderRow("My Delivery Addresses", onBack)
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colorResource(R.color.grey))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Add New Address in Nepal 📍", fontWeight = FontWeight.Bold, color = colorResource(R.color.darkPurple))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = houseNo,
                        onValueChange = { houseNo = it },
                        label = { Text("House No.") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = roomNo,
                        onValueChange = { roomNo = it },
                        label = { Text("Room No.") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = streetArea,
                    onValueChange = { streetArea = it },
                    label = { Text("Street / Area / Tole") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = postalCode,
                        onValueChange = { postalCode = it },
                        label = { Text("Zip / Postal Code") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Button(
                    onClick = {
                        if (streetArea.isNotBlank()) {
                            val formatted = "House #${houseNo.ifBlank { "N/A" }}, Room #${roomNo.ifBlank { "N/A" }}, $streetArea, $city, Zip: ${postalCode.ifBlank { "N/A" }}"
                            UserSession.savedAddresses.add(formatted)
                            houseNo = ""
                            roomNo = ""
                            streetArea = ""
                            postalCode = ""
                            Toast.makeText(context, "New Address Saved to Profile!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Please enter Street / Area name", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.orange)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Address", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Saved Profile Addresses (${UserSession.savedAddresses.size}):", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(UserSession.savedAddresses) { addr ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "📍 $addr",
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                color = colorResource(R.color.darkPurple)
                            )
                        }
                        IconButton(onClick = {
                            UserSession.savedAddresses.remove(addr)
                            Toast.makeText(context, "Address Deleted", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("🗑️", fontSize = 18.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodsView(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        HeaderRow("Managed Payment Methods", onBack)
        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = colorResource(R.color.grey))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🟣 Khalti Digital Wallet", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colorResource(R.color.darkPurple))
                Text("Public Key: 004fc7717a4b48eaaa1cd7d849bb4be7 (Active)", fontSize = 12.sp, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = colorResource(R.color.grey))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🟢 eSewa Digital Wallet", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colorResource(R.color.darkPurple))
                Text("Status: Connected & Active for Nepal Payments", fontSize = 12.sp, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = colorResource(R.color.grey))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("💵 Cash on Delivery (COD)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = colorResource(R.color.darkPurple))
                Text("Status: Default Active Option", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun SettingsView(onBack: () -> Unit) {
    var pushNotifications by remember { mutableStateOf(true) }
    var orderAlerts by remember { mutableStateOf(true) }
    var language by remember { mutableStateOf("English / Nepali") }
    var currency by remember { mutableStateOf("Nepali Rupees (Rs.)") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        HeaderRow("Settings & Preferences", onBack)
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Push Notifications", fontWeight = FontWeight.Medium)
            Switch(
                checked = pushNotifications,
                onCheckedChange = {
                    pushNotifications = it
                    Toast.makeText(context, if (it) "Notifications Enabled" else "Notifications Disabled", Toast.LENGTH_SHORT).show()
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Order Status Alerts", fontWeight = FontWeight.Medium)
            Switch(
                checked = orderAlerts,
                onCheckedChange = { orderAlerts = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        Text("App Language: $language", fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Currency: $currency", fontWeight = FontWeight.Medium)
    }
}

@Composable
fun HelpSupportView(onBack: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        HeaderRow("Help & Support Nepal", onBack)
        Spacer(modifier = Modifier.height(24.dp))

        Text("Need assistance with your FoodPal order?", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth().clickable {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+97714500000"))
            context.startActivity(intent)
        }, colors = CardDefaults.cardColors(containerColor = colorResource(R.color.grey))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("📞 Call Support Hotline", fontWeight = FontWeight.Bold)
                Text("+977-1-4500000 (Kathmandu, Nepal)", fontSize = 12.sp, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(modifier = Modifier.fillMaxWidth().clickable {
            Toast.makeText(context, "Email support: support@foodpal.np", Toast.LENGTH_SHORT).show()
        }, colors = CardDefaults.cardColors(containerColor = colorResource(R.color.grey))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("✉️ Email Customer Care", fontWeight = FontWeight.Bold)
                Text("support@foodpal.np", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun HeaderRow(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back",
                tint = colorResource(R.color.darkPurple)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.darkPurple)
        )
    }
}

@Composable
fun ProfileMenuRow(title: String, isLogout: Boolean = false, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (isLogout) Color.Red else Color.Black
            )
            Text(
                text = "›",
                fontSize = 22.sp,
                color = if (isLogout) Color.Red else Color.Gray,
                fontWeight = FontWeight.Light
            )
        }
    }
}
