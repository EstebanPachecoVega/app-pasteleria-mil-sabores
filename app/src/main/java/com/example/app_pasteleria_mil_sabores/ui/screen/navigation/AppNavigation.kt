package com.example.app_pasteleria_mil_sabores.ui.screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.mutableStateListOf
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
    val context = LocalContext.current

    // Pila de navegación para manejar el back button
    val navigationStack = remember { mutableStateListOf(Pantallas.LOGIN) }

    // Función para navegar a una nueva pantalla
    fun navigateTo(screen: Pantallas) {
        navigationStack.add(screen)
        pantallaActual = screen
    }

    // Función para retroceder
    fun navigateBack() {
        if (navigationStack.size > 1) {
            navigationStack.removeLast()
            pantallaActual = navigationStack.last()
        } else {
            // Si estamos en la pantalla principal y no hay más pantallas, cerrar la app
            if (pantallaActual == Pantallas.PRINCIPAL || pantallaActual == Pantallas.ADMIN_HOME) {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            } else {
                navigateTo(Pantallas.LOGIN)
            }
        }
    }

    // Función para actualizar el usuario logueado
    fun actualizarUsuarioLogueado(usuarioActualizado: Usuario) {
        usuarioLogueado = usuarioActualizado
    }

    // Función para cerrar sesión
    fun cerrarSesion() {
        usuarioLogueado = null
        pantallaActual = Pantallas.LOGIN
        navigationStack.clear()
        navigationStack.add(Pantallas.LOGIN)
        viewModel.cerrarSesion()
        carritoViewModel.limpiarCarrito()
        perfilViewModel.resetearContadores()
        perfilViewModel.limpiarEstado()
    }

    // BackHandler global
    BackHandler (enabled = true) {
        navigateBack()
    }

    // Debug para ver la navegación
    LaunchedEffect(pantallaActual) {
        println("DEBUG - Pantalla actual: $pantallaActual")
        println("DEBUG - Usuario logueado: ${usuarioLogueado?.username} - Tipo: ${usuarioLogueado?.tipoUsuario}")
        println("DEBUG - Pila de navegación: $navigationStack")
    }

    LaunchedEffect(usuarioLogueado) {
        // Limpiar estado del perfil cuando cambia el usuario
        if (usuarioLogueado != null) {
            perfilViewModel.limpiarEstado()
        }
    }

    when (pantallaActual) {
        Pantallas.LOGIN -> LoginScreen(
            viewModel = viewModel,
            onRegistrarClick = { navigateTo(Pantallas.REGISTRO) },
            onLoginExitoso = { usuario ->
                usuarioLogueado = usuario
                // Redirigir según tipo de usuario
                when (usuario.tipoUsuario) {
                    "Administrador" -> navigateTo(Pantallas.ADMIN_HOME)
                    else -> navigateTo(Pantallas.PRINCIPAL)
                }
            },
            onBackPressed = {
                // En login, si presionan back, minimizar la app
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        )

        Pantallas.REGISTRO -> RegistroScreen(
            viewModel = viewModel,
            onRegistroExitoso = {
                navigateTo(Pantallas.LOGIN)
                viewModel.limpiarError()
            },
            onVolver = {
                navigateBack()
                viewModel.limpiarError()
            },
            onBackPressed = { navigateBack() }
        )

        Pantallas.PRINCIPAL -> {
            usuarioLogueado?.let { usuario ->
                ClienteHomeScreen(
                    usuario = usuario,
                    viewModel = viewModel,
                    productoViewModel = productoViewModel,
                    carritoViewModel = carritoViewModel,
                    onCerrarSesion = { cerrarSesion() },
                    onVerPerfil = { navigateTo(Pantallas.PERFIL) },
                    onVerCarrito = { navigateTo(Pantallas.CARRITO) },
                    onVerPedidos = { /* Navegar a pedidos */ },
                    onVerSoporte = { /* Navegar a soporte */ },
                    onVerDetalleProducto = { producto ->
                        productoSeleccionado = producto
                        navigateTo(Pantallas.DETALLE_PRODUCTO)
                    },
                    onBackPressed = {
                        // En Home, el back button minimiza la app
                        val intent = Intent(Intent.ACTION_MAIN)
                        intent.addCategory(Intent.CATEGORY_HOME)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                    }
                )
            } ?: run {
                navigateTo(Pantallas.LOGIN)
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
                        navigateTo(Pantallas.ADMIN_PRODUCTOS)
                    },
                    onBackPressed = {
                        // En Admin Home, el back button minimiza la app
                        val intent = Intent(Intent.ACTION_MAIN)
                        intent.addCategory(Intent.CATEGORY_HOME)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                    }
                )
            } ?: run {
                navigateTo(Pantallas.LOGIN)
            }
        }

        Pantallas.ADMIN_PRODUCTOS -> {
            usuarioLogueado?.let { usuario ->
                AdminProductosScreen(
                    usuario = usuario,
                    viewModel = viewModel,
                    productoViewModel = productoViewModel,
                    onVolver = { navigateBack() },
                    onAgregarProducto = { navigateTo(Pantallas.AGREGAR_PRODUCTO) },
                    onBackPressed = { navigateBack() }
                )
            } ?: run {
                navigateTo(Pantallas.LOGIN)
            }
        }

        Pantallas.AGREGAR_PRODUCTO -> {
            AgregarProductoScreen(
                productoViewModel = productoViewModel,
                onCancelar = { navigateBack() },
                onGuardarExitoso = { navigateBack() },
                onBackPressed = { navigateBack() }
            )
        }

        Pantallas.DETALLE_PRODUCTO -> {
            productoSeleccionado?.let { producto ->
                DetalleProductoScreen(
                    producto = producto,
                    onVolver = { navigateBack() },
                    carritoViewModel = carritoViewModel,
                    proximamente = false,
                    onBackPressed = { navigateBack() }
                )
            } ?: run {
                navigateTo(Pantallas.PRINCIPAL)
            }
        }

        Pantallas.CARRITO -> {
            CarritoScreen(
                onVolver = { navigateBack() },
                onContinuarCompra = { navigateTo(Pantallas.PRINCIPAL) },
                onCheckout = {
                    println("DEBUG - Navegando a checkout")
                    // Futuro: navegar a pantalla de checkout
                },
                viewModel = carritoViewModel,
                usuarioActual = usuarioLogueado,
                onBackPressed = { navigateBack() }
            )
        }

        Pantallas.PERFIL -> {
            usuarioLogueado?.let { usuario ->
                PerfilScreen(
                    usuario = usuario,
                    viewModel = perfilViewModel,
                    onVolver = { navigateBack() },
                    onUsuarioActualizado = { usuarioActualizado ->
                        // Actualizar el usuario en el estado global
                        actualizarUsuarioLogueado(usuarioActualizado)
                    },
                    onBackPressed = { navigateBack() }
                )
            } ?: run {
                navigateTo(Pantallas.LOGIN)
            }
        }
    }
}