package com.example.app_pasteleria_mil_sabores.ui.screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.ui.screen.admin.AdminHomeScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.auth.LoginScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.auth.RegistroScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.cliente.ClienteHomeScreen
import com.example.app_pasteleria_mil_sabores.viewmodel.FormularioViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.ProductoViewModel

enum class Pantallas {
    LOGIN, REGISTRO, PRINCIPAL
}

@Composable
fun AppNavigation(
    viewModel: FormularioViewModel,
    productoViewModel: ProductoViewModel
) {
    var pantallaActual by remember { mutableStateOf(Pantallas.LOGIN) }
    var usuarioLogueado by remember { mutableStateOf<Usuario?>(null) }

    when (pantallaActual) {
        Pantallas.LOGIN -> LoginScreen(
            viewModel = viewModel,
            onRegistrarClick = { pantallaActual = Pantallas.REGISTRO },
            onLoginExitoso = { usuario ->
                usuarioLogueado = usuario
                pantallaActual = Pantallas.PRINCIPAL
            }
        )

        Pantallas.REGISTRO -> RegistroScreen(
            viewModel = viewModel,
            onRegistroExitoso = {
                pantallaActual = Pantallas.LOGIN
                viewModel.limpiarError()
            },
            onVolver = {
                pantallaActual = Pantallas.LOGIN
                viewModel.limpiarError()
            }
        )

        Pantallas.PRINCIPAL -> {
            usuarioLogueado?.let { usuario ->
                when (usuario.tipoUsuario) {
                    "Administrador" -> AdminHomeScreen(
                        usuario = usuario,
                        viewModel = viewModel,
                        productoViewModel = productoViewModel,
                        onCerrarSesion = {
                            usuarioLogueado = null
                            pantallaActual = Pantallas.LOGIN
                            viewModel.cerrarSesion()
                        }
                    )
                    else -> ClienteHomeScreen(
                        usuario = usuario,
                        viewModel = viewModel,
                        productoViewModel = productoViewModel,
                        onCerrarSesion = {
                            usuarioLogueado = null
                            pantallaActual = Pantallas.LOGIN
                            viewModel.cerrarSesion()
                        },
                        onVerPerfil = { /* Navegar a perfil */ },
                        onVerCarrito = { /* Navegar a carrito */ },
                        onVerPedidos = { /* Navegar a pedidos */ },
                        onVerSoporte = { /* Navegar a soporte */ }
                    )
                }
            } ?: run {
                pantallaActual = Pantallas.LOGIN
            }
        }
    }
}