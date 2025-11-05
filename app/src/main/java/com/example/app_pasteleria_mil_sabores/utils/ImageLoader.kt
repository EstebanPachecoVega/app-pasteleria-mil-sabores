package com.example.app_pasteleria_mil_sabores.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

@Composable
fun rememberImageResource(imageName: String): Int {
    val context = LocalContext.current
    return remember(imageName) {
        getImageResource(context, imageName)
    }
}

fun getImageResource(context: Context, imageName: String): Int {
    return context.resources.getIdentifier(
        imageName,
        "drawable",
        context.packageName
    )
}