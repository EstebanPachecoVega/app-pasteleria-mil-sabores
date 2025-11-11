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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.app_pasteleria_mil_sabores.data.PedidoRepository
import com.example.app_pasteleria_mil_sabores.model.Producto
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.ui.screen.admin.AdminHomeScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.admin.AdminProductosScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.admin.AgregarProductoScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.admin.AdminPedidosScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.admin.AdminUsuariosScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.admin.AdminReportesScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.admin.EditarProductoScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.auth.LoginScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.auth.RegistroScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.checkout.ConfirmacionPedidoScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.checkout.InformacionEnvioScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.checkout.PagoScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.checkout.ResumenPedidoScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.cliente.CarritoScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.cliente.ClienteHomeScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.cliente.DetallePedidoScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.cliente.DetalleProductoScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.cliente.MisPedidosScreen
import com.example.app_pasteleria_mil_sabores.ui.screen.cliente.PerfilScreen
import com.example.app_pasteleria_mil_sabores.viewmodel.CarritoViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.CheckoutViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.FormularioViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.PedidoViewModel
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
    AGREGAR_PRODUCTO,
    EDITAR_PRODUCTO,
    ADMIN_PEDIDOS,
    ADMIN_USUARIOS,
    ADMIN_REPORTES,
    RESUMEN_PEDIDO,
    INFORMACION_ENVIO,
    PAGO,
    CONFIRMACION_PEDIDO,
    MIS_PEDIDOS,
    DETALLE_PEDIDO
}

@Composable
fun AppNavigation(
    viewModel: FormularioViewModel,
    productoViewModel: ProductoViewModel,
    carritoViewModel: CarritoViewModel,
    perfilViewModel: PerfilViewModel,
    checkoutViewModel: CheckoutViewModel,
    pedidoViewModel: PedidoViewModel
) {
    var pantallaActual by remember { mutableStateOf(Pantallas.LOGIN) }
    var usuarioLogueado by remember { mutableStateOf<Usuario?>(null) }
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }
    var pedidoSeleccionadoId by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Obtener estados del carrito
    val cartItems by carritoViewModel.cartItems.collectAsState()
    val resumen by carritoViewModel.resumenCarrito.collectAsState()

    // Pila de navegación para manejar el back button
    val navigationStack: SnapshotStateList<Pantallas> = remember { mutableStateListOf(Pantallas.LOGIN) }

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
        checkoutViewModel.limpiarCheckout()
        pedidoViewModel.limpiarPedidoSeleccionado()

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
                    onVerPedidos = { navigateTo(Pantallas.MIS_PEDIDOS) },
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
                    onGestionarPedidos = {
                        navigateTo(Pantallas.ADMIN_PEDIDOS)
                    },
                    onGestionarUsuarios = {
                        navigateTo(Pantallas.ADMIN_USUARIOS)
                    },
                    onVerReportes = {
                        navigateTo(Pantallas.ADMIN_REPORTES)
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
                    onEditarProducto = { producto ->
                        productoSeleccionado = producto
                        navigateTo(Pantallas.EDITAR_PRODUCTO)
                    },
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

        Pantallas.EDITAR_PRODUCTO -> {
            productoSeleccionado?.let { producto ->
                EditarProductoScreen(
                    producto = producto,
                    productoViewModel = productoViewModel,
                    onCancelar = { navigateBack() },
                    onGuardarExitoso = { navigateBack() },
                    onBackPressed = { navigateBack() }
                )
            } ?: run {
                navigateTo(Pantallas.ADMIN_PRODUCTOS)
            }
        }

        Pantallas.ADMIN_PEDIDOS -> {
            usuarioLogueado?.let { usuario ->
                AdminPedidosScreen(
                    usuario = usuario,
                    pedidoViewModel = pedidoViewModel,
                    onVolver = { navigateBack() },
                    onBackPressed = { navigateBack() }
                )
            } ?: run {
                navigateTo(Pantallas.LOGIN)
            }
        }

        Pantallas.ADMIN_USUARIOS -> {
            usuarioLogueado?.let { usuario ->
                AdminUsuariosScreen(
                    usuario = usuario,
                    viewModel = viewModel,
                    onVolver = { navigateBack() },
                    onBackPressed = { navigateBack() }
                )
            } ?: run {
                navigateTo(Pantallas.LOGIN)
            }
        }

        Pantallas.ADMIN_REPORTES -> {
            AdminReportesScreen(
                onVolver = { navigateBack() },
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
                // Inicializar el pedido en el ViewModel
                LaunchedEffect(cartItems, resumen) {
                    checkoutViewModel.inicializarPedido(
                        carritoItems = cartItems,
                        resumen = resumen,
                        usuario = usuario
                    )
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
            usuarioLogueado?.let { usuario ->
                InformacionEnvioScreen(
                    checkoutViewModel = checkoutViewModel,
                    usuario = usuario,
                    onVolver = { navigateBack() },
                    onContinuarPago = { navigateTo(Pantallas.PAGO) },
                    onBackPressed = { navigateBack() }
                )
            } ?: run {
                navigateTo(Pantallas.LOGIN)
            }
        }

        Pantallas.PAGO -> {
            usuarioLogueado?.let { usuario ->
                PagoScreen(
                    checkoutViewModel = checkoutViewModel,
                    usuario = usuario,
                    onVolver = { navigateBack() },
                    onConfirmarPedido = { navigateTo(Pantallas.CONFIRMACION_PEDIDO) },
                    onBackPressed = { navigateBack() }
                )
            } ?: run {
                navigateTo(Pantallas.LOGIN)
            }
        }

        Pantallas.CONFIRMACION_PEDIDO -> {
            usuarioLogueado?.let { usuario ->
                ConfirmacionPedidoScreen(
                    checkoutViewModel = checkoutViewModel,
                    usuario = usuario,
                    onIrAlInicio = {
                        // Limpiar carrito y volver al inicio
                        carritoViewModel.limpiarCarrito()
                        checkoutViewModel.limpiarCheckout()
                        navigateTo(Pantallas.PRINCIPAL)
                    },
                    onBackPressed = { navigateBack() }
                )
            } ?: run {
                navigateTo(Pantallas.LOGIN)
            }
        }

        Pantallas.MIS_PEDIDOS -> {
            usuarioLogueado?.let { usuario ->
                MisPedidosScreen (
                    pedidoViewModel = pedidoViewModel,
                    usuario = usuario,
                    onVolver = { navigateBack() },
                    onVerDetallePedido = { pedidoId ->
                        pedidoSeleccionadoId = pedidoId
                        navigateTo(Pantallas.DETALLE_PEDIDO)
                    },
                    onBackPressed = { navigateBack() }
                )
            } ?: run {
                navigateTo(Pantallas.LOGIN)
            }
        }

        Pantallas.DETALLE_PEDIDO -> {
            pedidoSeleccionadoId?.let { id ->
                DetallePedidoScreen (
                    pedidoId = id,
                    pedidoViewModel = pedidoViewModel,
                    onVolver = { navigateBack() },
                    onBackPressed = { navigateBack() }
                )
            } ?: run {
                navigateTo(Pantallas.MIS_PEDIDOS)
            }
        }
    }
}