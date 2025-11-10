package com.example.app_pasteleria_mil_sabores.ui.screen.checkout

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.utils.formatearPrecio
import com.example.app_pasteleria_mil_sabores.viewmodel.CheckoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagoScreen(
    checkoutViewModel: CheckoutViewModel,
    usuario: Usuario,
    onVolver: () -> Unit,
    onConfirmarPedido: () -> Unit,
    onBackPressed: () -> Unit
) {
    BackHandler(enabled = true) {
        onBackPressed()
    }

    var metodoPagoSeleccionado by remember { mutableStateOf("") }
    val pedidoActual by checkoutViewModel.pedidoActual.collectAsState()

    // Calcular costo de envío
    val costoEnvio = remember(pedidoActual?.subtotal) {
        if ((pedidoActual?.subtotal ?: 0) >= 40000) 0 else 2500
    }

    val totalConEnvio = remember(pedidoActual?.total, costoEnvio) {
        (pedidoActual?.total ?: 0) + costoEnvio
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Método de Pago",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
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
                        onClick = {
                            checkoutViewModel.actualizarMetodoPago(metodoPagoSeleccionado)
                            checkoutViewModel.actualizarCostoEnvio()
                            onConfirmarPedido()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = MaterialTheme.shapes.large,
                        enabled = metodoPagoSeleccionado.isNotBlank()
                    ) {
                        Text(
                            "Confirmar Pedido",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    if (metodoPagoSeleccionado.isBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Selecciona un método de pago",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // Sección: Información de Envío
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "📦 Información de Envío",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    pedidoActual?.direccionEnvio?.let { direccion ->
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

                    pedidoActual?.informacionContacto?.let { contacto ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "👤 ${contacto.nombre}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "📧 ${contacto.email}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        contacto.telefono?.let { telefono ->
                            Text(
                                "📞 $telefono",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // Sección: Método de Pago
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .selectableGroup()
                ) {
                    Text(
                        "💳 Método de Pago",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Opción: Pago Contra Entrega
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = metodoPagoSeleccionado == "contra_entrega",
                                onClick = { metodoPagoSeleccionado = "contra_entrega" },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = metodoPagoSeleccionado == "contra_entrega",
                            onClick = null // null because the click is handled by the row
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            Icons.Default.LocalShipping,
                            contentDescription = "Pago contra entrega",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Pago Contra Entrega",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "Paga cuando recibas tu pedido",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Divider()

                    // Opción: Tarjeta de Crédito/Débito
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = metodoPagoSeleccionado == "tarjeta",
                                onClick = { /* No hacer nada - próximamente */ },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = metodoPagoSeleccionado == "tarjeta",
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            Icons.Default.CreditCard,
                            contentDescription = "Tarjeta de crédito/débito",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Tarjeta de Crédito/Débito",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                ),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "Próximamente disponible",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            // Sección: Resumen Final
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "🧾 Resumen Final",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    pedidoActual?.let { pedido ->
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
                            Text(
                                "Envío:",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                if (costoEnvio == 0) "GRATIS" else costoEnvio.formatearPrecio(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (costoEnvio == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (costoEnvio == 0) {
                            Text(
                                "¡Envío gratis por compra sobre ${40000.formatearPrecio()}!",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
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
                                "Total a Pagar:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                totalConEnvio.formatearPrecio(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp)) // Espacio para el bottom bar
        }
    }
}