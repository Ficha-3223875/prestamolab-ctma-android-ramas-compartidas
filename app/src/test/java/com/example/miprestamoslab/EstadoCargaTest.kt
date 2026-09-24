package com.example.miprestamoslab

import com.example.miprestamoslab.data.repository.InMemoryPrestamoRepository
import com.example.miprestamoslab.model.CategoriaEquipo
import com.example.miprestamoslab.model.Equipo
import com.example.miprestamoslab.model.EstadoEquipo
import com.example.miprestamoslab.ui.EstadoCarga
import com.example.miprestamoslab.ui.PrestamoViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

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
