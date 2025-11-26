package com.example.app_pasteleria_mil_sabores.ui.screen.auth

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.app_pasteleria_mil_sabores.viewmodel.FormularioViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistroScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockErrorMessage = MutableStateFlow<String?>(null)

    private val mockViewModel: FormularioViewModel = mockk(relaxed = true) {
        every { errorMessage } returns mockErrorMessage
    }

    private val mockOnRegistroExitoso: () -> Unit = mockk(relaxed = true)
    private val mockOnVolver: () -> Unit = mockk(relaxed = true)
    private val mockOnBackPressed: () -> Unit = mockk(relaxed = true)

    @Test
    fun testRegistroScreen_MuestraTituloCorrectamente() {
        composeTestRule.setContent {
            RegistroScreen(
                viewModel = mockViewModel,
                onRegistroExitoso = mockOnRegistroExitoso,
                onVolver = mockOnVolver,
                onBackPressed = mockOnBackPressed
            )
        }

        composeTestRule.onNodeWithText("Registro").assertExists()
    }

    @Test
    fun testRegistroScreen_TodosLosCamposExisten() {
        composeTestRule.setContent {
            RegistroScreen(
                viewModel = mockViewModel,
                onRegistroExitoso = mockOnRegistroExitoso,
                onVolver = mockOnVolver,
                onBackPressed = mockOnBackPressed
            )
        }

        composeTestRule.onNodeWithText("Nombre de usuario").assertExists()
        composeTestRule.onNodeWithText("Correo electrónico").assertExists()
        composeTestRule.onNodeWithText("Fecha de nacimiento (opcional)").assertExists()
        composeTestRule.onNodeWithText("Código promocional (opcional)").assertExists()
        composeTestRule.onNodeWithText("Contraseña").assertExists()
        composeTestRule.onNodeWithText("Confirmar Contraseña").assertExists()
    }

    @Test
    fun testRegistroScreen_BotonesVolverYRegistrarseExisten() {
        composeTestRule.setContent {
            RegistroScreen(
                viewModel = mockViewModel,
                onRegistroExitoso = mockOnRegistroExitoso,
                onVolver = mockOnVolver,
                onBackPressed = mockOnBackPressed
            )
        }

        composeTestRule.onNodeWithText("Volver").assertExists()
        composeTestRule.onNodeWithText("Registrarse").assertExists()
    }

    @Test
    fun testRegistroScreen_AlHacerClickEnVolver_EjecutaCallback() {
        composeTestRule.setContent {
            RegistroScreen(
                viewModel = mockViewModel,
                onRegistroExitoso = mockOnRegistroExitoso,
                onVolver = mockOnVolver,
                onBackPressed = mockOnBackPressed
            )
        }

        composeTestRule.onNodeWithText("Volver").performClick()

        verify { mockOnVolver.invoke() }
    }

    @Test
    fun testRegistroScreen_UsernameInvalido_MuestraMensajeError() {
        composeTestRule.setContent {
            RegistroScreen(
                viewModel = mockViewModel,
                onRegistroExitoso = mockOnRegistroExitoso,
                onVolver = mockOnVolver,
                onBackPressed = mockOnBackPressed
            )
        }

        composeTestRule.onNodeWithText("Nombre de usuario").performTextInput("ab")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Mínimo 3 caracteres sin espacios").assertExists()
    }

    @Test
    fun testRegistroScreen_EmailInvalido_MuestraMensajeError() {
        composeTestRule.setContent {
            RegistroScreen(
                viewModel = mockViewModel,
                onRegistroExitoso = mockOnRegistroExitoso,
                onVolver = mockOnVolver,
                onBackPressed = mockOnBackPressed
            )
        }

        composeTestRule.onNodeWithText("Correo electrónico").performTextInput("emailinvalido")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Debe ser un correo válido").assertExists()
    }

    @Test
    fun testRegistroScreen_PasswordsNoCoinciden_MuestraMensajeError() {
        composeTestRule.setContent {
            RegistroScreen(
                viewModel = mockViewModel,
                onRegistroExitoso = mockOnRegistroExitoso,
                onVolver = mockOnVolver,
                onBackPressed = mockOnBackPressed
            )
        }

        composeTestRule.onNodeWithText("Contraseña").performTextInput("password123")
        composeTestRule.onNodeWithText("Confirmar Contraseña").performTextInput("diferente")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Las contraseñas no coinciden").assertExists()
    }

    @Test
    fun testRegistroScreen_PasswordsCoinciden_MuestraMensajeConfirmacion() {
        composeTestRule.setContent {
            RegistroScreen(
                viewModel = mockViewModel,
                onRegistroExitoso = mockOnRegistroExitoso,
                onVolver = mockOnVolver,
                onBackPressed = mockOnBackPressed
            )
        }

        composeTestRule.onNodeWithText("Contraseña").performTextInput("password123")
        composeTestRule.onNodeWithText("Confirmar Contraseña").performTextInput("password123")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Contraseñas coinciden").assertExists()
    }

    @Test
    fun testRegistroScreen_FormularioCompletoValido_HabilitaBotonRegistrarse() {
        composeTestRule.setContent {
            RegistroScreen(
                viewModel = mockViewModel,
                onRegistroExitoso = mockOnRegistroExitoso,
                onVolver = mockOnVolver,
                onBackPressed = mockOnBackPressed
            )
        }

        composeTestRule.onNodeWithText("Nombre de usuario").performTextInput("usuario123")
        composeTestRule.onNodeWithText("Correo electrónico").performTextInput("test@duoc.cl")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("password123")
        composeTestRule.onNodeWithText("Confirmar Contraseña").performTextInput("password123")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Registrarse").assertIsEnabled()
    }

    @Test
    fun testRegistroScreen_FormularioIncompleto_DeshabilitaBotonRegistrarse() {
        composeTestRule.setContent {
            RegistroScreen(
                viewModel = mockViewModel,
                onRegistroExitoso = mockOnRegistroExitoso,
                onVolver = mockOnVolver,
                onBackPressed = mockOnBackPressed
            )
        }

        composeTestRule.onNodeWithText("Nombre de usuario").performTextInput("usuario123")
        composeTestRule.onNodeWithText("Correo electrónico").performTextInput("test@duoc.cl")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Registrarse").assertIsNotEnabled()
    }

    @Test
    fun testRegistroScreen_CodigoPromocionalValido_MuestraMensajeDescuento() {
        composeTestRule.setContent {
            RegistroScreen(
                viewModel = mockViewModel,
                onRegistroExitoso = mockOnRegistroExitoso,
                onVolver = mockOnVolver,
                onBackPressed = mockOnBackPressed
            )
        }

        composeTestRule.onNodeWithText("Código promocional (opcional)").performTextInput("FELICES50")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("10% de descuento permanente").assertExists()
    }

    @Test
    fun testRegistroScreen_CodigoPromocionalInvalido_MuestraMensajeError() {
        composeTestRule.setContent {
            RegistroScreen(
                viewModel = mockViewModel,
                onRegistroExitoso = mockOnRegistroExitoso,
                onVolver = mockOnVolver,
                onBackPressed = mockOnBackPressed
            )
        }

        composeTestRule.onNodeWithText("Código promocional (opcional)").performTextInput("CODIGO_INVALIDO")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Código no reconocido").assertExists()
    }

    @Test
    fun testRegistroScreen_FormularioValido_AlHacerClickEnRegistrarse_EjecutaRegistro() {
        composeTestRule.setContent {
            RegistroScreen(
                viewModel = mockViewModel,
                onRegistroExitoso = mockOnRegistroExitoso,
                onVolver = mockOnVolver,
                onBackPressed = mockOnBackPressed
            )
        }

        composeTestRule.onNodeWithText("Nombre de usuario").performTextInput("usuario123")
        composeTestRule.onNodeWithText("Correo electrónico").performTextInput("test@duoc.cl")
        composeTestRule.onNodeWithText("Contraseña").performTextInput("password123")
        composeTestRule.onNodeWithText("Confirmar Contraseña").performTextInput("password123")

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Registrarse").performClick()

        verify {
            mockViewModel.agregarUsuario(
                username = "usuario123",
                email = "test@duoc.cl",
                password = "password123",
                fechaNacimiento = null,
                codigoPromocion = null
            )
        }
    }
}