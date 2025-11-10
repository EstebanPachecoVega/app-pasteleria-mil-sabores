package com.example.app_pasteleria_mil_sabores.data

import com.example.app_pasteleria_mil_sabores.model.Pedido
import kotlinx.coroutines.flow.Flow

class PedidoRepository(private val pedidoDao: PedidoDao) {

    suspend fun insertar(pedido: Pedido) = pedidoDao.insertar(pedido)

    suspend fun actualizar(pedido: Pedido) = pedidoDao.actualizar(pedido)

    fun obtenerPedidosPorUsuario(usuarioId: String): Flow<List<Pedido>> =
        pedidoDao.obtenerPedidosPorUsuario(usuarioId)

    suspend fun obtenerPedidoPorId(id: String): Pedido? =
        pedidoDao.obtenerPedidoPorId(id)

    fun obtenerPedidosPorEstado(usuarioId: String, estado: String): Flow<List<Pedido>> =
        pedidoDao.obtenerPedidosPorEstado(usuarioId, estado)

    suspend fun actualizarEstado(id: String, estado: String) =
        pedidoDao.actualizarEstado(id, estado)
}