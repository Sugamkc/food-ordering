@file:Suppress("PackageName")

package com.example.foodordering.Activity.DashBoard

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodordering.Activity.BaseActivity
import com.example.foodordering.Activity.ItemList.Item
import com.example.foodordering.R
import com.example.foodordering.viewmodel.MainViewModel

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val scaffoldState = rememberScaffoldState()
    val viewModel = MainViewModel()

    val banner by viewModel.loadBanner().observeAsState(emptyList())
    val categories by viewModel.loadCategory().observeAsState(emptyList())
    val allFoods by viewModel.loadAllFoods().observeAsState(emptyList())

    var searchQuery by remember { mutableStateOf("") }

    val filteredFoods = if (searchQuery.isBlank()) {
        allFoods.take(6)
    } else {
        allFoods.filter {
            it.Title.contains(searchQuery, ignoreCase = true) ||
                    it.Description.contains(searchQuery, ignoreCase = true)
        }
    }

    val showBannerLoading = banner.isEmpty()
    val showCategoryLoading = categories.isEmpty()

    Scaffold(
        bottomBar = { MyBottomBar() },
        scaffoldState = scaffoldState
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            TopBar()
            Banner(banners = banner, showBannerLoading = showBannerLoading)
            Search(query = searchQuery, onQueryChange = { searchQuery = it })
            CategorySection(categories, showCategoryLoading)

            Text(
                text = if (searchQuery.isBlank()) "Popular Nepali Foods" else "Search Results (${filteredFoods.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.darkPurple),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            if (filteredFoods.isEmpty()) {
                Text(
                    text = "No food items found matching \"$searchQuery\"",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                filteredFoods.forEachIndexed { index, food ->
                    Item(item = food, index = index)
                }
            }
        }
    }
}
