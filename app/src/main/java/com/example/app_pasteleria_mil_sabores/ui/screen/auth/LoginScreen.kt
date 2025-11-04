package com.example.app_pasteleria_mil_sabores.ui.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
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

    val usuarioActual by viewModel.usuarioActual.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Navegar cuando el login sea exitoso
    LaunchedEffect(usuarioActual) {
        usuarioActual?.let { usuario ->
            onLoginExitoso(usuario)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (mostrarRecuperarPassword) {
            RecuperarPasswordSection(
                viewModel = viewModel,
                onCancelar = {
                    mostrarRecuperarPassword = false
                    viewModel.limpiarError()
                }
            )
        } else {
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                placeholder = { Text("ejemplo@duoc.cl") },
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (correo.isNotBlank() && password.isNotBlank()) {
                        viewModel.autenticarUsuario(correo, password)
                    } else {
                        viewModel.limpiarError()
                    }
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(text = "Iniciar Sesión")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = {
                    mostrarRecuperarPassword = true
                    viewModel.limpiarError()
                }) {
                    Text("¿Olvidaste tu contraseña?")
                }

                TextButton(onClick = {
                    onRegistrarClick()
                    viewModel.limpiarError()
                }) {
                    Text("Registrarse")
                }
            }

            // Información sobre los tipos de usuario
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Tipos de usuario:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "• admin@duoc.cl → Administrador",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "• cualquier@duoc.cl → Cliente",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RecuperarPasswordSection(
    viewModel: FormularioViewModel,
    onCancelar: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier.fillMaxWidth(0.8f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Recuperar Contraseña",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Ingresa tu correo") },
            placeholder = { Text("ejemplo@duoc.cl") },
            modifier = Modifier.fillMaxWidth()
        )

        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onCancelar) {
                Text("Cancelar")
            }

            Button(
                onClick = {
                    if (correo.isNotBlank()) {
                        viewModel.recuperarPassword(correo)
                    }
                },
                enabled = correo.isNotBlank()
            ) {
                Text("Recuperar")
            }
        }
    }
}