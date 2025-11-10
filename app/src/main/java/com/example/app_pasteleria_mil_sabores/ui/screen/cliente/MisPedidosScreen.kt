package com.example.app_pasteleria_mil_sabores.ui.screen.cliente

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.app_pasteleria_mil_sabores.model.Pedido
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.utils.formatearPrecio
import com.example.app_pasteleria_mil_sabores.viewmodel.PedidoViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisPedidosScreen(
    pedidoViewModel: PedidoViewModel,
    usuario: Usuario,
    onVolver: () -> Unit,
    onVerDetallePedido: (String) -> Unit,
    onBackPressed: () -> Unit
) {
    BackHandler(enabled = true) {
        onBackPressed()
    }

    val pedidos by pedidoViewModel.pedidos.collectAsState()
    val estadoFiltro by pedidoViewModel.estadoFiltro.collectAsState()
    val cargando by pedidoViewModel.cargando.collectAsState()
    val errorMessage by pedidoViewModel.errorMessage.collectAsState()

    var mostrarFiltros by remember { mutableStateOf(false) }

    // Cargar pedidos cuando se entra a la pantalla
    LaunchedEffect(usuario.id) {
        pedidoViewModel.cargarPedidos(usuario.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mis Pedidos",
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
                actions = {
                    IconButton(onClick = { mostrarFiltros = true }) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filtrar pedidos",
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
        ) {
            // Mostrar mensaje de error si existe
            errorMessage?.let { mensaje ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            mensaje,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { pedidoViewModel.limpiarError() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.FilterList, // Usar otro icono para cerrar
                                contentDescription = "Cerrar",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            // Estado del filtro actual
            estadoFiltro?.let { filtro ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Filtro: ${obtenerTextoEstado(filtro)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        TextButton(onClick = { pedidoViewModel.filtrarPorEstado(null) }) {
                            Text("Limpiar")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (cargando && pedidos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Cargando tus pedidos...")
                    }
                }
            } else {
                val pedidosMostrar = pedidoViewModel.obtenerPedidosFiltrados()

                if (pedidosMostrar.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                Icons.Default.ShoppingBag,
                                contentDescription = "Sin pedidos",
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                if (estadoFiltro != null) {
                                    "No hay pedidos ${obtenerTextoEstado(estadoFiltro!!).lowercase()}"
                                } else {
                                    "Aún no tienes pedidos"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Cuando realices un pedido, aparecerá aquí",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(pedidosMostrar, key = { it.id }) { pedido ->
                            PedidoCard(
                                pedido = pedido,
                                onClick = { onVerDetallePedido(pedido.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo de filtros
    if (mostrarFiltros) {
        AlertDialog(
            onDismissRequest = { mostrarFiltros = false },
            title = { Text("Filtrar por estado") },
            text = {
                Column {
                    listOf(
                        null to "Todos los pedidos",
                        "pendiente" to "Pendientes",
                        "confirmado" to "Confirmados",
                        "enviado" to "Enviados",
                        "entregado" to "Entregados",
                        "cancelado" to "Cancelados"
                    ).forEach { (estado, texto) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .selectable(
                                    selected = estadoFiltro == estado,
                                    onClick = {
                                        pedidoViewModel.filtrarPorEstado(estado)
                                        mostrarFiltros = false
                                    }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = estadoFiltro == estado,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(texto)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarFiltros = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
fun PedidoCard(
    pedido: Pedido,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header: Número de pedido y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Pedido #${pedido.id.takeLast(6).uppercase()}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Badge(
                    containerColor = when (pedido.estado) {
                        "pendiente" -> MaterialTheme.colorScheme.secondaryContainer
                        "confirmado" -> MaterialTheme.colorScheme.primaryContainer
                        "enviado" -> MaterialTheme.colorScheme.tertiaryContainer
                        "entregado" -> MaterialTheme.colorScheme.surfaceVariant
                        "cancelado" -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    contentColor = when (pedido.estado) {
                        "pendiente" -> MaterialTheme.colorScheme.onSecondaryContainer
                        "confirmado" -> MaterialTheme.colorScheme.onPrimaryContainer
                        "enviado" -> MaterialTheme.colorScheme.onTertiaryContainer
                        "entregado" -> MaterialTheme.colorScheme.onSurfaceVariant
                        "cancelado" -> MaterialTheme.colorScheme.onErrorContainer
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                ) {
                    Text(obtenerTextoEstado(pedido.estado))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Fecha y cantidad de productos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    formatearFecha(pedido.fechaCreacion),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "${pedido.productos.size} ${if (pedido.productos.size == 1) "producto" else "productos"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Productos (mostrar primeros 2)
            Column {
                pedido.productos.take(2).forEach { item ->
                    Text(
                        "• ${item.cantidad} x ${item.producto.nombre}",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1
                    )
                }
                if (pedido.productos.size > 2) {
                    Text(
                        "• ... y ${pedido.productos.size - 2} más",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Total:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    pedido.total.formatearPrecio(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// Funciones de utilidad
fun obtenerTextoEstado(estado: String): String {
    return when (estado) {
        "pendiente" -> "Pendiente"
        "confirmado" -> "Confirmado"
        "enviado" -> "Enviado"
        "entregado" -> "Entregado"
        "cancelado" -> "Cancelado"
        else -> estado
    }
}

fun formatearFecha(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy 'a las' HH:mm", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}