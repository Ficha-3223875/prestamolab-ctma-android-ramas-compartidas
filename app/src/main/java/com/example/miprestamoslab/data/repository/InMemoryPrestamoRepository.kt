package com.example.miprestamoslab.data.repository

import com.example.miprestamoslab.model.CategoriaEquipo
import com.example.miprestamoslab.model.Equipo
import com.example.miprestamoslab.model.EstadoEquipo
import com.example.miprestamoslab.model.EstadoSolicitud
import com.example.miprestamoslab.model.SolicitudPrestamo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryPrestamoRepository {

    private val _equipos = MutableStateFlow<List<Equipo>>(
        listOf(
            Equipo(1, "Osciloscopio Digital", CategoriaEquipo.HERRAMIENTAS, EstadoEquipo.DISPONIBLE),
            Equipo(2, "Multímetro Fluke", CategoriaEquipo.HERRAMIENTAS, EstadoEquipo.DISPONIBLE),
            Equipo(3, "Impresora 3D Ender", CategoriaEquipo.MAQUINARIA, EstadoEquipo.DISPONIBLE)
        )
    )
    val equipos: StateFlow<List<Equipo>> = _equipos.asStateFlow()

    private val _solicitudes = MutableStateFlow<List<SolicitudPrestamo>>(emptyList())
    val solicitudes: StateFlow<List<SolicitudPrestamo>> = _solicitudes.asStateFlow()

    private var contadorSolicitudes = 1

    fun obtenerEquipo(equipoId: Int): Equipo? {
        return _equipos.value.find { it.id == equipoId }
    }

    fun obtenerSolicitud(solicitudId: Int): SolicitudPrestamo? {
        return _solicitudes.value.find { it.id == solicitudId }
    }

    fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> {
        val nueva = solicitud.copy(id = contadorSolicitudes++)
        _solicitudes.update { it + nueva }
        return Result.success(Unit)
    }

    fun cancelarSolicitud(solicitudId: Int): Result<Unit> {
        _solicitudes.update { lista ->
            lista.map { if (it.id == solicitudId) it.copy(estado = EstadoSolicitud.CANCELADA) else it }
        }
        return Result.success(Unit)
    }

    fun aprobarSolicitud(solicitudId: Int): Result<Unit> {
        _solicitudes.update { lista ->
            lista.map { if (it.id == solicitudId) it.copy(estado = EstadoSolicitud.APROBADA) else it }
        }
        return Result.success(Unit)
    }

    fun rechazarSolicitud(solicitudId: Int, razon: String): Result<Unit> {
        _solicitudes.update { lista ->
            lista.map { if (it.id == solicitudId) it.copy(estado = EstadoSolicitud.RECHAZADA, razonRechazo = razon) else it }
        }
        return Result.success(Unit)
    }

    // HU_08: Registrar entrega física (Requiere estar en APROBADA)
    fun registrarEntregaFisica(solicitudId: Int): Result<Unit> {
        val solicitud = obtenerSolicitud(solicitudId)
            ?: return Result.failure(Exception("Solicitud no encontrada"))

        if (solicitud.estado != EstadoSolicitud.APROBADA) {
            return Result.failure(Exception("Solo se pueden entregar solicitudes en estado APROBADA"))
        }

        _solicitudes.update { lista ->
            lista.map { if (it.id == solicitudId) it.copy(estado = EstadoSolicitud.ENTREGADA) else it }
        }
        return Result.success(Unit)
    }

    // HU_09: Registrar devolución (Requiere estar en ENTREGADA)
    fun registrarDevolucion(solicitudId: Int): Result<Unit> {
        val solicitud = obtenerSolicitud(solicitudId)
            ?: return Result.failure(Exception("Solicitud no encontrada"))

        if (solicitud.estado != EstadoSolicitud.ENTREGADA) {
            return Result.failure(Exception("Solo se pueden devolver solicitudes en estado ENTREGADA"))
        }

        _solicitudes.update { lista ->
            lista.map { if (it.id == solicitudId) it.copy(estado = EstadoSolicitud.DEVUELTA) else it }
        }
        return Result.success(Unit)
    }
}