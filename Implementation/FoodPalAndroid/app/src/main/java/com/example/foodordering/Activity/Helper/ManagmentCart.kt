package com.example.foodordering.Activity.Helper

import android.content.Context
import android.widget.Toast
import com.example.foodordering.Domain.FoodModel
import com.example.foodordering.Helper.NotificationManager

class ManagmentCart(val context: Context) {

    private val tinyDB = TinyDB(context)

    fun insertItem(item: FoodModel) {
        item.numberInCart = item.numberInCart.coerceAtLeast(1)
        val listFood = getRawListCart()
        val existAlready = listFood.any { it.Title.equals(item.Title, ignoreCase = true) }
        val index = listFood.indexOfFirst { it.Title.equals(item.Title, ignoreCase = true) }

        if (existAlready && index >= 0) {
            listFood[index].numberInCart = item.numberInCart
        } else {
            listFood.add(item)
        }
        tinyDB.putListObject("CartList", listFood)
        NotificationManager.addNotification("Added to Cart 🛒", "Added ${item.Title} to your cart.")
        Toast.makeText(context, "Added ${item.Title} to your Cart", Toast.LENGTH_SHORT).show()
    }

    private fun getRawListCart(): ArrayList<FoodModel> {
        val list = tinyDB.getListObject("CartList") ?: arrayListOf()
        return ArrayList(list.filter { it != null && it.Title.isNotBlank() && !it.Title.contains("Coca-Cola", ignoreCase = true) })
    }

    fun getListCart(): ArrayList<FoodModel> {
        val filtered = getRawListCart()
        val foodTotal = filtered.sumOf { it.Price * it.numberInCart }

        if (foodTotal >= 1000.0) {
            val freeCoke = FoodModel(
                BestFood = false,
                CategotyId = "5",
                Description = "Free 2x Coca-Cola 250ml bottles included for orders above Rs. 1000!",
                Id = 9999,
                ImagePath = "https://images.unsplash.com/photo-1622483767028-3f66f32aef97",
                Price = 0.0,
                Star = 5.0,
                TimeValue = 5,
                Title = "Coca-Cola 250ml Bottle (x2 Free Gift)",
                Calorie = 140,
                numberInCart = 1
            )
            filtered.add(freeCoke)
        }

        return filtered
    }

    fun clearCart() {
        tinyDB.putListObject("CartList", arrayListOf<FoodModel>())
    }

    fun minusItem(listFood: ArrayList<FoodModel>, position: Int, listener: ChangeNumberItemsListener) {
        val rawList = getRawListCart()
        if (position < 0 || position >= rawList.size) return
        val currentCount = rawList[position].numberInCart
        if (currentCount <= 1) {
            rawList.removeAt(position)
        } else {
            rawList[position].numberInCart = currentCount - 1
        }
        tinyDB.putListObject("CartList", rawList)
        listener.onChanged()
    }

    fun plusItem(listFood: ArrayList<FoodModel>, position: Int, listener: ChangeNumberItemsListener) {
        val rawList = getRawListCart()
        if (position < 0 || position >= rawList.size) return
        rawList[position].numberInCart++
        tinyDB.putListObject("CartList", rawList)
        listener.onChanged()
    }

    fun getTotalFee(): Double {
        val listFood = getRawListCart()
        var fee = 0.0
        for (item in listFood) {
            fee += item.Price * item.numberInCart
        }
        return fee
    }
}
