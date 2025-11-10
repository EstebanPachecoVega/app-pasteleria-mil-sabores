package com.example.app_pasteleria_mil_sabores.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.app_pasteleria_mil_sabores.data.Convertidores

@Entity(tableName = "Pedido")
@TypeConverters(Convertidores::class)
data class Pedido(
    @PrimaryKey
    val id: String,
    val usuarioId: String,
    val productos: List<CartItem>,
    val estado: String,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val subtotal: Int,
    val descuentoAplicado: Int,
    val costoEnvio: Int,
    val total: Int,
    val direccionEnvio: Direccion?,
    val metodoPago: String,
    val informacionContacto: InformacionContacto
)