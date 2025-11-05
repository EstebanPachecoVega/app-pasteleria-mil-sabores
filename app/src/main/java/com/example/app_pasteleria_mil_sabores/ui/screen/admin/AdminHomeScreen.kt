package com.example.app_pasteleria_mil_sabores.ui.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.viewmodel.FormularioViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.ProductoViewModel

@Composable
fun AdminHomeScreen(
    usuario: Usuario,
    viewModel: FormularioViewModel,
    productoViewModel: ProductoViewModel,
    onCerrarSesion: () -> Unit
) {
    val usuariosState = viewModel.usuarios.collectAsState()
    val usuarios = usuariosState.value

    // Cargar usuarios al entrar
    LaunchedEffect(Unit) {
        viewModel.mostrarUsuarios()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Panel de Administración",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Administrador: ${usuario.nombre}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = "Admin",
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Estadísticas rápidas
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Resumen del Sistema",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Total de usuarios: ${usuarios.size}",
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = "Administradores: ${usuarios.count { it.tipoUsuario == "Administrador" }}"
                )
                Text(
                    text = "Clientes: ${usuarios.count { it.tipoUsuario == "Cliente" }}"
                )
            }
        }

        // Lista de usuarios
        Text(
            text = "Usuarios registrados:",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(usuarios) { usuarioItem ->
                UserCard(
                    usuario = usuarioItem,
                    isCurrentUser = usuarioItem.nombre == usuario.nombre
                )
            }
        }

        // Botón cerrar sesión
        Button(
            onClick = {
                viewModel.cerrarSesion()
                onCerrarSesion()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Cerrar Sesión")
        }
    }
}

@Composable
fun UserCard(usuario: Usuario, isCurrentUser: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentUser) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = usuario.nombre,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Tipo: ${usuario.tipoUsuario}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isCurrentUser) {
                Text(
                    text = "Tú",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}