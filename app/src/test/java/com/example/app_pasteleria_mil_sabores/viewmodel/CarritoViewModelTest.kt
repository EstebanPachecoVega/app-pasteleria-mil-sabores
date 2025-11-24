package com.example.app_pasteleria_mil_sabores.viewmodel

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import com.example.app_pasteleria_mil_sabores.data.ProductoRepository
import com.example.app_pasteleria_mil_sabores.model.Producto
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalCoroutinesApi::class)
class CarritoViewModelTest {

    private lateinit var carritoViewModel: CarritoViewModel
    private val mockProductoRepository: ProductoRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    private val productoTest = Producto(
        id = "1",
        nombre = "Torta de Chocolate",
        descripcion = "Deliciosa torta de chocolate",
        precio = 10000,
        imagen = "torta_chocolate",
        categoria = "Tortas",
        stock = 5,
        destacado = true,
        activo = true
    )

    @Before
    fun setUp() {
        // Configurar el dispatcher principal para pruebas
        Dispatchers.setMain(testDispatcher)

        // Configurar comportamiento del mock con MockK para funciones suspend
        coEvery { mockProductoRepository.obtenerStock(any()) } returns 10
        coEvery { mockProductoRepository.obtenerTodos() } returns flowOf(emptyList())

        carritoViewModel = CarritoViewModel(mockProductoRepository)
    }

    @Test
    fun carritoInicialmenteVacio() = runTest {
        assertTrue(carritoViewModel.estaVacio())
        assertEquals(0, carritoViewModel.itemCount.value)
    }

    @Test
    fun agregarProductoIncrementaContador() = runTest {
        carritoViewModel.agregarProducto(productoTest, 1)
        assertEquals(1, carritoViewModel.itemCount.value)
    }

    @Test
    fun eliminarProductoDelCarrito() = runTest {
        carritoViewModel.agregarProducto(productoTest, 1)
        carritoViewModel.eliminarProducto(productoTest.id)
        assertTrue(carritoViewModel.estaVacio())
    }

    @Test
    fun actualizarCantidadDeProducto() = runTest {
        carritoViewModel.agregarProducto(productoTest, 1)
        carritoViewModel.actualizarCantidad(productoTest.id, 3)

        val cantidad = carritoViewModel.getCantidadProducto(productoTest.id)
        assertEquals(3, cantidad)
    }

    @Test
    fun limpiarCarrito() = runTest {
        carritoViewModel.agregarProducto(productoTest, 2)
        carritoViewModel.limpiarCarrito()

        assertTrue(carritoViewModel.estaVacio())
        assertEquals(0, carritoViewModel.itemCount.value)
    }
}