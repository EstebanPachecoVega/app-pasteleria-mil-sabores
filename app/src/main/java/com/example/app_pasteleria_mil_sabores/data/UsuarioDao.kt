package com.example.app_pasteleria_mil_sabores.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.app_pasteleria_mil_sabores.model.Usuario

@Dao
interface UsuarioDao {
    @Insert
    suspend fun insertar(usuario : Usuario)

    @Query("SELECT * FROM Usuario")
    suspend fun obtenerUsuarios() : List<Usuario>
}