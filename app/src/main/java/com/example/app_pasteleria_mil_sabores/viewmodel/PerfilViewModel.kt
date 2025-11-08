package com.example.app_pasteleria_mil_sabores.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_pasteleria_mil_sabores.data.UsuarioDao
import com.example.app_pasteleria_mil_sabores.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerfilViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje = _mensaje.asStateFlow()

    private val _cambiosLimitadosRealizados = MutableStateFlow(0)
    val cambiosLimitadosRealizados = _cambiosLimitadosRealizados.asStateFlow()

    private val _usuarioActualizado = MutableStateFlow<Usuario?>(null)
    val usuarioActualizado = _usuarioActualizado.asStateFlow()

    private val _fotoPerfilActualizada = MutableStateFlow<String?>(null)
    val fotoPerfilActualizada = _fotoPerfilActualizada.asStateFlow()

    // Contador interno para cambios limitados
    private var cambiosRealizados = 0
    private val MAX_CAMBIOS = 3

    fun actualizarUsuario(usuarioActualizado: Usuario, esCambioLimitado: Boolean = false) {
        viewModelScope.launch {
            try {
                if (esCambioLimitado && cambiosRealizados >= MAX_CAMBIOS) {
                    _mensaje.value = "Has alcanzado el límite máximo de 3 cambios permitidos"
                    return@launch
                }

                val usuarioExistente = usuarioDao.buscarPorUsername(usuarioActualizado.username)
                if (usuarioExistente != null && usuarioExistente.id != usuarioActualizado.id) {
                    _mensaje.value = "El nombre de usuario ya está en uso"
                    return@launch
                }

                usuarioDao.actualizarUsuario(usuarioActualizado)
                if (esCambioLimitado) {
                    cambiosRealizados++
                    _cambiosLimitadosRealizados.value = cambiosRealizados
                }
                _mensaje.value = "Cambios guardados exitosamente"
                _usuarioActualizado.value = usuarioActualizado
            } catch (e: Exception) {
                _mensaje.value = "Error al guardar los cambios"
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    fun puedeRealizarCambiosLimitados(): Boolean {
        return cambiosRealizados < MAX_CAMBIOS
    }

    fun actualizarFotoPerfil(usuario: Usuario, fotoPerfilUri: String?) {
        viewModelScope.launch {
            try {
                usuarioDao.actualizarFotoPerfil(usuario.id, fotoPerfilUri)
                _fotoPerfilActualizada.value = fotoPerfilUri
                _mensaje.value = "Foto de perfil actualizada"

                // Actualizar el usuario completo
                val usuarioActualizado = usuario.copy(fotoPerfil = fotoPerfilUri)
                _usuarioActualizado.value = usuarioActualizado
            } catch (e: Exception) {
                _mensaje.value = "Error al actualizar la foto"
            }
        }
    }

    fun cambiarPassword(usuario: Usuario, nuevaPassword: String) {
        viewModelScope.launch {
            try {
                if (cambiosRealizados >= MAX_CAMBIOS) {
                    _mensaje.value = "Has alcanzado el límite máximo de 3 cambios permitidos"
                    return@launch
                }

                val usuarioActualizado = usuario.copy(password = nuevaPassword)
                usuarioDao.actualizarUsuario(usuarioActualizado)
                cambiosRealizados++
                _cambiosLimitadosRealizados.value = cambiosRealizados
                _mensaje.value = "Contraseña cambiada exitosamente"
                _usuarioActualizado.value = usuarioActualizado
            } catch (e: Exception) {
                _mensaje.value = "Error al cambiar la contraseña"
            }
        }
    }

    fun resetearContadores() {
        cambiosRealizados = 0
        _cambiosLimitadosRealizados.value = 0
    }

    fun limpiarEstado() {
        _fotoPerfilActualizada.value = null
        _mensaje.value = null
        _usuarioActualizado.value = null
    }

    fun getCambiosRestantes(): Int {
        return MAX_CAMBIOS - cambiosRealizados
    }
}