package com.example.miprestamoslab.data.repository

import com.example.miprestamoslab.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryPrestamoRepository : PrestamoRepository {

    private val _equipos = MutableStateFlow(
        listOf(
            Equipo(1, "Multímetro Digital", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
            Equipo(2, "Kit Arduino Uno", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
            Equipo(3, "Tablet Samsung", CategoriaEquipo.TABLETA, EstadoEquipo.DISPONIBLE),
            Equipo(4, "Cámara DSLR Canon", CategoriaEquipo.CAMARA, EstadoEquipo.DISPONIBLE),
            Equipo(5, "Soldador de Estaño", CategoriaEquipo.HERRAMIENTA, EstadoEquipo.DISPONIBLE),
            Equipo(6, "Teclado Mecánico", CategoriaEquipo.PERIFERICO, EstadoEquipo.DISPONIBLE),
            Equipo(7, "Osciloscopio USB", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
            Equipo(8, "Set Destornilladores", CategoriaEquipo.HERRAMIENTA, EstadoEquipo.DISPONIBLE)
        )
    )
    val equipos: StateFlow<List<Equipo>> = _equipos.asStateFlow()

    private val _solicitudes = MutableStateFlow<List<SolicitudPrestamo>>(emptyList())
    val solicitudes: StateFlow<List<SolicitudPrestamo>> = _solicitudes.asStateFlow()

    private var nextSolicitudId = 1

    override fun obtenerEquipos(): List<Equipo> = _equipos.value

    override fun obtenerEquipo(id: Int): Equipo? = _equipos.value.find { it.id == id }

    override fun obtenerSolicitudes(): List<SolicitudPrestamo> = _solicitudes.value

    override fun obtenerSolicitud(id: Int): SolicitudPrestamo? = _solicitudes.value.find { it.id == id }

    override fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> {
        val equipo = _equipos.value.find { it.id == solicitud.equipoId }
            ?: return Result.failure(IllegalArgumentException("Equipo no encontrado"))

        if (equipo.estado != EstadoEquipo.DISPONIBLE) {
            return Result.failure(IllegalStateException("El equipo no está disponible"))
        }

        val existeActiva = _solicitudes.value.any {
            it.equipoId == solicitud.equipoId && it.estado != EstadoSolicitud.CANCELADA && it.estado != EstadoSolicitud.DEVUELTA && it.estado != EstadoSolicitud.RECHAZADA
        }
        if (existeActiva) {
            return Result.failure(IllegalStateException("Ya existe una solicitud activa para este equipo"))
        }

        val nuevosEquipos = _equipos.value.map {
            if (it.id == solicitud.equipoId) it.copy(estado = EstadoEquipo.RESERVADO) else it
        }
        _equipos.value = nuevosEquipos

        val solicitudFinal = solicitud.copy(id = nextSolicitudId++)
        _solicitudes.value = _solicitudes.value + solicitudFinal

        return Result.success(Unit)
    }

    override fun cancelarSolicitud(id: Int): Result<Unit> {
        val solicitud = _solicitudes.value.find { it.id == id }
            ?: return Result.failure(IllegalArgumentException("Solicitud no encontrada"))

        if (solicitud.estado != EstadoSolicitud.SOLICITADA) {
            return Result.failure(IllegalStateException("Solo se pueden cancelar solicitudes en estado SOLICITADA"))
        }

        val nuevosEquipos = _equipos.value.map {
            if (it.id == solicitud.equipoId) it.copy(estado = EstadoEquipo.DISPONIBLE) else it
        }
        _equipos.value = nuevosEquipos

        val nuevasSolicitudes = _solicitudes.value.map {
            if (it.id == id) it.copy(estado = EstadoSolicitud.CANCELADA) else it
        }
        _solicitudes.value = nuevasSolicitudes

        return Result.success(Unit)
    }

    override fun aprobarSolicitud(id: Int): Result<Unit> {
        val solicitud = _solicitudes.value.find { it.id == id }
            ?: return Result.failure(IllegalArgumentException("Solicitud no encontrada"))

        if (solicitud.estado != EstadoSolicitud.SOLICITADA) {
            return Result.failure(IllegalStateException("Solo se pueden aprobar solicitudes en estado SOLICITADA"))
        }

        _solicitudes.value = _solicitudes.value.map {
            if (it.id == id) it.copy(estado = EstadoSolicitud.APROBADA) else it
        }

        return Result.success(Unit)
    }

    override fun rechazarSolicitud(id: Int, razon: String): Result<Unit> {
        val solicitud = _solicitudes.value.find { it.id == id }
            ?: return Result.failure(IllegalArgumentException("Solicitud no encontrada"))

        if (solicitud.estado != EstadoSolicitud.SOLICITADA) {
            return Result.failure(IllegalStateException("Solo se pueden rechazar solicitudes en estado SOLICITADA"))
        }

        if (razon.trim().length < 5) {
            return Result.failure(IllegalArgumentException("Debes indicar una razón de rechazo válida"))
        }

        _equipos.value = _equipos.value.map {
            if (it.id == solicitud.equipoId) it.copy(estado = EstadoEquipo.DISPONIBLE) else it
        }

        _solicitudes.value = _solicitudes.value.map {
            if (it.id == id) it.copy(estado = EstadoSolicitud.RECHAZADA, razonRechazo = razon.trim()) else it
        }

        return Result.success(Unit)
    }

    // --- SPRINT 4: GESTIÓN DE INVENTARIO ---

    override fun agregarEquipo(nombre: String, categoria: CategoriaEquipo, descripcion: String): Result<Unit> {
        if (nombre.isBlank()) {
            return Result.failure(IllegalArgumentException("El nombre del equipo no puede estar vacío"))
        }

        val nuevoId = (_equipos.value.maxOfOrNull { it.id } ?: 0) + 1
        val nuevoEquipo = Equipo(
            id = nuevoId,
            nombre = nombre.trim(),
            categoria = categoria,
            estado = EstadoEquipo.DISPONIBLE,
            descripcion = descripcion.trim()
        )

        _equipos.value = _equipos.value + nuevoEquipo
        return Result.success(Unit)
    }

    override fun editarEquipo(id: Int, nombre: String, categoria: CategoriaEquipo, descripcion: String): Result<Unit> {
        val equipoExistente = _equipos.value.find { it.id == id }
            ?: return Result.failure(IllegalArgumentException("Equipo no encontrado"))

        if (nombre.isBlank()) {
            return Result.failure(IllegalArgumentException("El nombre del equipo no puede estar vacío"))
        }

        _equipos.value = _equipos.value.map { equipo ->
            if (equipo.id == id) {
                equipo.copy(
                    nombre = nombre.trim(),
                    categoria = categoria,
                    descripcion = descripcion.trim()
                )
            } else {
                equipo
            }
        }

        return Result.success(Unit)
    }

    override fun cambiarEstadoEquipo(id: Int, nuevoEstado: EstadoEquipo): Result<Unit> {
        val equipoExistente = _equipos.value.find { it.id == id }
            ?: return Result.failure(IllegalArgumentException("Equipo no encontrado"))

        if (equipoExistente.estado == EstadoEquipo.PRESTADO && (nuevoEstado == EstadoEquipo.EN_MANTENIMIENTO || nuevoEstado == EstadoEquipo.DADO_DE_BAJA)) {
            return Result.failure(IllegalStateException("No se puede cambiar el estado de un equipo que se encuentra PRESTADO"))
        }

        _equipos.value = _equipos.value.map { equipo ->
            if (equipo.id == id) {
                equipo.copy(estado = nuevoEstado)
            } else {
                equipo
            }
        }

        return Result.success(Unit)
    }
}