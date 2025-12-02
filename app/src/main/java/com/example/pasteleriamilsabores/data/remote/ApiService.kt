package com.example.pasteleriamilsabores.data.remote

import com.example.pasteleriamilsabores.model.Product
import retrofit2.http.GET

interface ApiService {
    // La URL completa es: https://raw.githubusercontent.com/chalalo1533/ServicioRest/refs/heads/master/productos.json
    // Definiremos la base URL en el cliente y aquí solo la ruta relativa o el path.
    // Dado que la estructura de GitHub raw puede ser compleja, usaré el path relativo desde la base que configuraré luego.
    
    @GET("chalalo1533/ServicioRest/refs/heads/master/productos.json")
    suspend fun getProducts(): List<Product>
}
