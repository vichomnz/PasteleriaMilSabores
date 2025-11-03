package com.example.pasteleriamilsabores.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.pasteleriamilsabores.model.User

class LoginViewModel : ViewModel() {
    private val _users = mutableListOf<User>()
    val users: List<User> = _users

    var loginError = mutableStateOf(false)

    fun registerUser(user: User): Boolean {
        if (_users.any { it.email == user.email }) {
            return false // El usuario ya existe
        }
        _users.add(user)
        return true
    }

    fun login(email: String, password: String): Boolean {
        val foundUser = _users.find { it.email == email && it.password == password }
        loginError.value = foundUser == null
        return foundUser != null
    }
}