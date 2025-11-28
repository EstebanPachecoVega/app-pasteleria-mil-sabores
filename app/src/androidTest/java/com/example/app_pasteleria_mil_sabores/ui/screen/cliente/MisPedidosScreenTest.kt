// MisPedidosScreenTest.kt
package com.example.app_pasteleria_mil_sabores.ui.screen.cliente

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_pasteleria_mil_sabores.model.*
import com.example.app_pasteleria_mil_sabores.viewmodel.PedidoViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class MisPedidosScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockPedidoViewModel: PedidoViewModel = mockk(relaxed = true) {
        every { pedidos } returns MutableStateFlow(emptyList())
        every { estadoFiltro } returns MutableStateFlow(null)
        every { cargando } returns MutableStateFlow(false)
        every { errorMessage } returns MutableStateFlow(null)
        every { obtenerPedidosFiltrados() } returns emptyList()
    }

    private val usuarioTest = Usuario(
        id = "1",
        username = "testuser",
        email = "test@duoc.cl",
        password = "password",
        tipoUsuario = "Cliente"
    )

    private val informacionContactoTest = InformacionContacto(
        nombre = "Test User",
        email = "test@duoc.cl",
        telefono = "+56912345678"
    )

    private val direccionTest = Direccion(
        calle = "Calle Test",
        numero = "123",
        comuna = "Santiago",
        ciudad = "Santiago",
        region = "Metropolitana"
    )

    private val productoTest = Producto(
        id = "1",
        nombre = "Torta de Chocolate",
        descripcion = "Deliciosa torta de chocolate premium",
        precio = 15000,
        imagen = "torta_chocolate",
        categoria = "Tortas",
        stock = 5,
        destacado = true,
        activo = true
    )

    private val cartItemTest = CartItem(
        producto = productoTest,
        cantidad = 2
    )

    private val pedidoTest = Pedido(
        id = "1",
        usuarioId = "1",
        productos = listOf(cartItemTest),
        total = 30000,
        estado = "pendiente",
        fechaCreacion = System.currentTimeMillis(),
        metodoPago = "tarjeta",
        subtotal = 30000,
        descuentoAplicado = 0,
        costoEnvio = 0,
        direccionEnvio = direccionTest,
        informacionContacto = informacionContactoTest
    )

    @Test
    fun misPedidosScreenMuestraElementosBasicosCuandoNoHayPedidos() {
        // WHEN: Se renderiza la pantalla sin pedidos
        composeTestRule.setContent {
            MisPedidosScreen(
                pedidoViewModel = mockPedidoViewModel,
                usuario = usuarioTest,
                onVolver = { },
                onVerDetallePedido = { },
                onBackPressed = { }
            )
        }

        // THEN: Debe mostrar mensaje de no hay pedidos
        composeTestRule.onNodeWithText("Aún no tienes pedidos").assertExists()
        composeTestRule.onNodeWithContentDescription("Filtrar pedidos").assertExists()
    }

    @Test
    fun misPedidosScreenMuestraPedidosCuandoHayDatos() {
        // GIVEN: Hay pedidos disponibles
        val pedidoNormalizado = pedidoTest.copy(estado = "Pendiente")
        every { mockPedidoViewModel.pedidos } returns MutableStateFlow(listOf(pedidoNormalizado))
        every { mockPedidoViewModel.obtenerPedidosFiltrados() } returns listOf(pedidoNormalizado)

        composeTestRule.setContent {
            MisPedidosScreen(
                pedidoViewModel = mockPedidoViewModel,
                usuario = usuarioTest,
                onVolver = { },
                onVerDetallePedido = { },
                onBackPressed = { }
            )
        }

        Thread.sleep(1000)

        // THEN: Debe mostrar los pedidos con información verificable
        composeTestRule.onNodeWithText("Pedido #1").assertExists()
        composeTestRule.onNodeWithText("$30.000").assertExists()
        composeTestRule.onNodeWithText("Pendiente").assertExists()
        composeTestRule.onNodeWithText("1 producto").assertExists()
    }

    @Test
    fun misPedidosScreenLlamaAOnVolverCuandoSePresionaElBotonVolver() {
        // GIVEN: Callback mock
        var volverLlamado = false
        val onVolver: () -> Unit = { volverLlamado = true }

        composeTestRule.setContent {
            MisPedidosScreen(
                pedidoViewModel = mockPedidoViewModel,
                usuario = usuarioTest,
                onVolver = onVolver,
                onVerDetallePedido = { },
                onBackPressed = { }
            )
        }

        // WHEN: Se hace click en el botón volver
        composeTestRule.onNodeWithContentDescription("Volver").performClick()

        // THEN: El callback debe ejecutarse
        assertTrue(volverLlamado)
    }

    @Test
    fun misPedidosScreenLlamaAOnVerDetallePedidoCuandoSeSeleccionaPedido() {
        // GIVEN: Hay pedidos y callback mock
        val pedidoNormalizado = pedidoTest.copy(estado = "Pendiente")
        every { mockPedidoViewModel.pedidos } returns MutableStateFlow(listOf(pedidoNormalizado))
        every { mockPedidoViewModel.obtenerPedidosFiltrados() } returns listOf(pedidoNormalizado)

        var detalleLlamado = false
        var pedidoIdSeleccionado = ""
        val onVerDetallePedido: (String) -> Unit = { id ->
            detalleLlamado = true
            pedidoIdSeleccionado = id
        }

        composeTestRule.setContent {
            MisPedidosScreen(
                pedidoViewModel = mockPedidoViewModel,
                usuario = usuarioTest,
                onVolver = { },
                onVerDetallePedido = onVerDetallePedido,
                onBackPressed = { }
            )
        }

        Thread.sleep(1000)

        // WHEN: Se hace click en un pedido
        composeTestRule.onNodeWithText("Pedido #1").performClick()

        // THEN: El callback debe ejecutarse con el ID correcto
        assertTrue(detalleLlamado)
        assertEquals("1", pedidoIdSeleccionado)
    }

    @Test
    fun misPedidosScreenMuestraFiltrosCuandoSePresionaIconoFiltro() {
        composeTestRule.setContent {
            MisPedidosScreen(
                pedidoViewModel = mockPedidoViewModel,
                usuario = usuarioTest,
                onVolver = { },
                onVerDetallePedido = { },
                onBackPressed = { }
            )
        }

        // WHEN: Se hace click en el icono de filtros
        composeTestRule.onNodeWithContentDescription("Filtrar pedidos").performClick()

        // THEN: Debe mostrarse el diálogo de filtros
        composeTestRule.onNodeWithText("Filtrar por estado").assertExists()
        composeTestRule.onNodeWithText("Todos los pedidos").assertExists()
        composeTestRule.onNodeWithText("Pendientes").assertExists()
        composeTestRule.onNodeWithText("Confirmados").assertExists()
    }

    @Test
    fun misPedidosScreenEjecutaFiltradoCuandoSeSeleccionaEstado() {
        composeTestRule.setContent {
            MisPedidosScreen(
                pedidoViewModel = mockPedidoViewModel,
                usuario = usuarioTest,
                onVolver = { },
                onVerDetallePedido = { },
                onBackPressed = { }
            )
        }

        // WHEN: Se abre el diálogo y selecciona un filtro
        composeTestRule.onNodeWithContentDescription("Filtrar pedidos").performClick()
        composeTestRule.onNodeWithText("Pendientes").performClick()

        // THEN: El ViewModel debe ejecutar el filtrado
        verify { mockPedidoViewModel.filtrarPorEstado("pendiente") }
    }

    @Test
    fun misPedidosScreenMuestraMensajeCargando() {
        // GIVEN: El ViewModel está cargando
        every { mockPedidoViewModel.cargando } returns MutableStateFlow(true)

        composeTestRule.setContent {
            MisPedidosScreen(
                pedidoViewModel = mockPedidoViewModel,
                usuario = usuarioTest,
                onVolver = { },
                onVerDetallePedido = { },
                onBackPressed = { }
            )
        }

        // THEN: Debe mostrar indicador de carga
        composeTestRule.onNodeWithText("Cargando tus pedidos...").assertExists()
    }

    @Test
    fun misPedidosScreenMuestraInformacionBasicaDelPedido() {
        // GIVEN: Hay pedidos disponibles
        val pedidoNormalizado = pedidoTest.copy(estado = "Pendiente")
        every { mockPedidoViewModel.pedidos } returns MutableStateFlow(listOf(pedidoNormalizado))
        every { mockPedidoViewModel.obtenerPedidosFiltrados() } returns listOf(pedidoNormalizado)

        composeTestRule.setContent {
            MisPedidosScreen(
                pedidoViewModel = mockPedidoViewModel,
                usuario = usuarioTest,
                onVolver = { },
                onVerDetallePedido = { },
                onBackPressed = { }
            )
        }

        Thread.sleep(1000)

        // THEN: Debe mostrar información básica del pedido
        composeTestRule.onNodeWithText("Pedido #1").assertExists()
        composeTestRule.onNodeWithText("$30.000").assertExists()
        composeTestRule.onNodeWithText("Pendiente").assertExists()
        composeTestRule.onNodeWithText("1 producto").assertExists()
    }

    @Test
    fun misPedidosScreenMuestraInformacionCompletaDelPedido() {
        // GIVEN: Pedido con estado NORMALIZADO
        val pedidoSimple = Pedido(
            id = "TEST123",
            usuarioId = "1",
            productos = listOf(
                CartItem(
                    producto = Producto(
                        id = "1",
                        nombre = "Torta de Chocolate",
                        descripcion = "Deliciosa torta de chocolate premium",
                        precio = 15000,
                        imagen = "torta_chocolate",
                        categoria = "Tortas",
                        stock = 5,
                        destacado = true,
                        activo = true
                    ),
                    cantidad = 2
                )
            ),
            estado = "Pendiente",
            fechaCreacion = System.currentTimeMillis(),
            subtotal = 30000,
            descuentoAplicado = 0,
            costoEnvio = 0,
            total = 30000,
            direccionEnvio = direccionTest,
            metodoPago = "tarjeta",
            informacionContacto = informacionContactoTest
        )

        // Mock del ViewModel
        every { mockPedidoViewModel.pedidos } returns MutableStateFlow(listOf(pedidoSimple))
        every { mockPedidoViewModel.cargando } returns MutableStateFlow(false)
        every { mockPedidoViewModel.errorMessage } returns MutableStateFlow(null)
        every { mockPedidoViewModel.obtenerPedidosFiltrados() } returns listOf(pedidoSimple)

        composeTestRule.setContent {
            MisPedidosScreen(
                pedidoViewModel = mockPedidoViewModel,
                usuario = usuarioTest,
                onVolver = { },
                onVerDetallePedido = { },
                onBackPressed = { }
            )
        }

        Thread.sleep(1000)

        // THEN: Verificación mínima y efectiva
        composeTestRule.onNodeWithText("Aún no tienes pedidos").assertDoesNotExist()
        composeTestRule.onNodeWithText("$30.000").assertExists()
    }
}