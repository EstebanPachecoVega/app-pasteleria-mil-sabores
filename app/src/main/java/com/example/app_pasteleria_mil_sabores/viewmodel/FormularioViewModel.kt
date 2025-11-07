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

    init {
        viewModelScope.launch {
            // Crear administrador por defecto si no existe
            val adminExistente = usuarioDao.buscarPorEmail("admin@duoc.cl")
            if (adminExistente == null) {
                val admin = Usuario(
                    id = IdGenerator.generarIdUsuario(),
                    username = "admin",
                    email = "admin@duoc.cl",
                    password = "admin123",
                    tipoUsuario = "Administrador"
                )
                usuarioDao.insertar(admin)
                println("DEBUG - Administrador por defecto creado")
            }
        }
    }

    private fun determinarTipoUsuario(email: String): String {
        return when {
            email.equals("admin@duoc.cl", ignoreCase = true) -> "Administrador"
            email.endsWith("@profesor.duoc.cl", ignoreCase = true) -> "Profesor"
            email.endsWith("@duoc.cl", ignoreCase = true) -> "Cliente"
            email.endsWith("@gmail.com", ignoreCase = true) -> "Cliente"
            else -> "Cliente"
        }
    }

    fun agregarUsuario(
        username: String,
        email: String,
        password: String,
        fechaNacimiento: String? = null,
        codigoPromocion: String? = null
    ) {
        viewModelScope.launch {
            try {
                val usuarioExistente = usuarioDao.buscarPorEmail(email)
                if (usuarioExistente != null) {
                    _errorMessage.value = "El email ya está registrado"
                    return@launch
                }

                val usernameExistente = usuarioDao.buscarPorUsername(username)
                if (usernameExistente != null) {
                    _errorMessage.value = "El nombre de usuario ya está en uso"
                    return@launch
                }

                val tipoUsuario = determinarTipoUsuario(email)

                val nuevoUsuario = Usuario(
                    id = IdGenerator.generarIdUsuario(),
                    username = username,
                    email = email,
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

    fun autenticarUsuario(email: String, password: String) {
        viewModelScope.launch {
            try {
                val usuario = usuarioDao.autenticarUsuario(email, password)
                if (usuario != null) {
                    _usuarioActual.value = usuario
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Credenciales incorrectas"
                    _usuarioActual.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al autenticar: ${e.message}"
            }
        }
    }

    fun recuperarPassword(email: String) {
        viewModelScope.launch {
            try {
                val usuario = usuarioDao.buscarPorEmail(email)
                if (usuario != null) {
                    _errorMessage.value = "Se ha enviado un enlace de recuperación a $email"
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

    fun actualizarUsuarioActual(usuario: Usuario) {
        _usuarioActual.value = usuario
    }
}