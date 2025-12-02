package com.example.pasteleriamilsabores.model

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.example.pasteleriamilsabores.R
import java.util.UUID

@Entity(tableName = "products")
data class Product(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("nombre")
    val name: String,

    @SerializedName("descripcion")
    val description: String,

    @SerializedName("precio")
    val price: Double,

    // Hacemos nullable este campo para evitar el error si no viene en el JSON
    val unit: String? = "unidad",

    @DrawableRes
    val imageRes: Int = R.drawable.ic_launcher_foreground,

    @PrimaryKey
    @SerializedName("sku")

    val sku: String = UUID.randomUUID().toString()
)

data class CartItem(
    val product: Product,
    val quantity: Int
)
