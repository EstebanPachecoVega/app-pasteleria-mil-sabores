package com.example.app_pasteleria_mil_sabores.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DatePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Formateador de fecha
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    // Función para mostrar el DatePicker
    fun showDatePicker() {
        // Si ya hay una fecha, usarla como valor inicial
        val initialCalendar = if (value.isNotBlank()) {
            try {
                val fecha = dateFormatter.parse(value)
                Calendar.getInstance().apply { time = fecha }
            } catch (e: Exception) {
                calendar // Usar fecha actual si hay error
            }
        } else {
            calendar // Usar fecha actual si no hay valor
        }

        val datePickerDialog = android.app.DatePickerDialog(
            context,
            { _, year, month, day ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                val formattedDate = dateFormatter.format(selectedCalendar.time)
                onValueChange(formattedDate)
            },
            initialCalendar.get(Calendar.YEAR),
            initialCalendar.get(Calendar.MONTH),
            initialCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    // Custom colors para el campo deshabilitado pero interactivo
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        errorBorderColor = MaterialTheme.colorScheme.error,
        disabledBorderColor = MaterialTheme.colorScheme.outline,
        disabledTextColor = MaterialTheme.colorScheme.onSurface,
        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledTrailingIconColor = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
    )

    OutlinedTextField(
        value = value,
        onValueChange = { }, // No permitir edición manual
        label = { Text(label) },
        placeholder = { Text("dd/MM/yyyy") },
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) {
                if (enabled) {
                    showDatePicker()
                }
            },
        readOnly = true, // Usar readOnly en lugar de enabled=false para mostrar el valor
        trailingIcon = {
            Icon(
                Icons.Default.CalendarToday,
                contentDescription = "Seleccionar fecha",
                modifier = Modifier
                    .clickable(enabled = enabled) {
                        if (enabled) {
                            showDatePicker()
                        }
                    }
                    .padding(4.dp),
                tint = if (enabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        },
        isError = isError,
        supportingText = supportingText,
        colors = textFieldColors
    )
}