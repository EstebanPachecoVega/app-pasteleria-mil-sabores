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

    fun actualizarUsuario(usuarioActualizado: Usuario, esCambioLimitado: Boolean = false) {
        viewModelScope.launch {
            try {
                val usuarioExistente = usuarioDao.buscarPorUsername(usuarioActualizado.username)
                if (usuarioExistente != null && usuarioExistente.id != usuarioActualizado.id) {
                    _mensaje.value = "El nombre de usuario ya está en uso"
                    return@launch
                }

                usuarioDao.actualizarUsuario(usuarioActualizado)
                if (esCambioLimitado) {
                    _cambiosLimitadosRealizados.value += 1
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
        return _cambiosLimitadosRealizados.value < 3
    }

    fun actualizarFotoPerfil(usuario: Usuario, fotoPerfilUri: String?) {
        viewModelScope.launch {
            try {
                val usuarioActualizado = usuario.copy(fotoPerfil = fotoPerfilUri)
                usuarioDao.actualizarUsuario(usuarioActualizado)
                _mensaje.value = "Foto de perfil actualizada"
                _usuarioActualizado.value = usuarioActualizado
            } catch (e: Exception) {
                _mensaje.value = "Error al actualizar la foto"
            }
        }
    }

    fun cambiarPassword(usuario: Usuario, nuevaPassword: String) {
        viewModelScope.launch {
            try {
                val usuarioActualizado = usuario.copy(password = nuevaPassword)
                usuarioDao.actualizarUsuario(usuarioActualizado)
                _mensaje.value = "Contraseña cambiada exitosamente"
                _usuarioActualizado.value = usuarioActualizado
            } catch (e: Exception) {
                _mensaje.value = "Error al cambiar la contraseña"
            }
        }
    }

    fun resetearContadores() {
        _cambiosLimitadosRealizados.value = 0
    }
}