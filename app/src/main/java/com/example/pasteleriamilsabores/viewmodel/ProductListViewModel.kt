package com.example.pasteleriamilsabores.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pasteleriamilsabores.R
import com.example.pasteleriamilsabores.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductListViewModel : ViewModel() {

    private val _allProducts = MutableStateFlow<List<Product>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

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
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _allProducts.value = listOf(
                Product("TC001", "Torta Cuadrada de Chocolate", "Tortas Cuadradas", 45000.0, "CLP", R.drawable.tc001),
                Product("TC002", "Torta Cuadrada de Frutas", "Tortas Cuadradas", 50000.0, "CLP", R.drawable.tc002),
                Product("TT001", "Torta Circular de Vainilla", "Tortas Circulares", 40000.0, "CLP", R.drawable.tt001),
                Product("TT002", "Torta Circular de Manjar", "Tortas Circulares", 42000.0, "CLP", R.drawable.tt002),
                Product("PI001", "Mousse de Chocolate", "Postres Individuales", 5000.0, "CLP", R.drawable.pi001),
                Product("PI002", "Tiramisú Clásico", "Postres Individuales", 5500.0, "CLP", R.drawable.pi002),
                Product("PSA001", "Torta Sin Azúcar de Naranja", "Productos Sin Azúcar", 48000.0, "CLP", R.drawable.psa001),
                Product("PSA002", "Cheesecake Sin Azúcar", "Productos Sin Azúcar", 47000.0, "CLP", R.drawable.psa002),
                Product("PT001", "Empanada de Manzana", "Pastelería Tradicional", 3000.0, "CLP", R.drawable.pt001),
                Product("PT002", "Tarta de Santiago", "Pastelería Tradicional", 6000.0, "CLP", R.drawable.pt002),
                Product("PG001", "Brownie Sin Gluten", "Productos Sin Gluten", 4000.0, "CLP", R.drawable.pg001),
                Product("PG002", "Pan Sin Gluten", "Productos Sin Gluten", 3500.0, "CLP", R.drawable.pg002),
                Product("PV001", "Torta Vegana de Chocolate", "Productos Vegana", 50000.0, "CLP", R.drawable.pv001),
                Product("PV002", "Galletas Veganas de Avena", "Productos Vegana", 4500.0, "CLP", R.drawable.pv002),
                Product("TE001", "Torta Especial de Cumpleaños", "Tortas Especiales", 55000.0, "CLP", R.drawable.te001),
                Product("TE002", "Torta Especial de Boda", "Tortas Especiales", 60000.0, "CLP", R.drawable.te002)
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}
