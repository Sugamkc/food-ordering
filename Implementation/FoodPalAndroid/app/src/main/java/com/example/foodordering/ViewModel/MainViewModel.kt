package com.example.foodordering.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.foodordering.Domain.BannerModel
import com.example.foodordering.Domain.CategoryModel
import com.example.foodordering.Domain.FoodModel
import com.example.foodordering.repository.MainRepository

class MainViewModel : ViewModel() {
    private val repository = MainRepository()

    fun loadBanner(): LiveData<MutableList<BannerModel>> {
        return repository.loadBanner()
    }

    fun loadCategory(): LiveData<MutableList<CategoryModel>> {
        return repository.loadCategory()
    }

    fun loadFiltered(id: String): LiveData<MutableList<FoodModel>> {
        return repository.loadFiltered(id)
    }

    fun loadAllFoods(): LiveData<MutableList<FoodModel>> {
        return repository.loadAllFoods()
    }
}
