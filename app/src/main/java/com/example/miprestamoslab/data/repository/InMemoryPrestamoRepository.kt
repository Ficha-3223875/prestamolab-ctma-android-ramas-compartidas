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
    private var nextEquipoId = 9

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
            it.equipoId == solicitud.equipoId &&
                    it.estado != EstadoSolicitud.CANCELADA &&
                    it.estado != EstadoSolicitud.DEVUELTA &&
                    it.estado != EstadoSolicitud.RECHAZADA
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

    // HU_08: Registrar entrega física
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

    // HU_09: Registrar devolución
    fun registrarDevolucion(solicitudId: Int): Result<Unit> {
        val solicitud = obtenerSolicitud(solicitudId)
            ?: return Result.failure(Exception("Solicitud no encontrada"))

        if (solicitud.estado != EstadoSolicitud.ENTREGADA) {
            return Result.failure(Exception("Solo se pueden devolver solicitudes en estado ENTREGADA"))
        }

        // Modificación lógica: Liberar el equipo asignado a la solicitud
        _equipos.update { lista ->
            lista.map { if (it.id == solicitud.equipoId) it.copy(estado = EstadoEquipo.DISPONIBLE) else it }
        }

        _solicitudes.update { lista ->
            lista.map { if (it.id == solicitudId) it.copy(estado = EstadoSolicitud.DEVUELTA) else it }
        }
        return Result.success(Unit)
    }

    // SPRINT 4: GESTIÓN DE INVENTARIO (HU 10, HU 11, HU 12)
    override fun agregarEquipo(nombre: String, categoria: CategoriaEquipo, descripcion: String): Result<Unit> {
        val nuevoEquipo = Equipo(
            id = nextEquipoId++,
            nombre = nombre,
            categoria = categoria,
            estado = EstadoEquipo.DISPONIBLE
        )
        _equipos.update { it + nuevoEquipo }
        return Result.success(Unit)
    }

    override fun editarEquipo(id: Int, nombre: String, categoria: CategoriaEquipo, descripcion: String): Result<Unit> {
        _equipos.update { lista ->
            lista.map {
                if (it.id == id) it.copy(nombre = nombre, categoria = categoria) else it
            }
        }
        return Result.success(Unit)
    }

    override fun cambiarEstadoEquipo(id: Int, nuevoEstado: EstadoEquipo): Result<Unit> {
        _equipos.update { lista ->
            lista.map {
                if (it.id == id) it.copy(estado = nuevoEstado) else it
            }
        }
        return Result.success(Unit)
    }
}