package com.example.pasteleriamilsabores.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pasteleriamilsabores.model.CartItem
import com.example.pasteleriamilsabores.model.Product
import com.example.pasteleriamilsabores.model.Purchase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class CartViewModel(private val loginViewModel: LoginViewModel) : ViewModel() {


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
            val currentItem = cart[product.sku]
            if (currentItem != null) {
                cart[product.sku] = currentItem.copy(quantity = currentItem.quantity + 1)
            } else {
                cart[product.sku] = CartItem(product = product, quantity = 1)
            }
            cart
        }
    }

    fun decreaseQuantity(product: Product) {
        _cartItems.update { currentCart ->
            val cart = currentCart.toMutableMap()
            val currentItem = cart[product.sku]
            if (currentItem != null) {
                if (currentItem.quantity > 1) {
                    cart[product.sku] = currentItem.copy(quantity = currentItem.quantity - 1)
                } else {
                    cart.remove(product.sku)
                }
            }
            cart
        }
    }

    fun removeFromCart(product: Product) {
        _cartItems.update { currentCart ->
            val cart = currentCart.toMutableMap()
            cart.remove(product.sku)
            cart
        }
    }

    fun checkout() {
        val purchase = Purchase(
            items = _cartItems.value.values.map { it.product },
            total = totalPrice.value
        )
        loginViewModel.currentUser.value?.purchaseHistory?.add(purchase)
        _cartItems.value = emptyMap()
    }
}
