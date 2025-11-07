package com.example.app_pasteleria_mil_sabores.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_pasteleria_mil_sabores.data.UsuarioDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerfilViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje = _mensaje.asStateFlow()

    private val _cambiosLimitadosRealizados = MutableStateFlow(0)
    val cambiosLimitadosRealizados = _cambiosLimitadosRealizados.asStateFlow()

    fun actualizarUsuario(usuarioActualizado: com.example.app_pasteleria_mil_sabores.model.Usuario, esCambioLimitado: Boolean = false) {
        viewModelScope.launch {
            try {
                usuarioDao.actualizarUsuario(usuarioActualizado)
                if (esCambioLimitado) {
                    _cambiosLimitadosRealizados.value += 1
                }
                _mensaje.value = "Perfil actualizado correctamente"
            } catch (e: Exception) {
                _mensaje.value = "Error al actualizar: ${e.message}"
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    fun puedeRealizarCambiosLimitados(): Boolean {
        return _cambiosLimitadosRealizados.value < 3
    }

    fun actualizarFotoPerfil(usuario: com.example.app_pasteleria_mil_sabores.model.Usuario, fotoPerfilUri: String?) {
        viewModelScope.launch {
            try {
                val usuarioActualizado = usuario.copy(fotoPerfil = fotoPerfilUri)
                usuarioDao.actualizarUsuario(usuarioActualizado)
                _mensaje.value = "Foto de perfil actualizada"
            } catch (e: Exception) {
                _mensaje.value = "Error al actualizar foto: ${e.message}"
            }
        }
    }
}