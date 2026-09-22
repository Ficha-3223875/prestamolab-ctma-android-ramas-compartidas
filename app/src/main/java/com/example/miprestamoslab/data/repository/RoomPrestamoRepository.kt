package com.example.miprestamoslab.data.repository

import com.example.miprestamoslab.data.local.EquipoEntity
import com.example.miprestamoslab.data.local.PrestamoDao
import com.example.miprestamoslab.data.local.PrestamoDatabase
import com.example.miprestamoslab.data.local.SolicitudEntity
import com.example.miprestamoslab.model.CategoriaEquipo
import com.example.miprestamoslab.model.Equipo
import com.example.miprestamoslab.model.EstadoEquipo
import com.example.miprestamoslab.model.EstadoSolicitud
import com.example.miprestamoslab.model.SolicitudPrestamo
import androidx.room.withTransaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class RoomPrestamoRepository(
    private val database: PrestamoDatabase,
    private val dao: PrestamoDao = database.prestamoDao(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) : PrestamoRepository {

    private fun Flow<List<EquipoEntity>>.equiposAModelos(): Flow<List<Equipo>> =
        map { lista -> lista.map { it.toModel() } }

    private fun Flow<List<SolicitudEntity>>.solicitudesAModelos(): Flow<List<SolicitudPrestamo>> =
        map { lista -> lista.map { it.toModel() } }

    override val equipos: StateFlow<List<Equipo>> =
        dao.observarEquipos().equiposAModelos()
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override val solicitudes: StateFlow<List<SolicitudPrestamo>> =
        dao.observarSolicitudes().solicitudesAModelos()
            .stateIn(scope, SharingStarted.Eagerly, emptyList())

    override fun obtenerEquipos(): List<Equipo> = equipos.value

    override fun obtenerEquipo(id: Int): Equipo? = equipos.value.find { it.id == id }

    override fun obtenerSolicitudes(): List<SolicitudPrestamo> = solicitudes.value

    override fun obtenerSolicitud(id: Int): SolicitudPrestamo? =
        solicitudes.value.find { it.id == id }

    override suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> =
        runCatching {
            database.withTransaction {
                val equipo = dao.obtenerEquipoPorId(solicitud.equipoId)
                    ?: throw IllegalArgumentException("Equipo no encontrado")

                if (equipo.estado != EstadoEquipo.DISPONIBLE.name) {
                    throw IllegalStateException("El equipo no está disponible")
                }

                val existeActiva = dao.obtenerSolicitudesPorEquipo(solicitud.equipoId).any {
                    it.estado != EstadoSolicitud.CANCELADA.name &&
                        it.estado != EstadoSolicitud.DEVUELTA.name &&
                        it.estado != EstadoSolicitud.RECHAZADA.name
                }
                if (existeActiva) {
                    throw IllegalStateException("Ya existe una solicitud activa para este equipo")
                }

                dao.actualizarEstadoEquipo(solicitud.equipoId, EstadoEquipo.RESERVADO.name)
                dao.insertarSolicitud(SolicitudEntity.fromModel(solicitud.copy(id = 0)))
            }
        }

    override suspend fun cancelarSolicitud(id: Int): Result<Unit> =
        runCatching {
            database.withTransaction {
                val solicitud = dao.obtenerSolicitudPorId(id)
                    ?: throw IllegalArgumentException("Solicitud no encontrada")

                if (solicitud.estado != EstadoSolicitud.SOLICITADA.name) {
                    throw IllegalStateException("Solo se pueden cancelar solicitudes en estado SOLICITADA")
                }

                dao.actualizarEstadoEquipo(solicitud.equipoId, EstadoEquipo.DISPONIBLE.name)
                dao.actualizarEstadoSolicitud(id, EstadoSolicitud.CANCELADA.name)
            }
        }

    override suspend fun aprobarSolicitud(id: Int): Result<Unit> =
        runCatching {
            database.withTransaction {
                val solicitud = dao.obtenerSolicitudPorId(id)
                    ?: throw IllegalArgumentException("Solicitud no encontrada")

                if (solicitud.estado != EstadoSolicitud.SOLICITADA.name) {
                    throw IllegalStateException("Solo se pueden aprobar solicitudes en estado SOLICITADA")
                }

                dao.actualizarEstadoSolicitud(id, EstadoSolicitud.APROBADA.name)
                dao.actualizarEstadoEquipo(solicitud.equipoId, EstadoEquipo.PRESTADO.name)
            }
        }

    override suspend fun rechazarSolicitud(id: Int, razon: String): Result<Unit> =
        runCatching {
            database.withTransaction {
                val solicitud = dao.obtenerSolicitudPorId(id)
                    ?: throw IllegalArgumentException("Solicitud no encontrada")

                if (solicitud.estado != EstadoSolicitud.SOLICITADA.name) {
                    throw IllegalStateException("Solo se pueden rechazar solicitudes en estado SOLICITADA")
                }

                if (razon.trim().length < 5) {
                    throw IllegalArgumentException("Debes indicar una razón de rechazo válida")
                }

                dao.actualizarEstadoEquipo(solicitud.equipoId, EstadoEquipo.DISPONIBLE.name)
                dao.actualizarSolicitudRechazada(id, EstadoSolicitud.RECHAZADA.name, razon.trim())
            }
        }

    override suspend fun agregarEquipo(
        nombre: String,
        categoria: CategoriaEquipo,
        descripcion: String
    ): Result<Unit> = runCatching {
        if (nombre.isBlank()) {
            throw IllegalArgumentException("El nombre del equipo no puede estar vacío")
        }

        database.withTransaction {
            val nuevoId = (dao.obtenerMaxIdEquipo() ?: 0) + 1
            dao.insertarEquipo(
                EquipoEntity(
                    id = nuevoId,
                    nombre = nombre.trim(),
                    categoria = categoria.name,
                    estado = EstadoEquipo.DISPONIBLE.name,
                    descripcion = descripcion.trim()
                )
            )
        }
    }

    override suspend fun editarEquipo(
        id: Int,
        nombre: String,
        categoria: CategoriaEquipo,
        descripcion: String
    ): Result<Unit> = runCatching {
        dao.obtenerEquipoPorId(id)
            ?: throw IllegalArgumentException("Equipo no encontrado")

        if (nombre.isBlank()) {
            throw IllegalArgumentException("El nombre del equipo no puede estar vacío")
        }

        dao.actualizarDatosEquipo(
            id = id,
            nombre = nombre.trim(),
            categoria = categoria.name,
            descripcion = descripcion.trim()
        )
    }

    override suspend fun cambiarEstadoEquipo(
        id: Int,
        nuevoEstado: EstadoEquipo
    ): Result<Unit> = runCatching {
        val equipoExistente = dao.obtenerEquipoPorId(id)
            ?: throw IllegalArgumentException("Equipo no encontrado")

        val noPermitido = nuevoEstado == EstadoEquipo.EN_MANTENIMIENTO ||
            nuevoEstado == EstadoEquipo.DADO_DE_BAJA
        if (equipoExistente.estado == EstadoEquipo.PRESTADO.name && noPermitido) {
            throw IllegalStateException("No se puede cambiar el estado de un equipo que se encuentra PRESTADO")
        }

        dao.actualizarEstadoEquipo(id, nuevoEstado.name)
    }

    override fun liberarRecursos() {
        scope.cancel()
    }
}