package com.example.app_pasteleria_mil_sabores.ui.screen.cliente

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.app_pasteleria_mil_sabores.model.Pedido
import com.example.app_pasteleria_mil_sabores.utils.formatearPrecio
import com.example.app_pasteleria_mil_sabores.viewmodel.PedidoViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePedidoScreen(
    pedidoId: String,
    pedidoViewModel: PedidoViewModel,
    onVolver: () -> Unit,
    onBackPressed: () -> Unit
) {
    BackHandler(enabled = true) {
        onBackPressed()
    }

    val pedido by pedidoViewModel.pedidoSeleccionado.collectAsState()
    val cargando by pedidoViewModel.cargando.collectAsState()
    val errorMessage by pedidoViewModel.errorMessage.collectAsState()

    // Cargar pedido cuando se entra a la pantalla
    LaunchedEffect(pedidoId) {
        pedidoViewModel.cargarPedidoPorId(pedidoId)
    }

    // Limpiar pedido seleccionado al salir
    DisposableEffect(Unit) {
        onDispose {
            pedidoViewModel.limpiarPedidoSeleccionado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalle del Pedido",
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
        if (cargando && pedido == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Cargando detalle del pedido...")
                }
            }
        } else if (pedido == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        "Pedido no encontrado",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onVolver) {
                        Text("Volver a Mis Pedidos")
                    }
                }
            }
        } else {
            pedido?.let { pedidoDetalle ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    DetallePedidoCompleto(pedido = pedidoDetalle)
                }
            }
        }

        // Mostrar error si existe
        errorMessage?.let { mensaje ->
            LaunchedEffect(mensaje) {
                // Podrías mostrar un snackbar aquí
            }
        }
    }
}

@Composable
fun DetallePedidoCompleto(pedido: Pedido) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        // Header del pedido
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Pedido #${pedido.id}",
                        style = MaterialTheme.typography.titleMedium,
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
                        }
                    ) {
                        Text(obtenerTextoEstado(pedido.estado))
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Realizado el ${formatearFecha(pedido.fechaCreacion)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Información de envío
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "🚚 Información de Envío",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                pedido.direccionEnvio?.let { direccion ->
                    Column {
                        Text(
                            "${direccion.calle} ${direccion.numero}" +
                                    (direccion.departamento?.let { ", Depto. $it" } ?: ""),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "${direccion.comuna}, ${direccion.ciudad}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            direccion.region,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    "👤 Información de Contacto",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    pedido.informacionContacto.nombre,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    pedido.informacionContacto.email,
                    style = MaterialTheme.typography.bodyMedium
                )
                pedido.informacionContacto.telefono?.let { telefono ->
                    Text(
                        telefono,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Productos del pedido
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "🛒 Productos (${pedido.productos.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                pedido.productos.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                item.producto.nombre,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "${item.cantidad} x ${item.producto.precio.formatearPrecio()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            item.getPrecioTotal().formatearPrecio(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (item != pedido.productos.last()) {
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }

        // Resumen del pago
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "💳 Resumen del Pago",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Método de pago
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Método de pago:", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        when (pedido.metodoPago) {
                            "contra_entrega" -> "Pago Contra Entrega"
                            "tarjeta" -> "Tarjeta de Crédito/Débito"
                            else -> pedido.metodoPago
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                // Desglose de precios
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Subtotal:", style = MaterialTheme.typography.bodyMedium)
                    Text(pedido.subtotal.formatearPrecio(), style = MaterialTheme.typography.bodyMedium)
                }

                if (pedido.descuentoAplicado > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Descuento:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "-${pedido.descuentoAplicado.formatearPrecio()}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Envío:", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        if (pedido.costoEnvio == 0) "GRATIS" else pedido.costoEnvio.formatearPrecio(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (pedido.costoEnvio == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                // Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Total:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        pedido.total.formatearPrecio(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Información del estado
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "📋 Estado del Pedido",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    obtenerMensajeEstado(pedido.estado),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

fun obtenerMensajeEstado(estado: String): String {
    return when (estado) {
        "pendiente" -> "Tu pedido está siendo procesado. Te notificaremos cuando sea confirmado."
        "confirmado" -> "Tu pedido ha sido confirmado y está siendo preparado."
        "enviado" -> "Tu pedido ha sido enviado. Estará en tu dirección pronto."
        "entregado" -> "Tu pedido ha sido entregado. ¡Esperamos que lo disfrutes!"
        "cancelado" -> "Este pedido ha sido cancelado."
        else -> "Estado del pedido: ${obtenerTextoEstado(estado)}"
    }
}