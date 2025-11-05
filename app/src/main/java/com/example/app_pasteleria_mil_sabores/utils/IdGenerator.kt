package com.example.app_pasteleria_mil_sabores.utils

import java.util.UUID

object IdGenerator {

    /**
     * Genera un ID único para productos con formato: PROD_XXXXXXX
     */
    fun generarIdProducto(): String {
        return "PROD_${UUID.randomUUID().toString().substring(0, 8).uppercase()}"
    }

    /**
     * Genera ID para pedidos
     */
    fun generarIdPedido(): String {
        return "PED_${UUID.randomUUID().toString().substring(0, 8).uppercase()}"
    }

    /**
     * Genera ID para usuarios
     */
    fun generarIdUsuario(): String {
        return "USER_${UUID.randomUUID().toString().substring(0, 8).uppercase()}"
    }
}