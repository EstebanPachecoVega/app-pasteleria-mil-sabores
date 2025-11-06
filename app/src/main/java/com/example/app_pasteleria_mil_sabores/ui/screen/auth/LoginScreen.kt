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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.viewmodel.FormularioViewModel

@Composable
fun LoginScreen(
    viewModel: FormularioViewModel,
    onRegistrarClick: () -> Unit,
    onLoginExitoso: (Usuario) -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mostrarRecuperarPassword by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val usuarioActual by viewModel.usuarioActual.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Validaciones
    val correoValido = correo.isNotBlank() && (
            correo.endsWith("@duoc.cl", ignoreCase = true) ||
                    correo.endsWith("@profesor.duoc.cl", ignoreCase = true) ||
                    correo.endsWith("@gmail.com", ignoreCase = true) ||
                    correo.equals("admin@duoc.cl", ignoreCase = true)
            )
    val passwordValido = password.length >= 6
    val formularioValido = correoValido && passwordValido

    // Navegar cuando el login sea exitoso
    LaunchedEffect(usuarioActual) {
        usuarioActual?.let { usuario ->
            onLoginExitoso(usuario)
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
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (mostrarRecuperarPassword) {
            RecuperarPasswordSection(
                viewModel = viewModel,
                onVolver = {
                    mostrarRecuperarPassword = false
                    viewModel.limpiarError()
                }
            )
        } else {
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

            errorMessage?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (formularioValido) {
                        viewModel.autenticarUsuario(correo, password)
                    }
                },
                enabled = formularioValido,
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = {
                        mostrarRecuperarPassword = true
                        viewModel.limpiarError()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        "¿Olvidaste tu contraseña?",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                TextButton(
                    onClick = {
                        onRegistrarClick()
                        viewModel.limpiarError()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        "Registrarse",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
fun RecuperarPasswordSection(
    viewModel: FormularioViewModel,
    onVolver: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    val errorMessage by viewModel.errorMessage.collectAsState()

    val correoValido = correo.isNotBlank() && (correo.endsWith("@duoc.cl", ignoreCase = true) || correo.equals("admin@duoc.cl", ignoreCase = true))

    Column(
        modifier = Modifier.fillMaxWidth(0.8f).background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Recuperar Contraseña",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = {
                Text(
                    "Ingresa tu correo",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            placeholder = {
                Text(
                    "ejemplo@duoc.cl",
                    style = MaterialTheme.typography.bodySmall
                )
            },
            isError = correo.isNotBlank() && !correoValido,
            supportingText = {
                if (correo.isNotBlank() && !correoValido) {
                    Text(
                        text = "Debe ser un correo @duoc.cl",
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
            modifier = Modifier.fillMaxWidth()
        )

        errorMessage?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onVolver,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(
                    "Volver", // Cambiado de "Cancelar" a "Volver"
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Button(
                onClick = {
                    if (correoValido) {
                        viewModel.recuperarPassword(correo)
                    }
                },
                enabled = correoValido,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    "Recuperar",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}