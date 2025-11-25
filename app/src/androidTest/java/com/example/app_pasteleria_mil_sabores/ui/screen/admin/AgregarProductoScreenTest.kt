package com.example.app_pasteleria_mil_sabores.ui.screen.admin

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_pasteleria_mil_sabores.viewmodel.ProductoViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class AgregarProductoScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockProductoViewModel: ProductoViewModel = mockk(relaxed = true) {
        every { errorMessage } returns MutableStateFlow(null)
        every { cargando } returns MutableStateFlow(false)
        every { operacionExitosa } returns MutableStateFlow(false)
    }

    @Test
    fun pantallaAgregarProductoMuestraFormularioCompleto() {
        // WHEN: Se renderiza la pantalla de agregar producto
        composeTestRule.setContent {
            AgregarProductoScreen(
                productoViewModel = mockProductoViewModel,
                onCancelar = { },
                onGuardarExitoso = { },
                onBackPressed = { }
            )
        }

        // THEN: Deben mostrarse todos los campos del formulario
        composeTestRule.onNodeWithText("Agregar Nuevo Producto").assertExists()
        composeTestRule.onNodeWithText("Nombre del producto *").assertExists()
        composeTestRule.onNodeWithText("Descripción").assertExists()
        composeTestRule.onNodeWithText("Precio *").assertExists()
        composeTestRule.onNodeWithText("Stock *").assertExists()
        composeTestRule.onNodeWithText("Categoría *").assertExists()
        composeTestRule.onNodeWithText("Producto Destacado").assertExists()
    }

    @Test
    fun validacionCamposObligatorios() {
        composeTestRule.setContent {
            AgregarProductoScreen(
                productoViewModel = mockProductoViewModel,
                onCancelar = { },
                onGuardarExitoso = { },
                onBackPressed = { }
            )
        }

        // WHEN: Se dejan campos obligatorios vacíos
        // THEN: El botón de guardar debe estar deshabilitado
        composeTestRule.onNodeWithText("Guardar Producto").assertExists().assertIsNotEnabled()
    }

    @Test
    fun botonGuardarHabilitaConDatosValidos() {
        composeTestRule.setContent {
            AgregarProductoScreen(
                productoViewModel = mockProductoViewModel,
                onCancelar = { },
                onGuardarExitoso = { },
                onBackPressed = { }
            )
        }

        // WHEN: Se completan todos los campos obligatorios con datos válidos
        composeTestRule.onNodeWithText("Nombre del producto *").performTextInput("Torta Test")
        composeTestRule.onNodeWithText("Precio *").performTextInput("15000")
        composeTestRule.onNodeWithText("Stock *").performTextInput("10")

        // Seleccionar categoría
        composeTestRule.onNodeWithText("Categoría *").performClick()
        composeTestRule.onNodeWithText("Tradicional").performClick()

        // THEN: El botón de guardar debe habilitarse
        composeTestRule.onNodeWithText("Guardar Producto").assertExists().assertIsEnabled()
    }

    @Test
    fun validacionLongitudMinimaNombre() {
        composeTestRule.setContent {
            AgregarProductoScreen(
                productoViewModel = mockProductoViewModel,
                onCancelar = { },
                onGuardarExitoso = { },
                onBackPressed = { }
            )
        }

        // WHEN: Se ingresa un nombre muy corto
        composeTestRule.onNodeWithText("Nombre del producto *").performTextInput("Ab")

        // THEN: Debe mostrarse mensaje de error
        composeTestRule.onNodeWithText("Mínimo 3 caracteres").assertExists()
    }

    @Test
    fun validacionPrecioPositivo() {
        composeTestRule.setContent {
            AgregarProductoScreen(
                productoViewModel = mockProductoViewModel,
                onCancelar = { },
                onGuardarExitoso = { },
                onBackPressed = { }
            )
        }

        // WHEN: Se ingresa un precio inválido
        composeTestRule.onNodeWithText("Precio *").performTextInput("0")

        // THEN: Debe mostrarse mensaje de error
        composeTestRule.onNodeWithText("Precio debe ser mayor a 0").assertExists()
    }

    @Test
    fun botonCancelarEjecutaCallback() {
        // GIVEN: Callback mock
        var cancelarClicked = false

        composeTestRule.setContent {
            AgregarProductoScreen(
                productoViewModel = mockProductoViewModel,
                onCancelar = { cancelarClicked = true },
                onGuardarExitoso = { },
                onBackPressed = { }
            )
        }

        // WHEN: Se hace click en el botón cancelar
        composeTestRule.onNodeWithText("Cancelar").performClick()

        // THEN: El callback debe ejecutarse
        assertTrue(cancelarClicked)
    }
}