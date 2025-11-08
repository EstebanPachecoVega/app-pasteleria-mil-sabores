package com.example.app_pasteleria_mil_sabores.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "Usuario")
data class Usuario(
    @PrimaryKey
    val id: String,
    val username: String,
    val email: String,
    val password: String,
    val tipoUsuario: String,
    val fechaNacimiento: String? = null,
    val codigoPromocion: String? = null,
    val fechaRegistro: Long = System.currentTimeMillis(),
    val fotoPerfil: String? = null
) {
    val esEstudianteDuoc: Boolean
        get() = email.endsWith("@duoc.cl", ignoreCase = true) &&
                !email.endsWith("@profesor.duoc.cl", ignoreCase = true)

    val esProfesorDuoc: Boolean
        get() = email.endsWith("@profesor.duoc.cl", ignoreCase = true)

    val correoValido: Boolean
        get() = email.endsWith("@duoc.cl", ignoreCase = true) ||
                email.endsWith("@profesor.duoc.cl", ignoreCase = true) ||
                email.endsWith("@gmail.com", ignoreCase = true)

    val edad: Int?
        get() = calcularEdad()

    private fun calcularEdad(): Int? {
        if (fechaNacimiento == null) return null
        return try {
            val parts = fechaNacimiento.split("/")
            if (parts.size != 3) return null

            val day = parts[0].toInt()
            val month = parts[1].toInt()
            val year = parts[2].toInt()

            val today = Calendar.getInstance()
            val birthDate = Calendar.getInstance().apply {
                set(year, month - 1, day)
            }

            var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
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
            val today = Calendar.getInstance()
            val parts = fechaNacimiento.split("/")
            if (parts.size != 3) return false

            val day = parts[0].toInt()
            val month = parts[1].toInt()

            // Verificar si hoy es el cumpleaños (día y mes coinciden)
            today.get(Calendar.DAY_OF_MONTH) == day &&
                    (today.get(Calendar.MONTH) + 1) == month
        } catch (e: Exception) {
            false
        }
    }
}