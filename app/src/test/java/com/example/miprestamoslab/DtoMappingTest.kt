package com.example.miprestamoslab

import com.example.miprestamoslab.data.remote.EquipoDto
import com.example.miprestamoslab.data.remote.SolicitudDto
import com.example.miprestamoslab.data.remote.aDto
import com.example.miprestamoslab.model.CategoriaEquipo
import com.example.miprestamoslab.model.Equipo
import com.example.miprestamoslab.model.EstadoEquipo
import com.example.miprestamoslab.model.EstadoSolicitud
import com.example.miprestamoslab.model.SolicitudPrestamo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

/**
 * Semana 8 — mapeo DTO <-> dominio.
 * Garantiza que la UI/conocimiento del JSON quede aislado en la capa de datos.
 */
class DtoMappingTest {

    @Test
    fun dadoEquipoDto_cuandoSeMapea_entoncesConservaTodosLosCampos() {
        // Arrange
        val dto = EquipoDto(
            id = 7,
            nombre = "Osciloscopio",
            categoria = "ELECTRONICA",
            estado = "DISPONIBLE",
            descripcion = "100 MHz"
        )

        // Act
        val equipo = dto.aDominio()

        // Assert
        assertEquals(7, equipo.id)
        assertEquals("Osciloscopio", equipo.nombre)
        assertEquals(CategoriaEquipo.ELECTRONICA, equipo.categoria)
        assertEquals(EstadoEquipo.DISPONIBLE, equipo.estado)
        assertEquals("100 MHz", equipo.descripcion)
    }

    @Test
    fun dadoEquipoDtoSinEstado_entoncesUsaElValorPorDefecto() {
        // Arrange
        val dto = EquipoDto(id = 1, nombre = "Laptop", categoria = "PERIFERICO")

        // Act
        val equipo = dto.aDominio()

        // Assert
        assertEquals(EstadoEquipo.DISPONIBLE, equipo.estado)
        assertEquals("", equipo.descripcion)
    }

    @Test
    fun dadoEquipoDtoInvalido_cuandoSeMapea_entoncesFallaConMensajeDeCampo() {
        // Arrange
        val dto = EquipoDto(id = 1, categoria = "CAMARA") // sin nombre

        // Act / Assert
        val error = assertThrows(IllegalArgumentException::class.java) { dto.aDominio() }
        assertEquals(true, error.message!!.contains("nombre"))
    }

    @Test
    fun dadoEquipoDominio_cuandoSeConvierteATDto_entoncesEsIdaYVuelta() {
        // Arrange
        val equipo = Equipo(
            id = 3,
            nombre = "Taladro",
            categoria = CategoriaEquipo.HERRAMIENTA,
            estado = EstadoEquipo.EN_MANTENIMIENTO,
            descripcion = "Inalámbrico"
        )

        // Act
        val idaYVuelta = equipo.aDto().aDominio()

        // Assert
        assertEquals(equipo, idaYVuelta)
    }

    @Test
    fun dadaSolicitudDto_cuandoSeMapea_entoncesConservaEstadoYRazon() {
        // Arrange
        val dto = SolicitudDto(
            id = 12,
            equipoId = 3,
            ambienteDestino = "Taller 2",
            proposito = "Práctica de mantenimiento preventivo",
            duracionHoras = 4,
            estado = "RECHAZADA",
            razonRechazo = "El equipo está en mantenimiento"
        )

        // Act
        val solicitud = dto.aDominio()

        // Assert
        assertEquals(EstadoSolicitud.RECHAZADA, solicitud.estado)
        assertEquals("El equipo está en mantenimiento", solicitud.razonRechazo)
        assertEquals(4, solicitud.duracionHoras)
    }

    @Test
    fun dadaSolicitudSinEstado_entoncesSeAsumeSolicitada() {
        // Arrange
        val dto = SolicitudDto(equipoId = 1, duracionHoras = 2)

        // Act
        val solicitud = dto.aDominio()

        // Assert
        assertEquals(EstadoSolicitud.SOLICITADA, solicitud.estado)
        assertEquals(0, solicitud.id)
    }

    @Test
    fun dadaSolicitudDtoSinEquipo_entoncesFalla() {
        // Arrange
        val dto = SolicitudDto(duracionHoras = 2)

        // Act / Assert
        assertThrows(IllegalArgumentException::class.java) { dto.aDominio() }
    }
}
