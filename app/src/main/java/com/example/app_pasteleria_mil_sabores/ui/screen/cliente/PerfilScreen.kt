package com.example.app_pasteleria_mil_sabores.ui.screen.cliente

import android.net.Uri
import androidx.activity.compose.BackHandler
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
import android.R as AndroidR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    usuario: Usuario,
    viewModel: PerfilViewModel,
    onVolver: () -> Unit,
    onUsuarioActualizado: (Usuario) -> Unit,
    onBackPressed: () -> Unit
) {
    var username by remember { mutableStateOf(usuario.username) }
    var fechaNacimiento by remember { mutableStateOf(usuario.fechaNacimiento ?: "") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }
    var mostrarDialogoPassword by remember { mutableStateOf(false) }
    var fotoPerfilUri by remember { mutableStateOf(usuario.fotoPerfil) }
    var modoEdicion by remember { mutableStateOf(false) }
    var mostrarDialogoDescartar by remember { mutableStateOf(false) }

    val mensaje by viewModel.mensaje.collectAsState()
    val usuarioActualizado by viewModel.usuarioActualizado.collectAsState()
    val fotoPerfilActualizada by viewModel.fotoPerfilActualizada.collectAsState()
    val puedeRealizarCambios = viewModel.puedeRealizarCambiosLimitados()

    // Validaciones para el botón Guardar
    val cambiosValidos = (username != usuario.username ||
            fechaNacimiento != (usuario.fechaNacimiento ?: "")) &&
            username.isNotBlank() &&
            username.length >= 3 &&
            (fechaNacimiento.isBlank() ||
                    (Validaciones.validarFechaNacimiento(fechaNacimiento) &&
                            Validaciones.esMayorDe17Anios(fechaNacimiento)))

    // Función para verificar si hay cambios sin guardar REALES
    fun hayCambiosSinGuardar(): Boolean {
        val hayCambiosUsername = username != usuario.username
        val hayCambiosFecha = fechaNacimiento != (usuario.fechaNacimiento ?: "")
        val hayCambiosPassword = password.isNotBlank() || confirmarPassword.isNotBlank()

        return hayCambiosUsername || hayCambiosFecha || hayCambiosPassword
    }

    // Determinar el tipo de mensaje para el toast
    val toastType = when {
        mensaje?.contains("Error") == true -> ToastType.ERROR
        mensaje?.contains("éxito") == true -> ToastType.SUCCESS
        else -> ToastType.INFO
    }

    // Efecto para sincronizar la foto actualizada del ViewModel
    LaunchedEffect(fotoPerfilActualizada) {
        fotoPerfilActualizada?.let { nuevaUri ->
            fotoPerfilUri = nuevaUri
            // Actualizar el usuario globalmente con la nueva foto
            val usuarioConFotoActualizada = usuario.copy(fotoPerfil = nuevaUri)
            onUsuarioActualizado(usuarioConFotoActualizada)
        }
    }

    // Efecto para notificar cuando el usuario se actualice
    LaunchedEffect(usuarioActualizado) {
        usuarioActualizado?.let { actualizado ->
            onUsuarioActualizado(actualizado)
            // Actualizar el estado local con los nuevos datos
            username = actualizado.username
            fechaNacimiento = actualizado.fechaNacimiento ?: ""
            fotoPerfilUri = actualizado.fotoPerfil
        }
    }

    // Efecto para cargar la foto al entrar a la pantalla
    LaunchedEffect(usuario.id) {
        viewModel.cargarFotoPerfil(usuario.id)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            fotoPerfilUri = uri.toString()
            viewModel.actualizarFotoPerfil(usuario, fotoPerfilUri)
        }
    }

    // BackHandler personalizado para esta pantalla
    BackHandler (enabled = true) {
        if (modoEdicion && hayCambiosSinGuardar()) {
            mostrarDialogoDescartar = true
        } else {
            onVolver()
        }
    }

    // Diálogo para descartar cambios
    if (mostrarDialogoDescartar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoDescartar = false },
            title = { Text("¿Descartar los cambios sin guardar?") },
            text = { Text("Hay cambios sin guardar. ¿Seguro que quieres descartarlos?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoDescartar = false
                        modoEdicion = false
                        // Restaurar valores originales
                        username = usuario.username
                        fechaNacimiento = usuario.fechaNacimiento ?: ""
                        password = ""
                        confirmarPassword = ""
                    }
                ) {
                    Text("Descartar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogoDescartar = false }
                ) {
                    Text("Seguir editando")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (modoEdicion && hayCambiosSinGuardar()) {
                                mostrarDialogoDescartar = true
                            } else {
                                onVolver()
                            }
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            if (modoEdicion) {
                FloatingActionButton(
                    onClick = {
                        if (cambiosValidos) {
                            val usuarioActualizado = usuario.copy(
                                username = username,
                                fechaNacimiento = if (fechaNacimiento.isNotBlank()) fechaNacimiento else null
                            )
                            val esCambioLimitado = fechaNacimiento != (usuario.fechaNacimiento ?: "")
                            viewModel.actualizarUsuario(usuarioActualizado, esCambioLimitado)
                            modoEdicion = false
                        }
                    },
                    modifier = Modifier.size(70.dp),
                    containerColor = if (cambiosValidos) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (cambiosValidos) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = "Guardar cambios",
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            "Guardar",
                            style = MaterialTheme.typography.labelSmall
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
                    if (!fotoPerfilUri.isNullOrBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .data(fotoPerfilUri)
                                    .diskCacheKey("profile_${usuario.id}")
                                    .memoryCacheKey("profile_${usuario.id}")
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
                            painter = painterResource(AndroidR.drawable.ic_menu_gallery),
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

                Text(
                    text = usuario.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                if (modoEdicion && hayCambiosSinGuardar()) {
                                    mostrarDialogoDescartar = true
                                } else {
                                    modoEdicion = !modoEdicion
                                    if (!modoEdicion) {
                                        // Restaurar valores originales al cancelar
                                        username = usuario.username
                                        fechaNacimiento = usuario.fechaNacimiento ?: ""
                                        password = ""
                                        confirmarPassword = ""
                                    }
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

                    InfoItem("ID", usuario.id)

                    Spacer(modifier = Modifier.height(8.dp))

                    if (modoEdicion) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = {
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

                    InfoItem("Correo electrónico", usuario.email)

                    Spacer(modifier = Modifier.height(8.dp))

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
                                if (fechaNacimiento.isNotBlank() && modoEdicion) {
                                    when {
                                        !Validaciones.validarFechaNacimiento(fechaNacimiento) -> {
                                            Text(
                                                "Formato no válido. Use dd/MM/yyyy",
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

                        if (!puedeRealizarCambios) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "⚠️ Has alcanzado el límite máximo de 3 cambios permitidos",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Cambios restantes: ${viewModel.getCambiosRestantes()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        InfoItem(
                            "Fecha de nacimiento",
                            if (fechaNacimiento.isNotBlank()) fechaNacimiento else "No especificada"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (modoEdicion) {
                Button(
                    onClick = {
                        if (puedeRealizarCambios) {
                            mostrarDialogoPassword = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = puedeRealizarCambios
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cambiar Contraseña")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

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
            }
        }
    }

    CenteredToast(
        message = mensaje,
        onDismiss = { viewModel.limpiarMensaje() },
        type = toastType,
        duration = 3000
    )

    if (mostrarDialogoPassword) {
        CambiarPasswordDialog(
            password = password,
            confirmarPassword = confirmarPassword,
            onPasswordChange = {
                if (!it.contains(" ")) {
                    password = it
                }
            },
            onConfirmarPasswordChange = {
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