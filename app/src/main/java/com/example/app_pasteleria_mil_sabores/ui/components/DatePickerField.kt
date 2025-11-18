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
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

    // Calcular fecha mínima (101 años atrás) y máxima (hoy)
    val minCalendar = Calendar.getInstance().apply {
        set(currentYear - 101, currentMonth, currentDay)
    }
    val maxCalendar = Calendar.getInstance().apply {
        set(currentYear, currentMonth, currentDay)
    }

    // Formateador de fecha
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    // Función para mostrar el DatePicker
    fun showDatePicker() {
        val initialCalendar = if (value.isNotBlank()) {
            try {
                val fecha = dateFormatter.parse(value)
                Calendar.getInstance().apply { time = fecha }
            } catch (e: Exception) {
                calendar // Usar fecha actual si hay error
            }
        } else {
            // Valor inicial: 18 años atrás (para mayoría de edad)
            val defaultCalendar = Calendar.getInstance().apply {
                set(currentYear - 18, currentMonth, currentDay)
            }
            defaultCalendar
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

        // Establecer límites de fecha
        datePickerDialog.datePicker.minDate = minCalendar.timeInMillis
        datePickerDialog.datePicker.maxDate = maxCalendar.timeInMillis

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
        onValueChange = { },
        label = { Text(label) },
        placeholder = { Text("dd/MM/yyyy") },
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) {
                if (enabled) {
                    showDatePicker()
                }
            },
        readOnly = true,
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