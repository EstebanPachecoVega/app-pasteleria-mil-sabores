package com.example.app_pasteleria_mil_sabores.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var codigoPromocional by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmarPasswordVisible by remember { mutableStateOf(false) }

    val errorMessage by viewModel.errorMessage.collectAsState()

    val usernameValido = username.isNotBlank() && username.length >= 3
    val emailValido = email.isNotBlank() && (
            email.endsWith("@duoc.cl", ignoreCase = true) ||
                    email.endsWith("@profesor.duoc.cl", ignoreCase = true) ||
                    email.endsWith("@gmail.com", ignoreCase = true) ||
                    email.equals("admin@duoc.cl", ignoreCase = true)
            )
    val passwordValido = password.length >= 6
    val confirmarPasswordValido = confirmarPassword == password
    val formularioValido = usernameValido && emailValido && passwordValido && confirmarPasswordValido

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

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre de usuario") },
            placeholder = { Text("Ingrese su nombre de usuario") },
            isError = username.isNotBlank() && !usernameValido,
            supportingText = {
                if (username.isNotBlank() && !usernameValido) {
                    Text(
                        text = "Mínimo 3 caracteres",
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

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            placeholder = { Text("Ingrese su correo electrónico") },
            isError = email.isNotBlank() && !emailValido,
            supportingText = {
                if (email.isNotBlank() && !emailValido) {
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

        OutlinedTextField(
            value = fechaNacimiento,
            onValueChange = { fechaNacimiento = it },
            label = { Text("Fecha de nacimiento (opcional)") },
            placeholder = { Text("dd/MM/yyyy") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            ),
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = codigoPromocional,
            onValueChange = { codigoPromocional = it },
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

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
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

        OutlinedTextField(
            value = confirmarPassword,
            onValueChange = { confirmarPassword = it },
            label = { Text("Confirmar Contraseña") },
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