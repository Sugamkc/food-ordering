@file:Suppress("PackageName")

package com.example.foodordering.Activity.DetailsFoods

import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.foodordering.Activity.BaseActivity
import com.example.foodordering.Domain.FoodModel
import com.example.foodordering.Activity.Helper.ManagmentCart

class DetailsOfFoodsActivity : BaseActivity() {
    private lateinit var item: FoodModel
    private lateinit var managmentCart: ManagmentCart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the FoodModel object from intent
        item = intent.getSerializableExtra("object") as? FoodModel ?: FoodModel()
        item.numberInCart = 1.coerceAtLeast(item.numberInCart)
        managmentCart = ManagmentCart(this)

        setContent {
            DetailedScreen(
                item = item,
                onBackClick = { finish() },
                onAddToCartClick = {
                    managmentCart.insertItem(item)
                }
            )
        }
    }
}
