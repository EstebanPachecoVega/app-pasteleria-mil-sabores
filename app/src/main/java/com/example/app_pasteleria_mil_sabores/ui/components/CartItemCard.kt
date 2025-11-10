package com.example.app_pasteleria_mil_sabores.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.app_pasteleria_mil_sabores.model.CartItem
import com.example.app_pasteleria_mil_sabores.utils.formatearPrecio
import com.example.app_pasteleria_mil_sabores.utils.rememberImageResource

@Composable
fun CartItemCard(
    item: CartItem,
    onUpdateQuantity: (Int) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    showDeleteButton: Boolean = true,
    showQuantityControls: Boolean = true
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageResource = rememberImageResource(item.producto.imagen)

            // Imagen del producto
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (imageResource != 0) {
                    Image(
                        painter = painterResource(id = imageResource),
                        contentDescription = item.producto.nombre,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(android.R.drawable.ic_menu_gallery),
                        contentDescription = "Imagen no disponible",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información del producto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.producto.precio.formatearPrecio(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Controles de cantidad (si están habilitados)
                if (showQuantityControls) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (item.cantidad > 1) {
                                    onUpdateQuantity(item.cantidad - 1)
                                } else {
                                    onRemove()
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Remove,
                                contentDescription = "Reducir cantidad",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Text(
                            text = item.cantidad.toString(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .width(40.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )

                        IconButton(
                            onClick = {
                                if (item.cantidad < item.producto.stock) {
                                    onUpdateQuantity(item.cantidad + 1)
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Add,
                                contentDescription = "Aumentar cantidad",
                                tint = if (item.cantidad < item.producto.stock) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                } else {
                    // Mostrar solo la cantidad si los controles están deshabilitados
                    Text(
                        text = "Cantidad: ${item.cantidad}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Precio total y botón eliminar
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = item.getPrecioTotal().formatearPrecio(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Botón eliminar (si está habilitado)
                if (showDeleteButton) {
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar producto",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Indicador de stock máximo
        if (item.cantidad >= item.producto.stock && showQuantityControls) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "ⓘ Cantidad máxima disponible",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}