package com.example.app_pasteleria_mil_sabores

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.room.Room
import com.example.app_pasteleria_mil_sabores.data.UsuarioDatabase
import com.example.app_pasteleria_mil_sabores.ui.screen.RegistroScreen
import com.example.app_pasteleria_mil_sabores.ui.theme.ApppasteleriamilsaboresTheme
import com.example.app_pasteleria_mil_sabores.viewmodel.FormularioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FormularioApp()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    //RegistroScreen()
}

@Composable
fun FormularioApp(){
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

    RegistroScreen(viewModel = viewModel)
}