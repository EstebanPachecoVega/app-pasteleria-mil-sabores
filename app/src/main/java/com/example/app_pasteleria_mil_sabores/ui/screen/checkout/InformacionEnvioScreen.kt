package com.example.app_pasteleria_mil_sabores.ui.screen.checkout

import androidx.compose.runtime.Composable
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.viewmodel.CheckoutViewModel

@Composable
fun InformacionEnvioScreen(
    checkoutViewModel: CheckoutViewModel,
    usuario: Usuario,
    onVolver: () -> Unit,
    onContinuarPago: () -> Unit,
    onBackPressed: () -> Unit
) {
    // Implementación temporal
}