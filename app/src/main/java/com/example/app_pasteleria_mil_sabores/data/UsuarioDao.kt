package com.example.app_pasteleria_mil_sabores.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.app_pasteleria_mil_sabores.model.Usuario

@Dao
interface UsuarioDao {
    @Insert
    suspend fun insertar(usuario: Usuario)

    @Query("SELECT * FROM Usuario")
    suspend fun obtenerUsuarios(): List<Usuario>

    @Query("SELECT * FROM Usuario WHERE nombre = :nombre AND password = :password")
    suspend fun autenticarUsuario(nombre: String, password: String): Usuario?

    @Query("SELECT * FROM Usuario WHERE nombre = :nombre")
    suspend fun buscarPorNombre(nombre: String): Usuario?

    @Query("UPDATE Usuario SET password = :nuevaPassword WHERE nombre = :nombre")
    suspend fun actualizarPassword(nombre: String, nuevaPassword: String)

    // Nueva consulta para obtener usuarios por tipo
    @Query("SELECT * FROM Usuario WHERE tipoUsuario = :tipo")
    suspend fun obtenerUsuariosPorTipo(tipo: String): List<Usuario>
}