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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app_pasteleria_mil_sabores.model.Producto
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.ui.screen.admin.AdminHomeScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.admin.AdminProductosScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.admin.AgregarProductoScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.auth.LoginScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.auth.RegistroScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.checkout.ResumenPedidoScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.cliente.CarritoScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.cliente.ClienteHomeScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.cliente.DetalleProductoScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.cliente.PerfilScreen
import com.example.app_pasteleria_mil_sabores.viewmodel.CarritoViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.CheckoutViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.FormularioViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.PerfilViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.ProductoViewModel

enum class Pantallas {
    LOGIN,
    REGISTRO,
    PRINCIPAL,
    DETALLE_PRODUCTO,
    CARRITO,
    RESUMEN_PEDIDO,
    INFORMACION_ENVIO,
    PAGO,
    CONFIRMACION_PEDIDO,
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
    perfilViewModel: PerfilViewModel,
    checkoutViewModel: CheckoutViewModel
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
            navigationStack.removeAt(navigationStack.lastIndex)
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

    // Efecto para cargar foto de perfil cuando cambia el usuario
    LaunchedEffect(usuarioLogueado?.id) {
        usuarioLogueado?.let { usuario ->
            perfilViewModel.cargarFotoPerfil(usuario.id)
        }
    }

    // Función para actualizar el usuario logueado
    fun actualizarUsuarioLogueado(usuarioActualizado: Usuario) {
        usuarioLogueado = usuarioActualizado
        // Forzar recarga de la foto de perfil
        perfilViewModel.cargarFotoPerfil(usuarioActualizado.id)
    }

    // Función para cerrar sesión (MEJORADA)
    fun cerrarSesion() {
        val usuarioAnterior = usuarioLogueado
        usuarioLogueado = null
        pantallaActual = Pantallas.LOGIN
        navigationStack.clear()
        navigationStack.add(Pantallas.LOGIN)
        viewModel.cerrarSesion()
        carritoViewModel.limpiarCarrito()
        perfilViewModel.resetearContadores()
        perfilViewModel.limpiarEstado()

        // Limpiar cache específico del usuario anterior
        usuarioAnterior?.let {
            // Aquí podrías añadir lógica para limpiar cache de Coil si es necesario
            println("DEBUG - Sesión cerrada para usuario: ${it.username}")
        }
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
        println("DEBUG - Foto de perfil del usuario: ${usuarioLogueado?.fotoPerfil}")
    }

    LaunchedEffect(usuarioLogueado) {
        // Limpiar estado del perfil cuando cambia el usuario
        if (usuarioLogueado != null) {
            perfilViewModel.limpiarEstado()
            // Cargar foto de perfil del nuevo usuario
            perfilViewModel.cargarFotoPerfil(usuarioLogueado!!.id)
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
                onCheckout = { navigateTo(Pantallas.RESUMEN_PEDIDO) },
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
        Pantallas.RESUMEN_PEDIDO -> {
            usuarioLogueado?.let { usuario ->
                // Obtener los valores del carrito directamente
                val cartItems = carritoViewModel.cartItems.collectAsState().value
                val resumen = carritoViewModel.resumenCarrito.collectAsState().value

                // Inicializar el pedido en el ViewModel
                LaunchedEffect(cartItems, resumen) {
                    if (cartItems.isNotEmpty()) {
                        checkoutViewModel.inicializarPedido(
                            carritoItems = cartItems,
                            resumen = resumen,
                            usuario = usuario
                        )
                    }
                }

                ResumenPedidoScreen(
                    carritoViewModel = carritoViewModel,
                    usuario = usuario,
                    onVolver = { navigateBack() },
                    onContinuarEnvio = { navigateTo(Pantallas.INFORMACION_ENVIO) },
                    onBackPressed = { navigateBack() }
                )
            } ?: run {
                navigateTo(Pantallas.LOGIN)
            }
        }
        Pantallas.INFORMACION_ENVIO -> {
            // Por ahora, placeholder - lo implementaremos después
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Información de Envío - Próximamente")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button (onClick = { navigateBack() }) {
                        Text("Volver")
                    }
                }
            }
        }

        Pantallas.PAGO -> {
            // Por ahora, placeholder - lo implementaremos después
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Pago - Próximamente")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navigateBack() }) {
                        Text("Volver")
                    }
                }
            }
        }

        Pantallas.CONFIRMACION_PEDIDO -> {
            // Por ahora, placeholder - lo implementaremos después
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Confirmación de Pedido - Próximamente")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        // Ir al inicio (PRINCIPAL)
                        navigationStack.clear()
                        navigationStack.add(Pantallas.PRINCIPAL)
                        pantallaActual = Pantallas.PRINCIPAL
                    }) {
                        Text("Ir al Inicio")
                    }
                }
            }
        }
    }
}