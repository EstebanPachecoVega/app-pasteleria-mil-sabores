package com.example.app_pasteleria_mil_sabores.ui.screen.cliente

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.app_pasteleria_mil_sabores.model.Producto
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.utils.rememberImageResource
import com.example.app_pasteleria_mil_sabores.viewmodel.FormularioViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteHomeScreen(
    usuario: Usuario,
    viewModel: FormularioViewModel,
    productoViewModel: ProductoViewModel,
    onCerrarSesion: () -> Unit,
    onVerPerfil: () -> Unit,
    onVerCarrito: () -> Unit,
    onVerPedidos: () -> Unit,
    onVerSoporte: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var menuExpanded by remember { mutableStateOf(false) }
    val productos by productoViewModel.productos.collectAsState()

    LaunchedEffect(Unit) {
        productoViewModel.cargarProductos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Buscador centrado
                    OutlinedTextField(
                        value = query,
                        onValueChange = {
                            query = it
                            if (it.isNotEmpty()) {
                                productoViewModel.buscarProductos(it)
                            } else {
                                productoViewModel.cargarProductos()
                            }
                        },
                        placeholder = { Text("Buscar productos...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Buscar")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                },
                navigationIcon = {
                    // Carrito en esquina izquierda
                    IconButton(onClick = onVerCarrito) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                    }
                },
                actions = {
                    // Perfil en esquina derecha con menú desplegable
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "Perfil")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Mi Perfil") },
                                onClick = {
                                    menuExpanded = false
                                    onVerPerfil()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Mis Pedidos") },
                                onClick = {
                                    menuExpanded = false
                                    onVerPedidos()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Soporte") },
                                onClick = {
                                    menuExpanded = false
                                    onVerSoporte()
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Cerrar Sesión") },
                                onClick = {
                                    menuExpanded = false
                                    viewModel.cerrarSesion()
                                    onCerrarSesion()
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (productos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay productos disponibles",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(productos) { producto ->
                        ProductoCard(producto = producto)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoCard(producto: Producto) {
    val imageResource = rememberImageResource(producto.imagen)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Imagen del producto - Ahora usa la imagen real
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (imageResource != 0) {
                    Image(
                        painter = painterResource(id = imageResource),
                        contentDescription = producto.nombre,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Fallback si la imagen no existe
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
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = producto.descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${producto.precio}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Botón de agregar al carrito
            IconButton(
                onClick = { /* Agregar al carrito */ },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = "Agregar al carrito",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}