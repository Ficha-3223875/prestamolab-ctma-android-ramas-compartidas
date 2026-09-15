package com.example.miprestamoslab

import com.example.miprestamoslab.ui.PrestamoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert
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
    fun login_conCredencialesValidas_debeAutenticarUsuario() {
        val viewModel = PrestamoViewModel()

        var inicioSesionExitoso = false

        viewModel.login(
            correo = "aprendiz@sena.edu.co",
            contrasena = "123456"
        ) {
            inicioSesionExitoso = true
        }

        Assert.assertTrue(inicioSesionExitoso)
        Assert.assertEquals(
            "aprendiz@sena.edu.co",
            viewModel.uiState.value.usuarioAutenticado?.correo
        )
        Assert.assertEquals(
            "APRENDIZ",
            viewModel.uiState.value.usuarioAutenticado?.rol?.name
        )
    }

    @Test
    fun login_conCorreoDeEncargado_debeAsignarRolEncargado() {
        val viewModel = PrestamoViewModel()

        viewModel.login(
            correo = "encargado@sena.edu.co",
            contrasena = "123456"
        ) {}

        Assert.assertEquals(
            "ENCARGADO",
            viewModel.uiState.value.usuarioAutenticado?.rol?.name
        )
    }

    @Test
    fun login_conContrasenaIncorrecta_debeMostrarError() {
        val viewModel = PrestamoViewModel()

        var inicioSesionExitoso = false

        viewModel.login(
            correo = "aprendiz@sena.edu.co",
            contrasena = "654321"
        ) {
            inicioSesionExitoso = true
        }

        Assert.assertTrue(!inicioSesionExitoso)
        Assert.assertEquals(
            "Usuario o contraseña inválidos",
            viewModel.uiState.value.mensajeError
        )
        Assert.assertNull(viewModel.uiState.value.usuarioAutenticado)
    }

    @Test
    fun login_conCamposVacios_debeMostrarError() {
        val viewModel = PrestamoViewModel()

        viewModel.login("", "") {}

        Assert.assertEquals(
            "Por favor ingrese correo y contraseña",
            viewModel.uiState.value.mensajeError
        )
    }

    @Test
    fun logout_debeCerrarSesion() {
        val viewModel = PrestamoViewModel()

        viewModel.login(
            "aprendiz@sena.edu.co",
            "123456"
        ) {}

        viewModel.logout()

        Assert.assertNull(viewModel.uiState.value.usuarioAutenticado)
    }
}