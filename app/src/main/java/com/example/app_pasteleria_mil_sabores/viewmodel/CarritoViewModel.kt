package com.example.app_pasteleria_mil_sabores.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_pasteleria_mil_sabores.model.CartItem
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Descuento(
    val tipo: String,
    val porcentaje: Double,
    val descripcion: String,
    val esAplicable: Boolean
)

class CarritoViewModel : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _total = MutableStateFlow(0)
    val total: StateFlow<Int> = _total.asStateFlow()

    private val _itemCount = MutableStateFlow(0)
    val itemCount: StateFlow<Int> = _itemCount.asStateFlow()

    private val _operacionExitosa = MutableStateFlow(false)
    val operacionExitosa = _operacionExitosa.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        viewModelScope.launch {
            _cartItems.collect { items ->
                _total.value = items.sumOf { it.getPrecioTotal() }
                _itemCount.value = items.sumOf { it.cantidad }
            }
        }
    }

    fun calcularDescuentos(usuario: Usuario?): List<Descuento> {
        if (usuario == null) return emptyList()

        val descuentos = mutableListOf<Descuento>()

        // Descuento para estudiantes Duoc en cumpleaños
        if (usuario.esEstudianteDuoc && usuario.esSuCumpleanos()) {
            descuentos.add(Descuento(
                tipo = "ESTUDIANTE_CUMPLEANOS",
                porcentaje = 100.0,
                descripcion = "🎂 Torta gratis por cumpleaños",
                esAplicable = true
            ))
        }

        // Descuento para mayores de 50 años
        val edad = usuario.edad
        if (edad != null && edad >= 50) {
            descuentos.add(Descuento(
                tipo = "MAYOR_50",
                porcentaje = 50.0,
                descripcion = "👵 50% descuento (+50 años)",
                esAplicable = true
            ))
        }

        // Descuento por código promocional
        if (usuario.codigoPromocion?.equals("FELICES50", ignoreCase = true) == true) {
            descuentos.add(Descuento(
                tipo = "CODIGO_PROMOCIONAL",
                porcentaje = 10.0,
                descripcion = "🎉 10% descuento permanente",
                esAplicable = true
            ))
        }

        return descuentos
    }

    fun calcularTotalConDescuentos(usuario: Usuario?): Int {
        val subtotal = total.value
        val descuentos = calcularDescuentos(usuario)

        if (descuentos.isEmpty()) return subtotal

        // Aplicar el descuento más beneficioso
        val mayorDescuento = descuentos.maxByOrNull { it.porcentaje }
        return if (mayorDescuento != null && mayorDescuento.esAplicable) {
            (subtotal * (1 - mayorDescuento.porcentaje / 100)).toInt()
        } else {
            subtotal
        }
    }

    fun agregarProducto(producto: Producto, cantidad: Int) {
        viewModelScope.launch {
            try {
                val currentItems = _cartItems.value.toMutableList()
                val existingItemIndex = currentItems.indexOfFirst { it.producto.id == producto.id }

                if (existingItemIndex != -1) {
                    val existingItem = currentItems[existingItemIndex]
                    val nuevaCantidad = existingItem.cantidad + cantidad
                    if (nuevaCantidad <= producto.stock) {
                        currentItems[existingItemIndex] = existingItem.copy(cantidad = nuevaCantidad)
                        _operacionExitosa.value = true
                    } else {
                        currentItems[existingItemIndex] = existingItem.copy(cantidad = producto.stock)
                        _errorMessage.value = "Se ajustó al stock máximo disponible"
                    }
                } else {
                    if (cantidad <= producto.stock) {
                        currentItems.add(CartItem(producto, cantidad))
                        _operacionExitosa.value = true
                    } else {
                        currentItems.add(CartItem(producto, producto.stock))
                        _errorMessage.value = "Se ajustó al stock máximo disponible"
                    }
                }

                _cartItems.value = currentItems
            } catch (e: Exception) {
                _errorMessage.value = "Error al agregar producto al carrito: ${e.message}"
            }
        }
    }

    fun actualizarCantidad(productoId: String, nuevaCantidad: Int) {
        viewModelScope.launch {
            try {
                val currentItems = _cartItems.value.toMutableList()
                val itemIndex = currentItems.indexOfFirst { it.producto.id == productoId }

                if (itemIndex != -1) {
                    val item = currentItems[itemIndex]
                    if (nuevaCantidad > 0 && nuevaCantidad <= item.producto.stock) {
                        currentItems[itemIndex] = item.copy(cantidad = nuevaCantidad)
                        _operacionExitosa.value = true
                    } else if (nuevaCantidad <= 0) {
                        currentItems.removeAt(itemIndex)
                        _operacionExitosa.value = true
                    } else {
                        _errorMessage.value = "No puedes exceder el stock disponible"
                    }
                }

                _cartItems.value = currentItems
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar cantidad: ${e.message}"
            }
        }
    }

    fun eliminarProducto(productoId: String) {
        viewModelScope.launch {
            try {
                val currentItems = _cartItems.value.toMutableList()
                currentItems.removeAll { it.producto.id == productoId }
                _cartItems.value = currentItems
                _operacionExitosa.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar producto: ${e.message}"
            }
        }
    }

    fun limpiarCarrito() {
        viewModelScope.launch {
            try {
                _cartItems.value = emptyList()
                _operacionExitosa.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error al limpiar carrito: ${e.message}"
            }
        }
    }

    fun getCantidadProducto(productoId: String): Int {
        return _cartItems.value.find { it.producto.id == productoId }?.cantidad ?: 0
    }

    fun estaVacio(): Boolean {
        return _cartItems.value.isEmpty()
    }

    fun limpiarError() {
        _errorMessage.value = null
    }

    fun resetearOperacionExitosa() {
        _operacionExitosa.value = false
    }
}