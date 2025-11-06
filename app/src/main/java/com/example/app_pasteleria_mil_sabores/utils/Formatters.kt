package com.example.app_pasteleria_mil_sabores.utils

fun Int.formatearPrecio(): String {
    return "$${this.toString().replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1.")}"
}