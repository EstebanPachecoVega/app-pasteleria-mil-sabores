package com.example.app_pasteleria_mil_sabores.model

data class Direccion(
    val calle: String,
    val numero: String,
    val departamento: String? = null,
    val comuna: String,
    val ciudad: String,
    val region: String,
    val codigoPostal: String? = null,
    val coordenadas: Coordenadas? = null,
    val instruccionesEspeciales: String? = null
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