package com.example.app_pasteleria_mil_sabores

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import com.example.app_pasteleria_mil_sabores.data.PasteleriaDatabase
import com.example.app_pasteleria_mil_sabores.data.PedidoRepository
import com.example.app_pasteleria_mil_sabores.data.ProductoRepository
import com.example.app_pasteleria_mil_sabores.ui.screen.navigation.AppNavigation
import com.example.app_pasteleria_mil_sabores.ui.screen.splash.SplashScreen
import com.example.app_pasteleria_mil_sabores.ui.theme.AppPasteleriaMilSaboresTheme
import com.example.app_pasteleria_mil_sabores.viewmodel.CarritoViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.CheckoutViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.FormularioViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.PedidoViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.PerfilViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.ProductoViewModel
import com.example.app_pasteleria_mil_sabores.viewmodel.RecetaViewModel

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

    // Inicializar base de datos
    val database = remember {
        Room.databaseBuilder(
            context,
            PasteleriaDatabase::class.java,
            "pasteleria.db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    val productoRepository = remember {
        ProductoRepository(database.productoDao())
    }

    val pedidoRepository = remember {
        PedidoRepository(database.pedidoDao())
    }

    // Inicializar ViewModels
    val usuarioViewModel = remember {
        FormularioViewModel(database.usuarioDao())
    }

    val productoViewModel = remember {
        ProductoViewModel(productoRepository)
    }

    val carritoViewModel = remember {
        CarritoViewModel(productoRepository)
    }

    val perfilViewModel = remember {
        PerfilViewModel(database.usuarioDao())
    }

    val checkoutViewModel = remember {
        CheckoutViewModel(
            pedidoDao = database.pedidoDao(),
            productoRepository = productoRepository
        )
    }

    val pedidoViewModel = remember {
        PedidoViewModel(PedidoRepository(database.pedidoDao()))
    }

    val recetaViewModel = remember {
        RecetaViewModel()
    }

    LaunchedEffect (Unit) {
        // Ejecutar migración de estados una vez al iniciar la app
        pedidoRepository.migrarEstadosPedidos()
    }

    // Estado para splash screen
    val showSplash = remember { mutableStateOf(true) }

    if (showSplash.value) {
        SplashScreen(
            onSplashComplete = { showSplash.value = false }
        )
    } else {
        AppNavigation(
            viewModel = usuarioViewModel,
            productoViewModel = productoViewModel,
            carritoViewModel = carritoViewModel,
            perfilViewModel = perfilViewModel,
            checkoutViewModel = checkoutViewModel,
            pedidoViewModel = pedidoViewModel,
            recetaViewModel = recetaViewModel
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FormularioAppPreview() {
    AppPasteleriaMilSaboresTheme {
        FormularioApp()
    }
}