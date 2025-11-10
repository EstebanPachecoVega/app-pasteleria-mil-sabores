package com.example.app_pasteleria_mil_sabores.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "Pedido")
data class Pedido(
    @PrimaryKey
    val id: String,
    val usuarioId: String,
    val productos: List<CartItem>,
    val estado: String, // "pendiente", "confirmado", "enviado", "entregado"
    val fechaCreacion: Long = System.currentTimeMillis(),
    val subtotal: Int,
    val descuentoAplicado: Int,
    val costoEnvio: Int,
    val total: Int,
    val direccionEnvio: Direccion?,
    val metodoPago: String,
    val informacionContacto: InformacionContacto
)

data class Direccion(
    val calle: String,
    val numero: String,
    val comuna: String,
    val ciudad: String,
    val region: String,
    val coordenadas: Coordenadas? // lat, lng
)

data class Coordenadas(
    val latitud: Double,
    val longitud: Double
)

data class InformacionContacto(
    val nombre: String,
    val email: String,
    val telefono: String?
)