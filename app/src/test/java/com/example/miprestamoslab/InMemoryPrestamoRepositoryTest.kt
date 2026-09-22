package com.example.miprestamoslab

import kotlinx.coroutines.test.runTest

import com.example.miprestamoslab.data.repository.InMemoryPrestamoRepository
import com.example.miprestamoslab.model.CategoriaEquipo
import com.example.miprestamoslab.model.EstadoEquipo
import com.example.miprestamoslab.model.EstadoSolicitud
import com.example.miprestamoslab.model.SolicitudPrestamo
import org.junit.Assert
import org.junit.Test

class InMemoryPrestamoRepositoryTest {

    @Test
    fun obtenerEquipos_debeCargarLosEquiposIniciales() = runTest {
        val repository = InMemoryPrestamoRepository()

        val equipos = repository.obtenerEquipos()

        Assert.assertEquals(8, equipos.size)
        Assert.assertEquals("Multímetro Digital", equipos[0].nombre)
        Assert.assertEquals(EstadoEquipo.DISPONIBLE, equipos[0].estado)
    }

    @Test
    fun crearSolicitud_debeRegistrarSolicitudYReservarEquipo() = runTest {
        val repository = InMemoryPrestamoRepository()

        val solicitud = SolicitudPrestamo(
            id = 0,
            equipoId = 1,
            ambienteDestino = "Laboratorio de Electrónica",
            proposito = "Realizar práctica de medición de componentes electrónicos",
            duracionHoras = 3,
            estado = EstadoSolicitud.SOLICITADA
        )

        val resultado = repository.crearSolicitud(solicitud)

        Assert.assertTrue(resultado.isSuccess)
        Assert.assertEquals(1, repository.obtenerSolicitudes().size)

        val solicitudGuardada = repository.obtenerSolicitud(1)
        Assert.assertNotNull(solicitudGuardada)
        Assert.assertEquals(EstadoSolicitud.SOLICITADA, solicitudGuardada?.estado)

        val equipo = repository.obtenerEquipo(1)
        Assert.assertEquals(EstadoEquipo.RESERVADO, equipo?.estado)
    }

    @Test
    fun crearSolicitud_noDebePermitirDosSolicitudesActivasParaElMismoEquipo() = runTest {
        val repository = InMemoryPrestamoRepository()

        val solicitud1 = SolicitudPrestamo(
            id = 0,
            equipoId = 2,
            ambienteDestino = "Laboratorio 1",
            proposito = "Realizar una práctica con la tarjeta Arduino Uno",
            duracionHoras = 2,
            estado = EstadoSolicitud.SOLICITADA
        )

        val solicitud2 = SolicitudPrestamo(
            id = 0,
            equipoId = 2,
            ambienteDestino = "Laboratorio 2",
            proposito = "Realizar una segunda práctica con el equipo",
            duracionHoras = 1,
            estado = EstadoSolicitud.SOLICITADA
        )

        val primerResultado = repository.crearSolicitud(solicitud1)
        val segundoResultado = repository.crearSolicitud(solicitud2)

        Assert.assertTrue(primerResultado.isSuccess)
        Assert.assertTrue(segundoResultado.isFailure)
        Assert.assertEquals(1, repository.obtenerSolicitudes().size)
    }

    @Test
    fun cancelarSolicitud_debeCambiarEstadoYLiberarEquipo() = runTest {
        val repository = InMemoryPrestamoRepository()

        val solicitud = SolicitudPrestamo(
            id = 0,
            equipoId = 3,
            ambienteDestino = "Sala de Sistemas",
            proposito = "Realizar pruebas de desarrollo en la tablet",
            duracionHoras = 2,
            estado = EstadoSolicitud.SOLICITADA
        )

        repository.crearSolicitud(solicitud)

        val resultado = repository.cancelarSolicitud(1)

        Assert.assertTrue(resultado.isSuccess)
        Assert.assertEquals(
            EstadoSolicitud.CANCELADA,
            repository.obtenerSolicitud(1)?.estado
        )
        Assert.assertEquals(
            EstadoEquipo.DISPONIBLE,
            repository.obtenerEquipo(3)?.estado
        )
    }

    @Test
    fun aprobarSolicitud_debeCambiarEstadoAprobada() = runTest {
        val repository = InMemoryPrestamoRepository()

        val solicitud = SolicitudPrestamo(
            id = 0,
            equipoId = 4,
            ambienteDestino = "Laboratorio Multimedia",
            proposito = "Realizar grabación de contenido audiovisual para una actividad",
            duracionHoras = 4,
            estado = EstadoSolicitud.SOLICITADA
        )

        repository.crearSolicitud(solicitud)

        val resultado = repository.aprobarSolicitud(1)

        Assert.assertTrue(resultado.isSuccess)
        Assert.assertEquals(
            EstadoSolicitud.APROBADA,
            repository.obtenerSolicitud(1)?.estado
        )
    }

