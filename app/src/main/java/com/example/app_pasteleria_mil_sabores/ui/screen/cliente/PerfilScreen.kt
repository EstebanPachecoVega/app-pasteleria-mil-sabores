package com.example.app_pasteleria_mil_sabores.ui.screen.cliente

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.viewmodel.PerfilViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    usuario: Usuario,
    viewModel: PerfilViewModel,
    onVolver: () -> Unit
) {
    var username by remember { mutableStateOf(usuario.username) }
    var fechaNacimiento by remember { mutableStateOf(usuario.fechaNacimiento ?: "") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }
    var mostrarDialogoPassword by remember { mutableStateOf(false) }
    var fotoPerfilUri by remember { mutableStateOf(usuario.fotoPerfil) }

    val mensaje by viewModel.mensaje.collectAsState()
    val cambiosRealizados by viewModel.cambiosLimitadosRealizados.collectAsState()
    val puedeRealizarCambios = viewModel.puedeRealizarCambiosLimitados()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            fotoPerfilUri = uri.toString()
            viewModel.actualizarFotoPerfil(usuario, fotoPerfilUri)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(120.dp)
                ) {
                    if (fotoPerfilUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .data(fotoPerfilUri)
                                    .build()
                            ),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(android.R.drawable.ic_menu_gallery),
                            contentDescription = "Sin foto de perfil",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                        )
                    }

                    FloatingActionButton(
                        onClick = {
                            launcher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(40.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Cambiar foto")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = usuario.username,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
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

                    InfoItem("ID", usuario.id)

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Nombre de usuario") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = puedeRealizarCambios,
                        isError = username.isBlank() || username.length < 3,
                        supportingText = {
                            if (username.isBlank() || username.length < 3) {
                                Text(
                                    "Mínimo 3 caracteres",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    InfoItem("Correo electrónico", usuario.email)

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = fechaNacimiento,
                        onValueChange = { fechaNacimiento = it },
                        label = { Text("Fecha de nacimiento") },
                        placeholder = { Text("dd/MM/yyyy") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = puedeRealizarCambios
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { mostrarDialogoPassword = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = puedeRealizarCambios
            ) {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cambiar Contraseña")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (usuario.codigoPromocion != null || usuario.esEstudianteDuoc) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Mis Beneficios",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (usuario.codigoPromocion != null) {
                            BenefitItem(
                                icon = Icons.Default.Discount,
                                title = "Código Promocional",
                                description = "Código: ${usuario.codigoPromocion}",
                                benefit = when (usuario.codigoPromocion.uppercase()) {
                                    "FELICES50" -> "10% de descuento permanente en todas tus compras"
                                    else -> "Beneficio especial aplicado"
                                }
                            )
                        }

                        if (usuario.esEstudianteDuoc && usuario.esSuCumpleanos()) {
                            BenefitItem(
                                icon = Icons.Default.Cake,
                                title = "¡Feliz Cumpleaños!",
                                description = "Promoción especial",
                                benefit = "Torta gratis en tu cumpleaños por ser estudiante Duoc"
                            )
                        }

                        if (usuario.esEstudianteDuoc || usuario.esProfesorDuoc) {
                            BenefitItem(
                                icon = Icons.Default.School,
                                title = "Beneficio Educacional",
                                description = if (usuario.esEstudianteDuoc) "Estudiante Duoc" else "Profesor Duoc",
                                benefit = "Descuento especial para la comunidad Duoc UC"
                            )
                        }
                    }
                }
            }

            if (!puedeRealizarCambios) {
                Text(
                    "Has alcanzado el límite de cambios permitidos (3)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    "Cambios de información realizados: $cambiosRealizados/3",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            Button(
                onClick = {
                    val usuarioActualizado = usuario.copy(
                        username = username,
                        fechaNacimiento = if (fechaNacimiento.isNotBlank()) fechaNacimiento else null
                    )
                    viewModel.actualizarUsuario(usuarioActualizado, esCambioLimitado = true)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = puedeRealizarCambios &&
                        (username != usuario.username || fechaNacimiento != (usuario.fechaNacimiento ?: "")) &&
                        username.isNotBlank() && username.length >= 3
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar Cambios")
            }

            mensaje?.let { message ->
                Text(
                    text = message,
                    color = if (message.contains("Error")) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
                LaunchedEffect(message) {
                    if (!message.contains("Error")) {
                        delay(3000)
                        viewModel.limpiarMensaje()
                    }
                }
            }
        }
    }

    if (mostrarDialogoPassword) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoPassword = false },
            title = { Text("Cambiar Contraseña") },
            text = {
                Column {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Nueva contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = password.isNotBlank() && password.length < 6,
                        supportingText = {
                            if (password.isNotBlank() && password.length < 6) {
                                Text(
                                    "Mínimo 6 caracteres",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmarPassword,
                        onValueChange = { confirmarPassword = it },
                        label = { Text("Confirmar contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = confirmarPassword.isNotBlank() && password != confirmarPassword,
                        supportingText = {
                            if (confirmarPassword.isNotBlank()) {
                                Text(
                                    text = if (password == confirmarPassword) "Contraseñas coinciden" else "Las contraseñas no coinciden",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (password == confirmarPassword) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (password.length >= 6 && password == confirmarPassword) {
                            val usuarioActualizado = usuario.copy(password = password)
                            viewModel.actualizarUsuario(usuarioActualizado, esCambioLimitado = true)
                            password = ""
                            confirmarPassword = ""
                            mostrarDialogoPassword = false
                        }
                    },
                    enabled = password.length >= 6 && password == confirmarPassword
                ) {
                    Text("Cambiar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogoPassword = false
                    password = ""
                    confirmarPassword = ""
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun BenefitItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String, benefit: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 12.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = benefit,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}