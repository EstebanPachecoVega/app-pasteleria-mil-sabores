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
class DetallePedidoScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockPedidoViewModel: PedidoViewModel = mockk(relaxed = true) {
        every { pedidoSeleccionado } returns MutableStateFlow(null)
        every { cargando } returns MutableStateFlow(false)
        every { errorMessage } returns MutableStateFlow(null)
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
    fun detallePedidoScreenMuestraMensajeCuandoNoHayPedido() {
        // WHEN: Se renderiza la pantalla sin pedido seleccionado
        composeTestRule.setContent {
            DetallePedidoScreen(
                pedidoId = "1",
                pedidoViewModel = mockPedidoViewModel,
                onVolver = { },
                onBackPressed = { }
            )
        }

        // THEN: Debe mostrar mensaje de pedido no encontrado
        composeTestRule.onNodeWithText("Pedido no encontrado").assertExists()
        composeTestRule.onNodeWithText("Volver a Mis Pedidos").assertExists()
    }

    @Test
    fun detallePedidoScreenMuestraDetallesCompletosCuandoHayPedido() {
        // GIVEN: Hay un pedido seleccionado
        every { mockPedidoViewModel.pedidoSeleccionado } returns MutableStateFlow(pedidoTest)

        composeTestRule.setContent {
            DetallePedidoScreen(
                pedidoId = "1",
                pedidoViewModel = mockPedidoViewModel,
                onVolver = { },
                onBackPressed = { }
            )
        }

        // THEN: Debe mostrar todos los detalles del pedido
        composeTestRule.onNodeWithText("Pedido #1").assertExists()

        // Verificar que existe al menos un nodo con el precio
        val precioNodes = composeTestRule.onAllNodesWithText("$30.000").fetchSemanticsNodes()
        assertTrue("Debe haber al menos un precio $30.000", precioNodes.isNotEmpty())

        // VERIFICAR TEXTOS EXACTOS QUE VIMOS EN EL DEBUG
        composeTestRule.onNodeWithText("💳 Resumen del Pago").assertExists() // <- CORREGIDO
        composeTestRule.onNodeWithText("📋 Estado del Pedido").assertExists()
        composeTestRule.onNodeWithText("🚚 Información de Envío").assertExists()
        composeTestRule.onNodeWithText("👤 Información de Contacto").assertExists()
        composeTestRule.onNodeWithText("🛒 Productos (1)").assertExists()
        composeTestRule.onNodeWithText("Torta de Chocolate").assertExists()

        // Verificar detalles específicos del resumen de pago
        composeTestRule.onNodeWithText("Método de pago:").assertExists()
        composeTestRule.onNodeWithText("Subtotal:").assertExists()
        composeTestRule.onNodeWithText("Envío:").assertExists()
        composeTestRule.onNodeWithText("Total:").assertExists()
        composeTestRule.onNodeWithText("Tarjeta de Crédito/Débito").assertExists()
        composeTestRule.onNodeWithText("GRATIS").assertExists()
    }

    @Test
    fun detallePedidoScreenLlamaAOnVolverCuandoSePresionaElBotonVolver() {
        // GIVEN: Callback mock
        var volverLlamado = false
        val onVolver: () -> Unit = { volverLlamado = true }

        composeTestRule.setContent {
            DetallePedidoScreen(
                pedidoId = "1",
                pedidoViewModel = mockPedidoViewModel,
                onVolver = onVolver,
                onBackPressed = { }
            )
        }

        // WHEN: Se hace click en el botón volver
        composeTestRule.onNodeWithContentDescription("Volver").performClick()

        // THEN: El callback debe ejecutarse
        assertTrue(volverLlamado)
    }

    @Test
    fun detallePedidoScreenLlamaACargarPedidoAlInicializar() {
        // WHEN: Se renderiza la pantalla
        composeTestRule.setContent {
            DetallePedidoScreen(
                pedidoId = "1",
                pedidoViewModel = mockPedidoViewModel,
                onVolver = { },
                onBackPressed = { }
            )
        }

        // THEN: El ViewModel debe cargar el pedido
        verify { mockPedidoViewModel.cargarPedidoPorId("1") }
    }

    @Test
    fun detallePedidoScreenMuestraMensajeCargando() {
        // GIVEN: El ViewModel está cargando
        every { mockPedidoViewModel.cargando } returns MutableStateFlow(true)

        composeTestRule.setContent {
            DetallePedidoScreen(
                pedidoId = "1",
                pedidoViewModel = mockPedidoViewModel,
                onVolver = { },
                onBackPressed = { }
            )
        }

        // THEN: Debe mostrar indicador de carga
        composeTestRule.onNodeWithText("Cargando detalle del pedido...").assertExists()
    }

    @Test
    fun detallePedidoScreenMuestraInformacionEnvioYContacto() {
        // GIVEN: Hay un pedido seleccionado con información completa
        every { mockPedidoViewModel.pedidoSeleccionado } returns MutableStateFlow(pedidoTest)

        composeTestRule.setContent {
            DetallePedidoScreen(
                pedidoId = "1",
                pedidoViewModel = mockPedidoViewModel,
                onVolver = { },
                onBackPressed = { }
            )
        }

        // THEN: Debe mostrar la información de envío y contacto
        composeTestRule.onNodeWithText("Calle Test 123").assertExists()
        composeTestRule.onNodeWithText("Santiago, Santiago").assertExists()
        composeTestRule.onNodeWithText("Test User").assertExists()
        composeTestRule.onNodeWithText("test@duoc.cl").assertExists()
    }

    @Test
    fun detallePedidoScreenMuestraProductosDelPedido() {
        // GIVEN: Hay un pedido seleccionado
        every { mockPedidoViewModel.pedidoSeleccionado } returns MutableStateFlow(pedidoTest)

        composeTestRule.setContent {
            DetallePedidoScreen(
                pedidoId = "1",
                pedidoViewModel = mockPedidoViewModel,
                onVolver = { },
                onBackPressed = { }
            )
        }

        // THEN: Debe mostrar los productos del pedido
        composeTestRule.onNodeWithText("Torta de Chocolate").assertExists()
        composeTestRule.onNodeWithText("2 x $15.000").assertExists()
    }

    @Test
    fun detallePedidoScreenMuestraTotalDelPedido() {
        // GIVEN: Hay un pedido seleccionado
        every { mockPedidoViewModel.pedidoSeleccionado } returns MutableStateFlow(pedidoTest)

        composeTestRule.setContent {
            DetallePedidoScreen(
                pedidoId = "1",
                pedidoViewModel = mockPedidoViewModel,
                onVolver = { },
                onBackPressed = { }
            )
        }

        // ENTONCES: Debe mostrar el total del pedido
        composeTestRule.onNodeWithText("Total:").assertExists()

        // Verificar que existe al menos un nodo con el precio total
        val precioNodes = composeTestRule.onAllNodesWithText("$30.000").fetchSemanticsNodes()
        assertTrue("Debe mostrar el precio $30.000", precioNodes.isNotEmpty())
    }
}