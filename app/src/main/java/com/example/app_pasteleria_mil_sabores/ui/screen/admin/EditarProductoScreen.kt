package com.example.app_pasteleria_mil_sabores.ui.screen.admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.app_pasteleria_mil_sabores.model.Producto
import com.example.app_pasteleria_mil_sabores.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProductoScreen(
    producto: Producto,
    productoViewModel: ProductoViewModel,
    onCancelar: () -> Unit,
    onGuardarExitoso: () -> Unit,
    onBackPressed: () -> Unit
) {
    BackHandler(enabled = true) {
        onBackPressed()
    }

    var nombre by remember { mutableStateOf(producto.nombre) }
    var descripcion by remember { mutableStateOf(producto.descripcion) }
    var precio by remember { mutableStateOf(producto.precio.toString()) }
    var imagen by remember { mutableStateOf(producto.imagen) }
    var categoria by remember { mutableStateOf(producto.categoria) }
    var stock by remember { mutableStateOf(producto.stock.toString()) }
    var destacado by remember { mutableStateOf(producto.destacado) }
    var activo by remember { mutableStateOf(producto.activo) }

    val errorMessage by productoViewModel.errorMessage.collectAsState()
    val cargando by productoViewModel.cargando.collectAsState()
    val operacionExitosa by productoViewModel.operacionExitosa.collectAsState()

    val categoriasDisponibles = listOf(
        "individuales",
        "sin_azucar",
        "sin_gluten",
        "veganos",
        "circulares",
        "cuadradas",
        "especiales",
        "tradicional"
    )

    // Navegar automáticamente cuando la operación sea exitosa
    LaunchedEffect(operacionExitosa) {
        if (operacionExitosa) {
            productoViewModel.resetearOperacionExitosa()
            onGuardarExitoso()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Editar Producto",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onCancelar,
                        enabled = !cargando
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Campo Nombre
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = {
                        Text(
                            "Nombre del producto *",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = nombre.isNotBlank() && nombre.length < 3,
                    supportingText = {
                        if (nombre.isNotBlank() && nombre.length < 3) {
                            Text(
                                "Mínimo 3 caracteres",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        errorBorderColor = MaterialTheme.colorScheme.error,
                    )
                )

                // Campo Descripción
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = {
                        Text(
                            "Descripción",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    isError = descripcion.isNotBlank() && descripcion.length < 10,
                    supportingText = {
                        if (descripcion.isNotBlank() && descripcion.length < 10) {
                            Text(
                                "Mínimo 10 caracteres",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text(
                                "${descripcion.length}/500",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        errorBorderColor = MaterialTheme.colorScheme.error,
                    )
                )

                // Campos Precio y Stock en fila
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Campo Precio
                    OutlinedTextField(
                        value = precio,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() }) {
                                precio = it
                            }
                        },
                        label = {
                            Text(
                                "Precio *",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        isError = precio.isNotBlank() && (precio.toIntOrNull() == null || precio.toInt() <= 0),
                        supportingText = {
                            if (precio.isNotBlank()) {
                                if (precio.toIntOrNull() == null || precio.toInt() <= 0) {
                                    Text(
                                        "Precio debe ser mayor a 0",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                } else {
                                    Text(
                                        "$${precio.toInt()}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            errorBorderColor = MaterialTheme.colorScheme.error,
                        )
                    )

                    // Campo Stock
                    OutlinedTextField(
                        value = stock,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() }) {
                                stock = it
                            }
                        },
                        label = {
                            Text(
                                "Stock *",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        isError = stock.isNotBlank() && (stock.toIntOrNull() == null || stock.toInt() < 0),
                        supportingText = {
                            if (stock.isNotBlank() && (stock.toIntOrNull() == null || stock.toInt() < 0)) {
                                Text(
                                    "Stock no puede ser negativo",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            errorBorderColor = MaterialTheme.colorScheme.error,
                        )
                    )
                }

                // Campo Categoría con Dropdown
                var categoriaExpandida by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = categoriaExpandida,
                    onExpandedChange = { categoriaExpandida = !categoriaExpandida }
                ) {
                    OutlinedTextField(
                        value = categoria,
                        onValueChange = { },
                        label = {
                            Text(
                                "Categoría *",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpandida)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = categoriaExpandida,
                        onDismissRequest = { categoriaExpandida = false }
                    ) {
                        categoriasDisponibles.forEach { categoriaItem ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        categoriaItem.replace("_", " ").replaceFirstChar { it.uppercase() }
                                    )
                                },
                                onClick = {
                                    categoria = categoriaItem
                                    categoriaExpandida = false
                                }
                            )
                        }
                    }
                }

                // Campo Imagen
                OutlinedTextField(
                    value = imagen,
                    onValueChange = { imagen = it },
                    label = {
                        Text(
                            "Nombre de la imagen",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = {
                        Text(
                            "Ej: torta_chocolate",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    )
                )

                // Checkboxes para opciones
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = destacado,
                            onCheckedChange = { destacado = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                checkmarkColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        Text(
                            text = "Producto Destacado",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = activo,
                            onCheckedChange = { activo = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                checkmarkColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                        Text(
                            text = "Producto Activo",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                // Mostrar error si existe
                errorMessage?.let { message ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onCancelar,
                        enabled = !cargando,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            productoViewModel.actualizarProducto(
                                producto.copy(
                                    nombre = nombre.trim(),
                                    descripcion = descripcion.trim(),
                                    precio = precio.toIntOrNull() ?: producto.precio,
                                    imagen = if (imagen.isBlank()) producto.imagen else imagen.trim(),
                                    categoria = categoria,
                                    stock = stock.toIntOrNull() ?: producto.stock,
                                    destacado = destacado,
                                    activo = activo
                                )
                            )
                        },
                        enabled = !cargando &&
                                nombre.isNotBlank() &&
                                nombre.length >= 3 &&
                                precio.isNotBlank() &&
                                precio.toIntOrNull() != null &&
                                precio.toInt() > 0 &&
                                stock.isNotBlank() &&
                                stock.toIntOrNull() != null &&
                                stock.toInt() >= 0 &&
                                categoria.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        if (cargando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Guardar Cambios")
                        }
                    }
                }

                // Información de validación
                if (!cargando) {
                    Text(
                        text = "* Campos obligatorios",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}