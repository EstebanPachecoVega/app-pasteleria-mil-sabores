package com.example.app_pasteleria_mil_sabores.ui.screen.recetas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.app_pasteleria_mil_sabores.model.Receta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecetaDetailScreen(
    receta: Receta,
    onBack: () -> Unit // ESTE PARÁMETRO DEBE EXISTIR
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(receta.strMeal) },
                navigationIcon = {
                    IconButton(onClick = onBack) { // DEBE USAR onBack AQUÍ
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                Image(
                    painter = rememberImagePainter(receta.strMealThumb),
                    contentDescription = receta.strMeal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
            }

            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Categoría: ${receta.strCategory}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Origen: ${receta.strArea}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Ingredientes:",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    receta.getIngredientesYMedidas().forEach { (ingrediente, medida) ->
                        Text(
                            text = "• $ingrediente: $medida",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    Text(
                        text = "Instrucciones:",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    Text(
                        text = receta.strInstructions,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}