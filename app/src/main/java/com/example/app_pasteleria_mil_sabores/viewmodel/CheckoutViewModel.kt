package com.example.app_pasteleria_mil_sabores.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_pasteleria_mil_sabores.data.PedidoDao
import com.example.app_pasteleria_mil_sabores.data.ProductoRepository
import com.example.app_pasteleria_mil_sabores.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val pedidoDao: PedidoDao,
    private val productoRepository: ProductoRepository
) : ViewModel() {

    // Estados para el pedido actual
    private val _pedidoActual = MutableStateFlow<Pedido?>(null)
    val pedidoActual: StateFlow<Pedido?> = _pedidoActual.asStateFlow()

    // Estados para la información de envío
    private val _informacionContacto = MutableStateFlow<InformacionContacto?>(null)
    val informacionContacto: StateFlow<InformacionContacto?> = _informacionContacto.asStateFlow()

    private val _direccionEnvio = MutableStateFlow<Direccion?>(null)
    val direccionEnvio: StateFlow<Direccion?> = _direccionEnvio.asStateFlow()

    private val _ubicacionSeleccionada = MutableStateFlow<Coordenadas?>(null)
    val ubicacionSeleccionada: StateFlow<Coordenadas?> = _ubicacionSeleccionada.asStateFlow()

    // Estados para método de pago
    private val _metodoPago = MutableStateFlow<String?>(null)
    val metodoPago: StateFlow<String?> = _metodoPago.asStateFlow()

    private val _stockInsuficiente = MutableStateFlow<List<String>>(emptyList())
    val stockInsuficiente: StateFlow<List<String>> = _stockInsuficiente.asStateFlow()

    // Estados para UI
    private val _operacionExitosa = MutableStateFlow(false)
    val operacionExitosa = _operacionExitosa.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Inicializar pedido desde el carrito
    fun inicializarPedido(
        carritoItems: List<CartItem>,
        resumen: ResumenCarrito,
        usuario: Usuario?
    ) {
        viewModelScope.launch {
            try {
                val pedido = Pedido(
                    id = com.example.app_pasteleria_mil_sabores.utils.IdGenerator.generarIdPedido(),
                    usuarioId = usuario?.id ?: "",
                    productos = carritoItems,
                    estado = "pendiente",
                    subtotal = resumen.subtotal,
                    descuentoAplicado = resumen.descuentoAplicado,
                    costoEnvio = 0, // Se calculará después
                    total = resumen.total,
                    direccionEnvio = null,
                    metodoPago = "",
                    informacionContacto = InformacionContacto(
                        nombre = usuario?.username ?: "",
                        email = usuario?.email ?: "",
                        telefono = null
                    )
                )
                _pedidoActual.value = pedido
                _operacionExitosa.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error al inicializar pedido: ${e.message}"
            }
        }
    }

    // Actualizar información de contacto
    fun actualizarInformacionContacto(informacion: InformacionContacto) {
        viewModelScope.launch {
            try {
                _informacionContacto.value = informacion
                actualizarPedido { pedido ->
                    pedido.copy(informacionContacto = informacion)
                }
                _operacionExitosa.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar información de contacto: ${e.message}"
            }
        }
    }

    // Actualizar dirección de envío
    fun actualizarDireccion(direccion: Direccion) {
        viewModelScope.launch {
            try {
                _direccionEnvio.value = direccion
                actualizarPedido { pedido ->
                    pedido.copy(direccionEnvio = direccion)
                }
                _operacionExitosa.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar dirección: ${e.message}"
            }
        }
    }

    // Actualizar ubicación seleccionada
    fun actualizarUbicacionSeleccionada(coordenadas: Coordenadas?) {
        _ubicacionSeleccionada.value = coordenadas
    }

    // Actualizar método de pago
    fun actualizarMetodoPago(metodo: String) {
        viewModelScope.launch {
            try {
                _metodoPago.value = metodo
                actualizarPedido { pedido ->
                    pedido.copy(metodoPago = metodo)
                }
                _operacionExitosa.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar método de pago: ${e.message}"
            }
        }
    }

    // Calcular costo de envío
    fun calcularCostoEnvio(subtotal: Int): Int {
        return if (subtotal >= 40000) 0 else 2500
    }

    // Actualizar costo de envío en el pedido
    fun actualizarCostoEnvio() {
        viewModelScope.launch {
            try {
                actualizarPedido { pedido ->
                    val costoEnvio = calcularCostoEnvio(pedido.subtotal)
                    val nuevoTotal = pedido.subtotal - pedido.descuentoAplicado + costoEnvio
                    pedido.copy(costoEnvio = costoEnvio, total = nuevoTotal)
                }
                _operacionExitosa.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar costo de envío: ${e.message}"
            }
        }
    }

    // Función interna para actualizar el pedido
    private fun actualizarPedido(transform: (Pedido) -> Pedido) {
        _pedidoActual.value?.let { pedido ->
            _pedidoActual.value = transform(pedido)
        }
    }

    // Confirmar y guardar pedido en la base de datos
    suspend fun confirmarYGuardarPedido(): Boolean {
        return try {
            _pedidoActual.value?.let { pedido ->
                // 1. Verificar stock antes de confirmar
                val productosSinStock = mutableListOf<String>()

                for (item in pedido.productos) {
                    val stockSuficiente = productoRepository.verificarStockSuficiente(
                        item.producto.id,
                        item.cantidad
                    )
                    if (!stockSuficiente) {
                        productosSinStock.add(item.producto.nombre)
                    }
                }

                if (productosSinStock.isNotEmpty()) {
                    _stockInsuficiente.value = productosSinStock
                    _errorMessage.value = "Stock insuficiente para: ${productosSinStock.joinToString(", ")}"
                    return false
                }

                // 2. Descontar stock de cada producto
                for (item in pedido.productos) {
                    val exito = productoRepository.descontarStock(item.producto.id, item.cantidad)
                    if (!exito) {
                        _errorMessage.value = "Error al actualizar stock para ${item.producto.nombre}"
                        return false
                    }
                }

                // 3. Guardar el pedido
                val pedidoConfirmado = pedido.copy(estado = "confirmado")
                pedidoDao.insertar(pedidoConfirmado)
                _pedidoActual.value = pedidoConfirmado
                _operacionExitosa.value = true
                true
            } ?: false
        } catch (e: Exception) {
            _errorMessage.value = "Error al confirmar pedido: ${e.message}"
            false
        }
    }

    // Obtener pedidos por usuario
    fun obtenerPedidosPorUsuario(usuarioId: String) = pedidoDao.obtenerPedidosPorUsuario(usuarioId)

    // Obtener pedido por ID
    suspend fun obtenerPedidoPorId(id: String): Pedido? {
        return try {
            pedidoDao.obtenerPedidoPorId(id)
        } catch (e: Exception) {
            _errorMessage.value = "Error al obtener pedido: ${e.message}"
            null
        }
    }

    // Validaciones
    fun validarInformacionContacto(): Boolean {
        val info = _informacionContacto.value
        return info != null &&
                info.nombre.isNotBlank() &&
                info.email.isNotBlank() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(info.email).matches()
    }

    fun validarDireccion(): Boolean {
        val direccion = _direccionEnvio.value
        return direccion != null &&
                direccion.calle.isNotBlank() &&
                direccion.numero.isNotBlank() &&
                direccion.comuna.isNotBlank() &&
                direccion.ciudad.isNotBlank() &&
                direccion.region.isNotBlank()
    }

    fun validarMetodoPago(): Boolean {
        return _metodoPago.value != null && _metodoPago.value!!.isNotBlank()
    }

    fun esInformacionEnvioCompleta(): Boolean {
        return validarInformacionContacto() && validarDireccion()
    }

    fun esPagoCompleto(): Boolean {
        return validarMetodoPago() && _pedidoActual.value != null
    }

    // Limpiar estados
    fun limpiarCheckout() {
        viewModelScope.launch {
            _pedidoActual.value = null
            _informacionContacto.value = null
            _direccionEnvio.value = null
            _ubicacionSeleccionada.value = null
            _metodoPago.value = null
            _errorMessage.value = null
        }
    }

    fun limpiarError() {
        _errorMessage.value = null
    }

    fun resetearOperacionExitosa() {
        _operacionExitosa.value = false
    }

    fun limpiarErroresStock() {
        _stockInsuficiente.value = emptyList()
    }

    // Obtener pedido actual para confirmación
    fun obtenerPedidoConfirmado(): Pedido? {
        return _pedidoActual.value?.takeIf { it.estado == "confirmado" }
    }
}