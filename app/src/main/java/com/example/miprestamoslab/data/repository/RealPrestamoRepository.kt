package com.example.miprestamoslab.data.repository

import com.example.miprestamoslab.data.local.dao.EquipoDao
import com.example.miprestamoslab.data.local.dao.SolicitudPrestamoDao
import com.example.miprestamoslab.data.local.entity.EquipoEntity
import com.example.miprestamoslab.data.remote.PrestamoApiService
import com.example.miprestamoslab.data.toDomain
import com.example.miprestamoslab.data.toEntity
import com.example.miprestamoslab.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RealPrestamoRepository(
    private val equipoDao: EquipoDao,
    private val solicitudPrestamoDao: SolicitudPrestamoDao,
    private val apiService: PrestamoApiService
) : PrestamoRepository {

    override fun obtenerEquiposFlow(): Flow<List<Equipo>> {
        return equipoDao.obtenerEquiposFlow().map { list -> list.map { it.toDomain() } }
    }

    override fun obtenerSolicitudesFlow(): Flow<List<SolicitudPrestamo>> {
        return solicitudPrestamoDao.obtenerSolicitudesFlow().map { list -> list.map { it.toDomain() } }
    }

    override fun obtenerEquipos(): List<Equipo> {
        return equipoDao.obtenerEquipos().map { it.toDomain() }
    }

    override fun obtenerEquipo(id: Int): Equipo? {
        return equipoDao.obtenerEquipoPorId(id)?.toDomain()
    }

    override fun obtenerSolicitudes(): List<SolicitudPrestamo> {
        return solicitudPrestamoDao.obtenerSolicitudes().map { it.toDomain() }
    }

    override fun obtenerSolicitud(id: Int): SolicitudPrestamo? {
        return solicitudPrestamoDao.obtenerSolicitudPorId(id)?.toDomain()
    }

    override fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> {
        val equipoEntity = equipoDao.obtenerEquipoPorId(solicitud.equipoId)
            ?: return Result.failure(IllegalArgumentException("Equipo no encontrado"))

        if (equipoEntity.estado != "DISPONIBLE") {
            return Result.failure(IllegalStateException("El equipo no está disponible"))
        }

        val existeActiva = solicitudPrestamoDao.obtenerSolicitudes().any {
            it.equipoId == solicitud.equipoId && it.estado != "CANCELADA" && it.estado != "DEVUELTA" && it.estado != "RECHAZADA"
        }
        if (existeActiva) {
            return Result.failure(IllegalStateException("Ya existe una solicitud activa para este equipo"))
        }

        // Cambiar estado a RESERVADO localmente
        equipoDao.actualizarEquipo(equipoEntity.copy(estado = "RESERVADO"))

        // Guardar la solicitud localmente
        val entity = solicitud.toEntity(syncStatus = "PENDING_INSERT")
        solicitudPrestamoDao.insertarSolicitud(entity)

        return Result.success(Unit)
    }

    override fun cancelarSolicitud(id: Int): Result<Unit> {
        val solicitudEntity = solicitudPrestamoDao.obtenerSolicitudPorId(id)
            ?: return Result.failure(IllegalArgumentException("Solicitud no encontrada"))

        if (solicitudEntity.estado != "SOLICITADA") {
            return Result.failure(IllegalStateException("Solo se pueden cancelar solicitudes en estado SOLICITADA"))
        }

        val equipoEntity = equipoDao.obtenerEquipoPorId(solicitudEntity.equipoId)
        if (equipoEntity != null) {
            equipoDao.actualizarEquipo(equipoEntity.copy(estado = "DISPONIBLE"))
        }

        solicitudPrestamoDao.actualizarSolicitud(solicitudEntity.copy(estado = "CANCELADA", syncStatus = "PENDING_UPDATE"))
        return Result.success(Unit)
    }

    override fun aprobarSolicitud(id: Int): Result<Unit> {
        val solicitudEntity = solicitudPrestamoDao.obtenerSolicitudPorId(id)
            ?: return Result.failure(IllegalArgumentException("Solicitud no encontrada"))

        if (solicitudEntity.estado != "SOLICITADA") {
            return Result.failure(IllegalStateException("Solo se pueden aprobar solicitudes en estado SOLICITADA"))
        }

        solicitudPrestamoDao.actualizarSolicitud(solicitudEntity.copy(estado = "APROBADA", syncStatus = "PENDING_UPDATE"))
        return Result.success(Unit)
    }

    override fun rechazarSolicitud(id: Int, razon: String): Result<Unit> {
        val solicitudEntity = solicitudPrestamoDao.obtenerSolicitudPorId(id)
            ?: return Result.failure(IllegalArgumentException("Solicitud no encontrada"))

        if (solicitudEntity.estado != "SOLICITADA") {
            return Result.failure(IllegalStateException("Solo se pueden rechazar solicitudes en estado SOLICITADA"))
        }

        if (razon.trim().length < 5) {
            return Result.failure(IllegalArgumentException("Debes indicar una razón de rechazo válida"))
        }

        val equipoEntity = equipoDao.obtenerEquipoPorId(solicitudEntity.equipoId)
        if (equipoEntity != null) {
            equipoDao.actualizarEquipo(equipoEntity.copy(estado = "DISPONIBLE"))
        }

        solicitudPrestamoDao.actualizarSolicitud(solicitudEntity.copy(estado = "RECHAZADA", razonRechazo = razon.trim(), syncStatus = "PENDING_UPDATE"))
        return Result.success(Unit)
    }

    override fun agregarEquipo(nombre: String, categoria: CategoriaEquipo, descripcion: String): Result<Unit> {
        if (nombre.isBlank()) {
            return Result.failure(IllegalArgumentException("El nombre del equipo no puede estar vacío"))
        }

        val nuevoId = (equipoDao.obtenerEquipos().maxOfOrNull { it.id } ?: 0) + 1
        val nuevoEquipo = EquipoEntity(
            id = nuevoId,
            nombre = nombre.trim(),
            categoria = categoria.name,
            estado = "DISPONIBLE",
            descripcion = descripcion.trim(),
            syncStatus = "PENDING_INSERT"
        )

        equipoDao.insertarEquipo(nuevoEquipo)
        return Result.success(Unit)
    }

    override fun editarEquipo(id: Int, nombre: String, categoria: CategoriaEquipo, descripcion: String): Result<Unit> {
        val equipoEntity = equipoDao.obtenerEquipoPorId(id)
            ?: return Result.failure(IllegalArgumentException("Equipo no encontrado"))

        if (nombre.isBlank()) {
            return Result.failure(IllegalArgumentException("El nombre del equipo no puede estar vacío"))
        }

        equipoDao.actualizarEquipo(
            equipoEntity.copy(
                nombre = nombre.trim(),
                categoria = categoria.name,
                descripcion = descripcion.trim(),
                syncStatus = "PENDING_UPDATE"
            )
        )
        return Result.success(Unit)
    }

    override fun cambiarEstadoEquipo(id: Int, nuevoEstado: EstadoEquipo): Result<Unit> {
        val equipoEntity = equipoDao.obtenerEquipoPorId(id)
            ?: return Result.failure(IllegalArgumentException("Equipo no encontrado"))

        if (equipoEntity.estado == "PRESTADO" && (nuevoEstado == EstadoEquipo.EN_MANTENIMIENTO || nuevoEstado == EstadoEquipo.DADO_DE_BAJA)) {
            return Result.failure(IllegalStateException("No se puede cambiar el estado de un equipo que se encuentra PRESTADO"))
        }

        equipoDao.actualizarEquipo(equipoEntity.copy(estado = nuevoEstado.name, syncStatus = "PENDING_UPDATE"))
        return Result.success(Unit)
    }
}