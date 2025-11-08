package com.example.app_pasteleria_mil_sabores.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app_pasteleria_mil_sabores.ui.components.PasswordTextField
import com.example.app_pasteleria_mil_sabores.utils.Validaciones
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.viewmodel.FormularioViewModel

@Composable
fun LoginScreen(
    viewModel: FormularioViewModel,
    onRegistrarClick: () -> Unit,
    onLoginExitoso: (Usuario) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mostrarRecuperarPassword by remember { mutableStateOf(false) }

    val usuarioActual by viewModel.usuarioActual.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val emailValido = email.isNotBlank() && (
            email.endsWith("@duoc.cl", ignoreCase = true) ||
                    email.endsWith("@profesor.duoc.cl", ignoreCase = true) ||
                    email.endsWith("@gmail.com", ignoreCase = true) ||
                    email.equals("admin@duoc.cl", ignoreCase = true)
            )
    val passwordValido = Validaciones.validarPassword(password)
    val formularioValido = emailValido && passwordValido

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

            // Campo contraseña
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
                        viewModel.autenticarUsuario(email, password)
                    }
                },
                enabled = formularioValido,
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Iniciar Sesión")
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
                    Text("¿Olvidaste tu contraseña?")
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
                    Text("Registrarse")
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
    var email by remember { mutableStateOf("") }
    val errorMessage by viewModel.errorMessage.collectAsState()

    val emailValido = email.isNotBlank() && (email.endsWith("@duoc.cl", ignoreCase = true) || email.equals("admin@duoc.cl", ignoreCase = true))

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
            value = email,
            onValueChange = { email = it },
            label = { Text("Ingresa tu correo") },
            placeholder = { Text("ejemplo@duoc.cl") },
            isError = email.isNotBlank() && !emailValido,
            supportingText = {
                if (email.isNotBlank() && !emailValido) {
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
                Text("Volver")
            }

            Button(
                onClick = {
                    if (emailValido) {
                        viewModel.recuperarPassword(email)
                    }
                },
                enabled = emailValido,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Recuperar")
            }
        }
    }
}