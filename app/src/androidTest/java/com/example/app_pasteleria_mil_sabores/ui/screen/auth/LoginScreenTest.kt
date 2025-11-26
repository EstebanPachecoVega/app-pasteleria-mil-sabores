package com.example.app_pasteleria_mil_sabores.ui.screen.auth

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_pasteleria_mil_sabores.model.Usuario
import com.example.app_pasteleria_mil_sabores.viewmodel.FormularioViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockUsuarioActual = MutableStateFlow<Usuario?>(null)
    private val mockErrorMessage = MutableStateFlow<String?>(null)

    private val mockViewModel: FormularioViewModel = mockk(relaxed = true) {
        every { usuarioActual } returns mockUsuarioActual
        every { errorMessage } returns mockErrorMessage
    }

    private val mockOnRegistrarClick: () -> Unit = mockk(relaxed = true)
    private val mockOnLoginExitoso: (Usuario) -> Unit = mockk(relaxed = true)
    private val mockOnBackPressed: () -> Unit = mockk(relaxed = true)

    // Método helper para encontrar el botón específicamente
    private fun findLoginButton() = composeTestRule.onAllNodesWithText("Iniciar Sesión")[1]

    // Método helper para encontrar el título específicamente
    private fun findLoginTitle() = composeTestRule.onAllNodesWithText("Iniciar Sesión")[0]

    @Test
    fun testLoginScreen_MuestraTituloCorrectamente() {
        composeTestRule.setContent {
            LoginScreen(
                viewModel = mockViewModel,
                onRegistrarClick = mockOnRegistrarClick,
                onLoginExitoso = mockOnLoginExitoso,
                onBackPressed = mockOnBackPressed
            )
        }

        findLoginTitle().assertExists()
    }

    @Test
    fun testLoginScreen_CamposEmailYPasswordExisten() {
        composeTestRule.setContent {
            LoginScreen(
                viewModel = mockViewModel,
                onRegistrarClick = mockOnRegistrarClick,
                onLoginExitoso = mockOnLoginExitoso,
                onBackPressed = mockOnBackPressed
            )
        }

        composeTestRule.onNodeWithText("Correo electrónico").assertExists()
        composeTestRule.onNodeWithText("Contraseña").assertExists()
    }

    @Test
    fun testLoginScreen_BotonIniciarSesionExiste() {
        composeTestRule.setContent {
            LoginScreen(
                viewModel = mockViewModel,
                onRegistrarClick = mockOnRegistrarClick,
                onLoginExitoso = mockOnLoginExitoso,
                onBackPressed = mockOnBackPressed
            )
        }

        findLoginButton().assertExists()
    }

    @Test
    fun testLoginScreen_BotonRegistrarseExiste() {
        composeTestRule.setContent {
            LoginScreen(
                viewModel = mockViewModel,
                onRegistrarClick = mockOnRegistrarClick,
                onLoginExitoso = mockOnLoginExitoso,
                onBackPressed = mockOnBackPressed
            )
        }

        composeTestRule.onNodeWithText("Registrarse").assertExists()
    }

    @Test
    fun testLoginScreen_BotonOlvidasteContraseñaExiste() {
        composeTestRule.setContent {
            LoginScreen(
                viewModel = mockViewModel,
                onRegistrarClick = mockOnRegistrarClick,
                onLoginExitoso = mockOnLoginExitoso,
                onBackPressed = mockOnBackPressed
            )
        }

        composeTestRule.onNodeWithText("¿Olvidaste tu contraseña?").assertExists()
    }

    @Test
    fun testLoginScreen_AlHacerClickEnRegistrarse_EjecutaCallback() {
        composeTestRule.setContent {
            LoginScreen(
                viewModel = mockViewModel,
                onRegistrarClick = mockOnRegistrarClick,
                onLoginExitoso = mockOnLoginExitoso,
                onBackPressed = mockOnBackPressed
            )
        }

        composeTestRule.onNodeWithText("Registrarse").performClick()

        verify { mockOnRegistrarClick.invoke() }
    }

    @Test
    fun testLoginScreen_AlHacerClickEnOlvidasteContraseña_MuestraSeccionRecuperacion() {
        composeTestRule.setContent {
            LoginScreen(
                viewModel = mockViewModel,
                onRegistrarClick = mockOnRegistrarClick,
                onLoginExitoso = mockOnLoginExitoso,
                onBackPressed = mockOnBackPressed
            )
        }

        composeTestRule.onNodeWithText("¿Olvidaste tu contraseña?").performClick()

        composeTestRule.onNodeWithText("Recuperar Contraseña").assertExists()
    }

    @Test
    fun testLoginScreen_IngresoEmailValido_HabilitaBotonLogin() {
        composeTestRule.setContent {
            LoginScreen(
                viewModel = mockViewModel,
                onRegistrarClick = mockOnRegistrarClick,
                onLoginExitoso = mockOnLoginExitoso,
                onBackPressed = mockOnBackPressed
            )
        }

        composeTestRule.onNodeWithText("Correo electrónico").performTextInput("test@duoc.cl")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("password123")

        composeTestRule.waitForIdle()

        findLoginButton().assertIsEnabled()
    }

    @Test
    fun testLoginScreen_EmailInvalido_MuestraMensajeError() {
        composeTestRule.setContent {
            LoginScreen(
                viewModel = mockViewModel,
                onRegistrarClick = mockOnRegistrarClick,
                onLoginExitoso = mockOnLoginExitoso,
                onBackPressed = mockOnBackPressed
            )
        }

        composeTestRule.onNodeWithText("Correo electrónico").performTextInput("emailinvalido")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Debe ser un correo válido").assertExists()
    }

    @Test
    fun testLoginScreen_PasswordCorta_MuestraMensajeError() {
        composeTestRule.setContent {
            LoginScreen(
                viewModel = mockViewModel,
                onRegistrarClick = mockOnRegistrarClick,
                onLoginExitoso = mockOnLoginExitoso,
                onBackPressed = mockOnBackPressed
            )
        }

        composeTestRule.onNodeWithText("Contraseña").performTextInput("123")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Mínimo 6 caracteres sin espacios").assertExists()
    }

    @Test
    fun testLoginScreen_FormularioValido_AlHacerClickEnLogin_EjecutaAutenticacion() {
        composeTestRule.setContent {
            LoginScreen(
                viewModel = mockViewModel,
                onRegistrarClick = mockOnRegistrarClick,
                onLoginExitoso = mockOnLoginExitoso,
                onBackPressed = mockOnBackPressed
            )
        }

        composeTestRule.onNodeWithText("Correo electrónico").performTextInput("test@duoc.cl")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("password123")

        composeTestRule.waitForIdle()

        findLoginButton().performClick()

        verify { mockViewModel.autenticarUsuario("test@duoc.cl", "password123") }
    }
}