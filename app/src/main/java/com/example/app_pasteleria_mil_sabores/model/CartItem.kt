package com.example.app_pasteleria_mil_sabores.model

data class CartItem(
    val producto: Producto,
    var cantidad: Int,
    val fechaAgregado: Long = System.currentTimeMillis()
) {
    fun getPrecioTotal(): Int = producto.precio * cantidad
}