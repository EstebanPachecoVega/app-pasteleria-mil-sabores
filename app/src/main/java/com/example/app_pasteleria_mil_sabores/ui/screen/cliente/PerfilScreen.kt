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
import com.example.app_pasteleria_mil_sabores.ui.components.CenteredToast
import com.example.app_pasteleria_mil_sabores.ui.components.DatePickerField
import com.example.app_pasteleria_mil_sabores.ui.components.PasswordTextField
import com.example.app_pasteleria_mil_sabores.ui.components.ToastType
import com.example.app_pasteleria_mil_sabores.utils.Validaciones
import com.example.app_pasteleria_mil_sabores.viewmodel.PerfilViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    usuario: Usuario,
    viewModel: PerfilViewModel,
    onVolver: () -> Unit,
    onUsuarioActualizado: (Usuario) -> Unit
) {
    var username by remember { mutableStateOf(usuario.username) }
    var fechaNacimiento by remember { mutableStateOf(usuario.fechaNacimiento ?: "") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }
    var mostrarDialogoPassword by remember { mutableStateOf(false) }
    var fotoPerfilUri by remember { mutableStateOf(usuario.fotoPerfil) }
    var modoEdicion by remember { mutableStateOf(false) }

    val mensaje by viewModel.mensaje.collectAsState()
    val cambiosRealizados by viewModel.cambiosLimitadosRealizados.collectAsState()
    val usuarioActualizado by viewModel.usuarioActualizado.collectAsState()
    val puedeRealizarCambios = viewModel.puedeRealizarCambiosLimitados()

    // Determinar el tipo de mensaje para el toast
    val toastType = when {
        mensaje?.contains("Error") == true -> ToastType.ERROR
        mensaje?.contains("éxito") == true -> ToastType.SUCCESS
        else -> ToastType.INFO
    }

    // Efecto para notificar cuando el usuario se actualice
    LaunchedEffect(usuarioActualizado) {
        usuarioActualizado?.let { actualizado ->
            onUsuarioActualizado(actualizado)
        }
    }

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
                    text = username,
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
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Información Personal",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Button(
                            onClick = {
                                modoEdicion = !modoEdicion
                                if (!modoEdicion) {
                                    // Al cancelar edición, restaurar valores originales
                                    username = usuario.username
                                    fechaNacimiento = usuario.fechaNacimiento ?: ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (modoEdicion) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                if (modoEdicion) Icons.Default.Close else Icons.Default.Edit,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (modoEdicion) "Cancelar" else "Editar Perfil")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ID
                    InfoItem("ID", usuario.id)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Nombre de usuario
                    if (modoEdicion) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = {
                                // Filtrar espacios automáticamente
                                if (!it.contains(" ")) {
                                    username = it
                                }
                            },
                            label = { Text("Nombre de usuario") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = username.isNotBlank() && !Validaciones.validarUsername(username),
                            supportingText = {
                                if (username.isNotBlank() && !Validaciones.validarUsername(username)) {
                                    Text(
                                        "Mínimo 3 caracteres sin espacios",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        )
                    } else {
                        InfoItem("Nombre de usuario", username)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Correo electrónico
                    InfoItem("Correo electrónico", usuario.email)

                    Spacer(modifier = Modifier.height(8.dp))

                    // Fecha de nacimiento
                    if (modoEdicion) {
                        DatePickerField(
                            value = fechaNacimiento,
                            onValueChange = { nuevaFecha ->
                                fechaNacimiento = nuevaFecha
                            },
                            label = "Fecha de nacimiento",
                            enabled = puedeRealizarCambios,
                            isError = fechaNacimiento.isNotBlank() &&
                                    (!Validaciones.validarFechaNacimiento(fechaNacimiento) ||
                                            !Validaciones.esMayorDe17Anios(fechaNacimiento)),
                            supportingText = {
                                if (modoEdicion) {
                                    Text(
                                        "Cambios restantes: ${3 - cambiosRealizados}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (puedeRealizarCambios) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                    )
                                }
                                if (fechaNacimiento.isNotBlank() && modoEdicion) {
                                    when {
                                        !Validaciones.validarFechaNacimiento(fechaNacimiento) -> {
                                            Text(
                                                "Formato inválido. Use dd/MM/yyyy",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                        !Validaciones.esMayorDe17Anios(fechaNacimiento) -> {
                                            Text(
                                                "Debes tener 17 años o más",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    } else {
                        InfoItem(
                            "Fecha de nacimiento",
                            if (fechaNacimiento.isNotBlank()) fechaNacimiento else "No especificada"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Cambiar Contraseña (solo visible en modo edición)
            if (modoEdicion) {
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
            }

            // Sección de Beneficios
            if (usuario.codigoPromocion != null || usuario.esEstudianteDuoc || usuario.esProfesorDuoc) {
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

                        // BENEFICIO DE CUMPLEAÑOS - SIEMPRE VISIBLE PARA ESTUDIANTES DUOC
                        if (usuario.esEstudianteDuoc) {
                            BenefitItem(
                                icon = Icons.Default.Cake,
                                title = if (usuario.esSuCumpleanos()) "¡Feliz Cumpleaños!" else "Beneficio de Cumpleaños",
                                description = "Promoción especial",
                                benefit = if (usuario.esSuCumpleanos()) {
                                    "🎉 ¡Torta gratis hoy por ser tu cumpleaños! 🎉"
                                } else {
                                    "Torta gratis en tu cumpleaños por ser estudiante Duoc UC"
                                }
                            )
                        }

                        if (usuario.esEstudianteDuoc || usuario.esProfesorDuoc) {
                            BenefitItem(
                                icon = Icons.Default.School,
                                title = "Beneficio Educacional",
                                description = if (usuario.esEstudianteDuoc) "Estudiante Duoc UC" else "Profesor Duoc UC",
                                benefit = "Descuento especial para la comunidad Duoc UC"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Información de cambios (solo visible en modo edición)
            if (modoEdicion) {
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

                // Botón Guardar Cambios (solo visible en modo edición)
                Button(
                    onClick = {
                        val usuarioActualizado = usuario.copy(
                            username = username,
                            fechaNacimiento = if (fechaNacimiento.isNotBlank()) fechaNacimiento else null
                        )
                        val esCambioLimitado = fechaNacimiento != (usuario.fechaNacimiento ?: "")
                        viewModel.actualizarUsuario(usuarioActualizado, esCambioLimitado)
                        modoEdicion = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = (username != usuario.username || fechaNacimiento != (usuario.fechaNacimiento ?: "")) &&
                            username.isNotBlank() &&
                            username.length >= 3 &&
                            (fechaNacimiento.isBlank() ||
                                    (Validaciones.validarFechaNacimiento(fechaNacimiento) &&
                                            Validaciones.esMayorDe17Anios(fechaNacimiento)))
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardar Cambios")
                }
            }
        }
    }

    // CenteredToast para mensajes
    CenteredToast(
        message = mensaje,
        onDismiss = { viewModel.limpiarMensaje() },
        type = toastType,
        duration = 1000 // 3 segundos para mensajes importantes
    )

    // Diálogo para cambiar contraseña
    if (mostrarDialogoPassword) {
        CambiarPasswordDialog(
            password = password,
            confirmarPassword = confirmarPassword,
            onPasswordChange = {
                // Filtrar espacios automáticamente
                if (!it.contains(" ")) {
                    password = it
                }
            },
            onConfirmarPasswordChange = {
                // Filtrar espacios automáticamente
                if (!it.contains(" ")) {
                    confirmarPassword = it
                }
            },
            onConfirmar = {
                if (password.length >= 6 && password == confirmarPassword) {
                    viewModel.cambiarPassword(usuario, password)
                    password = ""
                    confirmarPassword = ""
                    mostrarDialogoPassword = false
                    modoEdicion = false
                }
            },
            onCancelar = {
                mostrarDialogoPassword = false
                password = ""
                confirmarPassword = ""
            }
        )
    }
}

@Composable
fun CambiarPasswordDialog(
    password: String,
    confirmarPassword: String,
    onPasswordChange: (String) -> Unit,
    onConfirmarPasswordChange: (String) -> Unit,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Cambiar Contraseña") },
        text = {
            Column {
                PasswordTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = "Nueva contraseña",
                    isError = password.isNotBlank() && !Validaciones.validarPassword(password),
                    supportingText = {
                        if (password.isNotBlank() && !Validaciones.validarPassword(password)) {
                            Text(
                                "Mínimo 6 caracteres sin espacios",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                PasswordTextField(
                    value = confirmarPassword,
                    onValueChange = onConfirmarPasswordChange,
                    label = "Confirmar contraseña",
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
                onClick = onConfirmar,
                enabled = Validaciones.validarPassword(password) && password == confirmarPassword
            ) {
                Text("Cambiar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    )
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