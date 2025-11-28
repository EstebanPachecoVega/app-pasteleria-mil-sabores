package com.example.app_pasteleria_mil_sabores.ui.screen.cliente

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_pasteleria_mil_sabores.model.CartItem
import com.example.app_pasteleria_mil_sabores.model.Producto
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.viewmodel.CarritoViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.Descuento
import com.example.app_pasteleria_mil_sabores.viewmodel.ResumenCarrito
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import java.util.Calendar

@RunWith(AndroidJUnit4::class)
class CarritoScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val resumenCarritoVacio = ResumenCarrito(
        subtotal = 0,
        descuentoAplicado = 0,
        total = 0,
        descuentos = emptyList()
    )

    private val resumenCarritoConProductos = ResumenCarrito(
        subtotal = 30000,
        descuentoAplicado = 0,
        total = 30000,
        descuentos = listOf(
            Descuento(
                tipo = "CODIGO_PROMOCIONAL",
                porcentaje = 10.0,
                descripcion = "🎉 10% descuento permanente",
                esAplicable = true
            )
        )
    )

    private val mockCarritoViewModel: CarritoViewModel = mockk(relaxed = true) {
        every { cartItems } returns MutableStateFlow(emptyList())
        every { resumenCarrito } returns MutableStateFlow(resumenCarritoVacio)
        every { itemCount } returns MutableStateFlow(0)
    }

    private val usuarioTest = Usuario(
        id = "1",
        username = "testuser",
        email = "test@duoc.cl",
        password = "password",
        tipoUsuario = "Cliente",
        fechaNacimiento = "01/01/2000"
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

    @Test
    fun carritoScreenMuestraElementosBasicosCuandoEstaVacio() {
        // WHEN: Se renderiza la pantalla del carrito vacío
        composeTestRule.setContent {
            CarritoScreen(
                onVolver = { },
                onContinuarCompra = { },
                onCheckout = { },
                viewModel = mockCarritoViewModel,
                usuarioActual = usuarioTest,
                onBackPressed = { }
            )
        }

        // THEN: Debe mostrar el mensaje de carrito vacío y botón para continuar comprando
        composeTestRule.onNodeWithText("Tu carrito está vacío").assertExists()
        composeTestRule.onNodeWithText("Continuar Comprando").assertExists()
        composeTestRule.onNodeWithContentDescription("Carrito vacío").assertExists()
    }

    @Test
    fun carritoScreenMuestraProductosCuandoHayElementos() {
        // GIVEN: Hay productos en el carrito
        every { mockCarritoViewModel.cartItems } returns MutableStateFlow(listOf(cartItemTest))
        every { mockCarritoViewModel.resumenCarrito } returns MutableStateFlow(resumenCarritoConProductos)
        every { mockCarritoViewModel.itemCount } returns MutableStateFlow(2)

        composeTestRule.setContent {
            CarritoScreen(
                onVolver = { },
                onContinuarCompra = { },
                onCheckout = { },
                viewModel = mockCarritoViewModel,
                usuarioActual = usuarioTest,
                onBackPressed = { }
            )
        }

        // THEN: Debe mostrar los productos y el resumen
        composeTestRule.waitUntil(5000L) {
            composeTestRule.onAllNodesWithText("Torta de Chocolate").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Torta de Chocolate").assertExists()
        composeTestRule.onNodeWithText("Proceder al Pago").assertExists()
    }

    @Test
    fun carritoScreenLlamaAOnVolverCuandoSePresionaElBotonVolver() {
        // GIVEN: Callback mock
        var volverLlamado = false
        val onVolver: () -> Unit = { volverLlamado = true }

        composeTestRule.setContent {
            CarritoScreen(
                onVolver = onVolver,
                onContinuarCompra = { },
                onCheckout = { },
                viewModel = mockCarritoViewModel,
                usuarioActual = usuarioTest,
                onBackPressed = { }
            )
        }

        // WHEN: Se hace click en el botón volver
        composeTestRule.onNodeWithContentDescription("Volver").performClick()

        // THEN: El callback debe ejecutarse
        assertTrue(volverLlamado)
    }

    @Test
    fun carritoScreenLlamaAOnContinuarCompraCuandoCarritoVacio() {
        // GIVEN: Callback mock
        var continuarCompraLlamado = false
        val onContinuarCompra: () -> Unit = { continuarCompraLlamado = true }

        composeTestRule.setContent {
            CarritoScreen(
                onVolver = { },
                onContinuarCompra = onContinuarCompra,
                onCheckout = { },
                viewModel = mockCarritoViewModel,
                usuarioActual = usuarioTest,
                onBackPressed = { }
            )
        }

        // WHEN: Se hace click en "Continuar Comprando"
        composeTestRule.onNodeWithText("Continuar Comprando").performClick()

        // THEN: El callback debe ejecutarse
        assertTrue(continuarCompraLlamado)
    }

    @Test
    fun carritoScreenLlamaAOnCheckoutCuandoHayProductos() {
        // GIVEN: Hay productos en el carrito y callback mock
        every { mockCarritoViewModel.cartItems } returns MutableStateFlow(listOf(cartItemTest))
        every { mockCarritoViewModel.resumenCarrito } returns MutableStateFlow(resumenCarritoConProductos)
        every { mockCarritoViewModel.itemCount } returns MutableStateFlow(2)

        var checkoutLlamado = false
        val onCheckout: () -> Unit = { checkoutLlamado = true }

        composeTestRule.setContent {
            CarritoScreen(
                onVolver = { },
                onContinuarCompra = { },
                onCheckout = onCheckout,
                viewModel = mockCarritoViewModel,
                usuarioActual = usuarioTest,
                onBackPressed = { }
            )
        }

        // Esperar a que cargue la UI
        composeTestRule.waitUntil(5000L) {
            composeTestRule.onAllNodesWithText("Proceder al Pago").fetchSemanticsNodes().isNotEmpty()
        }

        // WHEN: Se hace click en "Proceder al Pago"
        composeTestRule.onNodeWithText("Proceder al Pago").performClick()

        // THEN: El callback debe ejecutarse
        assertTrue(checkoutLlamado)
    }

    @Test
    fun carritoScreenMuestraBotonLimpiarCuandoHayProductos() {
        // GIVEN: Hay productos en el carrito
        every { mockCarritoViewModel.cartItems } returns MutableStateFlow(listOf(cartItemTest))
        every { mockCarritoViewModel.resumenCarrito } returns MutableStateFlow(resumenCarritoConProductos)
        every { mockCarritoViewModel.itemCount } returns MutableStateFlow(2)

        composeTestRule.setContent {
            CarritoScreen(
                onVolver = { },
                onContinuarCompra = { },
                onCheckout = { },
                viewModel = mockCarritoViewModel,
                usuarioActual = usuarioTest,
                onBackPressed = { }
            )
        }

        // THEN: Debe mostrar el botón "Limpiar"
        composeTestRule.waitUntil(5000L) {
            composeTestRule.onAllNodesWithText("Limpiar").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Limpiar").assertExists()
    }

    @Test
    fun carritoScreenMuestraDialogoLimpiarCarrito() {
        // GIVEN: Hay productos en el carrito
        every { mockCarritoViewModel.cartItems } returns MutableStateFlow(listOf(cartItemTest))
        every { mockCarritoViewModel.resumenCarrito } returns MutableStateFlow(resumenCarritoConProductos)
        every { mockCarritoViewModel.itemCount } returns MutableStateFlow(2)

        composeTestRule.setContent {
            CarritoScreen(
                onVolver = { },
                onContinuarCompra = { },
                onCheckout = { },
                viewModel = mockCarritoViewModel,
                usuarioActual = usuarioTest,
                onBackPressed = { }
            )
        }

        // Esperar a que la UI se cargue completamente
        composeTestRule.waitUntil(5000L) {
            composeTestRule.onAllNodesWithText("Limpiar").fetchSemanticsNodes().isNotEmpty()
        }

        // WHEN: Se hace click en "Limpiar"
        composeTestRule.onNodeWithText("Limpiar").performClick()

        // THEN: Debe mostrarse el diálogo de confirmación
        composeTestRule.waitUntil(3000L) {
            composeTestRule.onAllNodesWithText("Limpiar carrito").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Limpiar carrito").assertExists()

        // Verificar que aparece algún texto relacionado con eliminar productos
        composeTestRule.waitUntil(3000L) {
            composeTestRule.onAllNodesWithText("productos", substring = true).fetchSemanticsNodes().isNotEmpty() ||
                    composeTestRule.onAllNodesWithText("eliminar", substring = true).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun carritoScreenEjecutaLimpiarCarritoCuandoSeConfirma() {
        // GIVEN: Hay productos en el carrito
        every { mockCarritoViewModel.cartItems } returns MutableStateFlow(listOf(cartItemTest))
        every { mockCarritoViewModel.resumenCarrito } returns MutableStateFlow(resumenCarritoConProductos)
        every { mockCarritoViewModel.itemCount } returns MutableStateFlow(2)

        composeTestRule.setContent {
            CarritoScreen(
                onVolver = { },
                onContinuarCompra = { },
                onCheckout = { },
                viewModel = mockCarritoViewModel,
                usuarioActual = usuarioTest,
                onBackPressed = { }
            )
        }

        // Esperar a que la UI se cargue completamente
        composeTestRule.waitUntil(5000L) {
            composeTestRule.onAllNodesWithText("Limpiar").fetchSemanticsNodes().isNotEmpty()
        }

        // WHEN: Se abre el diálogo de limpiar carrito
        composeTestRule.onNodeWithText("Limpiar").performClick()

        // Esperar a que el diálogo aparezca
        composeTestRule.waitUntil(3000L) {
            composeTestRule.onAllNodesWithText("Limpiar", ignoreCase = true)
                .fetchSemanticsNodes().size >= 2
        }

        // Buscar todos los botones "Limpiar" y hacer click en el segundo (que debería ser el del diálogo)
        val limpiarButtons = composeTestRule.onAllNodesWithText("Limpiar", ignoreCase = true)
        if (limpiarButtons.fetchSemanticsNodes().size >= 2) {
            limpiarButtons[1].performClick()
        } else {
            try {
                composeTestRule.onNodeWithText("Confirmar", ignoreCase = true).performClick()
            } catch (e: Exception) {
                composeTestRule.onNodeWithText("Aceptar", ignoreCase = true).performClick()
            }
        }

        // THEN: El ViewModel debe ejecutar limpiarCarrito
        verify { mockCarritoViewModel.limpiarCarrito() }
    }

    @Test
    fun carritoScreenNoMuestraBotonLimpiarCuandoCarritoVacio() {
        // GIVEN: Carrito vacío
        every { mockCarritoViewModel.cartItems } returns MutableStateFlow(emptyList())
        every { mockCarritoViewModel.resumenCarrito } returns MutableStateFlow(resumenCarritoVacio)
        every { mockCarritoViewModel.itemCount } returns MutableStateFlow(0)

        composeTestRule.setContent {
            CarritoScreen(
                onVolver = { },
                onContinuarCompra = { },
                onCheckout = { },
                viewModel = mockCarritoViewModel,
                usuarioActual = usuarioTest,
                onBackPressed = { }
            )
        }

        // THEN: No debe mostrar el botón "Limpiar"
        composeTestRule.onNodeWithText("Limpiar").assertDoesNotExist()
    }

    @Test
    fun carritoScreenMuestraResumenCuandoHayProductos() {
        // GIVEN: Hay productos en el carrito con resumen
        every { mockCarritoViewModel.cartItems } returns MutableStateFlow(listOf(cartItemTest))
        every { mockCarritoViewModel.resumenCarrito } returns MutableStateFlow(resumenCarritoConProductos)
        every { mockCarritoViewModel.itemCount } returns MutableStateFlow(2)

        composeTestRule.setContent {
            CarritoScreen(
                onVolver = { },
                onContinuarCompra = { },
                onCheckout = { },
                viewModel = mockCarritoViewModel,
                usuarioActual = usuarioTest,
                onBackPressed = { }
            )
        }

        // THEN: Debe mostrar el resumen con precios
        composeTestRule.waitUntil(5000L) {
            composeTestRule.onAllNodesWithText("$30.000").fetchSemanticsNodes().isNotEmpty() ||
                    composeTestRule.onAllNodesWithText("30000").fetchSemanticsNodes().isNotEmpty()
        }
    }
}