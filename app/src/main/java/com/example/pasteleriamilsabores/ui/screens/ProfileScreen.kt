package com.example.pasteleriamilsabores.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pasteleriamilsabores.viewmodel.LoginViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ProfileScreen(loginViewModel: LoginViewModel) {
    val currentUser by loginViewModel.currentUser
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Perfil de Usuario", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        currentUser?.let {
            Text("Nombre: ${it.nombre} ${it.apellido}")
            Spacer(modifier = Modifier.height(8.dp))
            Text("RUT: ${it.rut}")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Teléfono: ${it.telefono}")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Dirección: ${it.direccion}")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Correo: ${it.email}")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Historial de Compras", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        currentUser?.purchaseHistory?.let { history ->
            if (history.isEmpty()) {
                Text("Aún no has realizado ninguna compra.")
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(history) { purchase ->
                        Card(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Fecha: ${sdf.format(purchase.date)}", style = MaterialTheme.typography.titleMedium)
                                Text("Total: $${String.format("%.2f", purchase.total)}")
                                Spacer(modifier = Modifier.height(8.dp))
                                purchase.items.forEach {
                                    Text("- ${it.name}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}