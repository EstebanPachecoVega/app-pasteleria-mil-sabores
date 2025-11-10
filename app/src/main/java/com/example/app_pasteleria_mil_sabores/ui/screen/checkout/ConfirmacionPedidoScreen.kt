package com.example.app_pasteleria_mil_sabores.ui.screen.checkout

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.utils.formatearPrecio
import com.example.app_pasteleria_mil_sabores.viewmodel.CheckoutViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmacionPedidoScreen(
    checkoutViewModel: CheckoutViewModel,
    usuario: Usuario,
    onIrAlInicio: () -> Unit,
    onBackPressed: () -> Unit
) {
    BackHandler(enabled = true) {
        onBackPressed()
    }

    val pedidoActual by checkoutViewModel.pedidoActual.collectAsState()
    val scope = rememberCoroutineScope()
    var pedidoConfirmado by remember { mutableStateOf(false) }
    var numeroPedido by remember { mutableStateOf("") }

    // Confirmar y guardar el pedido cuando se entra a esta pantalla
    LaunchedEffect(Unit) {
        scope.launch {
            val exito = checkoutViewModel.confirmarYGuardarPedido()
            if (exito) {
                pedidoConfirmado = true
                numeroPedido = pedidoActual?.id ?: "N/A"
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (pedidoConfirmado) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp),
                    tonalElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .navigationBarsPadding(),
                    ) {
                        Button(
                            onClick = onIrAlInicio,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Ir al inicio",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Ir al Inicio")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            if (pedidoConfirmado) {
                // Pantalla de confirmación exitosa
                ConfirmacionExitosa(
                    numeroPedido = numeroPedido,
                    pedido = pedidoActual,
                    usuario = usuario
                )
            } else {
                // Pantalla de carga
                CargandoConfirmacion()
            }
        }
    }
}

@Composable
fun ConfirmacionExitosa(
    numeroPedido: String,
    pedido: com.example.app_pasteleria_mil_sabores.model.Pedido?,
    usuario: Usuario
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icono de confirmación
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = "Pedido confirmado",
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Título
        Text(
            "¡Pedido Confirmado!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Número de pedido
        Text(
            "Nº de Pedido:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            numeroPedido,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Resumen del pedido
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    "📦 Resumen del Pedido",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                pedido?.let {
                    // Información de envío
                    Text(
                        "🚚 Envío a:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    it.direccionEnvio?.let { direccion ->
                        Text(
                            "${direccion.calle} ${direccion.numero}" +
                                    (direccion.departamento?.let { depto -> ", Depto. $depto" } ?: ""),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "${direccion.comuna}, ${direccion.ciudad}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            direccion.region,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    // Información de contacto
                    Text(
                        "👤 Contacto:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        it.informacionContacto.nombre,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        it.informacionContacto.email,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    it.informacionContacto.telefono?.let { telefono ->
                        Text(
                            telefono,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    // Método de pago
                    Text(
                        "💳 Método de Pago:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        when (it.metodoPago) {
                            "contra_entrega" -> "Pago Contra Entrega"
                            "tarjeta" -> "Tarjeta de Crédito/Débito"
                            else -> it.metodoPago
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Productos
                    Text(
                        "🛒 Productos (${it.productos.size}):",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    it.productos.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "${item.cantidad} x ${item.producto.nombre}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                item.getPrecioTotal().formatearPrecio(),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))

                    // Resumen financiero
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal:", style = MaterialTheme.typography.bodyMedium)
                        Text(it.subtotal.formatearPrecio(), style = MaterialTheme.typography.bodyMedium)
                    }

                    if (it.descuentoAplicado > 0) {
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
                                "-${it.descuentoAplicado.formatearPrecio()}",
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
                            if (it.costoEnvio == 0) "GRATIS" else it.costoEnvio.formatearPrecio(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (it.costoEnvio == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))

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
                            it.total.formatearPrecio(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Información adicional
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    "📋 Próximos Pasos",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "• Recibirás un email de confirmación\n" +
                            "• Tu pedido será preparado en 24-48 horas\n" +
                            "• Te contactaremos para coordinar la entrega\n" +
                            "• Tiempo estimado de entrega: 2-3 días hábiles",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp)) // Espacio para el bottom bar
    }
}

@Composable
fun CargandoConfirmacion() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icono de carga
        CircularProgressIndicator(
            modifier = Modifier.size(80.dp),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Confirmando tu pedido...",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Estamos procesando tu información y generando el número de pedido",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}