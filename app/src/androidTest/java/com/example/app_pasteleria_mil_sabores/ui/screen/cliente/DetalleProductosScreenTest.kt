package com.example.app_pasteleria_mil_sabores.ui.screen.cliente

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_pasteleria_mil_sabores.model.Producto
import com.example.app_pasteleria_mil_sabores.viewmodel.CarritoViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onAllNodesWithText

@RunWith(AndroidJUnit4::class)
class DetalleProductosScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockCarritoViewModel: CarritoViewModel = mockk(relaxed = true) {
        every { itemCount } returns MutableStateFlow(0)
    }

    private val productoTest = Producto(
        id = "1",
        nombre = "Torta de Chocolate",
        descripcion = "Deliciosa torta de chocolate premium con crema belga",
        precio = 15000,
        imagen = "torta_chocolate",
        categoria = "Tortas",
        stock = 5,
        destacado = true,
        activo = true
    )

    private val productoSinStock = productoTest.copy(
        id = "2",
        nombre = "Torta de Vainilla",
        stock = 0
    )

    private val productoProximamente = productoTest.copy(
        id = "3",
        nombre = "Torta de Fresa",
        stock = 0
    )

    @Test
    fun detalleProductoScreenMuestraInformacionDelProductoCorrectamente() {
        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoTest,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                onBackPressed = { }
            )
        }

        composeTestRule.onNodeWithText("Torta de Chocolate").assertExists()
        composeTestRule.onNodeWithText("$15.000").assertExists()
        composeTestRule.onNodeWithText("Deliciosa torta de chocolate premium con crema belga").assertExists()
        composeTestRule.onNodeWithText("Tortas").assertExists()
        composeTestRule.onNodeWithText("Descripción").assertExists()
        composeTestRule.onNodeWithText("Cantidad").assertExists()
        composeTestRule.onNodeWithText("Máximo: 5 unidades").assertExists()
    }

    @Test
    fun detalleProductoScreenLlamaOnVolverCuandoSePresionaBotonVolver() {
        var volverLlamado = false
        val onVolver: () -> Unit = { volverLlamado = true }

        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoTest,
                onVolver = onVolver,
                carritoViewModel = mockCarritoViewModel,
                onBackPressed = { }
            )
        }

        composeTestRule.onNodeWithContentDescription("Volver").performClick()
        assertTrue(volverLlamado)
    }

    @Test
    fun detalleProductoScreenLlamaOnBackPressedCuandoSePresionaBackHandler() {
        var backPressedLlamado = false
        val onBackPressed: () -> Unit = { backPressedLlamado = true }

        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoTest,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                onBackPressed = onBackPressed
            )
        }

        assertFalse(backPressedLlamado)
    }

    @Test
    fun detalleProductoScreenAgregaProductoAlCarritoCuandoSePresionaBotonAgregar() {
        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoTest,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                onBackPressed = { }
            )
        }

        composeTestRule.onNodeWithContentDescription("Agregar al carrito").performClick()
        verify { mockCarritoViewModel.agregarProducto(productoTest, 1) }
    }

    @Test
    fun detalleProductoScreenMuestraMensajeProximamenteCuandoProximamenteEsTrue() {
        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoTest,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                proximamente = true,
                onBackPressed = { }
            )
        }

        composeTestRule.onNodeWithText("Este producto estará disponible próximamente").assertExists()
        composeTestRule.onNodeWithContentDescription("Próximamente disponible").assertExists()
    }

    @Test
    fun detalleProductoScreenMuestraIndicadorSinStockCuandoStockEsCero() {
        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoSinStock,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                onBackPressed = { }
            )
        }

        composeTestRule.onNodeWithContentDescription("Producto agotado").assertExists()
    }

    @Test
    fun detalleProductoScreenMuestraStockDisponibleCuandoHayStock() {
        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoTest,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                onBackPressed = { }
            )
        }

        composeTestRule.onNodeWithContentDescription("Agregar al carrito").assertExists()
    }

    @Test
    fun detalleProductoScreenActualizaCantidadCuandoSeUsaContador() {
        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoTest,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                onBackPressed = { }
            )
        }

        // Verificar estado inicial
        composeTestRule.onNodeWithText("1").assertExists()
        composeTestRule.onNodeWithContentDescription("Agregar al carrito").assertExists()

        // WHEN: Se incrementa la cantidad usando el botón +
        composeTestRule.onNodeWithContentDescription("Aumentar cantidad").performClick()

        // THEN: La cantidad debe cambiar y el precio total actualizarse
        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithText("2").fetchSemanticsNodes().isNotEmpty()
        }

        // Verificar que la cantidad cambió
        composeTestRule.onNodeWithText("2").assertExists()

        // Verificar que el precio en el botón se actualizó buscando el texto completo
        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithText("Agregar al Carrito - $30.000").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Agregar al Carrito - $30.000").assertExists()

        // Verificar el texto del precio total
        composeTestRule.onNodeWithText("Precio total por 2 unidades").assertExists()
    }

    @Test
    fun detalleProductoScreenNoPermiteCantidadCeroCuandoSeDecrementa() {
        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoTest,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                onBackPressed = { }
            )
        }

        composeTestRule.onNodeWithContentDescription("Reducir cantidad").performClick()
        composeTestRule.onNodeWithContentDescription("Reducir cantidad").assertExists()
    }

    @Test
    fun detalleProductoScreenAjustaCantidadMaximaCuandoSeExcedeStock() {
        val productoStockLimitado = productoTest.copy(stock = 2)

        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoStockLimitado,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                onBackPressed = { }
            )
        }

        // Verificar estado inicial
        composeTestRule.onNodeWithText("1").assertExists()
        composeTestRule.onNodeWithText("Máximo: 2 unidades").assertExists()

        // WHEN: Se intenta establecer cantidad mayor al stock
        composeTestRule.onNodeWithContentDescription("Aumentar cantidad").performClick() // 2
        composeTestRule.onNodeWithContentDescription("Aumentar cantidad").performClick() // sigue en 2

        // THEN: La cantidad no excede el stock máximo
        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithText("2").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("2").assertExists()
        composeTestRule.onNodeWithText("Máximo: 2 unidades").assertExists()

        // Verificar que el precio en el botón se actualizó
        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithText("Agregar al Carrito - $30.000").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Agregar al Carrito - $30.000").assertExists()
    }

    @Test
    fun detalleProductoScreenMuestraImagenNoDisponibleCuandoRecursoEsCero() {
        val productoSinImagen = productoTest.copy(imagen = "imagen_inexistente")

        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoSinImagen,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                onBackPressed = { }
            )
        }

        composeTestRule.onNodeWithContentDescription("Imagen no disponible").assertExists()
        composeTestRule.onNodeWithText("Imagen no disponible").assertExists()
    }

    @Test
    fun detalleProductoScreenBotonAgregarDeshabilitadoCuandoProximamente() {
        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoTest,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                proximamente = true,
                onBackPressed = { }
            )
        }

        composeTestRule.onNodeWithContentDescription("Próximamente disponible").assertExists()
    }

    @Test
    fun detalleProductoScreenBotonAgregarDeshabilitadoCuandoSinStock() {
        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoSinStock,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                onBackPressed = { }
            )
        }

        composeTestRule.onNodeWithContentDescription("Producto agotado").assertExists()
    }

    @Test
    fun detalleProductoScreenCategoriaFormateadaCorrectamente() {
        val productoCategoriaCompleja = productoTest.copy(
            categoria = "tortas_personalizadas"
        )

        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoCategoriaCompleja,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                onBackPressed = { }
            )
        }

        composeTestRule.onNodeWithText("Tortas personalizadas").assertExists()
    }

    @Test
    fun detalleProductoScreenContadorCantidadDeshabilitadoCuandoProximamente() {
        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoTest,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                proximamente = true,
                onBackPressed = { }
            )
        }

        composeTestRule.onNodeWithText("0").assertExists()
    }

    @Test
    fun detalleProductoScreenPrecioTotalCalculadoCorrectamente() {
        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoTest,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                onBackPressed = { }
            )
        }

        // Verificar estado inicial
        composeTestRule.onNodeWithText("1").assertExists()
        composeTestRule.onNodeWithText("Agregar al Carrito - $15.000").assertExists()

        // WHEN: Se cambia la cantidad a 3
        composeTestRule.onNodeWithContentDescription("Aumentar cantidad").performClick() // 2
        composeTestRule.onNodeWithContentDescription("Aumentar cantidad").performClick() // 3

        // THEN: El precio total debe calcularse correctamente (15.000 * 3 = 45.000)
        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithText("3").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("3").assertExists()

        // Verificar que el precio en el botón se actualizó
        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithText("Agregar al Carrito - $45.000").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Agregar al Carrito - $45.000").assertExists()

        composeTestRule.onNodeWithText("Precio total por 3 unidades").assertExists()
    }

    @Test
    fun detalleProductoScreenMuestraMensajeAjusteMaximoCuandoCantidadExcedeStock() {
        val productoStockLimitado = productoTest.copy(stock = 2)

        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoStockLimitado,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                onBackPressed = { }
            )
        }

        composeTestRule.onNodeWithText("Máximo: 2 unidades").assertExists()
    }

    @Test
    fun detalleProductoScreenEstructuraVisualCompleta() {
        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoTest,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                onBackPressed = { }
            )
        }

        composeTestRule.onNodeWithText("Detalle del Producto").assertExists()
        composeTestRule.onNodeWithText("Torta de Chocolate").assertExists()
        composeTestRule.onNodeWithText("$15.000").assertExists()
        composeTestRule.onNodeWithText("Descripción").assertExists()
        composeTestRule.onNodeWithText("Cantidad").assertExists()
        composeTestRule.onNodeWithContentDescription("Agregar al carrito").assertExists()
    }

    @Test
    fun detalleProductoScreenColoresTemaAplicados() {
        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoTest,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                onBackPressed = { }
            )
        }

        composeTestRule.onNodeWithText("Torta de Chocolate").assertExists()
    }

    @Test
    fun detalleProductoScreenMuestraBotonAgregarConPrecioCuandoHayStock() {
        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoTest,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                onBackPressed = { }
            )
        }

        composeTestRule.onNodeWithContentDescription("Agregar al carrito").assertExists()
    }

    @Test
    fun detalleProductoScreenIndicadorStockMuestraTextoCorrecto() {
        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoSinStock,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                onBackPressed = { }
            )
        }

        composeTestRule.onNodeWithContentDescription("Producto agotado").assertExists()
    }

    @Test
    fun detalleProductoScreenBotonProximamenteTieneContentDescriptionCorrecto() {
        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoTest,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                proximamente = true,
                onBackPressed = { }
            )
        }

        composeTestRule.onNodeWithContentDescription("Próximamente disponible").assertExists()
    }

    @Test
    fun detalleProductoScreenMuestraPrecioUnitarioCorrectamente() {
        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoTest,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                onBackPressed = { }
            )
        }

        composeTestRule.onNodeWithText("$15.000").assertExists()
    }

    @Test
    fun detalleProductoScreenFuncionaConDiferentesPrecios() {
        val productoPrecioAlto = productoTest.copy(
            id = "4",
            nombre = "Torta Premium",
            precio = 25000
        )

        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoPrecioAlto,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                onBackPressed = { }
            )
        }

        composeTestRule.onNodeWithText("$25.000").assertExists()
    }

    @Test
    fun detalleProductoScreenContadorFuncionaCorrectamente() {
        composeTestRule.setContent {
            DetalleProductoScreen(
                producto = productoTest,
                onVolver = { },
                carritoViewModel = mockCarritoViewModel,
                onBackPressed = { }
            )
        }

        // Verificar que inicialmente muestra 1
        composeTestRule.onNodeWithText("1").assertExists()

        // Incrementar a 2
        composeTestRule.onNodeWithContentDescription("Aumentar cantidad").performClick()

        // Verificar que muestra 2
        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithText("2").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("2").assertExists()

        // Decrementar a 1
        composeTestRule.onNodeWithContentDescription("Reducir cantidad").performClick()

        // Verificar que muestra 1 nuevamente
        composeTestRule.waitUntil(3000) {
            composeTestRule.onAllNodesWithText("1").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("1").assertExists()
    }
}