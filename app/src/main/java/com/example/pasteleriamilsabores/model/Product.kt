package com.example.pasteleriamilsabores.model

import androidx.annotation.DrawableRes

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val unit: String,
    @DrawableRes val imageRes: Int
)

data class CartItem(
    val product: Product,
    val quantity: Int
)
