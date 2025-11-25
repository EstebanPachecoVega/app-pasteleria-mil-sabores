package com.example.app_pasteleria_mil_sabores.ui.screen.admin

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_pasteleria_mil_sabores.model.*
import com.example.app_pasteleria_mil_sabores.viewmodel.PedidoViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class AdminPedidosScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockPedidoViewModel: PedidoViewModel = mockk(relaxed = true) {
        every { todosLosPedidos } returns MutableStateFlow(emptyList())
        every { cargando } returns MutableStateFlow(false)
        every { errorMessage } returns MutableStateFlow(null)
        every { cargarTodosLosPedidos() } returns Unit
    }

    private val usuarioAdmin = Usuario(
        id = "1", username = "admin", email = "admin@duoc.cl",
        password = "password", tipoUsuario = "Administrador"
    )

    private fun crearPedidoEjemplo(): Pedido {
        val producto = Producto(
            id = "1", nombre = "Torta Chocolate", descripcion = "Deliciosa torta",
            precio = 15000, imagen = "torta_chocolate", categoria = "tortas",
            stock = 10, destacado = true, activo = true
        )

        val cartItem = CartItem(producto = producto, cantidad = 2)

        val informacionContacto = InformacionContacto(
            nombre = "Juan Pérez", email = "juan@duoc.cl", telefono = "+56912345678"
        )

        return Pedido(
            id = "pedido_123",
            usuarioId = "user_123",
            productos = listOf(cartItem),
            estado = "Pendiente",
            fechaCreacion = System.currentTimeMillis(),
            subtotal = 30000,
            descuentoAplicado = 0,
            costoEnvio = 2000,
            total = 32000,
            direccionEnvio = null,
            metodoPago = "Tarjeta",
            informacionContacto = informacionContacto
        )
    }

    @Test
    fun pantallaPedidosMuestraElementosPrincipales() {
        // Configurar para estado vacío pero sin loading
        every { mockPedidoViewModel.todosLosPedidos } returns MutableStateFlow(emptyList())
        every { mockPedidoViewModel.cargando } returns MutableStateFlow(false)

        composeTestRule.setContent {
            AdminPedidosScreen(
                usuario = usuarioAdmin,
                pedidoViewModel = mockPedidoViewModel,
                onVolver = { },
                onBackPressed = { }
            )
        }

        composeTestRule.waitForIdle()

        // Verificar elementos que están SIEMPRE presentes
        composeTestRule.onNodeWithText("Gestión de Pedidos - Todos").assertExists()
        composeTestRule.onNodeWithContentDescription("Volver").assertExists()
        composeTestRule.onNodeWithContentDescription("Filtrar").assertExists()
    }

    @Test
    fun mostrarMensajeCuandoNoHayPedidos() {
        // GIVEN: Estado vacío y sin carga
        every { mockPedidoViewModel.todosLosPedidos } returns MutableStateFlow(emptyList())
        every { mockPedidoViewModel.cargando } returns MutableStateFlow(false)

        composeTestRule.setContent {
            AdminPedidosScreen(
                usuario = usuarioAdmin,
                pedidoViewModel = mockPedidoViewModel,
                onVolver = { },
                onBackPressed = { }
            )
        }

        composeTestRule.waitForIdle()

        // THEN: Debe mostrar mensaje de no hay pedidos
        composeTestRule.onNodeWithText("No hay pedidos en el sistema").assertExists()
    }

    @Test
    fun mostrarLoadingInicial() {
        // GIVEN: Estado de carga activo
        every { mockPedidoViewModel.cargando } returns MutableStateFlow(true)
        every { mockPedidoViewModel.todosLosPedidos } returns MutableStateFlow(emptyList())

        composeTestRule.setContent {
            AdminPedidosScreen(
                usuario = usuarioAdmin,
                pedidoViewModel = mockPedidoViewModel,
                onVolver = { },
                onBackPressed = { }
            )
        }

        composeTestRule.waitForIdle()

        // THEN: Debe mostrar indicador de carga
        composeTestRule.onNodeWithText("Cargando todos los pedidos...").assertExists()
    }

    @Test
    fun botonFiltroFunciona() {
        // Configurar estado básico
        every { mockPedidoViewModel.todosLosPedidos } returns MutableStateFlow(emptyList())
        every { mockPedidoViewModel.cargando } returns MutableStateFlow(false)

        composeTestRule.setContent {
            AdminPedidosScreen(
                usuario = usuarioAdmin,
                pedidoViewModel = mockPedidoViewModel,
                onVolver = { },
                onBackPressed = { }
            )
        }

        composeTestRule.waitForIdle()

        // WHEN: Hacer click en filtro
        composeTestRule.onNodeWithContentDescription("Filtrar").performClick()

        // THEN: No debería crashear - si llegamos aquí, pasa
        assertTrue(true)
    }

    @Test
    fun pantallaSeRenderizaSinCrashear() {
        // Configuración mínima
        every { mockPedidoViewModel.todosLosPedidos } returns MutableStateFlow(emptyList())
        every { mockPedidoViewModel.cargando } returns MutableStateFlow(false)

        composeTestRule.setContent {
            AdminPedidosScreen(
                usuario = usuarioAdmin,
                pedidoViewModel = mockPedidoViewModel,
                onVolver = { },
                onBackPressed = { }
            )
        }

        composeTestRule.waitForIdle()

        // Solo verificar que tenemos elementos de UI básicos
        val hasSomeUI = composeTestRule.onAllNodesWithContentDescription("Volver").fetchSemanticsNodes().isNotEmpty()
        assertTrue("La pantalla debería tener elementos de UI", hasSomeUI)
    }

    @Test
    fun mostrarEstadisticasDePedidos() {
        // GIVEN: Múltiples pedidos con diferentes estados
        val pedidos = listOf(
            crearPedidoEjemplo().copy(estado = "Pendiente"),
            crearPedidoEjemplo().copy(id = "pedido2", estado = "Confirmado"),
            crearPedidoEjemplo().copy(id = "pedido3", estado = "En preparación")
        )

        every { mockPedidoViewModel.todosLosPedidos } returns MutableStateFlow(pedidos)
        every { mockPedidoViewModel.cargando } returns MutableStateFlow(false)
        every { mockPedidoViewModel.errorMessage } returns MutableStateFlow(null)

        composeTestRule.setContent {
            AdminPedidosScreen(
                usuario = usuarioAdmin,
                pedidoViewModel = mockPedidoViewModel,
                onVolver = { },
                onBackPressed = { }
            )
        }

        composeTestRule.waitForIdle()

        // THEN: Verificar que se muestran estadísticas
        composeTestRule.onNodeWithText("Total de pedidos: 3").assertExists()
    }

    @Test
    fun elementosNavegacionSonAccesibles() {
        composeTestRule.setContent {
            AdminPedidosScreen(
                usuario = usuarioAdmin,
                pedidoViewModel = mockPedidoViewModel,
                onVolver = { },
                onBackPressed = { }
            )
        }

        composeTestRule.waitForIdle()

        // Verificar que los elementos de navegación existen y son accesibles
        composeTestRule.onNodeWithContentDescription("Volver").assertExists().assertIsEnabled()
        composeTestRule.onNodeWithContentDescription("Filtrar").assertExists().assertIsEnabled()
    }

    @Test
    fun tituloEsVisible() {
        composeTestRule.setContent {
            AdminPedidosScreen(
                usuario = usuarioAdmin,
                pedidoViewModel = mockPedidoViewModel,
                onVolver = { },
                onBackPressed = { }
            )
        }

        composeTestRule.waitForIdle()

        // Verificar que el título es visible y tiene el texto correcto
        composeTestRule.onNodeWithText("Gestión de Pedidos - Todos")
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun sinErroresAlCargar() {
        // GIVEN: Estado normal sin errores
        every { mockPedidoViewModel.todosLosPedidos } returns MutableStateFlow(emptyList())
        every { mockPedidoViewModel.cargando } returns MutableStateFlow(false)
        every { mockPedidoViewModel.errorMessage } returns MutableStateFlow(null)

        composeTestRule.setContent {
            AdminPedidosScreen(
                usuario = usuarioAdmin,
                pedidoViewModel = mockPedidoViewModel,
                onVolver = { },
                onBackPressed = { }
            )
        }

        composeTestRule.waitForIdle()

        // THEN: No debería mostrar mensajes de error
        composeTestRule.onNodeWithText("Error").assertDoesNotExist()
    }

    @Test
    fun conDatosSeRenderizaCorrectamente() {
        // GIVEN: Datos disponibles
        val pedidos = listOf(crearPedidoEjemplo())
        every { mockPedidoViewModel.todosLosPedidos } returns MutableStateFlow(pedidos)
        every { mockPedidoViewModel.cargando } returns MutableStateFlow(false)

        composeTestRule.setContent {
            AdminPedidosScreen(
                usuario = usuarioAdmin,
                pedidoViewModel = mockPedidoViewModel,
                onVolver = { },
                onBackPressed = { }
            )
        }

        composeTestRule.waitForIdle()

        // THEN: La pantalla se renderiza sin errores (verificación básica)
        composeTestRule.onNodeWithText("Gestión de Pedidos - Todos").assertExists()

        // Verificar que no está en estado de carga
        composeTestRule.onNodeWithText("Cargando todos los pedidos...").assertDoesNotExist()

        // Verificar que no muestra mensaje de vacío
        composeTestRule.onNodeWithText("No hay pedidos en el sistema").assertDoesNotExist()
    }
}