package com.example.app_pasteleria_mil_sabores.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@Entity(tableName = "Usuario")
data class Usuario(
    @PrimaryKey
    val id: String,
    val nombre: String,
    val password: String,
    val tipoUsuario: String,
    val fechaNacimiento: String? = null, // Formato: "dd/MM/yyyy"
    val codigoPromocion: String? = null,
    val fechaRegistro: Long = System.currentTimeMillis()
) {
    // Propiedades computadas para validaciones futuras
    val esEstudianteDuoc: Boolean
        get() = nombre.endsWith("@duoc.cl", ignoreCase = true) &&
                !nombre.endsWith("@profesor.duoc.cl", ignoreCase = true)

    val esProfesorDuoc: Boolean
        get() = nombre.endsWith("@profesor.duoc.cl", ignoreCase = true)

    val correoValido: Boolean
        get() = nombre.endsWith("@duoc.cl", ignoreCase = true) ||
                nombre.endsWith("@profesor.duoc.cl", ignoreCase = true) ||
                nombre.endsWith("@gmail.com", ignoreCase = true)

    // Propiedad computada para la edad
    val edad: Int?
        get() = calcularEdad()

    // Función para calcular la edad
    private fun calcularEdad(): Int? {
        if (fechaNacimiento == null) return null
        return try {
            val parts = fechaNacimiento.split("/")
            if (parts.size != 3) return null

            val day = parts[0].toInt()
            val month = parts[1].toInt()
            val year = parts[2].toInt()

            val today = java.util.Calendar.getInstance()
            val birthDate = java.util.Calendar.getInstance().apply {
                set(year, month - 1, day)
            }

            var age = today.get(java.util.Calendar.YEAR) - birthDate.get(java.util.Calendar.YEAR)
            if (today.get(java.util.Calendar.DAY_OF_YEAR) < birthDate.get(java.util.Calendar.DAY_OF_YEAR)) {
                age--
            }
            age
        } catch (e: Exception) {
            null
        }
    }

    fun esSuCumpleanos(): Boolean {
        if (fechaNacimiento == null) return false
        return try {
            val today = java.util.Calendar.getInstance()
            val parts = fechaNacimiento.split("/")
            if (parts.size != 3) return false

            val day = parts[0].toInt()
            val month = parts[1].toInt()

            today.get(java.util.Calendar.DAY_OF_MONTH) == day &&
                    (today.get(java.util.Calendar.MONTH) + 1) == month
        } catch (e: Exception) {
            false
        }
    }
}