package com.example.app_pasteleria_mil_sabores.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_pasteleria_mil_sabores.model.CartItem
import com.example.app_pasteleria_mil_sabores.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
                // Actualizar total y contador cuando cambien los items
                _total.value = items.sumOf { it.getPrecioTotal() }
                _itemCount.value = items.sumOf { it.cantidad }
            }
        }
    }

    fun agregarProducto(producto: Producto, cantidad: Int) {
        viewModelScope.launch {
            try {
                val currentItems = _cartItems.value.toMutableList()
                val existingItemIndex = currentItems.indexOfFirst { it.producto.id == producto.id }

                if (existingItemIndex != -1) {
                    // Producto ya existe en el carrito, actualizar cantidad
                    val existingItem = currentItems[existingItemIndex]
                    val nuevaCantidad = existingItem.cantidad + cantidad
                    if (nuevaCantidad <= producto.stock) {
                        currentItems[existingItemIndex] = existingItem.copy(cantidad = nuevaCantidad)
                        _operacionExitosa.value = true
                    } else {
                        // Si excede el stock, ajustar al máximo disponible
                        currentItems[existingItemIndex] = existingItem.copy(cantidad = producto.stock)
                        _errorMessage.value = "Se ajustó al stock máximo disponible"
                    }
                } else {
                    // Producto nuevo en el carrito
                    if (cantidad <= producto.stock) {
                        currentItems.add(CartItem(producto, cantidad))
                        _operacionExitosa.value = true
                    } else {
                        // Si excede el stock, ajustar al máximo disponible
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
                        // Eliminar item si la cantidad es 0 o negativa
                        currentItems.removeAt(itemIndex)
                        _operacionExitosa.value = true
                    } else {
                        _errorMessage.value = "No puedes exceder el stock disponible"
                    }
                    // Si nuevaCantidad > stock, no hacemos nada (mantenemos cantidad actual)
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