package com.example.miprestamoslab

import com.example.miprestamoslab.data.local.SesionStore
import com.example.miprestamoslab.data.repository.InMemoryPrestamoRepository
import com.example.miprestamoslab.model.Usuario
import com.example.miprestamoslab.ui.PrestamoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

private class FakeSesionStore : SesionStore {
    private val _usuario = MutableStateFlow<Usuario?>(null)
    override val sesion: Flow<Usuario?> = _usuario.asStateFlow()
    override suspend fun guardarSesion(usuario: Usuario) {
        _usuario.value = usuario
    }

    override suspend fun limpiarSesion() {
        _usuario.value = null
    }

    fun sesionEnPersistencia(): Usuario? = _usuario.value
}

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
        val viewModel = PrestamoViewModel(InMemoryPrestamoRepository(), FakeSesionStore())
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
        val viewModel = PrestamoViewModel(InMemoryPrestamoRepository(), FakeSesionStore())
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
        val viewModel = PrestamoViewModel(InMemoryPrestamoRepository(), FakeSesionStore())
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
        val viewModel = PrestamoViewModel(InMemoryPrestamoRepository(), FakeSesionStore())

        // Probando con cadenas en blanco
        viewModel.login(correo = "   ", contrasena = "") {}

        val estadoUi = viewModel.uiState.value
        assertNull(estadoUi.usuarioAutenticado)
        assertEquals("Por favor ingrese correo y contraseña", estadoUi.mensajeError)
    }

    @Test
    fun dadoUsuarioAutenticado_cuandoHaceLogout_entoncesLimpiaLaSesion() {
        val viewModel = PrestamoViewModel(InMemoryPrestamoRepository(), FakeSesionStore())

        // 1. Iniciar sesión previa
        viewModel.login("aprendiz@sena.edu.co", "123456") {}
        assertNotNull("Debía haber un usuario logueado antes del logout", viewModel.uiState.value.usuarioAutenticado)

        // 2. Ejecutar cierre de sesión
        viewModel.logout()

        // 3. Verificar estado nulo
        assertNull(viewModel.uiState.value.usuarioAutenticado)
    }

    @Test
    fun dadoLoginExitoso_cuandoPersiste_entoncesSesionStoreGuardaAlUsuario() {
        val sesionStore = FakeSesionStore()
        val viewModel = PrestamoViewModel(InMemoryPrestamoRepository(), sesionStore)

        viewModel.login("aprendiz@sena.edu.co", "123456") {}

        val usuarioPersistido = sesionStore.sesionEnPersistencia()
        assertNotNull("La sesión debía persistirse en el store", usuarioPersistido)
        assertEquals("aprendiz@sena.edu.co", usuarioPersistido?.correo)
    }

    @Test
    fun dadoUsuarioLogueado_cuandoLogout_entoncesSesionStoreSeLimpia() {
        val sesionStore = FakeSesionStore()
        val viewModel = PrestamoViewModel(InMemoryPrestamoRepository(), sesionStore)

        viewModel.login("aprendiz@sena.edu.co", "123456") {}
        assertNotNull(sesionStore.sesionEnPersistencia())

        viewModel.logout()

        assertNull(sesionStore.sesionEnPersistencia())
    }
}