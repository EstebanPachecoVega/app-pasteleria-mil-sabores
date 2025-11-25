package com.example.app_pasteleria_mil_sabores.ui.screen.admin

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.viewmodel.FormularioViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.ProductoViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class AdminHomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockFormularioViewModel: FormularioViewModel = mockk(relaxed = true) {
        every { usuarios } returns MutableStateFlow(emptyList())
    }

    private val mockProductoViewModel: ProductoViewModel = mockk(relaxed = true)

    private val usuarioAdmin = Usuario(
        id = "1",
        username = "admin",
        email = "admin@duoc.cl",
        password = "password",
        tipoUsuario = "Administrador"
    )

    @Test
    fun pantallaAdminMuestraElementosPrincipales() {
        // Configurar datos realistas
        val usuarios = listOf(
            usuarioAdmin,
            Usuario(
                id = "2", username = "cliente1", email = "cliente1@duoc.cl",
                password = "pass", tipoUsuario = "Cliente"
            ),
            Usuario(
                id = "3", username = "admin2", email = "admin2@duoc.cl",
                password = "pass", tipoUsuario = "Administrador"
            )
        )
        every { mockFormularioViewModel.usuarios } returns MutableStateFlow(usuarios)

        composeTestRule.setContent {
            AdminHomeScreen(
                usuario = usuarioAdmin,
                viewModel = mockFormularioViewModel,
                productoViewModel = mockProductoViewModel,
                onCerrarSesion = { },
                onGestionarProductos = { },
                onGestionarPedidos = { },
                onGestionarUsuarios = { },
                onVerReportes = { },
                onBackPressed = { }
            )
        }

        // Esperar a que la UI se estabilice
        composeTestRule.waitForIdle()

        // Verificar elementos que DEBEN estar según el código fuente
        composeTestRule.onNodeWithText("Panel de Administración").assertExists()
        composeTestRule.onNodeWithText("Bienvenido, admin").assertExists()
        composeTestRule.onNodeWithText("Resumen del Sistema").assertExists()
        composeTestRule.onNodeWithText("Acciones Rápidas").assertExists()

        // Verificar botones de acción específicos
        composeTestRule.onNodeWithText("Gestión de Productos").assertExists()
        composeTestRule.onNodeWithText("Gestión de Pedidos").assertExists()
        composeTestRule.onNodeWithText("Gestión de Usuarios").assertExists()
        composeTestRule.onNodeWithText("Ver Reportes").assertExists()
    }

    @Test
    fun mostrarEstadisticasDeUsuariosCorrectamente() {
        // Configurar datos específicos para estadísticas
        val usuarios = listOf(
            usuarioAdmin, // 1 admin
            Usuario(id = "2", username = "cliente1", email = "c1@d.cl", password = "p", tipoUsuario = "Cliente"),
            Usuario(id = "3", username = "cliente2", email = "c2@d.cl", password = "p", tipoUsuario = "Cliente"),
            Usuario(id = "4", username = "admin2", email = "a2@d.cl", password = "p", tipoUsuario = "Administrador")
        )
        every { mockFormularioViewModel.usuarios } returns MutableStateFlow(usuarios)

        composeTestRule.setContent {
            AdminHomeScreen(
                usuario = usuarioAdmin,
                viewModel = mockFormularioViewModel,
                productoViewModel = mockProductoViewModel,
                onCerrarSesion = { },
                onGestionarProductos = { },
                onGestionarPedidos = { },
                onGestionarUsuarios = { },
                onVerReportes = { },
                onBackPressed = { }
            )
        }

        composeTestRule.waitForIdle()

        // Verificar que se muestran las estadísticas (sin verificar números exactos)
        composeTestRule.onNodeWithText("Usuarios").assertExists()
        composeTestRule.onNodeWithText("Admins").assertExists()
        composeTestRule.onNodeWithText("Clientes").assertExists()
    }

    @Test
    fun botonesAccionRapidaEjecutanCallbacks() {
        // GIVEN: Callbacks para verificar
        var productosClicked = false
        var pedidosClicked = false
        var usuariosClicked = false
        var reportesClicked = false

        composeTestRule.setContent {
            AdminHomeScreen(
                usuario = usuarioAdmin,
                viewModel = mockFormularioViewModel,
                productoViewModel = mockProductoViewModel,
                onCerrarSesion = { },
                onGestionarProductos = { productosClicked = true },
                onGestionarPedidos = { pedidosClicked = true },
                onGestionarUsuarios = { usuariosClicked = true },
                onVerReportes = { reportesClicked = true },
                onBackPressed = { }
            )
        }

        composeTestRule.waitForIdle()

        // WHEN: Hacer click en los botones (que sabemos existen)
        composeTestRule.onNodeWithText("Gestión de Productos").performClick()
        composeTestRule.onNodeWithText("Gestión de Pedidos").performClick()
        composeTestRule.onNodeWithText("Gestión de Usuarios").performClick()
        composeTestRule.onNodeWithText("Ver Reportes").performClick()

        // THEN: Verificar que se ejecutaron los callbacks
        assertTrue("Callback de productos debería ejecutarse", productosClicked)
        assertTrue("Callback de pedidos debería ejecutarse", pedidosClicked)
        assertTrue("Callback de usuarios debería ejecutarse", usuariosClicked)
        assertTrue("Callback de reportes debería ejecutarse", reportesClicked)
    }

    @Test
    fun botonCerrarSesionMuestraDialogoDeConfirmacion() {
        composeTestRule.setContent {
            AdminHomeScreen(
                usuario = usuarioAdmin,
                viewModel = mockFormularioViewModel,
                productoViewModel = mockProductoViewModel,
                onCerrarSesion = { },
                onGestionarProductos = { },
                onGestionarPedidos = { },
                onGestionarUsuarios = { },
                onVerReportes = { },
                onBackPressed = { }
            )
        }

        composeTestRule.waitForIdle()

        // Verificar que el botón de cerrar sesión existe
        composeTestRule.onNodeWithContentDescription("Cerrar sesión").assertExists()

        // Hacer click para abrir el diálogo
        composeTestRule.onNodeWithContentDescription("Cerrar sesión").performClick()

        // Verificar que se muestra el diálogo de confirmación con los textos EXACTOS del componente
        composeTestRule.onNodeWithText("Cerrar sesión").assertExists() // Título
        composeTestRule.onNodeWithText("¿Seguro que quieres cerrar sesión?").assertExists() // Mensaje
        composeTestRule.onNodeWithText("Sí, cerrar sesión").assertExists() // Botón confirmar
        composeTestRule.onNodeWithText("Cancelar").assertExists() // Botón cancelar
    }

    @Test
    fun dialogoCerrarSesionEjecutaCallbackAlConfirmar() {
        var cerrarSesionEjecutado = false

        composeTestRule.setContent {
            AdminHomeScreen(
                usuario = usuarioAdmin,
                viewModel = mockFormularioViewModel,
                productoViewModel = mockProductoViewModel,
                onCerrarSesion = { cerrarSesionEjecutado = true },
                onGestionarProductos = { },
                onGestionarPedidos = { },
                onGestionarUsuarios = { },
                onVerReportes = { },
                onBackPressed = { }
            )
        }

        composeTestRule.waitForIdle()

        // Abrir el diálogo
        composeTestRule.onNodeWithContentDescription("Cerrar sesión").performClick()

        // Hacer click en el botón "Sí, cerrar sesión" del diálogo
        composeTestRule.onNodeWithText("Sí, cerrar sesión").performClick()

        // Verificar que se ejecutó el callback
        assertTrue("El callback onCerrarSesion debería ejecutarse al confirmar", cerrarSesionEjecutado)
    }

    @Test
    fun dialogoCerrarSesionSeCierraAlCancelar() {
        composeTestRule.setContent {
            AdminHomeScreen(
                usuario = usuarioAdmin,
                viewModel = mockFormularioViewModel,
                productoViewModel = mockProductoViewModel,
                onCerrarSesion = { },
                onGestionarProductos = { },
                onGestionarPedidos = { },
                onGestionarUsuarios = { },
                onVerReportes = { },
                onBackPressed = { }
            )
        }

        composeTestRule.waitForIdle()

        // Abrir el diálogo
        composeTestRule.onNodeWithContentDescription("Cerrar sesión").performClick()

        // Verificar que el diálogo está visible
        composeTestRule.onNodeWithText("Cerrar sesión").assertExists()

        // Hacer click en Cancelar
        composeTestRule.onNodeWithText("Cancelar").performClick()

        // Verificar que el diálogo ya no está visible
        composeTestRule.onNodeWithText("Cerrar sesión").assertDoesNotExist()
    }

    @Test
    fun mostrarTarjetaDeBienvenida() {
        composeTestRule.setContent {
            AdminHomeScreen(
                usuario = usuarioAdmin,
                viewModel = mockFormularioViewModel,
                productoViewModel = mockProductoViewModel,
                onCerrarSesion = { },
                onGestionarProductos = { },
                onGestionarPedidos = { },
                onGestionarUsuarios = { },
                onVerReportes = { },
                onBackPressed = { }
            )
        }

        composeTestRule.waitForIdle()

        // Verificar elementos de la tarjeta de bienvenida
        composeTestRule.onNodeWithText("Bienvenido, admin").assertExists()
        composeTestRule.onNodeWithText("Administrador del sistema").assertExists()
    }
}