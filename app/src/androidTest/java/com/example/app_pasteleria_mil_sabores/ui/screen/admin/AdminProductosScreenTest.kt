package com.example.app_pasteleria_mil_sabores.ui.screen.admin

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_pasteleria_mil_sabores.model.Producto
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.viewmodel.FormularioViewModel
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
class AdminProductosScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockFormularioViewModel: FormularioViewModel = mockk(relaxed = true)
    private val mockProductoViewModel: ProductoViewModel = mockk(relaxed = true) {
        every { productos } returns MutableStateFlow(emptyList())
        every { cargando } returns MutableStateFlow(false)
    }

    private val usuarioAdmin = Usuario(
        id = "1",
        username = "admin",
        email = "admin@duoc.cl",
        password = "password",
        tipoUsuario = "Administrador"
    )

    private val productoTest = Producto(
        id = "1",
        nombre = "Torta de Chocolate",
        descripcion = "Deliciosa torta de chocolate premium",
        precio = 15000,
        imagen = "torta_chocolate",
        categoria = "tortas",
        stock = 5,
        destacado = true,
        activo = true
    )

    @Test
    fun pantallaProductosMuestraTituloYControles() {
        // WHEN: Se renderiza la pantalla de productos
        composeTestRule.setContent {
            AdminProductosScreen(
                usuario = usuarioAdmin,
                viewModel = mockFormularioViewModel,
                productoViewModel = mockProductoViewModel,
                onVolver = { },
                onAgregarProducto = { },
                onEditarProducto = { },
                onBackPressed = { }
            )
        }

        // THEN: Deben mostrarse los elementos básicos
        composeTestRule.onNodeWithText("Gestión de Productos").assertExists()
        composeTestRule.onNodeWithContentDescription("Agregar producto").assertExists()
    }

    @Test
    fun mostrarListaDeProductosCuandoHayDatos() {
        // GIVEN: Productos disponibles
        every { mockProductoViewModel.productos } returns MutableStateFlow(
            listOf(productoTest)
        )

        composeTestRule.setContent {
            AdminProductosScreen(
                usuario = usuarioAdmin,
                viewModel = mockFormularioViewModel,
                productoViewModel = mockProductoViewModel,
                onVolver = { },
                onAgregarProducto = { },
                onEditarProducto = { },
                onBackPressed = { }
            )
        }

        // THEN: Los productos deben mostrarse en la lista
        composeTestRule.onNodeWithText("Torta de Chocolate").assertExists()
        composeTestRule.onNodeWithText("$15000 - Stock: 5").assertExists()
        composeTestRule.onNodeWithText("tortas").assertExists()
        composeTestRule.onNodeWithText("⭐ Destacado").assertExists()
    }

    @Test
    fun mostrarMensajeCuandoNoHayProductos() {
        // GIVEN: No hay productos disponibles
        every { mockProductoViewModel.productos } returns MutableStateFlow(emptyList())

        composeTestRule.setContent {
            AdminProductosScreen(
                usuario = usuarioAdmin,
                viewModel = mockFormularioViewModel,
                productoViewModel = mockProductoViewModel,
                onVolver = { },
                onAgregarProducto = { },
                onEditarProducto = { },
                onBackPressed = { }
            )
        }

        // THEN: Debe mostrarse mensaje de "no hay productos"
        composeTestRule.onNodeWithText("No hay productos disponibles").assertExists()
        composeTestRule.onNodeWithText("Agregar Primer Producto").assertExists()
    }

    @Test
    fun botonAgregarProductoEjecutaCallback() {
        // GIVEN: Callback mock
        var agregarProductoClicked = false

        composeTestRule.setContent {
            AdminProductosScreen(
                usuario = usuarioAdmin,
                viewModel = mockFormularioViewModel,
                productoViewModel = mockProductoViewModel,
                onVolver = { },
                onAgregarProducto = { agregarProductoClicked = true },
                onEditarProducto = { },
                onBackPressed = { }
            )
        }

        // WHEN: Se hace click en el botón de agregar producto
        composeTestRule.onNodeWithContentDescription("Agregar producto").performClick()

        // THEN: El callback debe ejecutarse
        assertTrue(agregarProductoClicked)
    }

    @Test
    fun mostrarEstadisticasDeProductos() {
        // GIVEN: Productos con diferentes estados
        val productos = listOf(
            productoTest,
            productoTest.copy(id = "2", nombre = "Torta de Vainilla", destacado = false),
            productoTest.copy(id = "3", nombre = "Cheesecake", destacado = true)
        )
        every { mockProductoViewModel.productos } returns MutableStateFlow(productos)

        composeTestRule.setContent {
            AdminProductosScreen(
                usuario = usuarioAdmin,
                viewModel = mockFormularioViewModel,
                productoViewModel = mockProductoViewModel,
                onVolver = { },
                onAgregarProducto = { },
                onEditarProducto = { },
                onBackPressed = { }
            )
        }

        // THEN: Deben mostrarse las estadísticas correctas
        composeTestRule.onNodeWithText("Total de productos: 3").assertExists()
        composeTestRule.onNodeWithText("Productos destacados: 2").assertExists()
    }
}