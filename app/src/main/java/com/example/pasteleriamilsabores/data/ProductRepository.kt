package com.example.pasteleriamilsabores.data

import android.content.Context
import com.example.pasteleriamilsabores.data.local.AppDatabase
import com.example.pasteleriamilsabores.data.remote.RetrofitClient
import com.example.pasteleriamilsabores.model.Product

class ProductRepository(context: Context) {
    private val productDao = AppDatabase.getDatabase(context).productDao()
    private val apiService = RetrofitClient.instance

    suspend fun getProductsFromApi(): List<Product> {
        return apiService.getProducts()
    }

    suspend fun getProductsFromDb(): List<Product> {
        return productDao.getAllProducts()
    }

    suspend fun saveProductsToDb(products: List<Product>) {
        productDao.deleteAll() // Limpiar antes de guardar para evitar duplicados o datos viejos
        productDao.insertAll(products)
    }
}
