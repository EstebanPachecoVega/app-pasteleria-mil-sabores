package com.example.app_pasteleria_mil_sabores.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.app_pasteleria_mil_sabores.model.Usuario

@Dao
interface UsuarioDao {
    @Insert
    suspend fun insertar(usuario: Usuario)

    @Query("SELECT * FROM Usuario")
    suspend fun obtenerUsuarios(): List<Usuario>

    @Query("SELECT * FROM Usuario WHERE email = :email AND password = :password")
    suspend fun autenticarUsuario(email: String, password: String): Usuario?

    @Query("SELECT * FROM Usuario WHERE email = :email")
    suspend fun buscarPorEmail(email: String): Usuario?

    @Query("SELECT * FROM Usuario WHERE username = :username")
    suspend fun buscarPorUsername(username: String): Usuario?

    @Update
    suspend fun actualizarUsuario(usuario: Usuario)

    @Query("UPDATE Usuario SET password = :nuevaPassword WHERE id = :id")
    suspend fun actualizarPassword(id: String, nuevaPassword: String)

    @Query("SELECT * FROM Usuario WHERE tipoUsuario = :tipo")
    suspend fun obtenerUsuariosPorTipo(tipo: String): List<Usuario>

    @Query("SELECT * FROM Usuario WHERE id = :id")
    suspend fun obtenerUsuarioPorId(id: String): Usuario?

    @Query("SELECT * FROM Usuario WHERE codigoPromocion = :codigo")
    suspend fun buscarPorCodigoPromocional(codigo: String): Usuario?

    @Query("UPDATE Usuario SET fotoPerfil = :fotoPerfil WHERE id = :id")
    suspend fun actualizarFotoPerfil(id: String, fotoPerfil: String?)

    @Query("SELECT fotoPerfil FROM Usuario WHERE id = :id")
    suspend fun obtenerFotoPerfil(id: String): String?
}