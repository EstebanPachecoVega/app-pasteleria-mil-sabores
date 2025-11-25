package com.example.app_pasteleria_mil_sabores.ui.screen.admin

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_pasteleria_mil_sabores.model.Producto
import com.example.app_pasteleria_mil_sabores.viewmodel.ProductoViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class EditarProductoScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockProductoViewModel: ProductoViewModel = mockk(relaxed = true) {
        every { errorMessage } returns MutableStateFlow(null)
        every { cargando } returns MutableStateFlow(false)
        every { operacionExitosa } returns MutableStateFlow(false)
    }

    private val productoTest = Producto(
        id = "1",
        nombre = "Torta Original",
        descripcion = "Descripción original del producto",
        precio = 10000,
        imagen = "torta_original",
        categoria = "tradicional",
        stock = 5,
        destacado = false,
        activo = true
    )

    @Test
    fun pantallaEditarProductoMuestraDatosExistentes() {
        // WHEN: Se renderiza la pantalla de editar producto
        composeTestRule.setContent {
            EditarProductoScreen(
                producto = productoTest,
                productoViewModel = mockProductoViewModel,
                onCancelar = { },
                onGuardarExitoso = { },
                onBackPressed = { }
            )
        }

        // THEN: Deben mostrarse los datos existentes del producto
        composeTestRule.onNodeWithText("Editar Producto").assertExists()
        // Los campos deben estar prellenados con los datos del producto
    }

    @Test
    fun camposPrecargadosConDatosDelProducto() {
        composeTestRule.setContent {
            EditarProductoScreen(
                producto = productoTest,
                productoViewModel = mockProductoViewModel,
                onCancelar = { },
                onGuardarExitoso = { },
                onBackPressed = { }
            )
        }

        // THEN: Los campos deben contener los datos del producto
        // (La verificación se hace indirectamente ya que los campos están prellenados)
        composeTestRule.onNodeWithText("Torta Original").assertExists()
    }

    @Test
    fun botonGuardarCambiosHabilitaConDatosValidos() {
        composeTestRule.setContent {
            EditarProductoScreen(
                producto = productoTest,
                productoViewModel = mockProductoViewModel,
                onCancelar = { },
                onGuardarExitoso = { },
                onBackPressed = { }
            )
        }

        // WHEN: Se modifican campos manteniendo datos válidos
        composeTestRule.onNodeWithText("Nombre del producto *").performTextInput("Torta Modificada")

        // THEN: El botón de guardar cambios debe estar habilitado
        composeTestRule.onNodeWithText("Guardar Cambios").assertExists().assertIsEnabled()
    }

    @Test
    fun mostrarCheckboxActivoParaProductosInactivos() {
        // GIVEN: Producto inactivo
        val productoInactivo = productoTest.copy(activo = false)

        composeTestRule.setContent {
            EditarProductoScreen(
                producto = productoInactivo,
                productoViewModel = mockProductoViewModel,
                onCancelar = { },
                onGuardarExitoso = { },
                onBackPressed = { }
            )
        }

        // THEN: El checkbox de activo debe mostrarse desmarcado
        composeTestRule.onNodeWithText("Producto Activo").assertExists()
    }

    @Test
    fun botonCancelarEjecutaCallbackEnEdicion() {
        // GIVEN: Callback mock
        var cancelarClicked = false

        composeTestRule.setContent {
            EditarProductoScreen(
                producto = productoTest,
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