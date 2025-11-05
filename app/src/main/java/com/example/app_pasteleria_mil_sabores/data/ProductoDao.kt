package com.example.app_pasteleria_mil_sabores.data

import androidx.room.*
import com.example.app_pasteleria_mil_sabores.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(producto: Producto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(productos: List<Producto>)

    @Update
    suspend fun actualizar(producto: Producto)

    @Query("UPDATE Producto SET activo = :activo WHERE id = :id")
    suspend fun cambiarEstado(id: String, activo: Boolean)

    @Query("SELECT * FROM Producto WHERE activo = 1")
    fun obtenerTodos(): Flow<List<Producto>>

    @Query("SELECT * FROM Producto WHERE id = :id AND activo = 1")
    suspend fun obtenerPorId(id: String): Producto?

    @Query("SELECT * FROM Producto WHERE categoria = :categoria AND activo = 1")
    fun obtenerPorCategoria(categoria: String): Flow<List<Producto>>

    @Query("SELECT * FROM Producto WHERE destacado = 1 AND activo = 1")
    fun obtenerDestacados(): Flow<List<Producto>>

    @Query("SELECT * FROM Producto WHERE nombre LIKE '%' || :query || '%' AND activo = 1")
    fun buscar(query: String): Flow<List<Producto>>

    @Query("SELECT DISTINCT categoria FROM Producto WHERE activo = 1")
    fun obtenerCategorias(): Flow<List<String>>
}