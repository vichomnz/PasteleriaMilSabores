package com.example.pasteleriamilsabores.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pasteleriamilsabores.model.User
import com.example.pasteleriamilsabores.ui.theme.PasteleriaMilSaboresTheme
import com.example.pasteleriamilsabores.viewmodel.LoginViewModel

@Composable
fun RegisterScreen(
    loginViewModel: LoginViewModel,
    onRegisterSuccess: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var registrationError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Registro", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { if (it.length <= 20) nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            isError = registrationError != null
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = apellido,
            onValueChange = { if (it.length <= 20) apellido = it },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth(),
            isError = registrationError != null
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = edad,
            onValueChange = { edad = it },
            label = { Text("Edad") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = registrationError != null
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = rut,
            onValueChange = { if (it.length <= 10) rut = it },
            label = { Text("RUT") },
            modifier = Modifier.fillMaxWidth(),
            isError = registrationError != null
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() } && newValue.length <= 9) {
                    telefono = newValue
                }
            },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = registrationError != null
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth(),
            isError = registrationError != null
        )

        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = email,
            onValueChange = { if (it.length <= 30) email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            isError = registrationError != null
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            isError = registrationError != null
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            isError = registrationError != null
        )

        registrationError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val edadInt = edad.toIntOrNull()
                when {
                    nombre.isBlank() || apellido.isBlank() || rut.isBlank() || telefono.isBlank() || direccion.isBlank() || email.isBlank() || password.isBlank() -> {
                        registrationError = "Todos los campos son obligatorios"
                    }
                    nombre.length > 20 -> {
                        registrationError = "El nombre no puede tener más de 20 caracteres"
                    }
                    apellido.length > 20 -> {
                        registrationError = "El apellido no puede tener más de 20 caracteres"
                    }
                    edadInt == null || edadInt < 18 || edadInt > 99 -> {
                        registrationError = "La edad debe ser un número entre 18 y 99"
                    }
                    rut.length < 9 || rut.length > 10 -> {
                        registrationError = "El RUT debe tener entre 9 y 10 caracteres"
                    }
                    telefono.length < 8 || telefono.length > 9 -> {
                        registrationError = "El teléfono debe tener entre 8 y 9 dígitos"
                    }
                    email.length > 30 -> {
                        registrationError = "El correo no puede tener más de 30 caracteres"
                    }
                    !email.contains("@") -> {
                        registrationError = "El correo debe contener un @"
                    }
                    password != confirmPassword -> {
                        registrationError = "Las contraseñas no coinciden"
                    }
                    else -> {
                        val success = loginViewModel.registerUser(
                            User(
                                nombre = nombre,
                                apellido = apellido,
                                edad = edadInt!!,
                                rut = rut,
                                telefono = telefono,
                                direccion = direccion,
                                email = email,
                                password = password
                            )
                        )
                        if (success) {
                            onRegisterSuccess()
                        } else {
                            registrationError = "El usuario ya existe"
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    PasteleriaMilSaboresTheme {
        RegisterScreen(
            loginViewModel = viewModel(),
            onRegisterSuccess = {}
        )
    }
}
