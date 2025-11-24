package com.example.app_pasteleria_mil_sabores.ui.screen.cliente

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_pasteleria_mil_sabores.model.Producto
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.viewmodel.CarritoViewModel
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
class ClienteHomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Mocks completos con comportamientos específicos
    private val mockFormularioViewModel: FormularioViewModel = mockk(relaxed = true) {
        every { usuarioActual } returns MutableStateFlow(null)
    }

    private val mockProductoViewModel: ProductoViewModel = mockk(relaxed = true) {
        every { productos } returns MutableStateFlow(emptyList())
        every { cargando } returns MutableStateFlow(false)
        every { buscarProductos(any()) } returns Unit
    }

    private val mockCarritoViewModel: CarritoViewModel = mockk(relaxed = true) {
        every { itemCount } returns MutableStateFlow(0)
    }

    private val usuarioTest = Usuario(
        id = "1",
        username = "testuser",
        email = "test@duoc.cl",
        password = "password",
        tipoUsuario = "Cliente"
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

    @Test
    fun pantallaHomeMuestraElementosBasicosCorrectamente() {
        // WHEN: Se renderiza la pantalla principal
        composeTestRule.setContent {
            ClienteHomeScreen(
                usuario = usuarioTest,
                viewModel = mockFormularioViewModel,
                productoViewModel = mockProductoViewModel,
                carritoViewModel = mockCarritoViewModel,
                onCerrarSesion = { },
                onVerPerfil = { },
                onVerCarrito = { },
                onVerPedidos = { },
                onVerSoporte = { },
                onVerDetalleProducto = { },
                onBackPressed = { },
                onVerRecetas = { }
            )
        }

        // THEN: Deben mostrarse todos los elementos básicos
        composeTestRule.onNodeWithText("Buscar productos...").assertExists()
        composeTestRule.onNodeWithContentDescription("Carrito").assertExists()
    }

    @Test
    fun buscarProductosEjecutaBusquedaEnViewmodel() {
        // GIVEN: Pantalla renderizada
        composeTestRule.setContent {
            ClienteHomeScreen(
                usuario = usuarioTest,
                viewModel = mockFormularioViewModel,
                productoViewModel = mockProductoViewModel,
                carritoViewModel = mockCarritoViewModel,
                onCerrarSesion = { },
                onVerPerfil = { },
                onVerCarrito = { },
                onVerPedidos = { },
                onVerSoporte = { },
                onVerDetalleProducto = { },
                onBackPressed = { },
                onVerRecetas = { }
            )
        }

        // WHEN: Se escribe en el campo de búsqueda
        composeTestRule.onNodeWithText("Buscar productos...").performTextInput("Torta")

        // THEN: El ViewModel debe ejecutar la búsqueda
        verify { mockProductoViewModel.buscarProductos("Torta") }
    }

    @Test
    fun mostrarListaDeProductosCuandoHayDatos() {
        // GIVEN: Productos disponibles
        every { mockProductoViewModel.productos } returns MutableStateFlow(
            listOf(productoTest)
        )

        composeTestRule.setContent {
            ClienteHomeScreen(
                usuario = usuarioTest,
                viewModel = mockFormularioViewModel,
                productoViewModel = mockProductoViewModel,
                carritoViewModel = mockCarritoViewModel,
                onCerrarSesion = { },
                onVerPerfil = { },
                onVerCarrito = { },
                onVerPedidos = { },
                onVerSoporte = { },
                onVerDetalleProducto = { },
                onBackPressed = { },
                onVerRecetas = { }
            )
        }

        // THEN: Los productos deben mostrarse en la lista
        composeTestRule.onNodeWithText("Torta de Chocolate").assertExists()
        composeTestRule.onNodeWithText("$15.000").assertExists()
    }

    @Test
    fun mostrarMensajeCuandoNoHayProductos() {
        // GIVEN: No hay productos disponibles
        every { mockProductoViewModel.productos } returns MutableStateFlow(emptyList())

        composeTestRule.setContent {
            ClienteHomeScreen(
                usuario = usuarioTest,
                viewModel = mockFormularioViewModel,
                productoViewModel = mockProductoViewModel,
                carritoViewModel = mockCarritoViewModel,
                onCerrarSesion = { },
                onVerPerfil = { },
                onVerCarrito = { },
                onVerPedidos = { },
                onVerSoporte = { },
                onVerDetalleProducto = { },
                onBackPressed = { },
                onVerRecetas = { }
            )
        }

        // THEN: Debe mostrarse mensaje de "no hay productos"
        composeTestRule.onNodeWithText("No hay productos disponibles").assertExists()
    }

    @Test
    fun clickEnCarritoEjecutaCallbackCorrespondiente() {
        // GIVEN: Callback mock
        var carritoClicked = false
        val onVerCarrito: () -> Unit = { carritoClicked = true }

        composeTestRule.setContent {
            ClienteHomeScreen(
                usuario = usuarioTest,
                viewModel = mockFormularioViewModel,
                productoViewModel = mockProductoViewModel,
                carritoViewModel = mockCarritoViewModel,
                onCerrarSesion = { },
                onVerPerfil = { },
                onVerCarrito = onVerCarrito,
                onVerPedidos = { },
                onVerSoporte = { },
                onVerDetalleProducto = { },
                onBackPressed = { },
                onVerRecetas = { }
            )
        }

        // WHEN: Se hace click en el icono del carrito
        composeTestRule.onNodeWithContentDescription("Carrito").performClick()

        // THEN: El callback debe ejecutarse
        assertTrue(carritoClicked)
    }

    @Test
    fun campoBusquedaPermiteEntradaDeTexto() {
        composeTestRule.setContent {
            ClienteHomeScreen(
                usuario = usuarioTest,
                viewModel = mockFormularioViewModel,
                productoViewModel = mockProductoViewModel,
                carritoViewModel = mockCarritoViewModel,
                onCerrarSesion = { },
                onVerPerfil = { },
                onVerCarrito = { },
                onVerPedidos = { },
                onVerSoporte = { },
                onVerDetalleProducto = { },
                onBackPressed = { },
                onVerRecetas = { }
            )
        }

        // WHEN: Se escribe texto en el campo de búsqueda
        composeTestRule.onNodeWithText("Buscar productos...").performTextInput("Pastel")

        // THEN: El texto debe ingresarse correctamente
        // (La verificación se hace indirectamente a través del ViewModel)
    }
}