    @Test
    fun rechazarSolicitud_debeGuardarRazonYLiberarEquipo() = runTest {
        val repository = InMemoryPrestamoRepository()

        val solicitud = SolicitudPrestamo(
            id = 0,
            equipoId = 5,
            ambienteDestino = "Taller",
            proposito = "Realizar práctica de soldadura con componentes electrónicos",
            duracionHoras = 2,
            estado = EstadoSolicitud.SOLICITADA
        )

        repository.crearSolicitud(solicitud)

        val resultado = repository.rechazarSolicitud(
            1,
            "El equipo se encuentra reservado para otra actividad."
        )

        Assert.assertTrue(resultado.isSuccess)

        val solicitudRechazada = repository.obtenerSolicitud(1)

        Assert.assertEquals(
            EstadoSolicitud.RECHAZADA,
            solicitudRechazada?.estado
        )

        Assert.assertEquals(
            "El equipo se encuentra reservado para otra actividad.",
            solicitudRechazada?.razonRechazo
        )

        Assert.assertEquals(
            EstadoEquipo.DISPONIBLE,
            repository.obtenerEquipo(5)?.estado
        )
    }

    @Test
    fun rechazarSolicitud_noDebeAceptarRazonMuyCorta() = runTest {
        val repository = InMemoryPrestamoRepository()

        val solicitud = SolicitudPrestamo(
            id = 0,
            equipoId = 6,
            ambienteDestino = "Laboratorio",
            proposito = "Realizar pruebas de funcionamiento del teclado mecánico",
            duracionHoras = 1,
            estado = EstadoSolicitud.SOLICITADA
        )

        repository.crearSolicitud(solicitud)

        val resultado = repository.rechazarSolicitud(1, "No")

        Assert.assertTrue(resultado.isFailure)
        Assert.assertEquals(
            EstadoSolicitud.SOLICITADA,
            repository.obtenerSolicitud(1)?.estado
        )
    }

    @Test
    fun agregarEquipo_debeCrearNuevoEquipoDisponible() = runTest {
        val repository = InMemoryPrestamoRepository()

        val resultado = repository.agregarEquipo(
            nombre = "Laptop Lenovo",
            categoria = CategoriaEquipo.PERIFERICO,
            descripcion = "Laptop para actividades de desarrollo"
        )

        Assert.assertTrue(resultado.isSuccess)

        val equipo = repository.obtenerEquipo(9)

        Assert.assertNotNull(equipo)
        Assert.assertEquals("Laptop Lenovo", equipo?.nombre)
        Assert.assertEquals(CategoriaEquipo.PERIFERICO, equipo?.categoria)
        Assert.assertEquals(EstadoEquipo.DISPONIBLE, equipo?.estado)
        Assert.assertEquals(
            "Laptop para actividades de desarrollo",
            equipo?.descripcion
        )
    }

    @Test
    fun editarEquipo_debeActualizarDatosDelEquipo() = runTest {
        val repository = InMemoryPrestamoRepository()

        val resultado = repository.editarEquipo(
            id = 1,
            nombre = "Multímetro Fluke",
            categoria = CategoriaEquipo.ELECTRONICA,
            descripcion = "Multímetro digital actualizado"
        )

        Assert.assertTrue(resultado.isSuccess)

        val equipo = repository.obtenerEquipo(1)

        Assert.assertEquals("Multímetro Fluke", equipo?.nombre)
        Assert.assertEquals(CategoriaEquipo.ELECTRONICA, equipo?.categoria)
        Assert.assertEquals(
            "Multímetro digital actualizado",
            equipo?.descripcion
        )
    }

    @Test
    fun cambiarEstadoEquipo_debeActualizarEstado() = runTest {
        val repository = InMemoryPrestamoRepository()

        val resultado = repository.cambiarEstadoEquipo(
            7,
            EstadoEquipo.EN_MANTENIMIENTO
        )

        Assert.assertTrue(resultado.isSuccess)
        Assert.assertEquals(
            EstadoEquipo.EN_MANTENIMIENTO,
            repository.obtenerEquipo(7)?.estado
        )
    }

    @Test
    fun cambiarEstadoEquipo_noDebePermitirMantenimientoSiEstaPrestado() = runTest {
        val repository = InMemoryPrestamoRepository()

        repository.cambiarEstadoEquipo(8, EstadoEquipo.PRESTADO)

        val resultado = repository.cambiarEstadoEquipo(
            8,
            EstadoEquipo.EN_MANTENIMIENTO
        )

        Assert.assertTrue(resultado.isFailure)
        Assert.assertEquals(
            EstadoEquipo.PRESTADO,
            repository.obtenerEquipo(8)?.estado
        )
    }
}