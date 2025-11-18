package com.example.app_pasteleria_mil_sabores.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_pasteleria_mil_sabores.model.Receta
import com.example.app_pasteleria_mil_sabores.model.RecetaBasic
import com.example.app_pasteleria_mil_sabores.repository.RecetaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RecetaState {
    object Loading : RecetaState()
    data class Success(val postres: List<RecetaBasic>) : RecetaState()
    data class Error(val message: String) : RecetaState()
}

sealed class RecetaDetailState {
    object Loading : RecetaDetailState()
    data class Success(val receta: Receta) : RecetaDetailState()
    data class Error(val message: String) : RecetaDetailState()
    object Initial : RecetaDetailState()
}

class RecetaViewModel : ViewModel() {
    private val repository = RecetaRepository()

    private val _recetaState = MutableStateFlow<RecetaState>(RecetaState.Loading)
    val recetaState: StateFlow<RecetaState> = _recetaState.asStateFlow()

    private val _recetaDetailState = MutableStateFlow<RecetaDetailState>(RecetaDetailState.Initial)
    val recetaDetailState: StateFlow<RecetaDetailState> = _recetaDetailState.asStateFlow()

    init {
        cargarPostres()
    }

    fun cargarPostres() {
        viewModelScope.launch {
            _recetaState.value = RecetaState.Loading
            try {
                val postres = repository.getPostres()
                _recetaState.value = RecetaState.Success(postres)
            } catch (e: Exception) {
                _recetaState.value = RecetaState.Error("Error al cargar los postres: ${e.message}")
            }
        }
    }

    fun cargarDetallesReceta(id: String) {
        viewModelScope.launch {
            _recetaDetailState.value = RecetaDetailState.Loading
            try {
                val receta = repository.getRecetaById(id)
                if (receta != null) {
                    _recetaDetailState.value = RecetaDetailState.Success(receta)
                } else {
                    _recetaDetailState.value = RecetaDetailState.Error("No se encontró la receta")
                }
            } catch (e: Exception) {
                _recetaDetailState.value = RecetaDetailState.Error("Error al cargar los detalles: ${e.message}")
            }
        }
    }

    fun limpiarDetalles() {
        _recetaDetailState.value = RecetaDetailState.Initial
    }
}