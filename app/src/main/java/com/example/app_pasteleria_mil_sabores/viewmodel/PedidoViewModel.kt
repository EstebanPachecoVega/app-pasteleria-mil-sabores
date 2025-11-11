package com.example.app_pasteleria_mil_sabores.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_pasteleria_mil_sabores.data.PedidoRepository
import com.example.app_pasteleria_mil_sabores.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PedidoViewModel(private val pedidoRepository: PedidoRepository) : ViewModel() {

    private val _pedidos = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidos: StateFlow<List<Pedido>> = _pedidos.asStateFlow()

    private val _todosLosPedidos = MutableStateFlow<List<Pedido>>(emptyList())
    val todosLosPedidos: StateFlow<List<Pedido>> = _todosLosPedidos.asStateFlow()

    private val _pedidoSeleccionado = MutableStateFlow<Pedido?>(null)
    val pedidoSeleccionado: StateFlow<Pedido?> = _pedidoSeleccionado.asStateFlow()

    private val _estadoFiltro = MutableStateFlow<String?>(null)
    val estadoFiltro: StateFlow<String?> = _estadoFiltro.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // FUNCIÓN PERMANENTE PARA NORMALIZAR ESTADOS
    private fun normalizarEstado(estado: String): String {
        return when (estado.lowercase()) {
            "pendiente" -> "Pendiente"
            "confirmado" -> "Confirmado"
            "en preparación", "en preparacion" -> "En preparación"
            "enviado" -> "Enviado"
            "entregado" -> "Entregado"
            "cancelado" -> "Cancelado"
            else -> estado // Mantener el estado original si no coincide
        }
    }

    // FUNCIÓN PARA NORMALIZAR UN PEDIDO COMPLETO
    private fun normalizarPedido(pedido: Pedido): Pedido {
        return pedido.copy(estado = normalizarEstado(pedido.estado))
    }

    fun cargarPedidos(usuarioId: String) {
        viewModelScope.launch {
            try {
                _cargando.value = true
                _errorMessage.value = null

                pedidoRepository.obtenerPedidosPorUsuario(usuarioId).collect { listaPedidos ->
                    // Normalizar todos los pedidos al cargar
                    val pedidosNormalizados = listaPedidos.map { normalizarPedido(it) }
                    _pedidos.value = pedidosNormalizados.sortedByDescending { it.fechaCreacion }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar pedidos: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    // NUEVA FUNCIÓN: Cargar todos los pedidos del sistema
    fun cargarTodosLosPedidos() {
        viewModelScope.launch {
            try {
                _cargando.value = true
                _errorMessage.value = null

                pedidoRepository.obtenerTodosLosPedidos().collect { listaPedidos ->
                    // Normalizar todos los pedidos al cargar
                    val pedidosNormalizados = listaPedidos.map { normalizarPedido(it) }
                    _todosLosPedidos.value = pedidosNormalizados.sortedByDescending { it.fechaCreacion }
                    println("DEBUG - Pedidos cargados y normalizados: ${listaPedidos.size}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar todos los pedidos: ${e.message}"
                println("DEBUG - Error cargando pedidos: ${e.message}")
            } finally {
                _cargando.value = false
                println("DEBUG - Carga de pedidos completada")
            }
        }
    }

    fun cargarPedidoPorId(id: String) {
        viewModelScope.launch {
            try {
                _cargando.value = true
                _errorMessage.value = null
                val pedido = pedidoRepository.obtenerPedidoPorId(id)
                _pedidoSeleccionado.value = pedido?.let { normalizarPedido(it) }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar pedido: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun actualizarEstadoPedido(id: String, nuevoEstado: String) {
        viewModelScope.launch {
            try {
                _errorMessage.value = null
                // Asegurarse de que el nuevo estado esté normalizado
                val estadoNormalizado = normalizarEstado(nuevoEstado)
                pedidoRepository.actualizarEstado(id, estadoNormalizado)

                // Recargar pedidos para reflejar el cambio
                _pedidoSeleccionado.value?.let { pedido ->
                    if (pedido.id == id) {
                        cargarPedidoPorId(id)
                    }
                }
                // Recargar todos los pedidos para actualizar la lista
                cargarTodosLosPedidos()
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar estado: ${e.message}"
            }
        }
    }

    fun filtrarPorEstado(estado: String?) {
        _estadoFiltro.value = estado
    }

    fun obtenerPedidosFiltrados(): List<Pedido> {
        return _estadoFiltro.value?.let { estado ->
            _pedidos.value.filter { it.estado == estado }
        } ?: _pedidos.value
    }

    fun limpiarError() {
        _errorMessage.value = null
    }

    fun limpiarPedidoSeleccionado() {
        _pedidoSeleccionado.value = null
    }
}