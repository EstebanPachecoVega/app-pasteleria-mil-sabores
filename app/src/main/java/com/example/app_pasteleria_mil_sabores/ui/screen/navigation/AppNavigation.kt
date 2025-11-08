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
import com.example.app_pasteleria_mil_sabores.ui.screen.admin.AdminProductosScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.admin.AgregarProductoScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.auth.LoginScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.auth.RegistroScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.cliente.CarritoScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.cliente.ClienteHomeScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.cliente.DetalleProductoScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.cliente.PerfilScreen
import com.example.app_pasteleria_mil_sabores.viewmodel.CarritoViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.FormularioViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.PerfilViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.ProductoViewModel

enum class Pantallas {
    LOGIN,
    REGISTRO,
    PRINCIPAL,
    DETALLE_PRODUCTO,
    CARRITO,
    PERFIL,
    ADMIN_HOME,
    ADMIN_PRODUCTOS,
    AGREGAR_PRODUCTO
}

@Composable
fun AppNavigation(
    viewModel: FormularioViewModel,
    productoViewModel: ProductoViewModel,
    carritoViewModel: CarritoViewModel,
    perfilViewModel: PerfilViewModel
) {
    var pantallaActual by remember { mutableStateOf(Pantallas.LOGIN) }
    var usuarioLogueado by remember { mutableStateOf<Usuario?>(null) }
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }

    // Función para actualizar el usuario logueado
    fun actualizarUsuarioLogueado(usuarioActualizado: Usuario) {
        usuarioLogueado = usuarioActualizado
    }

    // Función para cerrar sesión
    fun cerrarSesion() {
        usuarioLogueado = null
        pantallaActual = Pantallas.LOGIN
        viewModel.cerrarSesion()
        carritoViewModel.limpiarCarrito()
        perfilViewModel.resetearContadores() // Resetear contadores al cerrar sesión
    }

    LaunchedEffect(pantallaActual) {
        println("DEBUG - Pantalla actual: $pantallaActual")
        println("DEBUG - Usuario logueado: ${usuarioLogueado?.username} - Tipo: ${usuarioLogueado?.tipoUsuario}")
    }

    when (pantallaActual) {
        Pantallas.LOGIN -> LoginScreen(
            viewModel = viewModel,
            onRegistrarClick = { pantallaActual = Pantallas.REGISTRO },
            onLoginExitoso = { usuario ->
                usuarioLogueado = usuario
                // Redirigir según tipo de usuario
                when (usuario.tipoUsuario) {
                    "Administrador" -> pantallaActual = Pantallas.ADMIN_HOME
                    else -> pantallaActual = Pantallas.PRINCIPAL
                }
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
                ClienteHomeScreen(
                    usuario = usuario,
                    viewModel = viewModel,
                    productoViewModel = productoViewModel,
                    carritoViewModel = carritoViewModel,
                    onCerrarSesion = { cerrarSesion() },
                    onVerPerfil = {
                        pantallaActual = Pantallas.PERFIL
                    },
                    onVerCarrito = {
                        pantallaActual = Pantallas.CARRITO
                    },
                    onVerPedidos = { /* Navegar a pedidos */ },
                    onVerSoporte = { /* Navegar a soporte */ },
                    onVerDetalleProducto = { producto ->
                        productoSeleccionado = producto
                        pantallaActual = Pantallas.DETALLE_PRODUCTO
                    }
                )
            } ?: run {
                pantallaActual = Pantallas.LOGIN
            }
        }

        Pantallas.ADMIN_HOME -> {
            usuarioLogueado?.let { usuario ->
                AdminHomeScreen(
                    usuario = usuario,
                    viewModel = viewModel,
                    productoViewModel = productoViewModel,
                    onCerrarSesion = { cerrarSesion() },
                    onGestionarProductos = {
                        pantallaActual = Pantallas.ADMIN_PRODUCTOS
                    }
                )
            } ?: run {
                pantallaActual = Pantallas.LOGIN
            }
        }

        Pantallas.ADMIN_PRODUCTOS -> {
            usuarioLogueado?.let { usuario ->
                AdminProductosScreen(
                    usuario = usuario,
                    viewModel = viewModel,
                    productoViewModel = productoViewModel,
                    onVolver = {
                        pantallaActual = Pantallas.ADMIN_HOME
                    },
                    onAgregarProducto = {
                        pantallaActual = Pantallas.AGREGAR_PRODUCTO
                    }
                )
            } ?: run {
                pantallaActual = Pantallas.LOGIN
            }
        }

        Pantallas.AGREGAR_PRODUCTO -> {
            AgregarProductoScreen(
                productoViewModel = productoViewModel,
                onCancelar = {
                    // Volver a la gestión de productos
                    pantallaActual = Pantallas.ADMIN_PRODUCTOS
                },
                onGuardarExitoso = {
                    // Volver a la gestión de productos después de guardar
                    pantallaActual = Pantallas.ADMIN_PRODUCTOS
                }
            )
        }

        Pantallas.DETALLE_PRODUCTO -> {
            productoSeleccionado?.let { producto ->
                DetalleProductoScreen(
                    producto = producto,
                    onVolver = {
                        pantallaActual = Pantallas.PRINCIPAL
                    },
                    carritoViewModel = carritoViewModel,
                    proximamente = false
                )
            } ?: run {
                pantallaActual = Pantallas.PRINCIPAL
            }
        }

        Pantallas.CARRITO -> {
            CarritoScreen(
                onVolver = {
                    pantallaActual = Pantallas.PRINCIPAL
                },
                onContinuarCompra = {
                    pantallaActual = Pantallas.PRINCIPAL
                },
                onCheckout = {
                    println("DEBUG - Navegando a checkout")
                    // Futuro: navegar a pantalla de checkout
                },
                viewModel = carritoViewModel,
                usuarioActual = usuarioLogueado
            )
        }

        Pantallas.PERFIL -> {
            usuarioLogueado?.let { usuario ->
                PerfilScreen(
                    usuario = usuario,
                    viewModel = perfilViewModel,
                    onVolver = {
                        pantallaActual = Pantallas.PRINCIPAL
                    },
                    onUsuarioActualizado = { usuarioActualizado ->
                        // Actualizar el usuario en el estado global
                        actualizarUsuarioLogueado(usuarioActualizado)
                    }
                )
            } ?: run {
                pantallaActual = Pantallas.LOGIN
            }
        }
    }
}