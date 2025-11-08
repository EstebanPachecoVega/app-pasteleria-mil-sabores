package com.example.app_pasteleria_mil_sabores.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import android.R as AndroidR

@Composable
fun FotoPerfilComposable(
    fotoUri: String?,
    usuarioId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Forzar recarga cuando cambia el usuario
    val imageRequest = remember (fotoUri, usuarioId) {
        if (fotoUri != null) {
            ImageRequest.Builder(context)
                .data(fotoUri)
                .diskCacheKey("profile_$usuarioId") // Clave única por usuario
                .memoryCacheKey("profile_$usuarioId") // Clave única por usuario
                .crossfade(true)
                .build()
        } else {
            null
        }
    }

    if (fotoUri != null && imageRequest != null) {
        Image(
            painter = rememberAsyncImagePainter(
                model = imageRequest,
                error = painterResource(AndroidR.drawable.ic_menu_gallery)
            ),
            contentDescription = "Foto de perfil",
            modifier = modifier
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Image(
            painter = painterResource(AndroidR.drawable.ic_menu_gallery),
            contentDescription = "Sin foto de perfil",
            modifier = modifier
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}