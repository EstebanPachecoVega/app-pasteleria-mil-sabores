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
class ProductoViewModelTest {

    private lateinit var productoViewModel: ProductoViewModel
    private val mockProductoRepository: ProductoRepository = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    private val productosTest = listOf(
        Producto(
            id = "1",
            nombre = "Torta de Chocolate",
            descripcion = "Deliciosa torta",
            precio = 10000,
            imagen = "torta_chocolate",
            categoria = "Tortas",
            stock = 5,
            destacado = true,
            activo = true
        )
    )

    @Before
    fun setUp() {
        // Configurar el dispatcher principal para pruebas
        Dispatchers.setMain(testDispatcher)

        // Usar coEvery para funciones suspend
        coEvery { mockProductoRepository.obtenerTodos() } returns flowOf(productosTest)
        coEvery { mockProductoRepository.obtenerCategorias() } returns flowOf(listOf("Tortas", "Postres"))

        productoViewModel = ProductoViewModel(mockProductoRepository)
    }

    @Test
    fun cargarProductosCorrectamente() = runTest {
        productoViewModel.cargarProductos()

        assertEquals(1, productoViewModel.productos.value.size)
        assertEquals("Torta de Chocolate", productoViewModel.productos.value[0].nombre)
    }

    @Test
    fun cargarCategorias() = runTest {
        productoViewModel.cargarCategorias()

        assertEquals(2, productoViewModel.categorias.value.size)
        assertTrue(productoViewModel.categorias.value.contains("Tortas"))
    }
}