package com.example.app_pasteleria_mil_sabores.ui.screen.auth

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import com.example.app_pasteleria_mil_sabores.ui.components.EmailTextField
import com.example.app_pasteleria_mil_sabores.ui.components.PasswordTextField
import com.example.app_pasteleria_mil_sabores.utils.Validaciones
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.viewmodel.FormularioViewModel

@Composable
fun LoginScreen(
    viewModel: FormularioViewModel,
    onRegistrarClick: () -> Unit,
    onLoginExitoso: (Usuario) -> Unit,
    onBackPressed: () -> Unit
) {
    BackHandler(enabled = true) {
        onBackPressed()
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mostrarRecuperarPassword by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

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
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (formularioValido) {
                            viewModel.autenticarUsuario(email, password)
                        }
                        focusManager.clearFocus()
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
    val focusManager = LocalFocusManager.current

    val emailValido = email.isNotBlank() && (email.endsWith("@duoc.cl", ignoreCase = true) || email.equals("admin@duoc.cl", ignoreCase = true))

    Column(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Recuperar Contraseña",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        EmailTextField(
            value = email,
            onValueChange = {
                // Filtrar espacios automáticamente
                if (!it.contains(" ")) {
                    email = it
                }
            },
            label = "Ingresa tu correo",
            placeholder = "ejemplo@duoc.cl",
            isError = email.isNotBlank() && !emailValido,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (emailValido) {
                        viewModel.recuperarPassword(email)
                    }
                    focusManager.clearFocus()
                }
            ),
            supportingText = {
                if (email.isNotBlank() && !emailValido) {
                    Text(
                        text = "Debe ser un correo @duoc.cl",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
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