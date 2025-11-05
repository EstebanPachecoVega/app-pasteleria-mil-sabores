package com.example.app_pasteleria_mil_sabores.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_pasteleria_mil_sabores.data.ProductoRepository
import com.example.app_pasteleria_mil_sabores.model.Producto
import com.example.app_pasteleria_mil_sabores.utils.IdGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductoViewModel(private val productoRepository: ProductoRepository) : ViewModel() {

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos = _productos.asStateFlow()

    private val _categorias = MutableStateFlow<List<String>>(emptyList())
    val categorias = _categorias.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando = _cargando.asStateFlow()

    private val _operacionExitosa = MutableStateFlow(false)
    val operacionExitosa = _operacionExitosa.asStateFlow()

    init {
        inicializarDatos()
    }

    private fun inicializarDatos() {
        viewModelScope.launch {
            _cargando.value = true
            try {
                productoRepository.inicializarDatos()
                cargarProductos()
                cargarCategorias()
            } catch (e: Exception) {
                _errorMessage.value = "Error al inicializar datos: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun cargarProductos() {
        viewModelScope.launch {
            try {
                productoRepository.obtenerTodos().collect { productos ->
                    _productos.value = productos
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar productos: ${e.message}"
            }
        }
    }

    fun cargarCategorias() {
        viewModelScope.launch {
            try {
                productoRepository.obtenerCategorias().collect { categorias ->
                    _categorias.value = categorias
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar categorías: ${e.message}"
            }
        }
    }

    fun buscarProductos(query: String) {
        viewModelScope.launch {
            try {
                productoRepository.buscar(query).collect { productos ->
                    _productos.value = productos
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al buscar productos: ${e.message}"
            }
        }
    }

    fun obtenerPorCategoria(categoria: String) {
        viewModelScope.launch {
            try {
                productoRepository.obtenerPorCategoria(categoria).collect { productos ->
                    _productos.value = productos
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al filtrar por categoría: ${e.message}"
            }
        }
    }

    fun crearNuevoProducto(
        nombre: String,
        descripcion: String,
        precio: Int,
        imagen: String,
        categoria: String,
        stock: Int,
        destacado: Boolean = false
    ) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val nuevoProducto = Producto(
                    id = IdGenerator.generarIdProducto(),
                    nombre = nombre,
                    descripcion = descripcion,
                    precio = precio,
                    imagen = imagen,
                    categoria = categoria,
                    stock = stock,
                    destacado = destacado,
                    activo = true
                )
                productoRepository.insertar(nuevoProducto)
                _operacionExitosa.value = true
                cargarProductos() // Recargar para reflejar el cambio
            } catch (e: Exception) {
                _errorMessage.value = "Error al crear producto: ${e.message}"
                _operacionExitosa.value = false
            } finally {
                _cargando.value = false
            }
        }
    }

    fun actualizarProducto(producto: Producto) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                productoRepository.actualizar(producto)
                _operacionExitosa.value = true
                cargarProductos()
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar producto: ${e.message}"
                _operacionExitosa.value = false
            } finally {
                _cargando.value = false
            }
        }
    }

    fun eliminarProducto(id: String) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                productoRepository.cambiarEstado(id, false)
                _operacionExitosa.value = true
                cargarProductos()
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar producto: ${e.message}"
                _operacionExitosa.value = false
            } finally {
                _cargando.value = false
            }
        }
    }

    fun limpiarError() {
        _errorMessage.value = null
    }

    fun resetearOperacionExitosa() {
        _operacionExitosa.value = false
    }
}