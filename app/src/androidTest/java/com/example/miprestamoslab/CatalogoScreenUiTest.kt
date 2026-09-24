package com.example.miprestamoslab

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.miprestamoslab.model.CategoriaEquipo
import com.example.miprestamoslab.model.Equipo
import com.example.miprestamoslab.model.EstadoEquipo
import com.example.miprestamoslab.ui.EstadoCarga
import com.example.miprestamoslab.ui.screens.CatalogoScreen
import com.example.miprestamoslab.ui.theme.MiPrestamosLabTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Prueba UI/instrumentada del recorrido crítico: catálogo y sus estados de carga (TC-17).
 * Ejecutar con dispositivo/emulador: `./gradlew connectedDebugAndroidTest`.
 */
class CatalogoScreenUiTest {

    @get:Rule
    val compose = createComposeRule()

    private val equipos = listOf(
        Equipo(
            id = 1,
            nombre = "Multímetro Digital",
            categoria = CategoriaEquipo.ELECTRONICA,
            estado = EstadoEquipo.DISPONIBLE
        ),
        Equipo(
            id = 2,
            nombre = "Taladro Percutor",
            categoria = CategoriaEquipo.HERRAMIENTA,
            estado = EstadoEquipo.PRESTADO
        )
    )

    private fun montarCatalogo(
        estadoCarga: EstadoCarga = EstadoCarga.CONTENIDO,
        errorCarga: String? = null,
        onReintentar: () -> Unit = {}
    ) {
        compose.setContent {
            MiPrestamosLabTheme {
                CatalogoScreen(
                    equipos = equipos,
                    usuario = null,
                    estadoCarga = estadoCarga,
                    errorCarga = errorCarga,
                    onEquipoClick = {},
                    onVerMisSolicitudes = {},
                    onVerSolicitudesPendientes = {},
                    onGestionInventario = {},
                    onReintentarCarga = onReintentar,
                    onLogout = {}
                )
            }
        }
    }

    @Test
    fun dadoCatalogoConContenido_cuandoSeAbre_entoncesMuestraTituloYEquipos() {
        // Arrange / Act
        montarCatalogo()

        // Assert
        compose.onNodeWithText("Catálogo de Equipos").assertIsDisplayed()
        compose.onNodeWithText("Multímetro Digital").assertIsDisplayed()
        compose.onNodeWithContentDescription("Estado del equipo: Disponible").assertIsDisplayed()
    }

    @Test
    fun dadoEquipoNoDisponible_cuandoSeMuestra_entoncesLaTarjetaEstaBloqueada() {
        // Arrange / Act
        montarCatalogo()

        // Assert
        compose.onNodeWithText("Taladro Percutor").assertIsDisplayed()
        compose.onNodeWithText("No disponible para préstamo").assertIsDisplayed()
    }

    @Test
    fun dadoEstadoCargando_cuandoSeAbre_entoncesMuestraElIndicador() {
        // Arrange / Act
        montarCatalogo(estadoCarga = EstadoCarga.CARGANDO)

        // Assert
        compose.onNodeWithContentDescription("Indicador de carga").assertIsDisplayed()
    }

    @Test
    fun dadoEstadoVacio_cuandoSeAbre_entoncesMuestraElMensajeVacio() {
        // Arrange / Act
        montarCatalogo(estadoCarga = EstadoCarga.VACIO)

        // Assert
        compose.onNodeWithText("Aún no hay equipos registrados en el inventario").assertIsDisplayed()
    }

    @Test
    fun dadoEstadoError_cuandoSePulsaReintentar_entoncesSeInvocaElReintento() {
        // Arrange
        var reintentos = 0
        montarCatalogo(
            estadoCarga = EstadoCarga.ERROR,
            errorCarga = "Sin conexión con el servidor"
        ) { reintentos++ }

        // Act
        compose.onNodeWithText("Sin conexión con el servidor").assertIsDisplayed()
        compose.onNodeWithContentDescription("Reintentar carga").performClick()

        // Assert
        assertEquals(1, reintentos)
    }
}
