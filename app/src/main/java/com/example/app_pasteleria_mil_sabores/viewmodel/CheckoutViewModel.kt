package com.example.app_pasteleria_mil_sabores.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_pasteleria_mil_sabores.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CheckoutViewModel : ViewModel() {
    private val _pedidoActual = MutableStateFlow<Pedido?>(null)
    val pedidoActual: StateFlow<Pedido?> = _pedidoActual.asStateFlow()

    private val _informacionEnvio = MutableStateFlow<InformacionContacto?>(null)
    val informacionEnvio: StateFlow<InformacionContacto?> = _informacionEnvio.asStateFlow()

    private val _direccionEnvio = MutableStateFlow<Direccion?>(null)
    val direccionEnvio: StateFlow<Direccion?> = _direccionEnvio.asStateFlow()

    private val _metodoPago = MutableStateFlow<String?>(null)
    val metodoPago: StateFlow<String?> = _metodoPago.asStateFlow()

    fun inicializarPedido(
        carritoItems: List<CartItem>,
        resumen: ResumenCarrito,
        usuario: Usuario?
    ) {
        viewModelScope.launch {
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
        }
    }

    fun actualizarInformacionContacto(informacion: InformacionContacto) {
        _informacionEnvio.value = informacion
        actualizarPedido { pedido ->
            pedido.copy(informacionContacto = informacion)
        }
    }

    fun actualizarDireccion(direccion: Direccion) {
        _direccionEnvio.value = direccion
        actualizarPedido { pedido ->
            pedido.copy(direccionEnvio = direccion)
        }
    }

    fun actualizarMetodoPago(metodo: String) {
        _metodoPago.value = metodo
        actualizarPedido { pedido ->
            pedido.copy(metodoPago = metodo)
        }
    }

    fun calcularCostoEnvio(subtotal: Int): Int {
        return if (subtotal >= 40000) 0 else 2500
    }

    fun actualizarCostoEnvio() {
        actualizarPedido { pedido ->
            val costoEnvio = calcularCostoEnvio(pedido.subtotal)
            val nuevoTotal = pedido.subtotal - pedido.descuentoAplicado + costoEnvio
            pedido.copy(costoEnvio = costoEnvio, total = nuevoTotal)
        }
    }

    private fun actualizarPedido(transform: (Pedido) -> Pedido) {
        _pedidoActual.value?.let { pedido ->
            _pedidoActual.value = transform(pedido)
        }
    }

    fun confirmarPedido(): Pedido? {
        return _pedidoActual.value?.copy(estado = "confirmado")
    }

    fun limpiarCheckout() {
        _pedidoActual.value = null
        _informacionEnvio.value = null
        _direccionEnvio.value = null
        _metodoPago.value = null
    }
}