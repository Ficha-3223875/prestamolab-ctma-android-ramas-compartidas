package com.example.miprestamoslab

import com.example.miprestamoslab.model.CategoriaEquipo
import com.example.miprestamoslab.model.EstadoEquipo
import com.example.miprestamoslab.ui.ListadoUiState
import com.example.miprestamoslab.ui.OperacionUiState
import com.example.miprestamoslab.ui.PrestamoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class PrestamoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun inicializacion_debeCargarEquiposExitosamente() = runTest {
        val viewModel = PrestamoViewModel()

        advanceUntilIdle()

        val state = viewModel.uiState.value

        Assert.assertTrue(
            "Debería cargar equipos inicialmente",
            state.listadoEquipos is ListadoUiState.Contenido
        )

        val equipos =
            (state.listadoEquipos as ListadoUiState.Contenido).equipos

        Assert.assertTrue(
            "La lista de equipos no debería estar vacía",
            equipos.isNotEmpty()
        )
    }

    @Test
    fun login_flujoCompleto_debeActualizarEstados() = runTest {
        val viewModel = PrestamoViewModel()
        var exito = false

        viewModel.login(
            "aprendiz@sena.edu.co",
            "123456"
        ) {
            exito = true
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value

        Assert.assertTrue(
            "El callback de éxito debería llamarse",
            exito
        )

        Assert.assertEquals(
            "Estado de operación debería ser Exitosa",
            OperacionUiState.Exitosa,
            state.operacionState
        )

        Assert.assertNotNull(
            "Usuario debería estar autenticado",
            state.usuarioAutenticado
        )

        Assert.assertEquals(
            "aprendiz@sena.edu.co",
            state.usuarioAutenticado?.correo
        )
    }

    @Test
    fun login_credencialesInvalidas_debeMostrarErrorEnOperacion() = runTest {
        val viewModel = PrestamoViewModel()

        viewModel.login(
            "error@sena.edu.co",
            "wrong"
        ) {}

        advanceUntilIdle()

        val state = viewModel.uiState.value

        Assert.assertTrue(
            "Estado debería ser Fallida",
            state.operacionState is OperacionUiState.Fallida
        )

        Assert.assertEquals(
            "Usuario o contraseña inválidos",
            (state.operacionState as OperacionUiState.Fallida).error
        )

        Assert.assertNull(
            "No debería haber usuario autenticado",
            state.usuarioAutenticado
        )
    }

    @Test
    fun crearSolicitud_conDatosValidos_debeCambiarEstadoEquipo() = runTest {
        val viewModel = PrestamoViewModel()

        viewModel.crearSolicitud(
            1,
            "Ambiente 101",
            "Uso para prácticas de circuitos",
            "2"
        ) {}

        advanceUntilIdle()

        val state = viewModel.uiState.value

        Assert.assertEquals(
            "La solicitud debería ser exitosa",
            OperacionUiState.Exitosa,
            state.operacionState
        )

        val listaState =
            state.listadoEquipos as ListadoUiState.Contenido

        val equipo1 =
            listaState.equipos.find { it.id == 1 }

        Assert.assertEquals(
            "El equipo debería cambiar a RESERVADO",
            EstadoEquipo.RESERVADO,
            equipo1?.estado
        )
    }

    @Test
    fun gestionInventario_agregarYEditarEquipo_funcionaCorrectamente() = runTest {
        val viewModel = PrestamoViewModel()

        // Agregar equipo
        viewModel.agregarEquipo(
            "Nuevo Drone",
            CategoriaEquipo.ELECTRONICA,
            "Drone de prueba"
        ) {}

        advanceUntilIdle()

        var state = viewModel.uiState.value

        Assert.assertEquals(
            OperacionUiState.Exitosa,
            state.operacionState
        )

        val lista =
            (state.listadoEquipos as ListadoUiState.Contenido).equipos

        val drone =
            lista.find { it.nombre == "Nuevo Drone" }

        Assert.assertNotNull(
            "El equipo agregado debería existir",
            drone
        )

        // Editar equipo
        viewModel.editarEquipo(
            drone!!.id,
            "Drone Actualizado",
            CategoriaEquipo.OTRO,
            "Nueva desc"
        ) {}

        advanceUntilIdle()

        state = viewModel.uiState.value

        val listaActualizada =
            (state.listadoEquipos as ListadoUiState.Contenido).equipos

        val droneEditado =
            listaActualizada.find { it.id == drone.id }

        Assert.assertEquals(
            "Drone Actualizado",
            droneEditado?.nombre
        )

        Assert.assertEquals(
            CategoriaEquipo.OTRO,
            droneEditado?.categoria
        )

        Assert.assertEquals(
            "Nueva desc",
            droneEditado?.descripcion
        )
    }

    @Test
    fun validacionSolicitud_camposInvalidos_noEnviaOperacionAlRepositorio() = runTest {
        val viewModel = PrestamoViewModel()

        // Propósito muy corto: menos de 10 caracteres
        viewModel.crearSolicitud(
            1,
            "A1",
            "Corto",
            "10"
        ) {}

        advanceUntilIdle()

        val state = viewModel.uiState.value

        Assert.assertTrue(
            "Debería fallar por validación",
            state.operacionState is OperacionUiState.Fallida
        )

        val error =
            (state.operacionState as OperacionUiState.Fallida).error

        Assert.assertTrue(
            "El error debería mencionar el propósito o la duración",
            error.contains("propósito") || error.contains("duración")
        )
    }

    @Test
    fun logout_debeLimpiarEstadoDeUsuario() = runTest {
        val viewModel = PrestamoViewModel()

        viewModel.login(
            "test@sena.edu.co",
            "123456"
        ) {}

        advanceUntilIdle()

        Assert.assertNotNull(
            "Debe existir un usuario antes de cerrar sesión",
            viewModel.uiState.value.usuarioAutenticado
        )

        viewModel.logout()

        Assert.assertNull(
            "El usuario debería ser nulo tras logout",
            viewModel.uiState.value.usuarioAutenticado
        )

        Assert.assertEquals(
            OperacionUiState.Inactiva,
            viewModel.uiState.value.operacionState
        )
    }
}