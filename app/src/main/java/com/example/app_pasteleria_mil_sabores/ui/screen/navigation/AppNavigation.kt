package com.example.app_pasteleria_mil_sabores.ui.screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.app_pasteleria_mil_sabores.model.Producto
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.ui.screen.admin.AdminHomeScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.auth.LoginScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.auth.RegistroScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.cliente.CarritoScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.cliente.ClienteHomeScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.cliente.DetalleProductoScreen
import com.example.app_pasteleria_mil_sabores.viewmodel.CarritoViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.FormularioViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.ProductoViewModel

enum class Pantallas {
    LOGIN, REGISTRO, PRINCIPAL, DETALLE_PRODUCTO, CARRITO
}

@Composable
fun AppNavigation(
    viewModel: FormularioViewModel,
    productoViewModel: ProductoViewModel,
    carritoViewModel: CarritoViewModel // Agregar CarritoViewModel
) {
    var pantallaActual by remember { mutableStateOf(Pantallas.LOGIN) }
    var usuarioLogueado by remember { mutableStateOf<Usuario?>(null) }
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }

    // Debug
    LaunchedEffect (pantallaActual, productoSeleccionado) {
        println("DEBUG - Pantalla actual: $pantallaActual")
        println("DEBUG - Producto seleccionado: ${productoSeleccionado?.nombre}")
    }

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
                            carritoViewModel.limpiarCarrito() // Limpiar carrito al cerrar sesión
                        }
                    )
                    else -> ClienteHomeScreen(
                        usuario = usuario,
                        viewModel = viewModel,
                        productoViewModel = productoViewModel,
                        carritoViewModel = carritoViewModel, // Pasar CarritoViewModel
                        onCerrarSesion = {
                            usuarioLogueado = null
                            pantallaActual = Pantallas.LOGIN
                            viewModel.cerrarSesion()
                            carritoViewModel.limpiarCarrito() // Limpiar carrito al cerrar sesión
                        },
                        onVerPerfil = { /* Navegar a perfil */ },
                        onVerCarrito = {
                            pantallaActual = Pantallas.CARRITO // Navegar a carrito
                        },
                        onVerPedidos = { /* Navegar a pedidos */ },
                        onVerSoporte = { /* Navegar a soporte */ },
                        onVerDetalleProducto = { producto ->
                            productoSeleccionado = producto
                            pantallaActual = Pantallas.DETALLE_PRODUCTO
                        }
                    )
                }
            } ?: run {
                pantallaActual = Pantallas.LOGIN
            }
        }

        Pantallas.DETALLE_PRODUCTO -> {
            println("DEBUG - Entrando a DETALLE_PRODUCTO")
            println("DEBUG - Producto: $productoSeleccionado")

            productoSeleccionado?.let { producto ->
                println("DEBUG - Producto encontrado: ${producto.nombre}")
                DetalleProductoScreen(
                    producto = producto,
                    onVolver = {
                        println("DEBUG - Volviendo desde detalle")
                        pantallaActual = Pantallas.PRINCIPAL
                    },
                    carritoViewModel = carritoViewModel // Pasar CarritoViewModel
                )
            } ?: run {
                println("DEBUG - ERROR: Producto es null, volviendo a PRINCIPAL")
                pantallaActual = Pantallas.PRINCIPAL
            }
        }

        Pantallas.CARRITO -> {
            CarritoScreen(
                onVolver = { pantallaActual = Pantallas.PRINCIPAL },
                onContinuarCompra = { pantallaActual = Pantallas.PRINCIPAL },
                onCheckout = {
                    println("DEBUG - Navegando a checkout")
                },
                viewModel = carritoViewModel,
                usuarioActual = usuarioLogueado // ← Agregar esta línea
            )
        }
    }
}