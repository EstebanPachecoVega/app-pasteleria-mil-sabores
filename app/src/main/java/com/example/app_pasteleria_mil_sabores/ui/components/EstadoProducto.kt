package com.example.app_pasteleria_mil_sabores.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.app_pasteleria_mil_sabores.model.Producto
import com.example.app_pasteleria_mil_sabores.utils.formatearPrecio

/**
 * Componente que muestra el estado completo de un producto
 * Incluye stock, precio y mensajes de estado
 */
@Composable
fun EstadoProducto(
    producto: Producto,
    proximamente: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Precio
        Text(
            text = producto.precio.formatearPrecio(),
            style = MaterialTheme.typography.titleSmall,
            color = if (proximamente) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.primary
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Indicador de estado
        IndicadorStock(
            stock = producto.stock,
            proximamente = proximamente,
            modifier = Modifier.align(_root_ide_package_.androidx.compose.ui.Alignment.Start)
        )

        // Mensaje adicional para estado "Próximamente"
        if (proximamente) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Disponible pronto",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start
            )
        }
    }
}