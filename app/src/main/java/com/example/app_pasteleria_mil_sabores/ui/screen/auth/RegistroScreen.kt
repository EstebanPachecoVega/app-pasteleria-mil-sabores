package com.example.app_pasteleria_mil_sabores.ui.screen.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import com.example.app_pasteleria_mil_sabores.ui.components.DatePickerField
import com.example.app_pasteleria_mil_sabores.ui.components.EmailTextField
import com.example.app_pasteleria_mil_sabores.ui.components.PasswordTextField
import com.example.app_pasteleria_mil_sabores.ui.components.UsernameTextField
import com.example.app_pasteleria_mil_sabores.utils.Validaciones
import com.example.app_pasteleria_mil_sabores.viewmodel.FormularioViewModel

@Composable
fun RegistroScreen(
    viewModel: FormularioViewModel,
    onRegistroExitoso: () -> Unit,
    onVolver: () -> Unit,
    onBackPressed: () -> Unit
) {
    BackHandler(enabled = true) {
        onBackPressed()
    }

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var codigoPromocional by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val errorMessage by viewModel.errorMessage.collectAsState()

    // USANDO LAS VALIDACIONES REUTILIZABLES
    val usernameValido = Validaciones.validarUsername(username)
    val emailValido = email.isNotBlank() && (
            email.endsWith("@duoc.cl", ignoreCase = true) ||
                    email.endsWith("@profesor.duoc.cl", ignoreCase = true) ||
                    email.endsWith("@gmail.com", ignoreCase = true) ||
                    email.equals("admin@duoc.cl", ignoreCase = true)
            )
    val passwordValido = Validaciones.validarPassword(password)
    val confirmarPasswordValido = confirmarPassword == password
    val fechaValida = fechaNacimiento.isBlank() || Validaciones.validarFechaNacimientoCompleta(fechaNacimiento)
    val formularioValido = usernameValido && emailValido && passwordValido && confirmarPasswordValido && fechaValida

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registro",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        UsernameTextField(
            value = username,
            onValueChange = {
                // Filtrar espacios automáticamente
                if (!it.contains(" ")) {
                    username = it
                }
            },
            label = "Nombre de usuario",
            placeholder = "Ingrese su nombre de usuario",
            isError = username.isNotBlank() && !usernameValido,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            supportingText = {
                if (username.isNotBlank() && !usernameValido) {
                    Text(
                        text = "Mínimo 3 caracteres sin espacios",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        EmailTextField(
            value = email,
            onValueChange = {
                // Filtrar espacios automáticamente
                if (!it.contains(" ")) {
                    email = it
                }
            },
            label = "Correo electrónico",
            placeholder = "Ingrese su correo electrónico",
            isError = email.isNotBlank() && !emailValido,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            supportingText = {
                if (email.isNotBlank() && !emailValido) {
                    Text(
                        text = "Debe ser un correo válido",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // DatePicker para fecha de nacimiento - SIN keyboardOptions y keyboardActions
        DatePickerField(
            value = fechaNacimiento,
            onValueChange = { nuevaFecha ->
                fechaNacimiento = nuevaFecha
            },
            label = "Fecha de nacimiento (opcional)",
            isError = fechaNacimiento.isNotBlank() && !fechaValida,
            supportingText = {
                if (fechaNacimiento.isNotBlank()) {
                    when {
                        !Validaciones.validarFechaNacimiento(fechaNacimiento) -> {
                            Text(
                                "Formato no válido. Use dd/MM/yyyy",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        !Validaciones.validarFechaNoFutura(fechaNacimiento) -> {
                            Text(
                                "La fecha no puede ser futura",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        !Validaciones.esMayorDe17Anios(fechaNacimiento) -> {
                            Text(
                                "Debes tener 17 años o más.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                } else {
                    Text(
                        "Opcional - Haz clic para seleccionar fecha",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = codigoPromocional,
            onValueChange = {
                // Filtrar espacios automáticamente
                if (!it.contains(" ")) {
                    codigoPromocional = it
                }
            },
            label = { Text("Código promocional (opcional)") },
            placeholder = { Text("Ej: CODIGO_PROMOCIONAL") },
            trailingIcon = {
                if (codigoPromocional.equals("FELICES50", ignoreCase = true)) {
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = "Código válido",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            supportingText = {
                if (codigoPromocional.isNotBlank()) {
                    Text(
                        text = if (codigoPromocional.equals("FELICES50", ignoreCase = true)) {
                            "10% de descuento permanente"
                        } else {
                            "Código no reconocido"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (codigoPromocional.equals("FELICES50", ignoreCase = true)) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.error
                        }
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            ),
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo contraseña con validación de espacios
        PasswordTextField(
            value = password,
            onValueChange = {
                // Filtrar espacios automáticamente
                if (!it.contains(" ")) {
                    password = it
                }
            },
            label = "Contraseña",
            modifier = Modifier.fillMaxWidth(0.8f),
            isError = password.isNotBlank() && !passwordValido,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
            supportingText = {
                if (password.isNotBlank() && !passwordValido) {
                    Text(
                        text = "Mínimo 6 caracteres sin espacios",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo confirmar contraseña con validación de espacios
        PasswordTextField(
            value = confirmarPassword,
            onValueChange = {
                // Filtrar espacios automáticamente
                if (!it.contains(" ")) {
                    confirmarPassword = it
                }
            },
            label = "Confirmar Contraseña",
            modifier = Modifier.fillMaxWidth(0.8f),
            isError = confirmarPassword.isNotBlank() && !confirmarPasswordValido,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (formularioValido) {
                        viewModel.agregarUsuario(
                            username = username,
                            email = email,
                            password = password,
                            fechaNacimiento = if (fechaNacimiento.isNotBlank()) fechaNacimiento else null,
                            codigoPromocion = if (codigoPromocional.isNotBlank()) codigoPromocional else null
                        )
                        username = ""
                        email = ""
                        password = ""
                        confirmarPassword = ""
                        fechaNacimiento = ""
                        codigoPromocional = ""
                        onRegistroExitoso()
                    }
                    focusManager.clearFocus()
                }
            ),
            supportingText = {
                if (confirmarPassword.isNotBlank()) {
                    Text(
                        text = if (confirmarPasswordValido) "Contraseñas coinciden" else "Las contraseñas no coinciden",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (confirmarPasswordValido) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        errorMessage?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    onVolver()
                    viewModel.limpiarError()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text("Volver")
            }

            Button(
                onClick = {
                    if (formularioValido) {
                        viewModel.agregarUsuario(
                            username = username,
                            email = email,
                            password = password,
                            fechaNacimiento = if (fechaNacimiento.isNotBlank()) fechaNacimiento else null,
                            codigoPromocion = if (codigoPromocional.isNotBlank()) codigoPromocional else null
                        )
                        username = ""
                        email = ""
                        password = ""
                        confirmarPassword = ""
                        fechaNacimiento = ""
                        codigoPromocional = ""
                        onRegistroExitoso()
                    }
                },
                enabled = formularioValido,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Registrarse")
            }
        }
    }
}