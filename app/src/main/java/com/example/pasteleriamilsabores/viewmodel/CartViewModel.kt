package com.example.pasteleriamilsabores.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pasteleriamilsabores.model.CartItem
import com.example.pasteleriamilsabores.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class CartViewModel : ViewModel() {

    private val _cartItems = MutableStateFlow<Map<String, CartItem>>(emptyMap())
    val cartItems: StateFlow<List<CartItem>> = _cartItems
        .map { it.values.toList().sortedBy { item -> item.product.name } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val totalPrice: StateFlow<Double> = _cartItems
        .map { cartMap ->
            cartMap.values.sumOf { it.product.price * it.quantity }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    fun addToCart(product: Product) {
        _cartItems.update { currentCart ->
            val cart = currentCart.toMutableMap()
            val currentItem = cart[product.id]
            if (currentItem != null) {
                cart[product.id] = currentItem.copy(quantity = currentItem.quantity + 1)
            } else {
                cart[product.id] = CartItem(product = product, quantity = 1)
            }
            cart
        }
    }

    fun decreaseQuantity(product: Product) {
        _cartItems.update { currentCart ->
            val cart = currentCart.toMutableMap()
            val currentItem = cart[product.id]
            if (currentItem != null) {
                if (currentItem.quantity > 1) {
                    cart[product.id] = currentItem.copy(quantity = currentItem.quantity - 1)
                } else {
                    cart.remove(product.id)
                }
            }
            cart
        }
    }

    fun removeFromCart(product: Product) {
        _cartItems.update { currentCart ->
            val cart = currentCart.toMutableMap()
            cart.remove(product.id)
            cart
        }
    }

    fun checkout() {
        Log.d("CartViewModel", "Iniciando Checkout...")
        Log.d("CartViewModel", "Total: ${totalPrice.value}")
        _cartItems.value.values.forEach { item ->
            Log.d("CartViewModel", "Item: ${item.product.name}, Cantidad: ${item.quantity}")
        }
        _cartItems.value = emptyMap()
    }
}
