package com.example.app_pasteleria_mil_sabores.ui.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
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

    val errorMessage by viewModel.errorMessage.collectAsState()

    // Determinar el tipo de usuario que se creará
    val tipoUsuario = remember(correo) {
        when {
            correo.equals("admin@duoc.cl", ignoreCase = true) -> "Administrador"
            correo.endsWith("@duoc.cl", ignoreCase = true) -> "Cliente"
            else -> "Cliente"
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
            text = "Registro",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            placeholder = { Text("ejemplo@duoc.cl") },
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        // Mostrar el tipo de usuario que se creará
        if (correo.isNotBlank()) {
            Text(
                text = "Serás registrado como: $tipoUsuario",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmarPassword,
            onValueChange = { confirmarPassword = it },
            label = { Text("Confirmar Contraseña") },
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

        Row (
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {
                onVolver()
                viewModel.limpiarError()
            }) {
                Text("Volver")
            }

            Button(
                onClick = {
                    if (correo.isNotBlank() && password.isNotBlank()) {
                        if (password == confirmarPassword) {
                            viewModel.agregarUsuario(correo, password)
                            // Limpiar campos después del registro
                            correo = ""
                            password = ""
                            confirmarPassword = ""
                            onRegistroExitoso()
                        } else {
                            // Mostrar error de contraseñas no coinciden
                            viewModel.limpiarError()
                        }
                    }
                },
                enabled = correo.isNotBlank() && password.isNotBlank() && password == confirmarPassword
            ) {
                Text("Registrarse")
            }
        }

        // Información sobre los tipos de usuario
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Información:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "• admin@duoc.cl → Cuenta de Administrador",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "• cualquier@duoc.cl → Cuenta de Cliente",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}