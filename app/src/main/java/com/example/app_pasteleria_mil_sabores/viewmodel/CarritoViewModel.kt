package com.example.app_pasteleria_mil_sabores.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_pasteleria_mil_sabores.data.ProductoRepository
import com.example.app_pasteleria_mil_sabores.model.CartItem
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class Descuento(
    val tipo: String,
    val porcentaje: Double,
    val descripcion: String,
    val esAplicable: Boolean
)

data class ResumenCarrito(
    val subtotal: Int,
    val descuentoAplicado: Int,
    val total: Int,
    val descuentos: List<Descuento>
)

class CarritoViewModel(
    private val productoRepository: ProductoRepository
) : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)

    private val _resumenCarrito = MutableStateFlow(ResumenCarrito(0, 0, 0, emptyList()))
    val resumenCarrito: StateFlow<ResumenCarrito> = _resumenCarrito.asStateFlow()

    private val _itemCount = MutableStateFlow(0)
    val itemCount: StateFlow<Int> = _itemCount.asStateFlow()

    private val _operacionExitosa = MutableStateFlow(false)
    val operacionExitosa = _operacionExitosa.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    init {
        // Combinar los flujos de items y usuario para recalcular automáticamente
        viewModelScope.launch {
            combine(_cartItems, _usuarioActual) { items, usuario ->
                val subtotal = items.sumOf { it.getPrecioTotal() }
                _itemCount.value = items.sumOf { it.cantidad }
                calcularResumenCarrito(subtotal, usuario)
            }.collect()
        }
    }

    // Función para verificar si un producto es una torta
    private fun esTorta(producto: Producto): Boolean {
        // Verifica por categoría o nombre que contenga "torta"
        return producto.categoria.equals("tortas", ignoreCase = true) ||
                producto.nombre.contains("torta", ignoreCase = true) ||
                producto.nombre.contains("cake", ignoreCase = true)
    }

    fun calcularDescuentos(usuario: Usuario?, cartItems: List<CartItem>): List<Descuento> {
        if (usuario == null) return emptyList()

        val descuentos = mutableListOf<Descuento>()
        val tieneTortaEnCarrito = cartItems.any { esTorta(it.producto) }

        // Descuento para estudiantes Duoc en cumpleaños - SOLO SI HAY TORTA EN CARRITO
        if (usuario.esEstudianteDuoc && usuario.esSuCumpleanos() && tieneTortaEnCarrito) {
            // Verificar que no se haya excedido el límite de 1 torta gratis
            val tortasEnCarrito = cartItems.filter { esTorta(it.producto) }
            val puedeAplicarDescuento = tortasEnCarrito.size == 1 && tortasEnCarrito[0].cantidad == 1

            if (puedeAplicarDescuento) {
                descuentos.add(Descuento(
                    tipo = "ESTUDIANTE_CUMPLEANOS",
                    porcentaje = 100.0,
                    descripcion = "🎂 Torta gratis por cumpleaños (1 por cliente)",
                    esAplicable = true
                ))
            }
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

    // Modifica la función calcularResumenCarrito
    private fun calcularResumenCarrito(subtotal: Int, usuario: Usuario? = null) {
        val descuentos = calcularDescuentos(usuario, _cartItems.value)

        if (descuentos.isEmpty()) {
            _resumenCarrito.value = ResumenCarrito(
                subtotal = subtotal,
                descuentoAplicado = 0,
                total = subtotal,
                descuentos = emptyList()
            )
            return
        }

        // Para el descuento de torta gratis, aplicar solo al precio de UNA torta
        val descuentoTorta = descuentos.find { it.tipo == "ESTUDIANTE_CUMPLEANOS" }
        val otrosDescuentos = descuentos.filter { it.tipo != "ESTUDIANTE_CUMPLEANOS" }

        var descuentoAplicado = 0
        var total = subtotal

        if (descuentoTorta != null && descuentoTorta.esAplicable) {
            // Encontrar la primera torta en el carrito y aplicar 100% de descuento solo a esa unidad
            val tortaEnCarrito = _cartItems.value.find { esTorta(it.producto) }
            if (tortaEnCarrito != null) {
                // Solo descontar el precio de 1 torta, no importa la cantidad
                descuentoAplicado = tortaEnCarrito.producto.precio
                total = subtotal - descuentoAplicado
            }
        }

        // Aplicar otros descuentos si existen
        val mayorOtroDescuento = otrosDescuentos.maxByOrNull { it.porcentaje }
        if (mayorOtroDescuento != null && mayorOtroDescuento.esAplicable) {
            val descuentoAdicional = (total * (mayorOtroDescuento.porcentaje / 100)).toInt()
            descuentoAplicado += descuentoAdicional
            total -= descuentoAdicional
        }

        _resumenCarrito.value = ResumenCarrito(
            subtotal = subtotal,
            descuentoAplicado = descuentoAplicado,
            total = total,
            descuentos = descuentos
        )
    }

    // Método para actualizar el usuario actual
    fun setUsuarioActual(usuario: Usuario?) {
        _usuarioActual.value = usuario
    }

    fun agregarProducto(producto: Producto, cantidad: Int) {
        viewModelScope.launch {
            try {
                // Verificar stock antes de agregar
                val stockDisponible = productoRepository.obtenerStock(producto.id) ?: 0

                if (cantidad > stockDisponible) {
                    _errorMessage.value = "Stock insuficiente. Solo quedan $stockDisponible unidades"
                    return@launch
                }

                val currentItems = _cartItems.value.toMutableList()
                val usuario = _usuarioActual.value

                // Validación especial para tortas si el usuario tiene descuento de cumpleaños
                if (esTorta(producto) && usuario?.esEstudianteDuoc == true &&
                    usuario.esSuCumpleanos() == true) {

                    val tortasEnCarrito = currentItems.filter { esTorta(it.producto) }
                    val cantidadTotalTortas = tortasEnCarrito.sumOf { it.cantidad }

                    // Limitar a 1 torta gratis por cliente en cumpleaños
                    if (cantidadTotalTortas >= 1) {
                        _errorMessage.value = "Solo puedes llevar 1 torta gratis en tu cumpleaños"
                        return@launch
                    }

                    // Si ya tiene una torta en el carrito y trata de agregar otra, bloquear
                    val existingTortaIndex = currentItems.indexOfFirst {
                        it.producto.id == producto.id && esTorta(it.producto)
                    }

                    if (existingTortaIndex != -1) {
                        val existingTorta = currentItems[existingTortaIndex]
                        if (existingTorta.cantidad >= 1) {
                            _errorMessage.value = "Solo puedes llevar 1 torta gratis en tu cumpleaños"
                            return@launch
                        }
                    }
                }

                val existingItemIndex = currentItems.indexOfFirst { it.producto.id == producto.id }

                if (existingItemIndex != -1) {
                    val existingItem = currentItems[existingItemIndex]
                    val nuevaCantidad = existingItem.cantidad + cantidad

                    // Validación adicional de stock para tortas con descuento
                    if (esTorta(producto) && usuario?.esEstudianteDuoc == true &&
                        usuario.esSuCumpleanos() == true) {

                        // Para tortas en cumpleaños, máximo 1 unidad
                        if (nuevaCantidad > 1) {
                            _errorMessage.value = "Solo puedes llevar 1 torta gratis en tu cumpleaños"
                            return@launch
                        }
                    }

                    if (nuevaCantidad <= producto.stock) {
                        currentItems[existingItemIndex] = existingItem.copy(cantidad = nuevaCantidad)
                        _operacionExitosa.value = true
                    } else {
                        currentItems[existingItemIndex] = existingItem.copy(cantidad = producto.stock)
                        _errorMessage.value = "Se ajustó al stock máximo disponible"
                    }
                } else {
                    // Para nuevo producto en el carrito
                    var cantidadFinal = cantidad

                    // Validación para nueva torta en cumpleaños
                    if (esTorta(producto) && usuario?.esEstudianteDuoc == true &&
                        usuario.esSuCumpleanos() == true) {

                        val tortasEnCarrito = currentItems.filter { esTorta(it.producto) }
                        val cantidadTotalTortas = tortasEnCarrito.sumOf { it.cantidad }

                        if (cantidadTotalTortas >= 1) {
                            _errorMessage.value = "Solo puedes llevar 1 torta gratis en tu cumpleaños"
                            return@launch
                        }

                        // Limitar a 1 unidad máximo
                        if (cantidadFinal > 1) {
                            cantidadFinal = 1
                            _errorMessage.value = "Solo puedes llevar 1 torta gratis en tu cumpleaños"
                        }
                    }

                    if (cantidadFinal <= producto.stock) {
                        currentItems.add(CartItem(producto, cantidadFinal))
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

                    // Verificar stock antes de actualizar
                    val stockDisponible = productoRepository.obtenerStock(productoId) ?: 0

                    if (nuevaCantidad > stockDisponible) {
                        _errorMessage.value = "Stock insuficiente. Solo quedan $stockDisponible unidades"
                        return@launch
                    }

                    // Validación para tortas en cumpleaños
                    if (esTorta(item.producto) && _usuarioActual.value?.esEstudianteDuoc == true &&
                        _usuarioActual.value?.esSuCumpleanos() == true) {

                        if (nuevaCantidad > 1) {
                            _errorMessage.value = "Solo puedes llevar 1 torta gratis en tu cumpleaños"
                            return@launch
                        }
                    }

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

    suspend fun verificarStockCarrito(): Boolean {
        val currentItems = _cartItems.value
        for (item in currentItems) {
            val stockDisponible = productoRepository.obtenerStock(item.producto.id) ?: 0
            if (item.cantidad > stockDisponible) {
                _errorMessage.value = "Stock insuficiente para ${item.producto.nombre}. Disponible: $stockDisponible"
                return false
            }
        }
        return true
    }

    suspend fun obtenerStockActual(productoId: String): Int? {
        return productoRepository.obtenerStock(productoId)
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