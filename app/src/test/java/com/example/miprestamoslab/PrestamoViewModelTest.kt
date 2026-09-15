package com.example.miprestamoslab

import com.example.miprestamoslab.ui.PrestamoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

class PrestamoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun dadoCredencialesValidas_cuandoLogin_entoncesAutenticaCorrectamente() {
        // Arrange
        val viewModel = PrestamoViewModel()
        val correoPrueba = "usuario.sena@sena.edu.co"
        var fueExitoso = false

        // Act
        viewModel.login(
            correo = correoPrueba,
            contrasena = "123456"
        ) {
            fueExitoso = true
        }

        // Assert
        val estadoActual = viewModel.uiState.value
        assertTrue("El callback de éxito debía ejecutarse", fueExitoso)
        assertNotNull(estadoActual.usuarioAutenticado)
        assertEquals(correoPrueba, estadoActual.usuarioAutenticado?.correo)
        assertEquals("APRENDIZ", estadoActual.usuarioAutenticado?.rol?.name)
    }

    @Test
    fun dadoCorreoDeEncargado_cuandoAutentica_entoncesAsignaRolEncargado() {
        val viewModel = PrestamoViewModel()
        // Mantiene la palabra 'encargado' para pasar la condición interna del ViewModel,
        // pero con una estructura de correo distinta.
        val correoEncargado = "encargado.laboratorio@sena.edu.co"

        viewModel.login(correo = correoEncargado, contrasena = "123456") {}

        val estado = viewModel.uiState.value
        assertNotNull("El usuario debería haberse autenticado", estado.usuarioAutenticado)
        assertEquals("ENCARGADO", estado.usuarioAutenticado?.rol?.name)
    }

    @Test
    fun dadoContrasenaErronea_cuandoLogin_entoncesNotificaErrorYNoAutentica() {
        val viewModel = PrestamoViewModel()
        var seEjecutoExito = false

        viewModel.login(
            correo = "aprendiz@sena.edu.co",
            contrasena = "clave_incorrecta_999"
        ) {
            seEjecutoExito = true
        }

        val estado = viewModel.uiState.value
        assertFalse(seEjecutoExito)
        assertNull(estado.usuarioAutenticado)
        assertEquals("Usuario o contraseña inválidos", estado.mensajeError)
    }

    @Test
    fun dadoCredencialesEnBlanco_cuandoIntentaIngresar_entoncesMuestraMensajeDeCamposRequeridos() {
        val viewModel = PrestamoViewModel()

        // Probando con cadenas en blanco
        viewModel.login(correo = "   ", contrasena = "") {}

        val estadoUi = viewModel.uiState.value
        assertNull(estadoUi.usuarioAutenticado)
        assertEquals("Por favor ingrese correo y contraseña", estadoUi.mensajeError)
    }

    @Test
    fun dadoUsuarioAutenticado_cuandoHaceLogout_entoncesLimpiaLaSesion() {
        val viewModel = PrestamoViewModel()

        // 1. Iniciar sesión previa
        viewModel.login("aprendiz@sena.edu.co", "123456") {}
        assertNotNull("Debía haber un usuario logueado antes del logout", viewModel.uiState.value.usuarioAutenticado)

        // 2. Ejecutar cierre de sesión
        viewModel.logout()

        // 3. Verificar estado nulo
        assertNull(viewModel.uiState.value.usuarioAutenticado)
    }
}