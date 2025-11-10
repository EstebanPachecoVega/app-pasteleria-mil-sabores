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

    private val _pedidoSeleccionado = MutableStateFlow<Pedido?>(null)
    val pedidoSeleccionado: StateFlow<Pedido?> = _pedidoSeleccionado.asStateFlow()

    private val _estadoFiltro = MutableStateFlow<String?>(null)
    val estadoFiltro: StateFlow<String?> = _estadoFiltro.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun cargarPedidos(usuarioId: String) {
        viewModelScope.launch {
            try {
                _cargando.value = true
                _errorMessage.value = null

                pedidoRepository.obtenerPedidosPorUsuario(usuarioId).collect { listaPedidos ->
                    _pedidos.value = listaPedidos.sortedByDescending { it.fechaCreacion }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar pedidos: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun cargarPedidoPorId(id: String) {
        viewModelScope.launch {
            try {
                _cargando.value = true
                _errorMessage.value = null
                _pedidoSeleccionado.value = pedidoRepository.obtenerPedidoPorId(id)
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar pedido: ${e.message}"
            } finally {
                _cargando.value = false
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

    fun actualizarEstadoPedido(id: String, nuevoEstado: String) {
        viewModelScope.launch {
            try {
                _errorMessage.value = null
                pedidoRepository.actualizarEstado(id, nuevoEstado)
                // Recargar pedidos para reflejar el cambio
                _pedidoSeleccionado.value?.let { pedido ->
                    if (pedido.id == id) {
                        cargarPedidoPorId(id)
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar estado: ${e.message}"
            }
        }
    }

    fun limpiarError() {
        _errorMessage.value = null
    }

    fun limpiarPedidoSeleccionado() {
        _pedidoSeleccionado.value = null
    }
}