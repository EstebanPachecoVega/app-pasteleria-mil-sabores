package com.example.app_pasteleria_mil_sabores.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.MaterialTheme

@Composable
fun UsernameTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "Ingrese su nombre de usuario",
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Next
    ),
    keyboardActions: androidx.compose.foundation.text.KeyboardActions? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions ?: androidx.compose.foundation.text.KeyboardActions.Default,
        isError = isError,
        supportingText = supportingText,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            errorBorderColor = MaterialTheme.colorScheme.error,
        ),
        modifier = modifier.fillMaxWidth()
    )
}