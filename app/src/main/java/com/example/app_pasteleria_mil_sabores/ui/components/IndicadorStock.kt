package com.example.app_pasteleria_mil_sabores.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun IndicadorStock(
    stock: Int,
    proximamente: Boolean = false, // Nuevo parámetro
    modifier: Modifier = Modifier
) {
    val (texto, colorFondo, colorTexto) = when {
        proximamente -> Triple(
            "Próximamente",
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer
        )
        stock > 10 -> Triple(
            "Disponible",
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onPrimary
        )
        stock > 0 -> Triple(
            "Últimas $stock unidades",
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onPrimary
        )
        else -> Triple(
            "Agotado",
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer
        )
    }

    Surface(
        color = colorFondo,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelMedium,
            color = colorTexto,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}