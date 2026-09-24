package com.example.miprestamoslab

import com.example.miprestamoslab.data.repository.InMemoryEvidenciaRepository
import com.example.miprestamoslab.data.repository.InMemoryPrestamoRepository
import com.example.miprestamoslab.model.EstadoEvidencia
import com.example.miprestamoslab.ui.PrestamoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * HU-13 (Semana 9): adjuntar evidencia fotográfica.
 * Verifica el registro de la URI con metadatos y los mensajes recuperables para el usuario.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EvidenciaTest {

    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun configurar() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun dadaEvidenciaValida_cuandoSeRegistra_entoncesSePersisteLaUriConMetadatos() = runTest {
        // Arrange
        val repositorio = InMemoryEvidenciaRepository()

        // Act
        val resultado = repositorio.registrarEvidencia(
            solicitudId = 3,
            uri = "content://media/picker/0/com.android.providers.media.photopicker/media/1000000042"
        )

        // Assert
        assertTrue(resultado.isSuccess)
        val evidencia = resultado.getOrNull()!!
        assertEquals(3, evidencia.solicitudId)
        assertTrue(evidencia.uri.startsWith("content://"))
        assertTrue(evidencia.fechaRegistro > 0)
        assertEquals(EstadoEvidencia.LOCAL, evidencia.estado)
    }

    @Test
    fun dadaUriVacia_cuandoSeRegistra_entoncesFallaSinRomperLaApp() = runTest {
        // Arrange
        val repositorio = InMemoryEvidenciaRepository()

        // Act
        val resultado = repositorio.registrarEvidencia(solicitudId = 3, uri = "  ")

        // Assert
        assertTrue(resultado.isFailure)
        assertTrue(resultado.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun dadaSolicitudSinEvidencias_cuandoSeObserva_entoncesListaVacia() = runTest {
        // Arrange
        val repositorio = InMemoryEvidenciaRepository()

        // Act
        val evidencias = repositorio.observarEvidencias(solicitudId = 7).first()

        // Assert
        assertTrue(evidencias.isEmpty())
    }

    @Test
    fun dadoViewModel_cuandoSeAdjuntaEvidencia_entoncesMuestraConfirmacionYLaListaSeActualiza() {
        // Arrange
        val repositorio = InMemoryEvidenciaRepository()
        val viewModel = PrestamoViewModel(
            repository = InMemoryPrestamoRepository(),
            sesionStore = SesionFalsa(),
            evidenciaRepository = repositorio
        )
        viewModel.cargarEvidencias(solicitudId = 5)

        // Act
        viewModel.registrarEvidencia(solicitudId = 5, uri = "content://media/1")

        // Assert
        assertEquals(1, viewModel.evidencias.value.size)
        assertEquals("Evidencia adjuntada correctamente", viewModel.uiState.value.mensaje)
        assertEquals(EstadoEvidencia.LOCAL, viewModel.evidencias.value[0].estado)
    }

    @Test
    fun dadoViewModelSinRepositorioDeEvidencias_cuandoSeAdjunta_entoncesInformaQueNoEstaHabilitado() {
        // Arrange
        val viewModel = PrestamoViewModel(InMemoryPrestamoRepository(), SesionFalsa())

        // Act
        viewModel.registrarEvidencia(solicitudId = 1, uri = "content://media/1")

        // Assert
        val mensaje = viewModel.uiState.value.mensaje
        assertNotNull(mensaje)
        assertTrue(mensaje!!.contains("no están habilitadas"))
    }
}
