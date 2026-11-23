@file:Suppress("PackageName")

package com.example.foodordering.Activity.Favorite

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodordering.Activity.BaseActivity
import com.example.foodordering.Domain.FoodModel
import com.example.foodordering.R

class FavoriteActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White
            ) {
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
                            text = "Favorite Foods",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.darkPurple)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val favoriteList = listOf(
                        FoodModel(true, "1", "Delicious cheesy pepperoni pizza", 1, "https://images.unsplash.com/photo-1513104890138-7c749659a591", 1, 350.0, 1, 4.8, 20, 25, "Pepperoni Pizza", 450, 0),
                        FoodModel(true, "2", "Juicy beef burger", 2, "https://images.unsplash.com/photo-1568901346375-23c9450c58cd", 1, 250.0, 1, 4.7, 15, 20, "Cheeseburger", 600, 0)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(favoriteList) { food ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = colorResource(R.color.grey))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = food.Title,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colorResource(R.color.darkPurple)
                                        )
                                        Text(
                                            text = "Rs. ${food.Price}",
                                            fontSize = 14.sp,
                                            color = colorResource(R.color.orange),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { finish() },
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.orange)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(top = 16.dp)
                    ) {
                        Text(text = "Back to Home", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
