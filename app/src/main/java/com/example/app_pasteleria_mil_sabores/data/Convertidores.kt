package com.example.app_pasteleria_mil_sabores.data

import androidx.room.TypeConverter
import com.example.app_pasteleria_mil_sabores.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Convertidores {
    private val gson = Gson()

    // Convertidores para CartItem
    @TypeConverter
    fun fromListaCartItem(valor: List<CartItem>): String {
        return gson.toJson(valor)
    }

    @TypeConverter
    fun toListaCartItem(valor: String): List<CartItem> {
        return gson.fromJson(valor, object : TypeToken<List<CartItem>>() {}.type)
    }

    // Convertidores para Direccion
    @TypeConverter
    fun fromDireccion(valor: Direccion?): String {
        return gson.toJson(valor)
    }

    @TypeConverter
    fun toDireccion(valor: String): Direccion? {
        return gson.fromJson(valor, Direccion::class.java)
    }

    // Convertidores para InformacionContacto
    @TypeConverter
    fun fromInformacionContacto(valor: InformacionContacto): String {
        return gson.toJson(valor)
    }

    @TypeConverter
    fun toInformacionContacto(valor: String): InformacionContacto {
        return gson.fromJson(valor, InformacionContacto::class.java)
    }

    // Convertidores para Coordenadas
    @TypeConverter
    fun fromCoordenadas(valor: Coordenadas?): String {
        return gson.toJson(valor)
    }

    @TypeConverter
    fun toCoordenadas(valor: String): Coordenadas? {
        return gson.fromJson(valor, Coordenadas::class.java)
    }
}