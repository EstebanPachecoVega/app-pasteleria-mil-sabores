package com.example.app_pasteleria_mil_sabores.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ContadorCantidad(
    cantidad: String,
    onCantidadChange: (String) -> Unit,
    stockDisponible: Int,
    proximamente: Boolean = false, // Nuevo parámetro
    modifier: Modifier = Modifier
) {
    val habilitado = stockDisponible > 0 && !proximamente

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Botón -
        IconButton(
            onClick = {
                val current = cantidad.toIntOrNull() ?: 1
                if (current > 1) {
                    onCantidadChange((current - 1).toString())
                }
            },
            enabled = habilitado && (cantidad.toIntOrNull() ?: 1) > 1,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                Icons.Outlined.Remove,
                contentDescription = "Reducir cantidad",
                tint = if (habilitado && (cantidad.toIntOrNull() ?: 1) > 1) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }

        // Campo de cantidad
        OutlinedTextField(
            value = if (proximamente) "0" else cantidad,
            onValueChange = { if (habilitado) {
                if (it.isEmpty()) {
                    onCantidadChange("1")
                } else if (it.all { char -> char.isDigit() } && it.length <= 3) {
                    val parsedValue = it.toIntOrNull() ?: 1
                    if (parsedValue > stockDisponible) {
                        onCantidadChange(stockDisponible.toString())
                    } else if (parsedValue < 1) {
                        onCantidadChange("1")
                    } else {
                        onCantidadChange(it)
                    }
                }
            }},
            modifier = Modifier
                .width(80.dp)
                .height(56.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            enabled = habilitado,
            readOnly = proximamente // Solo lectura para "Próximamente"
        )

        // Botón +
        IconButton(
            onClick = {
                val current = cantidad.toIntOrNull() ?: 1
                if (current < stockDisponible) {
                    onCantidadChange((current + 1).toString())
                } else {
                    onCantidadChange(stockDisponible.toString())
                }
            },
            enabled = habilitado && (cantidad.toIntOrNull() ?: 1) < stockDisponible,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                Icons.Outlined.Add,
                contentDescription = "Aumentar cantidad",
                tint = if (habilitado && (cantidad.toIntOrNull() ?: 1) < stockDisponible) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}