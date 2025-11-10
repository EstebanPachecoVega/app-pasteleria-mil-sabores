package com.example.app_pasteleria_mil_sabores.ui.screen.checkout

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.app_pasteleria_mil_sabores.model.Direccion
import com.example.app_pasteleria_mil_sabores.model.InformacionContacto
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.viewmodel.CheckoutViewModel
import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.shadow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformacionEnvioScreen(
    checkoutViewModel: CheckoutViewModel,
    usuario: Usuario,
    onVolver: () -> Unit,
    onContinuarPago: () -> Unit,
    onBackPressed: () -> Unit
) {
    BackHandler(enabled = true) {
        onBackPressed()
    }

    val context = LocalContext.current

    // Estados locales para el formulario
    var nombre by remember { mutableStateOf(usuario.username) }
    var email by remember { mutableStateOf(usuario.email) }
    var telefono by remember { mutableStateOf("") }

    var calle by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }
    var departamento by remember { mutableStateOf("") }
    var comuna by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }

    val informacionContacto by checkoutViewModel.informacionContacto.collectAsState()
    val direccionEnvio by checkoutViewModel.direccionEnvio.collectAsState()

    // Cargar datos existentes si los hay
    LaunchedEffect(informacionContacto) {
        informacionContacto?.let { info ->
            nombre = info.nombre
            email = info.email
            telefono = info.telefono ?: ""
        }
    }

    LaunchedEffect(direccionEnvio) {
        direccionEnvio?.let { dir ->
            calle = dir.calle
            numero = dir.numero
            departamento = dir.departamento ?: ""
            comuna = dir.comuna
            ciudad = dir.ciudad
            region = dir.region
        }
    }

    val scrollState = rememberScrollState()

    // Validación del formulario
    val isFormValid = remember(nombre, email, calle, numero, comuna, ciudad, region) {
        nombre.isNotBlank() &&
                email.isNotBlank() &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                calle.isNotBlank() &&
                numero.isNotBlank() &&
                comuna.isNotBlank() &&
                ciudad.isNotBlank() &&
                region.isNotBlank()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Información de Envío",
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
                            // Guardar información en el ViewModel
                            val infoContacto = InformacionContacto(
                                nombre = nombre,
                                email = email,
                                telefono = telefono.ifBlank { null }
                            )

                            val direccion = Direccion(
                                calle = calle,
                                numero = numero,
                                departamento = departamento.ifBlank { null },
                                comuna = comuna,
                                ciudad = ciudad,
                                region = region,
                                coordenadas = null // Por ahora null, se integrará con geolocalización después
                            )

                            checkoutViewModel.actualizarInformacionContacto(infoContacto)
                            checkoutViewModel.actualizarDireccion(direccion)
                            onContinuarPago()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = MaterialTheme.shapes.large,
                        enabled = isFormValid
                    ) {
                        Text(
                            "Continuar a Pago",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    if (!isFormValid) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Completa todos los campos obligatorios (*)",
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
            // Sección: Información Personal
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
                        "Información Personal",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre completo *") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = "Nombre")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = nombre.isBlank()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email *") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = "Email")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono (Opcional)") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = "Teléfono")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                }
            }

            // Sección: Dirección de Envío
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
                        "Dirección de Envío",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = calle,
                            onValueChange = { calle = it },
                            label = { Text("Calle *") },
                            leadingIcon = {
                                Icon(Icons.Default.LocationOn, contentDescription = "Calle")
                            },
                            modifier = Modifier.weight(2f),
                            singleLine = true,
                            isError = calle.isBlank()
                        )

                        OutlinedTextField(
                            value = numero,
                            onValueChange = { numero = it },
                            label = { Text("Número *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = numero.isBlank()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = departamento,
                        onValueChange = { departamento = it },
                        label = { Text("Departamento/Casa (Opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = comuna,
                            onValueChange = { comuna = it },
                            label = { Text("Comuna *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            isError = comuna.isBlank()
                        )

                        OutlinedTextField(
                            value = ciudad,
                            onValueChange = { ciudad = it },
                            label = { Text("Ciudad *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            isError = ciudad.isBlank()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = region,
                        onValueChange = { region = it },
                        label = { Text("Región *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = region.isBlank()
                    )

                    // Sección para geolocalización (placeholder por ahora)
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // TODO: Integrar con API de geolocalización
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Geolocalización",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    "Seleccionar ubicación en el mapa",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    "Próximamente disponible",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            // Información de ayuda
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "📦 Información de Envío",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "• Envío gratis en compras sobre \$40.000\n" +
                                "• Costo de envío: \$2.500 para compras menores\n" +
                                "• Tiempo de entrega: 2-3 días hábiles",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp)) // Espacio para el bottom bar
        }
    }
}