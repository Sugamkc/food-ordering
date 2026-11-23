@file:Suppress("PackageName")

package com.example.foodordering.Activity.Cart

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodordering.Activity.BaseActivity
import com.example.foodordering.Activity.Helper.ManagmentCart
import com.example.foodordering.Activity.Order.OrderActivity
import com.example.foodordering.Domain.FoodModel
import com.example.foodordering.Helper.KhaltiManager
import com.example.foodordering.Helper.NotificationManager
import com.example.foodordering.Helper.OrderManager
import com.example.foodordering.Helper.UserSession
import com.example.foodordering.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

// Restaurant Central Coordinates (Durbar Marg, Kathmandu)
private const val RESTAURANT_LAT = 27.7172
private const val RESTAURANT_LNG = 85.3240
private const val MAX_DELIVERY_RADIUS_KM = 20.0

private fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return r * c
}

class CartActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleDeepLink(intent)
        setContent {
            CartScreen(
                onBackClick = { finish() }
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val data: Uri? = intent?.data
        if (data != null && data.scheme == "foodpal" && data.host == "payment-success") {
            val pidx = data.getQueryParameter("pidx") ?: data.getQueryParameter("idx")

            if (!pidx.isNullOrBlank()) {
                Toast.makeText(this, "Verifying Khalti Payment... 🟣", Toast.LENGTH_SHORT).show()

                KhaltiManager.verifyPayment(
                    pidx = pidx,
                    onVerified = { isCompleted, status, transactionId ->
                        if (isCompleted) {
                            Toast.makeText(this, "Payment Verified! Status: $status ✓", Toast.LENGTH_LONG).show()

                            val db = FirebaseFirestore.getInstance()
                            db.collection("orders")
                                .whereEqualTo("pidx", pidx)
                                .get()
                                .addOnSuccessListener { documents ->
                                    for (doc in documents) {
                                        val orderId = doc.id
                                        db.collection("orders").document(orderId).update(
                                            mapOf(
                                                "paymentStatus" to "PAID",
                                                "orderStatus" to "CONFIRMED",
                                                "transactionId" to transactionId
                                            )
                                        )
                                        FirebaseDatabase.getInstance().getReference("Orders")
                                            .child(orderId)
                                            .updateChildren(mapOf("paymentStatus" to "PAID", "orderStatus" to "CONFIRMED"))
                                    }
                                }

                            val orderIntent = Intent(this, OrderActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            }
                            startActivity(orderIntent)
                            finish()
                        } else {
                            Toast.makeText(this, "Payment incomplete. Status: $status", Toast.LENGTH_LONG).show()
                        }
                    },
                    onError = { errorMsg ->
                        Toast.makeText(this, "Verification Error: $errorMsg", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }
    }
}

private fun getReadableAddress(context: Context, latitude: Double, longitude: Double): String {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            val addr = addresses[0]
            val street = addr.thoroughfare ?: addr.subLocality ?: ""
            val city = addr.locality ?: addr.subAdminArea ?: "Kathmandu"
            val country = addr.countryName ?: "Nepal"
            if (street.isNotBlank()) "$street, $city, $country" else "$city, $country"
        } else {
            "Kathmandu, Nepal"
        }
    } catch (e: Exception) {
        "Kathmandu, Nepal"
    }
}

private fun saveOrderToFirestore(
    context: Context,
    orderId: String,
    totalAmount: Double,
    address: String,
    items: List<FoodModel>,
    pidx: String,
    onResult: (Boolean) -> Unit
) {
    val uid = try { FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous_user" } catch (e: Exception) { "anonymous_user" }
    val orderData = hashMapOf(
        "orderId" to orderId,
        "userId" to uid,
        "totalAmount" to totalAmount,
        "totalPaisa" to (totalAmount * 100).toLong(),
        "address" to address,
        "paymentMethod" to "KHALTI",
        "paymentStatus" to if (pidx == "PENDING") "PENDING" else "PAID",
        "orderStatus" to "AWAITING_PAYMENT",
        "pidx" to pidx,
        "items" to items.map { mapOf("Id" to it.Id, "Title" to it.Title, "Price" to it.Price, "numberInCart" to it.numberInCart) },
        "timestamp" to System.currentTimeMillis()
    )

    FirebaseFirestore.getInstance().collection("orders").document(orderId)
        .set(orderData)
        .addOnSuccessListener {
            FirebaseDatabase.getInstance().getReference("Orders").child(orderId).setValue(orderData)
            onResult(true)
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Firestore Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            onResult(false)
        }
}

@SuppressLint("MissingPermission")
@Composable
fun CartScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val managmentCart = remember { ManagmentCart(context) }
    var cartItems by remember { mutableStateOf(managmentCart.getListCart()) }
    var refreshKey by remember { mutableStateOf(0) }

    var showCheckoutDialog by remember { mutableStateOf(false) }
    var showMapPickerDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    var useCurrentGpsLocation by remember { mutableStateOf(true) }
    var isFastDelivery by remember { mutableStateOf(false) }
    var selectedSavedAddress by remember { mutableStateOf(UserSession.savedAddresses.firstOrNull() ?: "") }
    var deliveryAddress by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Cash on Delivery") }
    var selectedLatLng by remember { mutableStateOf(LatLng(RESTAURANT_LAT, RESTAURANT_LNG)) }
    var gpsStatus by remember { mutableStateOf("Tap 'Verify Current GPS' or pick on Map") }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val distanceKm = remember(selectedLatLng) {
        calculateDistanceKm(RESTAURANT_LAT, RESTAURANT_LNG, selectedLatLng.latitude, selectedLatLng.longitude)
    }
    val isDeliveryAvailable = distanceKm <= MAX_DELIVERY_RADIUS_KM

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            try {
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            selectedLatLng = LatLng(location.latitude, location.longitude)
                            val exactAddress = getReadableAddress(context, location.latitude, location.longitude)
                            deliveryAddress = exactAddress
                            gpsStatus = "GPS Location: $exactAddress 📍"
                        } else {
                            gpsStatus = "GPS location unavailable, please pick on Map."
                        }
                    }
            } catch (e: Exception) {
                gpsStatus = "Error getting location: ${e.message}"
            }
        } else {
            gpsStatus = "Location permission denied. Please pick on Map or choose saved address."
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 80.dp)
        ) {
            // Top back button
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
                    text = "My Cart & Checkout",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.darkPurple)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Your cart is empty", color = colorResource(R.color.darkPurple), fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(cartItems) { item ->
                        CartItem(
                            cartItems = cartItems,
                            item = item,
                            managmentCart = managmentCart,
                            onItemChange = {
                                cartItems = managmentCart.getListCart()
                                refreshKey++
                            }
                        )
                    }
                }

                CartSummary(
                    itemTotal = managmentCart.getTotalFee(),
                    tax = 2.0,
                    delivery = 100.0,
                    onOrderClick = {
                        showCheckoutDialog = true
                        try {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        } catch (e: Exception) {
                            // ignore
                        }
                    }
                )
            }
        }

        // Full-Screen Checkout & Shipping Address View
        if (showCheckoutDialog) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { showCheckoutDialog = false }) {
                            Icon(
                                painter = painterResource(id = R.drawable.back),
                                contentDescription = "Back",
                                tint = colorResource(R.color.darkPurple)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Checkout & Delivery Destination 📍",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.darkPurple)
                        )
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.grey))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "Payment Method:", fontWeight = FontWeight.Bold, color = colorResource(R.color.darkPurple))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { paymentMethod = "Cash on Delivery" }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(selected = paymentMethod == "Cash on Delivery", onClick = { paymentMethod = "Cash on Delivery" })
                                Text("Cash on Delivery (COD)", fontWeight = FontWeight.Medium)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { paymentMethod = "Khalti" }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(selected = paymentMethod == "Khalti", onClick = { paymentMethod = "Khalti" })
                                Text("Khalti Official API Gateway 🟣", fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    // 20 KM Delivery Radius Validation Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDeliveryAvailable) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = if (isDeliveryAvailable) "✅ Delivery Available" else "❌ Delivery Unreachable (Outside 20 km)",
                                fontWeight = FontWeight.Bold,
                                color = if (isDeliveryAvailable) Color(0xFF2E7D32) else Color(0xFFC62828),
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Distance from Restaurant: ${String.format(Locale.getDefault(), "%.1f", distanceKm)} km (Max Radius: 20.0 km)",
                                fontSize = 12.sp,
                                color = Color.DarkGray
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.grey))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(text = "Choose Delivery Destination:", fontWeight = FontWeight.Bold, color = colorResource(R.color.darkPurple))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { useCurrentGpsLocation = true }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(selected = useCurrentGpsLocation, onClick = { useCurrentGpsLocation = true })
                                Text("📍 Current GPS / Map Location", fontWeight = FontWeight.Medium)
                            }

                            if (useCurrentGpsLocation) {
                                Text(text = gpsStatus, fontSize = 11.sp, color = Color.Gray)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            permissionLauncher.launch(
                                                arrayOf(
                                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                                )
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.darkBrown)),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Verify GPS 📍", fontSize = 11.sp, color = Color.White)
                                    }

                                    Button(
                                        onClick = { showMapPickerDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.orange)),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Pick Map 🗺️", fontSize = 11.sp, color = Color.White)
                                    }
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { useCurrentGpsLocation = false }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(selected = !useCurrentGpsLocation, onClick = { useCurrentGpsLocation = false })
                                Text("🏠 Saved Profile Addresses", fontWeight = FontWeight.Medium)
                            }

                            if (!useCurrentGpsLocation) {
                                if (UserSession.savedAddresses.isEmpty()) {
                                    Text("No saved addresses in Profile yet.", fontSize = 12.sp, color = Color.Red)
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        UserSession.savedAddresses.forEach { addr ->
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { selectedSavedAddress = addr }
                                                    .padding(vertical = 2.dp)
                                            ) {
                                                RadioButton(selected = selectedSavedAddress == addr, onClick = { selectedSavedAddress = addr })
                                                Text(addr, fontSize = 12.sp, color = colorResource(R.color.darkPurple))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            if (!isDeliveryAvailable && useCurrentGpsLocation) {
                                Toast.makeText(
                                    context,
                                    "Delivery Unreachable! Your location is ${String.format(Locale.getDefault(), "%.1f", distanceKm)} km away (Max radius is 20 km).",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@Button
                            }

                            val finalAddress = if (useCurrentGpsLocation) deliveryAddress.ifBlank { "Kathmandu, Nepal" } else selectedSavedAddress.ifBlank { "Kathmandu, Nepal" }
                            val fullAddress = "$finalAddress [Distance: ${String.format(Locale.getDefault(), "%.1f", distanceKm)} km]"

                            showCheckoutDialog = false
                            deliveryAddress = finalAddress
                            val total = managmentCart.getTotalFee() + 2.0 + 100.0
                            val orderId = "ORD-${System.currentTimeMillis()}"

                            if (paymentMethod == "Khalti") {
                                Toast.makeText(context, "Saving order & initiating Khalti API...", Toast.LENGTH_SHORT).show()

                                saveOrderToFirestore(
                                    context = context,
                                    orderId = orderId,
                                    totalAmount = total,
                                    address = fullAddress,
                                    items = cartItems.toList(),
                                    pidx = "PENDING"
                                ) { success ->
                                    if (success) {
                                        KhaltiManager.initiatePayment(
                                            totalAmountNpr = total,
                                            deliveryFeeNpr = 100.0,
                                            orderId = orderId,
                                            customerName = UserSession.name.ifBlank { "Test Bahadur" },
                                            customerEmail = UserSession.email.ifBlank { "test@khalti.com" },
                                            customerPhone = "9800000001",
                                            cartItems = cartItems.toList(),
                                            onSuccess = { paymentUrl, realPidx ->
                                                Log.d("Khalti", "Real PIDX received: $realPidx, URL: $paymentUrl")

                                                FirebaseFirestore.getInstance().collection("orders").document(orderId)
                                                    .update("pidx", realPidx)

                                                OrderManager.addOrder(cartItems.toList(), total, "$fullAddress [Paid via: Khalti (Pidx: $realPidx)]")
                                                managmentCart.clearCart()
                                                cartItems = arrayListOf()

                                                KhaltiManager.openPaymentGateway(context, paymentUrl)
                                            },
                                            onError = { errorMsg ->
                                                Toast.makeText(context, "Khalti Init Error: $errorMsg", Toast.LENGTH_LONG).show()
                                                Log.e("Khalti", errorMsg)
                                            }
                                        )
                                    }
                                }
                            } else {
                                val uid = try { FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous_user" } catch (e: Exception) { "anonymous_user" }
                                val orderData = hashMapOf(
                                    "orderId" to orderId,
                                    "userId" to uid,
                                    "totalAmount" to total,
                                    "totalPaisa" to (total * 100).toLong(),
                                    "address" to fullAddress,
                                    "paymentMethod" to "Cash on Delivery",
                                    "paymentStatus" to "PENDING",
                                    "orderStatus" to "CONFIRMED",
                                    "items" to cartItems.map { mapOf("Id" to it.Id, "Title" to it.Title, "Price" to it.Price, "numberInCart" to it.numberInCart) },
                                    "timestamp" to System.currentTimeMillis()
                                )

                                FirebaseFirestore.getInstance().collection("orders").document(orderId)
                                    .set(orderData)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Saved order #$orderId to Firestore ✓", Toast.LENGTH_SHORT).show()
                                    }

                                FirebaseDatabase.getInstance().getReference("Orders").child(orderId).setValue(orderData)

                                OrderManager.addOrder(cartItems.toList(), total, "$fullAddress [Paid via: Cash on Delivery]")
                                NotificationManager.addNotification("Order Placed Successfully! 🎉", "Your order to '$fullAddress' has been placed via Cash on Delivery. Delivery on the way 🛵")
                                managmentCart.clearCart()
                                cartItems = arrayListOf()
                                showSuccessDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDeliveryAvailable || !useCurrentGpsLocation) colorResource(R.color.orange) else Color.Gray
                        ),
                        enabled = isDeliveryAvailable || !useCurrentGpsLocation,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                    ) {
                        Text(
                            text = if (isDeliveryAvailable || !useCurrentGpsLocation) "Proceed to Pay & Place Order" else "Delivery Unreachable (>20 km)",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // In-App Google Maps Location Picker Dialog
    if (showMapPickerDialog) {
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(selectedLatLng, 15f)
        }

        AlertDialog(
            onDismissRequest = { showMapPickerDialog = false },
            title = { Text("Select Location on Google Map 🗺️") },
            text = {
                Column(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                        cameraPositionState = cameraPositionState
                    ) {
                        Marker(
                            state = MarkerState(position = cameraPositionState.position.target),
                            title = "Selected Delivery Point",
                            snippet = "FoodPal Delivery Destination"
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = cameraPositionState.position.target
                        selectedLatLng = target
                        val exactAddress = getReadableAddress(context, target.latitude, target.longitude)
                        deliveryAddress = exactAddress
                        gpsStatus = "Location Captured: $exactAddress 📍"
                        showMapPickerDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.orange))
                ) {
                    Text("Confirm Selected Location 📍", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showMapPickerDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Order Success & Driver Rating Popup
    if (showSuccessDialog) {
        var rating by remember { mutableStateOf(5) }
        var feedbackText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = null,
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(colorResource(R.color.orange)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "✓",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Thank You!",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Order Completed",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.darkPurple)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Please rate your last Driver",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 5 Star Rating Bar
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in (1..5)) {
                            Text(
                                text = if (i <= rating) "★" else "☆",
                                fontSize = 28.sp,
                                color = if (i <= rating) Color(0xFFFFB300) else Color.LightGray,
                                modifier = Modifier.clickable { rating = i }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = feedbackText,
                        onValueChange = { feedbackText = it },
                        placeholder = { Text("Leave feedback", fontSize = 13.sp, color = Color.Gray) },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            showSuccessDialog = false
                            managmentCart.clearCart()
                            cartItems = arrayListOf()
                            onBackClick()
                        }
                    ) {
                        Text("Skip", color = Color.Gray, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            Toast.makeText(
                                context,
                                "Thank you for your feedback!",
                                Toast.LENGTH_SHORT
                            ).show()
                            showSuccessDialog = false
                            managmentCart.clearCart()
                            cartItems = arrayListOf()
                            onBackClick()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.orange)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Submit", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
    }
}
