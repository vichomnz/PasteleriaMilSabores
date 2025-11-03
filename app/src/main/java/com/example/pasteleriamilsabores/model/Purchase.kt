package com.example.pasteleriamilsabores.model

import java.util.Date

data class Purchase(
    val items: List<Product>,
    val total: Double,
    val date: Date = Date()
)