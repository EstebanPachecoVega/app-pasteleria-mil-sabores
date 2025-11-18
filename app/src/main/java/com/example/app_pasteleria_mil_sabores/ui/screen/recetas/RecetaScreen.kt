package com.example.app_pasteleria_mil_sabores.ui.screen.recetas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.RecetaDetailState
import com.example.app_pasteleria_mil_sabores.viewmodel.RecetaViewModel

@Composable
fun RecetaScreen(
    onBackPressed: () -> Unit,
    viewModel: RecetaViewModel = viewModel()
) {
    val recetaState by viewModel.recetaState.collectAsState()
    val recetaDetailState by viewModel.recetaDetailState.collectAsState()

    when (recetaDetailState) {
        is RecetaDetailState.Success -> {
            val receta = (recetaDetailState as RecetaDetailState.Success).receta
            RecetaDetailScreen(
                receta = receta,
                onBack = { viewModel.limpiarDetalles() }
            )
        }
        else -> {
            RecetaListScreen(
                state = recetaState,
                onRecetaClick = { id -> viewModel.cargarDetallesReceta(id) },
                onRetry = { viewModel.cargarPostres() },
                onBackPressed = onBackPressed
            )
        }
    }
}