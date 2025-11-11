package com.example.app_pasteleria_mil_sabores.ui.screen.admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.app_pasteleria_mil_sabores.model.Pedido
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.viewmodel.PedidoViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPedidosScreen(
    usuario: Usuario,
    pedidoViewModel: PedidoViewModel,
    onVolver: () -> Unit,
    onBackPressed: () -> Unit
) {
    BackHandler(enabled = true) {
        onBackPressed()
    }

    // Usar todosLosPedidos en lugar de pedidos
    val todosLosPedidos by pedidoViewModel.todosLosPedidos.collectAsState()
    val cargando by pedidoViewModel.cargando.collectAsState()
    val errorMessage by pedidoViewModel.errorMessage.collectAsState()

    // Estados para el filtro
    var mostrarFiltros by remember { mutableStateOf(false) }
    var estadoFiltro by remember { mutableStateOf<String?>(null) }

    // Cargar TODOS los pedidos al entrar - con LaunchedEffect correcto
    LaunchedEffect(Unit) {
        println("DEBUG - Iniciando carga de pedidos...")
        pedidoViewModel.cargarTodosLosPedidos()
    }

    // DEBUG: Logs para diagnóstico
    LaunchedEffect(todosLosPedidos) {
        println("DEBUG - Pedidos en UI: ${todosLosPedidos.size}")
        todosLosPedidos.forEachIndexed { index, pedido ->
            println("DEBUG - Pedido $index: ${pedido.id} - ${pedido.estado} - $${pedido.total}")
        }
    }

    LaunchedEffect(cargando) {
        println("DEBUG - Cargando estado: $cargando")
    }

    LaunchedEffect(todosLosPedidos) {
        println("DEBUG - AdminPedidosScreen - Pedidos recibidos: ${todosLosPedidos.size}")
        todosLosPedidos.forEachIndexed { index, pedido ->
            println("DEBUG - Pedido $index: ${pedido.id} - Estado: '${pedido.estado}' - Total: $${pedido.total}")
            println("DEBUG - Cliente: ${pedido.informacionContacto.nombre}")
            println("DEBUG - Productos: ${pedido.productos.size}")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Gestión de Pedidos - Todos",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarFiltros = !mostrarFiltros }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filtrar")
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
        ) {
            // Filtros
            if (mostrarFiltros) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Filtrar por Estado",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val estados = listOf(
                            "Pendiente", "Confirmado", "En preparación",
                            "Enviado", "Entregado", "Cancelado"
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(estados) { estado ->
                                FilterChip(
                                    selected = estadoFiltro == estado,
                                    onClick = {
                                        estadoFiltro = if (estadoFiltro == estado) null else estado
                                    },
                                    label = { Text(estado) }
                                )
                            }
                        }
                    }
                }
            }

            // Header informativo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total de pedidos: ${todosLosPedidos.size}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        estadoFiltro?.let { filtro ->
                            Text(
                                text = "Filtrado por: $filtro (${todosLosPedidos.count { it.estado == filtro }})",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        // Estadísticas rápidas
                        Text(
                            text = buildString {
                                append("Pendientes: ${todosLosPedidos.count { it.estado == "Pendiente" }}")
                                append(" | ")
                                append("En preparación: ${todosLosPedidos.count { it.estado == "En preparación" }}")
                                append(" | ")
                                append("Confirmados: ${todosLosPedidos.count { it.estado == "Confirmado" }}")
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // CONTENIDO PRINCIPAL - Corregido
            if (cargando && todosLosPedidos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Cargando todos los pedidos...")
                    }
                }
            } else if (todosLosPedidos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Sin pedidos",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "No hay pedidos en el sistema",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Los pedidos aparecerán aquí cuando los clientes realicen compras",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                val pedidosFiltrados = if (estadoFiltro != null) {
                    todosLosPedidos.filter { it.estado == estadoFiltro }
                } else {
                    todosLosPedidos
                }

                // DEBUG: Verificar pedidos filtrados
                LaunchedEffect(pedidosFiltrados) {
                    println("DEBUG - Pedidos a mostrar: ${pedidosFiltrados.size}")
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(pedidosFiltrados, key = { it.id }) { pedido ->
                        println("DEBUG - Renderizando pedido: ${pedido.id}")
                        PedidoAdminCard(
                            pedido = pedido,
                            onActualizarEstado = { nuevoEstado ->
                                pedidoViewModel.actualizarEstadoPedido(pedido.id, nuevoEstado)
                            }
                        )
                    }
                }
            }

            // Mostrar error si existe
            errorMessage?.let { message ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidoAdminCard(
    pedido: Pedido,
    onActualizarEstado: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (pedido.estado) {
                "Pendiente" -> MaterialTheme.colorScheme.surfaceVariant
                "Confirmado" -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                "En preparación" -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                "Enviado" -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                "Entregado" -> MaterialTheme.colorScheme.surface
                "Cancelado" -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Pedido #${pedido.id.take(8).uppercase()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Cliente: ${pedido.informacionContacto.nombre}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Email: ${pedido.informacionContacto.email}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$${pedido.total}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Badge(
                        containerColor = when (pedido.estado) {
                            "Pendiente" -> MaterialTheme.colorScheme.surfaceVariant
                            "Confirmado" -> MaterialTheme.colorScheme.primary
                            "En preparación" -> MaterialTheme.colorScheme.secondary
                            "Enviado" -> MaterialTheme.colorScheme.tertiary
                            "Entregado" -> MaterialTheme.colorScheme.surface
                            "Cancelado" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        contentColor = when (pedido.estado) {
                            "Pendiente" -> MaterialTheme.colorScheme.onSurfaceVariant
                            "Confirmado" -> MaterialTheme.colorScheme.onPrimary
                            "En preparación" -> MaterialTheme.colorScheme.onSecondary
                            "Enviado" -> MaterialTheme.colorScheme.onTertiary
                            "Entregado" -> MaterialTheme.colorScheme.onSurface
                            "Cancelado" -> MaterialTheme.colorScheme.onError
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    ) {
                        Text(
                            text = pedido.estado,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Información del pedido
            Column {
                Text(
                    text = "Fecha: ${formatearFecha(pedido.fechaCreacion)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Productos: ${pedido.productos.size} items",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                pedido.direccionEnvio?.let { direccion ->
                    // Usar los campos correctos del modelo Direccion
                    val direccionCompleta = "${direccion.calle} ${direccion.numero}" +
                            (direccion.departamento?.let { ", Depto. $it" } ?: "") +
                            ", ${direccion.comuna}, ${direccion.ciudad}"
                    Text(
                        text = "Dirección: $direccionCompleta",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "Método de pago: ${pedido.metodoPago}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Selector de estado
            val estados = listOf(
                "Pendiente", "Confirmado", "En preparación", "Enviado", "Entregado", "Cancelado"
            )
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = pedido.estado,
                    onValueChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    label = { Text("Cambiar estado") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    estados.forEach { estado ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    estado,
                                    color = when (estado) {
                                        "Pendiente" -> MaterialTheme.colorScheme.onSurfaceVariant
                                        "Confirmado" -> MaterialTheme.colorScheme.primary
                                        "En preparación" -> MaterialTheme.colorScheme.secondary
                                        "Enviado" -> MaterialTheme.colorScheme.tertiary
                                        "Entregado" -> MaterialTheme.colorScheme.onSurface
                                        "Cancelado" -> MaterialTheme.colorScheme.error
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            },
                            onClick = {
                                onActualizarEstado(estado)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun formatearFecha(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}