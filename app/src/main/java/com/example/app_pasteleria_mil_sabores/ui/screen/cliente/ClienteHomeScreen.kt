package com.example.app_pasteleria_mil_sabores.ui.screen.cliente

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.model.Producto
import com.example.app_pasteleria_mil_sabores.viewmodel.FormularioViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.ProductoViewModel
import com.example.app_pasteleria_mil_sabores.utils.rememberImageResource
import com.example.app_pasteleria_mil_sabores.utils.formatearPrecio
import com.example.app_pasteleria_mil_sabores.viewmodel.CarritoViewModel
import com.example.app_pasteleria_mil_sabores.ui.components.DialogoConfirmacionLogout
import com.example.app_pasteleria_mil_sabores.ui.components.ItemMenuCerrarSesion
import com.example.app_pasteleria_mil_sabores.ui.components.BotonAgregarCarrito
import com.example.app_pasteleria_mil_sabores.ui.components.EstadoProducto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteHomeScreen(
    usuario: Usuario,
    viewModel: FormularioViewModel,
    productoViewModel: ProductoViewModel,
    carritoViewModel: CarritoViewModel,
    onCerrarSesion: () -> Unit,
    onVerPerfil: () -> Unit,
    onVerCarrito: () -> Unit,
    onVerPedidos: () -> Unit,
    onVerSoporte: () -> Unit,
    onVerDetalleProducto: (Producto) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var menuExpanded by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val productos by productoViewModel.productos.collectAsState()
    val itemCount by carritoViewModel.itemCount.collectAsState()

    LaunchedEffect(Unit) {
        productoViewModel.cargarProductos()
    }

    if (showLogoutDialog) {
        DialogoConfirmacionLogout(
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                viewModel.cerrarSesion()
                onCerrarSesion()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
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
                    Box {
                        IconButton(onClick = onVerCarrito) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                        }
                        if (itemCount > 0) {
                            Badge(
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Text(
                                    text = if (itemCount > 99) "99+" else itemCount.toString(),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            if (usuario.fotoPerfil != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        ImageRequest.Builder(LocalContext.current)
                                            .data(usuario.fotoPerfil)
                                            .build()
                                    ),
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(Icons.Default.AccountCircle, contentDescription = "Perfil")
                            }
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
                            Divider()
                            ItemMenuCerrarSesion(
                                onClick = {
                                    menuExpanded = false
                                    showLogoutDialog = true
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
                        ProductoCard(
                            producto = producto,
                            onAgregarAlCarrito = {
                                carritoViewModel.agregarProducto(producto, 1)
                            },
                            onVerDetalle = { onVerDetalleProducto(producto) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoCard(
    producto: Producto,
    onAgregarAlCarrito: (Producto) -> Unit,
    onVerDetalle: (Producto) -> Unit,
    proximamente: Boolean = false
) {
    val imageResource = rememberImageResource(producto.imagen)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onVerDetalle(producto) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Imagen
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
                    Icon(
                        painter = painterResource(android.R.drawable.ic_menu_gallery),
                        contentDescription = "Imagen no disponible",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (proximamente) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = producto.precio.formatearPrecio(),
                    style = MaterialTheme.typography.titleSmall.copy(
                    ),
                    color = if (proximamente) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )

                // Solo mostrar "Agotado" o "Próximamente"
                if (producto.stock <= 0 || proximamente) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (proximamente) "Próximamente" else "Agotado",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (proximamente) {
                            MaterialTheme.colorScheme.tertiary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }
            }

            // Botón Agregar Carrito
            BotonAgregarCarrito(
                producto = producto,
                onAgregarAlCarrito = { producto, cantidad -> onAgregarAlCarrito(producto) },
                proximamente = proximamente,
                esHomeScreen = true,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .width(120.dp)
            )
        }
    }
}