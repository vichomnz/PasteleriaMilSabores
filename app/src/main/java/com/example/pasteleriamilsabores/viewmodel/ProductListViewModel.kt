package com.example.pasteleriamilsabores.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pasteleriamilsabores.R
import com.example.pasteleriamilsabores.data.ProductRepository
import com.example.pasteleriamilsabores.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductListViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Estados para mensajes de UI (Errores, Éxitos)
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    val filteredProducts: StateFlow<List<Product>> = _searchQuery
        .combine(_allProducts) { query, products ->
            if (query.isBlank()) {
                products
            } else {
                products.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.description.contains(query, ignoreCase = true)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        checkAndSeedDatabase()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun loadFromApi(onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            try {
                val products = repository.getProductsFromApi()
                _allProducts.value = products
                onSuccess()
            } catch (e: Exception) {
                Log.e("ProductListViewModel", "Error loading from API", e)
                _message.value = "Error al cargar desde API: ${e.localizedMessage}"
                onError()
            }
        }
    }

    fun loadFromDb(onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            try {
                val products = repository.getProductsFromDb()
                if (products.isNotEmpty()) {
                    _allProducts.value = products
                    onSuccess()
                } else {
                    _message.value = "No hay datos locales almacenados"
                    onError()
                }
            } catch (e: Exception) {
                Log.e("ProductListViewModel", "Error loading from DB", e)
                _message.value = "Error al cargar base de datos local: ${e.localizedMessage}"
                onError()
            }
        }
    }

    fun saveToDb() {
        viewModelScope.launch {
            try {
                val currentProducts = _allProducts.value
                if (currentProducts.isNotEmpty()) {
                    repository.saveProductsToDb(currentProducts)
                    _message.value = "Datos guardados localmente con éxito"
                } else {
                    _message.value = "No hay datos para guardar"
                }
            } catch (e: Exception) {
                Log.e("ProductListViewModel", "Error saving to DB", e)
                _message.value = "Error al guardar: ${e.localizedMessage}" 
            }
        }
    }

    // Función privada para verificar y poblar la BD al inicio
    private fun checkAndSeedDatabase() {
        viewModelScope.launch {
            try {
                val existingProducts = repository.getProductsFromDb()
                if (existingProducts.isEmpty()) {
                    Log.d("ProductListViewModel", "Base de datos vacía. Insertando productos por defecto.")
                    val defaultProducts = listOf(
                        Product(id = "TC001", name = "Torta Cuadrada de Chocolate", description = "Tortas Cuadradas", price = 45000.0, unit = "CLP", imageRes = R.drawable.tc001, sku = "TC001"),
                        Product(id = "TC002", name = "Torta Cuadrada de Frutas", description = "Tortas Cuadradas", price = 50000.0, unit = "CLP", imageRes = R.drawable.tc002, sku = "TC002"),
                        Product(id = "TT001", name = "Torta Circular de Vainilla", description = "Tortas Circulares", price = 40000.0, unit = "CLP", imageRes = R.drawable.tt001, sku = "TT001"),
                        Product(id = "TT002", name = "Torta Circular de Manjar", description = "Tortas Circulares", price = 42000.0, unit = "CLP", imageRes = R.drawable.tt002, sku = "TT002"),
                        Product(id = "PI001", name = "Mousse de Chocolate", description = "Postres Individuales", price = 5000.0, unit = "CLP", imageRes = R.drawable.pi001, sku = "PI001"),
                        Product(id = "PI002", name = "Tiramisú Clásico", description = "Postres Individuales", price = 5500.0, unit = "CLP", imageRes = R.drawable.pi002, sku = "PI002"),
                        Product(id = "PSA001", name = "Torta Sin Azúcar de Naranja", description = "Productos Sin Azúcar", price = 48000.0, unit = "CLP", imageRes = R.drawable.psa001, sku = "PSA001"),
                        Product(id = "PSA002", name = "Cheesecake Sin Azúcar", description = "Productos Sin Azúcar", price = 47000.0, unit = "CLP", imageRes = R.drawable.psa002, sku = "PSA002"),
                        Product(id = "PT001", name = "Empanada de Manzana", description = "Pastelería Tradicional", price = 3000.0, unit = "CLP", imageRes = R.drawable.pt001, sku = "PT001"),
                        Product(id = "PT002", name = "Tarta de Santiago", description = "Pastelería Tradicional", price = 6000.0, unit = "CLP", imageRes = R.drawable.pt002, sku = "PT002"),
                        Product(id = "PG001", name = "Brownie Sin Gluten", description = "Productos Sin Gluten", price = 4000.0, unit = "CLP", imageRes = R.drawable.pg001, sku = "PG001"),
                        Product(id = "PG002", name = "Pan Sin Gluten", description = "Productos Sin Gluten", price = 3500.0, unit = "CLP", imageRes = R.drawable.pg002, sku = "PG002"),
                        Product(id = "PV001", name = "Torta Vegana de Chocolate", description = "Productos Vegana", price = 50000.0, unit = "CLP", imageRes = R.drawable.pv001, sku = "PV001"),
                        Product(id = "PV002", name = "Galletas Veganas de Avena", description = "Productos Vegana", price = 4500.0, unit = "CLP", imageRes = R.drawable.pv002, sku = "PV002"),
                        Product(id = "TE001", name = "Torta Especial de Cumpleaños", description = "Tortas Especiales", price = 55000.0, unit = "CLP", imageRes = R.drawable.te001, sku = "TE001"),
                        Product(id = "TE002", name = "Torta Especial de Boda", description = "Tortas Especiales", price = 60000.0, unit = "CLP", imageRes = R.drawable.te002, sku = "TE002")
                    )
                    repository.saveProductsToDb(defaultProducts)
                } else {
                    Log.d("ProductListViewModel", "La base de datos ya contiene ${existingProducts.size} productos.")
                }
            } catch (e: Exception) {
                Log.e("ProductListViewModel", "Error en checkAndSeedDatabase", e)
            }
        }
    }

    // Función pública para forzar la carga de productos de prueba si fuera necesario (ya no usada desde UI)
    fun seedDefaultProducts(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                checkAndSeedDatabase()
                // Recargamos desde DB para actualizar la lista en UI si es necesario
                loadFromDb(onSuccess = onSuccess, onError = {})
            } catch (e: Exception) {
                Log.e("ProductListViewModel", "Error seeding DB", e)
                _message.value = "Error al cargar datos de prueba: ${e.localizedMessage}"
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
