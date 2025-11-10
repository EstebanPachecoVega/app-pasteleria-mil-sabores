package com.example.app_pasteleria_mil_sabores.data

import androidx.room.*
import com.example.app_pasteleria_mil_sabores.model.Pedido
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {
    @Insert
    suspend fun insertar(pedido: Pedido)

    @Update
    suspend fun actualizar(pedido: Pedido)

    @Query("SELECT * FROM Pedido WHERE usuarioId = :usuarioId ORDER BY fechaCreacion DESC")
    fun obtenerPedidosPorUsuario(usuarioId: String): Flow<List<Pedido>>

    @Query("SELECT * FROM Pedido WHERE id = :id")
    suspend fun obtenerPedidoPorId(id: String): Pedido?

    @Query("SELECT * FROM Pedido WHERE usuarioId = :usuarioId AND estado = :estado ORDER BY fechaCreacion DESC")
    fun obtenerPedidosPorEstado(usuarioId: String, estado: String): Flow<List<Pedido>>

    @Query("UPDATE Pedido SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: String, estado: String)
}