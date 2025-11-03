package com.example.pasteleriamilsabores.model

data class User(
    val nombre: String,
    val apellido: String,
    val edad: Int,
    val rut: String,
    val telefono: String,
    val direccion: String,
    val email: String,
    val password: String
)