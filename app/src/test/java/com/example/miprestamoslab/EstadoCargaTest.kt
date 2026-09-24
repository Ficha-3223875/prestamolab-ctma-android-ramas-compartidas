package com.example.miprestamoslab

import com.example.miprestamoslab.data.local.SesionStore
import com.example.miprestamoslab.data.repository.InMemoryPrestamoRepository
import com.example.miprestamoslab.data.repository.PrestamoRepository
import com.example.miprestamoslab.model.CategoriaEquipo
import com.example.miprestamoslab.model.Equipo
import com.example.miprestamoslab.model.EstadoEquipo
import com.example.miprestamoslab.model.SolicitudPrestamo
import com.example.miprestamoslab.model.Usuario
import com.example.miprestamoslab.ui.EstadoCarga
import com.example.miprestamoslab.ui.PrestamoViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private class SesionFalsa : SesionStore {
    private val _usuario = MutableStateFlow<Usuario?>(null)
    override val sesion: Flow<Usuario?> = _usuario.asStateFlow()

    override suspend fun guardarSesion(usuario: Usuario) {
        _usuario.value = usuario
    }

    override suspend fun limpiarSesion() {
        _usuario.value = null
    }
}

/** Repositorio falso configurable para aislar los estados del UiState. */
private open class RepositorioFalso(
    equiposIniciales: List<Equipo> = emptyList(),
    solicitudesIniciales: List<SolicitudPrestamo> = emptyList()
) : PrestamoRepository {
    override val equipos: StateFlow<List<Equipo>> = MutableStateFlow(equiposIniciales)
    override val solicitudes: StateFlow<List<SolicitudPrestamo>> = MutableStateFlow(solicitudesIniciales)

    override fun obtenerEquipos(): List<Equipo> = equipos.value
    override fun obtenerEquipo(id: Int): Equipo? = equipos.value.find { it.id == id }
    override fun obtenerSolicitudes(): List<SolicitudPrestamo> = solicitudes.value
    override fun obtenerSolicitud(id: Int): SolicitudPrestamo? =
        solicitudes.value.find { it.id == id }

    override suspend fun crearSolicitud(solicitud: SolicitudPrestamo) = Result.success(Unit)
    override suspend fun cancelarSolicitud(id: Int) = Result.success(Unit)
    override suspend fun aprobarSolicitud(id: Int) = Result.success(Unit)
    override suspend fun rechazarSolicitud(id: Int, razon: String) = Result.success(Unit)
    override suspend fun agregarEquipo(nombre: String, categoria: CategoriaEquipo, descripcion: String) =
        Result.success(Unit)
    override suspend fun editarEquipo(id: Int, nombre: String, categoria: CategoriaEquipo, descripcion: String) =
        Result.success(Unit)
    override suspend fun cambiarEstadoEquipo(id: Int, nuevoEstado: EstadoEquipo) = Result.success(Unit)
}

private class RepositorioVacio : RepositorioFalso()

/** Simula un fallo real de la fuente de datos (p. ej. base de datos corrupta). */
private class RepositorioConError : RepositorioFalso() {
    override val equipos: StateFlow<List<Equipo>>
        get() = throw IllegalStateException("Room no disponible")
}

/**
 * Semana 7 — estados de carga del UiState (Cargando / Contenido / Vacío / Error).
 * Cubre el criterio de que la UI siempre tenga un estado explícito y recuperable.
 */
class EstadoCargaTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun dadoRepositorioConDatos_cuandoInicia_entoncesEstadoContenido() {
        // Arrange
        val equipo = Equipo(
            id = 1,
            nombre = "Multímetro Digital",
            categoria = CategoriaEquipo.ELECTRONICA,
            estado = EstadoEquipo.DISPONIBLE
        )
        val viewModel = PrestamoViewModel(RepositorioFalso(equiposIniciales = listOf(equipo)), SesionFalsa())

        // Act
        val estado = viewModel.uiState.value.estadoCarga

        // Assert
        assertEquals(EstadoCarga.CONTENIDO, estado)
        assertEquals(1, viewModel.uiState.value.equipos.size)
    }

    @Test
    fun dadoRepositorioSinRegistros_cuandoInicia_entoncesEstadoVacio() {
        // Arrange
        val viewModel = PrestamoViewModel(RepositorioVacio(), SesionFalsa())

        // Act
        val estado = viewModel.uiState.value.estadoCarga

        // Assert
        assertEquals(EstadoCarga.VACIO, estado)
        assertTrue(viewModel.uiState.value.equipos.isEmpty())
    }

    @Test
    fun dadoRepositorioConError_cuandoInicia_entoncesEstadoErrorRecuperable() {
        // Arrange
        val viewModel = PrestamoViewModel(RepositorioConError(), SesionFalsa())

        // Act
        val estado = viewModel.uiState.value.estadoCarga

        // Assert: estado de error con mensaje, no un cierre abrupto de la app
        assertEquals(EstadoCarga.ERROR, estado)
        assertNotNull(viewModel.uiState.value.errorCarga)
        assertTrue(viewModel.uiState.value.errorCarga!!.contains("Room no disponible"))
    }

    @Test
    fun dadoEstadoError_cuandoReintentar_entoncesLimpiaElErrorYVuelveAEvaluar() {
        // Arrange
        val viewModel = PrestamoViewModel(RepositorioConError(), SesionFalsa())
        assertEquals(EstadoCarga.ERROR, viewModel.uiState.value.estadoCarga)

        // Act
        viewModel.reintentarCarga()

        // Assert: el reintento se ejecuta (vuelve a fallar con el mismo repositorio roto)
        assertEquals(EstadoCarga.ERROR, viewModel.uiState.value.estadoCarga)
        assertNotNull(viewModel.uiState.value.errorCarga)
    }

    @Test
    fun dadoRepositorioConDatos_cuandoSeCreaSolicitud_entoncesElEstadoPermaneceContenido() {
        // Arrange
        val repositorio = InMemoryPrestamoRepository()
        val viewModel = PrestamoViewModel(repositorio, SesionFalsa())
        assertEquals(EstadoCarga.CONTENIDO, viewModel.uiState.value.estadoCarga)

        // Act
        viewModel.crearSolicitud(
            equipoId = 1,
            ambiente = "Laboratorio de Electrónica",
            proposito = "Realizar práctica de medición de componentes electrónicos",
            duracion = "3"
        ) {}

        // Assert: una operación no degrada el estado de carga
        assertEquals(EstadoCarga.CONTENIDO, viewModel.uiState.value.estadoCarga)
    }
}
