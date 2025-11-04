package com.example.app_pasteleria_mil_sabores.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Usuario")
data class Usuario (
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val nombre : String,
    val password : String,
    val tipoUsuario: String
)