package com.example.app_pasteleria_mil_sabores

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import com.example.app_pasteleria_mil_sabores.data.UsuarioDatabase
import com.example.app_pasteleria_mil_sabores.ui.screen.navigation.AppNavigation
import com.example.app_pasteleria_mil_sabores.ui.screen.splash.SplashScreen
import com.example.app_pasteleria_mil_sabores.ui.theme.AppPasteleriaMilSaboresTheme
import com.example.app_pasteleria_mil_sabores.viewmodel.FormularioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppPasteleriaMilSaboresTheme {
                FormularioApp()
            }
        }
    }
}

@Composable
fun FormularioApp() {
    val context = LocalContext.current

    val database = remember {
        Room.databaseBuilder(
            context,
            UsuarioDatabase::class.java,
            "usuario.db"
        ).build()
    }

    val viewModel = remember {
        FormularioViewModel(database.usuarioDao())
    }

    val showSplash = remember { mutableStateOf(true) }

    if (showSplash.value) {
        SplashScreen(
            onSplashComplete = { showSplash.value = false }
        )
    } else {
        AppNavigation(viewModel = viewModel)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppPasteleriaMilSaboresTheme {
        FormularioApp()
    }
}