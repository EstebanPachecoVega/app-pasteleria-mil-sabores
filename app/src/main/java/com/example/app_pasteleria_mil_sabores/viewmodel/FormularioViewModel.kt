package com.example.app_pasteleria_mil_sabores.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_pasteleria_mil_sabores.data.UsuarioDao
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.utils.IdGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FormularioViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {
    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val usuarios = _usuarios.asStateFlow()
    val usuarioActual = _usuarioActual.asStateFlow()
    val errorMessage = _errorMessage.asStateFlow()

    // Función para determinar el tipo de usuario basado en el correo
    private fun determinarTipoUsuario(correo: String): String {
        return when {
            correo.equals("admin@duoc.cl", ignoreCase = true) -> "Administrador"
            correo.endsWith("@profesor.duoc.cl", ignoreCase = true) -> "Profesor"
            correo.endsWith("@duoc.cl", ignoreCase = true) -> "Cliente"
            correo.endsWith("@gmail.com", ignoreCase = true) -> "Cliente"
            else -> "Cliente" // Por defecto es Cliente
        }
    }

    fun agregarUsuario(
        correo: String,
        password: String,
        fechaNacimiento: String? = null,
        codigoPromocion: String? = null
    ) {
        viewModelScope.launch {
            try {
                // Verificar si el usuario ya existe
                val usuarioExistente = usuarioDao.buscarPorNombre(correo)
                if (usuarioExistente != null) {
                    _errorMessage.value = "El usuario ya existe"
                    return@launch
                }

                // Determinar automáticamente el tipo de usuario
                val tipoUsuario = determinarTipoUsuario(correo)

                val nuevoUsuario = Usuario(
                    id = IdGenerator.generarIdUsuario(),
                    nombre = correo,
                    password = password,
                    tipoUsuario = tipoUsuario,
                    fechaNacimiento = fechaNacimiento,
                    codigoPromocion = codigoPromocion
                )

                usuarioDao.insertar(nuevoUsuario)
                _usuarios.value = usuarioDao.obtenerUsuarios()
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Error al registrar usuario: ${e.message}"
            }
        }
    }

    // Mantener la función original para compatibilidad
    fun agregarUsuario(nombre: String, password: String) {
        agregarUsuario(nombre, password, null, null)
    }

    fun autenticarUsuario(nombre: String, password: String) {
        viewModelScope.launch {
            try {
                val usuario = usuarioDao.autenticarUsuario(nombre, password)
                if (usuario != null) {
                    _usuarioActual.value = usuario
                    _errorMessage.value = null
                    println("Usuario autenticado: ${usuario.nombre}, Tipo: ${usuario.tipoUsuario}")
                } else {
                    _errorMessage.value = "Credenciales incorrectas"
                    _usuarioActual.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al autenticar: ${e.message}"
            }
        }
    }

    fun recuperarPassword(nombre: String) {
        viewModelScope.launch {
            try {
                val usuario = usuarioDao.buscarPorNombre(nombre)
                if (usuario != null) {
                    // En una app real, aquí enviarías un email o SMS
                    _errorMessage.value = "Se ha enviado un enlace de recuperación a $nombre"
                } else {
                    _errorMessage.value = "Usuario no encontrado"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al recuperar contraseña: ${e.message}"
            }
        }
    }

    fun cerrarSesion() {
        _usuarioActual.value = null
    }

    fun limpiarError() {
        _errorMessage.value = null
    }

    fun mostrarUsuarios() {
        viewModelScope.launch {
            _usuarios.value = usuarioDao.obtenerUsuarios()
        }
    }
}