package com.example.foodordering.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.foodordering.Domain.BannerModel
import com.example.foodordering.Domain.CategoryModel
import com.example.foodordering.Domain.FoodModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class MainRepository {

    private val firebaseDatabase = FirebaseDatabase.getInstance()

    private fun getMockCategories(): MutableList<CategoryModel> {
        return mutableListOf(
            CategoryModel(1, "https://img.icons8.com/color/96/dumpling.png", "Momo"),
            CategoryModel(2, "https://img.icons8.com/color/96/rice-bowl.png", "Dal Bhat"),
            CategoryModel(3, "https://img.icons8.com/color/96/noodles.png", "Chowmein"),
            CategoryModel(4, "https://img.icons8.com/color/96/bread.png", "Sel Bread"),
            CategoryModel(5, "https://img.icons8.com/color/96/drink.png", "Lassi & Tea")
        )
    }

    private fun getMockBanners(): MutableList<BannerModel> {
        return mutableListOf(
            BannerModel("https://images.unsplash.com/photo-1543353071-873f17a7a088"), // Authentic Thakali Thali spread
            BannerModel("https://images.unsplash.com/photo-1626777552726-4a6b54c97e46"), // Nepali Momo platter
            BannerModel("https://images.unsplash.com/photo-1589301760014-d929f3979dbc")  // Newari Khaja set
        )
    }

    private fun getMockFoods(id: String): MutableList<FoodModel> {
        val allFoods = mutableListOf(
            FoodModel(true, "1", "Juicy steam buff momos served with traditional homemade tomato sesame achar.", 1, "https://images.unsplash.com/photo-1626777552726-4a6b54c97e46", 1, 350.0, 1, 4.9, 15, 20, "Steam Buff Momo", 450, 0),
            FoodModel(true, "1", "Crispy fried chicken momos tossed in spicy chili sauce (C. Momo).", 2, "https://images.unsplash.com/photo-1534422298391-e4f8c172dddb", 1, 450.0, 1, 4.8, 20, 25, "Chicken C. Momo", 550, 0),
            FoodModel(true, "1", "Authentic Jhol Momo served in a bowl of warm, rich sesame-peanut soup.", 3, "https://images.unsplash.com/photo-1496116218417-1a781b1c416c", 1, 400.0, 1, 4.9, 20, 25, "Traditional Jhol Momo", 500, 0),
            FoodModel(true, "2", "Complete authentic Thakali Dal Bhat set with lentil soup, rice, and tarkari.", 4, "https://images.unsplash.com/photo-1543353071-873f17a7a088", 1, 750.0, 1, 4.9, 25, 30, "Thakali Special Thali", 900, 0),
            FoodModel(false, "2", "Traditional Newari Khaja Set (Samay Baji) with beaten rice, spiced meat, and eggs.", 5, "https://images.unsplash.com/photo-1589301760014-d929f3979dbc", 1, 850.0, 1, 4.7, 20, 25, "Newari Khaja Set", 999, 0),
            FoodModel(true, "3", "Soft and buttery tandoori naan served with rich shahi paneer curry.", 6, "https://images.unsplash.com/photo-1601050690597-df0568f70950", 1, 320.0, 1, 4.8, 15, 20, "Butter Naan with Paneer Curry", 420, 0),
            FoodModel(false, "3", "Tangy, spicy street-style Chatpate tossed with crispy noodles, lemon, and fresh herbs.", 7, "https://images.unsplash.com/photo-1569718212165-3a8278d5f624", 1, 150.0, 1, 4.7, 10, 10, "Spicy Street Chatpate", 200, 0),
            FoodModel(false, "4", "Crispy hollow puris filled with spicy tamarind water and spiced potato mix.", 8, "https://images.unsplash.com/photo-1601050690597-df0568f70950", 1, 180.0, 1, 4.9, 10, 10, "Crispy Golgappa (Panipuri)", 250, 0),
            FoodModel(false, "4", "Crispy, ring-shaped sweet rice bread (Sel Roti) made for festivals and special occasions.", 9, "https://images.unsplash.com/photo-1601050690597-df0568f70950", 1, 250.0, 1, 4.8, 10, 15, "Sel Bread (2 pcs)", 300, 0),
            FoodModel(false, "5", "Traditional thick and creamy sweet Himalayan Mango Lassi.", 10, "https://images.unsplash.com/photo-1546173159-315724a31696", 1, 250.0, 1, 4.9, 5, 10, "Himalayan Mango Lassi", 300, 0),
            FoodModel(true, "2", "Juicy Himalayan yak/beef burger with local cheese.", 11, "https://images.unsplash.com/photo-1568901346375-23c9450c58cd", 1, 550.0, 1, 4.7, 15, 20, "Himalayan Cheeseburger", 650, 0),
            FoodModel(false, "3", "Spicy Chicken Sekuwa (Nepalese Barbecue skewers).", 12, "https://images.unsplash.com/photo-1555939594-58d7cb561ad1", 1, 650.0, 1, 4.8, 20, 25, "Chicken Sekuwa", 750, 0)
        )
        if (id == "all") return allFoods
        val filtered = allFoods.filter { it.CategotyId == id }.toMutableList()
        return if (filtered.isEmpty()) allFoods else filtered
    }

    fun loadAllFoods(): LiveData<MutableList<FoodModel>> {
        val listData = MutableLiveData<MutableList<FoodModel>>()
        listData.value = getMockFoods("all")
        return listData
    }

    fun loadCategory(): LiveData<MutableList<CategoryModel>> {
        val listData = MutableLiveData<MutableList<CategoryModel>>()
        listData.value = getMockCategories()

        val ref = firebaseDatabase.getReference("Category")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<CategoryModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(CategoryModel::class.java)
                    item?.let { list.add(it) }
                }
                if (list.isNotEmpty()) {
                    listData.value = list
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // keep mock
            }
        })

        return listData
    }

    fun loadBanner(): LiveData<MutableList<BannerModel>> {
        val listData = MutableLiveData<MutableList<BannerModel>>()
        listData.value = getMockBanners()

        val ref = firebaseDatabase.getReference("Banners")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<BannerModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(BannerModel::class.java)
                    item?.let { list.add(it) }
                }
                if (list.isNotEmpty()) {
                    listData.value = list
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // keep mock
            }
        })

        return listData
    }

    fun loadFiltered(id: String): LiveData<MutableList<FoodModel>> {
        val listData = MutableLiveData<MutableList<FoodModel>>()
        listData.value = getMockFoods(id)

        val ref = firebaseDatabase.getReference("Foods")
        val query: Query = ref.orderByChild("CategoryId").equalTo(id)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<FoodModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(FoodModel::class.java)
                    if (item != null) {
                        lists.add(item)
                    }
                }
                if (lists.isNotEmpty()) {
                    listData.value = lists
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // keep mock
            }
        })

        return listData
    }
}
