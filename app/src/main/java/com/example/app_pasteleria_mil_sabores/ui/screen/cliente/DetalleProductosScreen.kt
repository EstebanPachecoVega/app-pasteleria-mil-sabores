package com.example.app_pasteleria_mil_sabores.ui.screen.cliente

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.app_pasteleria_mil_sabores.model.Producto
import com.example.app_pasteleria_mil_sabores.utils.rememberImageResource
import com.example.app_pasteleria_mil_sabores.utils.formatearPrecio
import com.example.app_pasteleria_mil_sabores.viewmodel.CarritoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    producto: Producto,
    onVolver: () -> Unit,
    carritoViewModel: CarritoViewModel
) {
    var cantidad by remember { mutableStateOf(if (producto.stock > 0) "1" else "0") }
    var isEditing by remember { mutableStateOf(false) }
    val imageResource = rememberImageResource(producto.imagen)

    // Calculamos el máximo disponible
    val maxDisponible = producto.stock

    // Convertir a Int para cálculos, con validación
    val cantidadInt = cantidad.toIntOrNull() ?: if (producto.stock > 0) 1 else 0

    // Efecto para validar automáticamente cuando se deja de editar
    LaunchedEffect(isEditing) {
        if (!isEditing) {
            val parsed = cantidad.toIntOrNull() ?: if (producto.stock > 0) 1 else 0
            val corrected = when {
                producto.stock == 0 -> 0 // Si está agotado, forzar 0
                parsed < 1 -> 1
                parsed > maxDisponible -> maxDisponible
                else -> parsed
            }
            if (corrected.toString() != cantidad) {
                cantidad = corrected.toString()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalle del Producto",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Sección de Imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (imageResource != 0) {
                    Image(
                        painter = painterResource(id = imageResource),
                        contentDescription = producto.nombre,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(MaterialTheme.shapes.large),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.shapes.large
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_gallery),
                                contentDescription = "Imagen no disponible",
                                modifier = Modifier.size(60.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Imagen no disponible",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            // Sección de Información Principal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Nombre del Producto
                    Text(
                        text = producto.nombre,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Precio
                    Text(
                        text = producto.precio.formatearPrecio(),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stock en la sección de información principal
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Stock disponible:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (producto.stock > 0) "${producto.stock} unidades" else "Agotado",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = if (producto.stock > 0) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.error
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Badges de Categoría y Estado
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Badge de Categoría
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = producto.categoria
                                    .replace("_", " ")
                                    .replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        // Badge de Estado (Disponible/Agotado) - SIN mostrar cantidad
                        Surface(
                            color = if (producto.stock > 0) {
                                MaterialTheme.colorScheme.primary // Marrón
                            } else {
                                MaterialTheme.colorScheme.errorContainer // Rojo/gris
                            },
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = if (producto.stock > 0) "Disponible" else "Agotado",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (producto.stock > 0) {
                                    MaterialTheme.colorScheme.onPrimary // Crema
                                } else {
                                    MaterialTheme.colorScheme.onErrorContainer
                                },
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Sección de Descripción
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = producto.descripcion,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Espacio antes del contador
            Spacer(modifier = Modifier.height(24.dp))

            // Sección de Cantidad y Agregar al Carrito
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Contador de Cantidad
                    Text(
                        text = "Cantidad",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Contador con botones + y - y campo de entrada editable
                    if (producto.stock > 0) {
                        // MOSTRAR CONTADOR NORMAL SI HAY STOCK
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Botón -
                            IconButton(
                                onClick = {
                                    val current = cantidad.toIntOrNull() ?: 1
                                    if (current > 1) {
                                        cantidad = (current - 1).toString()
                                    }
                                },
                                enabled = (cantidad.toIntOrNull() ?: 1) > 1,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.Remove,
                                    contentDescription = "Reducir cantidad",
                                    tint = if ((cantidad.toIntOrNull() ?: 1) > 1) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }

                            // Campo de cantidad editable
                            OutlinedTextField(
                                value = cantidad,
                                onValueChange = { newValue: String ->
                                    if (newValue.isEmpty()) {
                                        cantidad = "1"
                                    } else if (newValue.all { it.isDigit() } && newValue.length <= 3) {
                                        val parsedValue = newValue.toIntOrNull() ?: 1
                                        if (parsedValue > maxDisponible) {
                                            cantidad = maxDisponible.toString()
                                        } else if (parsedValue < 1) {
                                            cantidad = "1"
                                        } else {
                                            cantidad = newValue
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(56.dp)
                                    .onFocusChanged { focusState: FocusState ->
                                        isEditing = focusState.isFocused
                                        if (!focusState.isFocused) {
                                            val parsed = if (cantidad.isEmpty()) 1 else cantidad.toIntOrNull() ?: 1
                                            val corrected = when {
                                                parsed < 1 -> 1
                                                parsed > maxDisponible -> maxDisponible
                                                else -> parsed
                                            }
                                            cantidad = corrected.toString()
                                        }
                                    },
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                ),
                                singleLine = true,
                                enabled = producto.stock > 0
                            )

                            // Botón +
                            IconButton(
                                onClick = {
                                    val current = cantidad.toIntOrNull() ?: 1
                                    if (current < maxDisponible) {
                                        cantidad = (current + 1).toString()
                                    } else {
                                        cantidad = maxDisponible.toString()
                                    }
                                },
                                enabled = (cantidad.toIntOrNull() ?: 1) < maxDisponible,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Outlined.Add,
                                    contentDescription = "Aumentar cantidad",
                                    tint = if ((cantidad.toIntOrNull() ?: 1) < maxDisponible) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }

                        // Indicador de stock máximo
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Máximo: $maxDisponible unidades",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            if (cantidadInt > maxDisponible) {
                                Text(
                                    text = "ⓘ Se ajustó al máximo disponible",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    } else {
                        // MOSTRAR CAMPO DESHABILITADO CON 0 SI NO HAY STOCK
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = "0",
                                onValueChange = { },
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(56.dp),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                ),
                                singleLine = true,
                                enabled = false // Deshabilitado cuando no hay stock
                            )
                        }

                        // Mensaje de producto agotado
                        Text(
                            text = "Producto agotado - no disponible",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón Agregar al Carrito - MEJORADO
                    Button(
                        onClick = {
                            val finalCantidad = when {
                                cantidad.isEmpty() -> if (producto.stock > 0) 1 else 0
                                cantidadInt < 1 -> if (producto.stock > 0) 1 else 0
                                cantidadInt > maxDisponible -> maxDisponible
                                else -> cantidadInt
                            }

                            if (finalCantidad.toString() != cantidad) {
                                cantidad = finalCantidad.toString()
                            }

                            carritoViewModel.agregarProducto(producto, finalCantidad)
                        },
                        enabled = producto.stock > 0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (producto.stock > 0) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            contentColor = if (producto.stock > 0) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        ),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Agregar al carrito",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (producto.stock > 0) {
                                "Agregar al Carrito - ${(producto.precio * cantidadInt).formatearPrecio()}"
                            } else {
                                "Producto Agotado"
                            },
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                    // Mensaje informativo - MEJORADO
                    if (producto.stock > 0) {
                        Text(
                            text = "Precio total por $cantidadInt ${if (cantidadInt == 1) "unidad" else "unidades"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Espacio final para scroll
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
