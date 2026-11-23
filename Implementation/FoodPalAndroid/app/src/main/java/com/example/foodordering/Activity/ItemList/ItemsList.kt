@file:Suppress("PackageName")

package com.example.foodordering.Activity.ItemList

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImage
import com.example.foodordering.Activity.DetailsFoods.DetailsOfFoodsActivity
import com.example.foodordering.Domain.FoodModel
import com.example.foodordering.R

@Composable
fun ItemList(items: List<FoodModel>) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        itemsIndexed(items) { index, item ->
            Item(item = item, index = index)
        }
    }
}

@Composable
fun Item(item: FoodModel, index: Int) {
    val context = LocalContext.current
    val isEvenRow = index % 2 == 0

    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .background(colorResource(R.color.grey), shape = RoundedCornerShape(10.dp))
            .wrapContentHeight()
            .clickable {
                val intent = Intent(context, DetailsOfFoodsActivity::class.java).apply {
                    putExtra("object", item)
                }
                startActivity(context, intent, null)
            },
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isEvenRow) {
            FoodImage(item = item)
            FoodDetails(item = item)
        } else {
            FoodDetails(item = item)
            FoodImage(item = item)
        }
    }
}

@Composable
fun FoodImage(item: FoodModel) {
    AsyncImage(
        model = item.ImagePath,
        contentDescription = null,
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(colorResource(R.color.grey), shape = RoundedCornerShape(10.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun RowScope.FoodDetails(item: FoodModel) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = if (item.Title.isBlank()) "Nepali Dish" else item.Title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(top = 8.dp),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )

        TimingRow(item.TimeValue)
        RatingBarRow(item.Star)

        val hasOffer = (item.Id % 3 == 0) || item.BestFood
        if (hasOffer) {
            val discountPercent = when (item.Id % 3) {
                0 -> 10
                1 -> 15
                else -> 20
            }
            val originalPrice = item.Price / (1.0 - (discountPercent / 100.0))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(
                    text = "Rs. ${originalPrice.toInt()}",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    textDecoration = TextDecoration.LineThrough
                )
                Text(
                    text = "Rs. ${item.Price.toInt()}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.orange)
                )
                Box(
                    modifier = Modifier
                        .background(colorResource(R.color.darkBrown), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(text = "$discountPercent% OFF", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Text(
                text = "Rs. ${item.Price.toInt()}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.orange),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun TimingRow(timeValue: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.time),
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = "takes $timeValue min",
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
fun RatingBarRow(star: Double) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.star),
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = "$star Ratings",
            style = MaterialTheme.typography.body1
        )
    }
}
