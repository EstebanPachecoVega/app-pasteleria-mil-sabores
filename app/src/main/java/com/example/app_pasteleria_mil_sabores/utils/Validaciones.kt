package com.example.app_pasteleria_mil_sabores.utils

import java.text.SimpleDateFormat
import java.util.*

object Validaciones {

    // Validar que no tenga espacios y cumpla longitud mínima
    fun validarSinEspacios(texto: String, longitudMinima: Int = 1): Boolean {
        return texto.isNotBlank() &&
                texto.length >= longitudMinima &&
                !texto.contains(" ")
    }

    // Validar nombre de usuario (3+ caracteres, sin espacios)
    fun validarUsername(username: String): Boolean {
        return validarSinEspacios(username, 3)
    }

    // Validar contraseña (6+ caracteres, sin espacios)
    fun validarPassword(password: String): Boolean {
        return validarSinEspacios(password, 6)
    }

    // Validar fecha (formato dd/MM/yyyy o vacío)
    fun validarFechaNacimiento(fecha: String): Boolean {
        if (fecha.isBlank()) return true // Opcional

        // Validar formato básico
        if (!fecha.contains("/")) return false

        val partes = fecha.split("/")
        if (partes.size != 3) return false

        val dia = partes[0]
        val mes = partes[1]
        val anio = partes[2]

        // Validar longitudes
        if (dia.length != 2 || mes.length != 2 || anio.length != 4) return false

        // Validar que sean números
        if (!dia.all { it.isDigit() } || !mes.all { it.isDigit() } || !anio.all { it.isDigit() }) return false

        // Validar rangos razonables
        val diaNum = dia.toInt()
        val mesNum = mes.toInt()
        val anioNum = anio.toInt()

        return diaNum in 1..31 &&
                mesNum in 1..12 &&
                anioNum in 1900..2100
    }

    // Validar si es mayor de edad (18+ años)
    fun esMayorDeEdad(fechaNacimiento: String): Boolean {
        if (fechaNacimiento.isBlank()) return true

        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaNac = dateFormat.parse(fechaNacimiento)
            val hoy = Calendar.getInstance()
            val nacimiento = Calendar.getInstance().apply { time = fechaNac }

            var edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)

            // Ajustar si aún no ha cumplido años este año
            if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) {
                edad--
            }

            edad >= 18
        } catch (e: Exception) {
            false
        }
    }

    // Validar si tiene más de 17 años (para registro)
    fun esMayorDe17Anios(fechaNacimiento: String): Boolean {
        if (fechaNacimiento.isBlank()) return true

        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaNac = dateFormat.parse(fechaNacimiento)
            val hoy = Calendar.getInstance()
            val nacimiento = Calendar.getInstance().apply { time = fechaNac }

            var edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)

            // Ajustar si aún no ha cumplido años este año
            if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) {
                edad--
            }

            edad >= 17 // Mayor de 17 años (18+)
        } catch (e: Exception) {
            false
        }
    }

    // Validar que la fecha no sea futura
    fun validarFechaNoFutura(fecha: String): Boolean {
        if (fecha.isBlank()) return true

        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaIngresada = dateFormat.parse(fecha)
            val hoy = Calendar.getInstance()

            !fechaIngresada.after(hoy.time)
        } catch (e: Exception) {
            false
        }
    }

    // Validación completa de fecha de nacimiento
    fun validarFechaNacimientoCompleta(fecha: String): Boolean {
        if (fecha.isBlank()) return true // Opcional

        return validarFechaNacimiento(fecha) &&
                validarFechaNoFutura(fecha) &&
                esMayorDe17Anios(fecha)
    }

    // Obtener la edad actual
    fun obtenerEdad(fechaNacimiento: String): Int? {
        if (fechaNacimiento.isBlank()) return null

        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaNac = dateFormat.parse(fechaNacimiento)
            val hoy = Calendar.getInstance()
            val nacimiento = Calendar.getInstance().apply { time = fechaNac }

            var edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)

            // Ajustar si aún no ha cumplido años este año
            if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) {
                edad--
            }

            edad
        } catch (e: Exception) {
            null
        }
    }

    // Solo limpiar espacios, sin formateo automático
    fun limpiarFechaInput(input: String): String {
        return input.filter { it != ' ' }
    }
}