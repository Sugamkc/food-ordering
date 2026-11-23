@file:Suppress("PackageName")

package com.example.foodordering.Activity.DetailsFoods

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
fun DescriptionSection(description: String, modifier: Modifier = Modifier) {
    Text(
        text = "Description",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = colorResource(R.color.darkPurple),
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
    Text(
        text = if (description.isNotBlank()) description else "No description available",
        fontSize = 14.sp,
        color = Color.DarkGray,
        lineHeight = 20.sp,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    )
}
