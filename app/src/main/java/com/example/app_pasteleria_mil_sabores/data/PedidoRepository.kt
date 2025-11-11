package com.example.app_pasteleria_mil_sabores.data

import com.example.app_pasteleria_mil_sabores.model.Pedido
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class PedidoRepository(private val pedidoDao: PedidoDao) {

    suspend fun insertar(pedido: Pedido) = pedidoDao.insertar(pedido)

    suspend fun actualizar(pedido: Pedido) = pedidoDao.actualizar(pedido)

    fun obtenerPedidosPorUsuario(usuarioId: String): Flow<List<Pedido>> =
        pedidoDao.obtenerPedidosPorUsuario(usuarioId)

    suspend fun obtenerPedidoPorId(id: String): Pedido? =
        pedidoDao.obtenerPedidoPorId(id)

    fun obtenerPedidosPorEstado(usuarioId: String, estado: String): Flow<List<Pedido>> =
        pedidoDao.obtenerPedidosPorEstado(usuarioId, estado)

    fun obtenerTodosLosPedidos(): Flow<List<Pedido>> =
        pedidoDao.obtenerTodosLosPedidos()

    suspend fun actualizarEstado(id: String, estado: String) =
        pedidoDao.actualizarEstado(id, estado)

    // FUNCIÓN PERMANENTE PARA MIGRAR ESTADOS (ejecutar una vez)
    suspend fun migrarEstadosPedidos() {
        try {
            val todosPedidos = pedidoDao.obtenerTodosLosPedidos().firstOrNull() ?: emptyList()
            var pedidosActualizados = 0

            todosPedidos.forEach { pedido ->
                val estadoNormalizado = normalizarEstado(pedido.estado)
                if (estadoNormalizado != pedido.estado) {
                    pedidoDao.actualizarEstado(pedido.id, estadoNormalizado)
                    pedidosActualizados++
                }
            }

            println("DEBUG - Migración completada: $pedidosActualizados pedidos actualizados")
        } catch (e: Exception) {
            println("DEBUG - Error en migración: ${e.message}")
        }
    }

    // FUNCIÓN AUXILIAR PARA NORMALIZAR ESTADOS
    private fun normalizarEstado(estado: String): String {
        return when (estado.lowercase()) {
            "pendiente" -> "Pendiente"
            "confirmado" -> "Confirmado"
            "en preparación", "en preparacion" -> "En preparación"
            "enviado" -> "Enviado"
            "entregado" -> "Entregado"
            "cancelado" -> "Cancelado"
            else -> estado
        }
    }
}