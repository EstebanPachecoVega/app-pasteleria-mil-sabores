package com.example.app_pasteleria_mil_sabores.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

@Composable
fun CenteredToast(
    message: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    type: ToastType = ToastType.SUCCESS,
    duration: Long = 1000 // 1 segundo por defecto
) {
    AnimatedVisibility(
        visible = message != null,
        enter = fadeIn(animationSpec = tween(300)) + scaleIn(
            animationSpec = tween(300),
            initialScale = 0.8f
        ),
        exit = fadeOut(animationSpec = tween(300)) + scaleOut(
            animationSpec = tween(300),
            targetScale = 0.8f
        )
    ) {
        if (message != null) {
            Dialog(
                onDismissRequest = onDismiss,
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                    usePlatformDefaultWidth = false
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .shadow(
                                elevation = 16.dp,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clip(RoundedCornerShape(16.dp)),
                        color = when (type) {
                            ToastType.SUCCESS -> MaterialTheme.colorScheme.primaryContainer
                            ToastType.ERROR -> MaterialTheme.colorScheme.errorContainer
                            ToastType.INFO -> MaterialTheme.colorScheme.secondaryContainer
                        }
                    ) {
                        Box(
                            modifier = Modifier.padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column (
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = when (type) {
                                        ToastType.SUCCESS -> Icons.Default.CheckCircle
                                        ToastType.ERROR -> Icons.Default.Error
                                        ToastType.INFO -> Icons.Default.Info
                                    },
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .padding(bottom = 8.dp),
                                    tint = when (type) {
                                        ToastType.SUCCESS -> MaterialTheme.colorScheme.primary
                                        ToastType.ERROR -> MaterialTheme.colorScheme.error
                                        ToastType.INFO -> MaterialTheme.colorScheme.secondary
                                    }
                                )
                                Text(
                                    text = message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = when (type) {
                                        ToastType.SUCCESS -> MaterialTheme.colorScheme.onPrimaryContainer
                                        ToastType.ERROR -> MaterialTheme.colorScheme.onErrorContainer
                                        ToastType.INFO -> MaterialTheme.colorScheme.onSecondaryContainer
                                    },
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Auto-dismiss después del tiempo especificado
            LaunchedEffect(message) {
                delay(duration)
                onDismiss()
            }
        }
    }
}

enum class ToastType {
    SUCCESS, ERROR, INFO
}