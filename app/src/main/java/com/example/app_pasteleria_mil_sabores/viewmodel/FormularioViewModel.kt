package com.example.app_pasteleria_mil_sabores.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_pasteleria_mil_sabores.data.UsuarioDao
import com.example.app_pasteleria_mil_sabores.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FormularioViewModel (private val usuarioDao: UsuarioDao) : ViewModel(){
    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())

    val usuarios = _usuarios.asStateFlow()

    fun agregarUsuarios(nombre: String, password: String){
        val nuevoUsuario = Usuario(nombre = nombre, password = password)

        viewModelScope.launch {
            usuarioDao.insertar(nuevoUsuario)
            _usuarios.value = usuarioDao.obtenerUsuarios()
        }
    }

    fun mostrarUsuarios(){
        viewModelScope.launch {
            _usuarios.value = usuarioDao.obtenerUsuarios()
        }
    }
}