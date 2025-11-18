package com.example.app_pasteleria_mil_sabores.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ItemMenuCerrarSesion(
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text("Cerrar Sesión")
        },
        onClick = onClick,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Cerrar sesión",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )
}