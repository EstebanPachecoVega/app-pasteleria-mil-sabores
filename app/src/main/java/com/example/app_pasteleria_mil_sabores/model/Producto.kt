package com.example.app_pasteleria_mil_sabores.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Producto")
data class Producto(
    @PrimaryKey
    val id: String,
    val nombre: String,
    val descripcion: String,
    val precio: Int,
    val imagen: String,
    val categoria: String,
    val stock: Int,
    val destacado: Boolean = false,
    val activo: Boolean = true,
    val fechaCreacion: Long = System.currentTimeMillis(),
)