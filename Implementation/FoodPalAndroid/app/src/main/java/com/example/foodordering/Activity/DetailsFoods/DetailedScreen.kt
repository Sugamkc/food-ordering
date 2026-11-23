@file:Suppress("PackageName")

package com.example.foodordering.Activity.DetailsFoods

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.foodordering.Activity.Cart.CartActivity
import com.example.foodordering.Domain.FoodModel
import com.example.foodordering.R

@Composable
fun DetailedScreen(
    item: FoodModel,
    onBackClick: () -> Unit,
    onAddToCartClick: () -> Unit
) {
    var numberInCart by remember { mutableStateOf(item.numberInCart.coerceAtLeast(1)) }
    var showAddToCartPopup by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val totalPrice = item.Price * numberInCart

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 85.dp)
                .verticalScroll(rememberScrollState())
        ) {
            HeaderSections(
                item = item,
                numberInCart = numberInCart,
                onBackClick = onBackClick,
                onIncrement = {
                    numberInCart++
                    item.numberInCart = numberInCart
                },
                onDecrement = {
                    if (numberInCart > 1) {
                        numberInCart--
                        item.numberInCart = numberInCart
                    }
                }
            )
            DescriptionSection(item.Description)
            Spacer(modifier = Modifier.height(16.dp))
            NumberRow(
                item = item,
                numberInCart = numberInCart,
                onIncrement = {
                    numberInCart++
                    item.numberInCart = numberInCart
                },
                onDecrement = {
                    if (numberInCart > 1) {
                        numberInCart--
                        item.numberInCart = numberInCart
                    }
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        FooterSection(
            onAddToCartClick = {
                onAddToCartClick()
                showAddToCartPopup = true
            },
            totalPrice = totalPrice,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (showAddToCartPopup) {
        AlertDialog(
            onDismissRequest = { showAddToCartPopup = false },
            title = { Text("Added to Cart! 🛒") },
            text = { Text("Added '${item.Title}' (${numberInCart}x) to your cart.") },
            confirmButton = {
                Button(
                    onClick = {
                        showAddToCartPopup = false
                        context.startActivity(Intent(context, CartActivity::class.java))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.orange)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Go to Cart", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddToCartPopup = false }) {
                    Text("Continue Shopping")
                }
            }
        )
    }
}
