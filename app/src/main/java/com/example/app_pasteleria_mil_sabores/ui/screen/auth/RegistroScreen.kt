package com.example.app_pasteleria_mil_sabores.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.app_pasteleria_mil_sabores.viewmodel.FormularioViewModel

@Composable
fun RegistroScreen(
    viewModel: FormularioViewModel,
    onRegistroExitoso: () -> Unit,
    onVolver: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var codigoPromocional by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmarPasswordVisible by remember { mutableStateOf(false) }

    val errorMessage by viewModel.errorMessage.collectAsState()

    // Validaciones
    val correoValido = correo.isNotBlank() && (
            correo.endsWith("@duoc.cl", ignoreCase = true) ||
                    correo.endsWith("@profesor.duoc.cl", ignoreCase = true) ||
                    correo.endsWith("@gmail.com", ignoreCase = true) ||
                    correo.equals("admin@duoc.cl", ignoreCase = true)
            )
    val passwordValido = password.length >= 6
    val confirmarPasswordValido = confirmarPassword == password
    val formularioValido = correoValido && passwordValido && confirmarPasswordValido

    // Determinar el tipo de usuario que se creará
    val tipoUsuario = remember(correo) {
        when {
            correo.equals("admin@duoc.cl", ignoreCase = true) -> "Administrador"
            correo.endsWith("@profesor.duoc.cl", ignoreCase = true) -> "Profesor"
            correo.endsWith("@duoc.cl", ignoreCase = true) -> "Cliente"
            correo.endsWith("@gmail.com", ignoreCase = true) -> "Cliente"
            else -> "Cliente"
        }
    }

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

        // Campo de correo
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = {
                Text(
                    "Correo electrónico",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            placeholder = {
                Text(
                    "Ingrese su correo electrónico",
                    style = MaterialTheme.typography.bodySmall
                )
            },
            isError = correo.isNotBlank() && !correoValido,
            supportingText = {
                if (correo.isNotBlank() && !correoValido) {
                    Text(
                        text = "Debe ser un correo válido",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor = MaterialTheme.colorScheme.error,
            ),
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de fecha de nacimiento (opcional)
        OutlinedTextField(
            value = fechaNacimiento,
            onValueChange = { fechaNacimiento = it },
            label = {
                Text(
                    "Fecha de nacimiento (opcional)",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            placeholder = {
                Text(
                    "dd/MM/yyyy",
                    style = MaterialTheme.typography.bodySmall
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            ),
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de código promocional (opcional)
        OutlinedTextField(
            value = codigoPromocional,
            onValueChange = { codigoPromocional = it },
            label = {
                Text(
                    "Código promocional (opcional)",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            placeholder = {
                Text(
                    "Ej: CODIGO_PROMOCIONAL",
                    style = MaterialTheme.typography.bodySmall
                )
            },
            trailingIcon = {
                if (codigoPromocional.equals("FELICES50", ignoreCase = true)) {
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = "Código válido",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
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

        // Campo de contraseña con ojo
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {
                Text(
                    "Contraseña",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            isError = password.isNotBlank() && !passwordValido,
            supportingText = {
                if (password.isNotBlank() && !passwordValido) {
                    Text(
                        text = "Mínimo 6 caracteres",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor = MaterialTheme.colorScheme.error,
            ),
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de confirmar contraseña con ojo
        OutlinedTextField(
            value = confirmarPassword,
            onValueChange = { confirmarPassword = it },
            label = {
                Text(
                    "Confirmar Contraseña",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            visualTransformation = if (confirmarPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmarPasswordVisible = !confirmarPasswordVisible }) {
                    Icon(
                        imageVector = if (confirmarPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (confirmarPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            isError = confirmarPassword.isNotBlank() && !confirmarPasswordValido,
            supportingText = {
                if (confirmarPassword.isNotBlank()) {
                    Text(
                        text = if (confirmarPasswordValido) "Contraseñas coinciden" else "Las contraseñas no coinciden",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (confirmarPasswordValido) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor = MaterialTheme.colorScheme.error,
            ),
            modifier = Modifier.fillMaxWidth(0.8f)
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

        Row (
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
                Text(
                    "Volver",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Button(
                onClick = {
                    if (formularioValido) {
                        viewModel.agregarUsuario(
                            correo = correo,
                            password = password,
                            fechaNacimiento = if (fechaNacimiento.isNotBlank()) fechaNacimiento else null,
                            codigoPromocion = if (codigoPromocional.isNotBlank()) codigoPromocional else null
                        )
                        // Limpiar campos después del registro
                        correo = ""
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
                Text(
                    "Registrarse",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}