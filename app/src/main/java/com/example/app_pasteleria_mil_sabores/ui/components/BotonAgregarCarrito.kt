package com.example.app_pasteleria_mil_sabores.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app_pasteleria_mil_sabores.model.Producto
import com.example.app_pasteleria_mil_sabores.utils.formatearPrecio

@Composable
fun BotonAgregarCarrito(
    producto: Producto,
    cantidad: Int = 1,
    onAgregarAlCarrito: (Producto, Int) -> Unit,
    modifier: Modifier = Modifier,
    textoPersonalizado: String? = null,
    proximamente: Boolean = false,
    esHomeScreen: Boolean = false
) {
    val stockDisponible = producto.stock > 0 && !proximamente
    val precioTotal = producto.precio * cantidad

    // Determinar icono según estado
    val icono = when {
        proximamente -> Icons.Default.Schedule
        else -> Icons.Default.ShoppingCart
    }

    // Texto para HomeScreen - Siempre mostrar "Agregar al carrito" cuando esté disponible
    val texto = when {
        proximamente -> "Próximamente"
        !stockDisponible -> "Agotado"
        esHomeScreen -> "Agregar al carrito"
        textoPersonalizado != null -> textoPersonalizado
        cantidad > 1 -> "Agregar - ${precioTotal.formatearPrecio()}"
        else -> "Agregar al carrito"
    }

    // Determinar colores según estado
    val (colorFondo, colorContenido) = when {
        proximamente -> Pair(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer
        )
        stockDisponible -> Pair(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onPrimary
        )
        else -> Pair(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Button(
        onClick = {
            if (stockDisponible) {
                onAgregarAlCarrito(producto, cantidad)
            }
        },
        enabled = stockDisponible,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorFondo,
            contentColor = colorContenido
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Siempre mostrar el icono
        Icon(
            icono,
            contentDescription = when {
                proximamente -> "Próximamente disponible"
                stockDisponible -> "Agregar al carrito"
                else -> "Producto agotado"
            },
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = texto,
            style = MaterialTheme.typography.labelMedium
        )
    }
}