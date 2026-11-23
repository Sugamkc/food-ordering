package com.example.foodordering.Activity.Cart

import android.icu.text.DecimalFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodordering.R

@Composable
fun CartSummary(
    itemTotal: Double,
    tax: Double,
    delivery: Double,
    onOrderClick: () -> Unit
) {
    val total = itemTotal + tax + delivery
    val decimalFormat = DecimalFormat("#.00")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .background(colorResource(R.color.grey), shape = RoundedCornerShape(10.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = "Subtotal:",
                modifier = Modifier.weight(1f),
                color = colorResource(R.color.darkPurple)
            )
            Text(
                text = "Rs. ${decimalFormat.format(itemTotal)}",
                color = colorResource(R.color.darkPurple),
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = "Tax:",
                modifier = Modifier.weight(1f),
                color = colorResource(R.color.darkPurple)
            )
            Text(
                text = "Rs. ${decimalFormat.format(tax)}",
                color = colorResource(R.color.darkPurple),
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = "Delivery:",
                modifier = Modifier.weight(1f),
                color = colorResource(R.color.darkPurple)
            )
            Text(
                text = "Rs. ${decimalFormat.format(delivery)}",
                color = colorResource(R.color.darkPurple),
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Total:",
                modifier = Modifier.weight(1f),
                color = colorResource(R.color.darkPurple),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = "Rs. ${decimalFormat.format(total)}",
                color = colorResource(R.color.darkPurple),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        Button(
            onClick = { onOrderClick() },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.orange)
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(
                text = "Place Order",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
