package com.example.prestamolabctma_2.data

import com.example.prestamolabctma_2.model.CategoriaEquipo
import com.example.prestamolabctma_2.model.Equipo
import com.example.prestamolabctma_2.model.EstadoEquipo
import com.example.prestamolabctma_2.model.EstadoSolicitud
import com.example.prestamolabctma_2.model.SolicitudPrestamo

class InMemoryPrestamoRepository : PrestamoRepository {

    private val equipos = mutableListOf(
        Equipo(1, "Multímetro Digital", CategoriaEquipo.MEDICION, EstadoEquipo.DISPONIBLE),
        Equipo(2, "Kit Arduino Uno", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
        Equipo(3, "Laptop ASUS ROG", CategoriaEquipo.COMPUTACION, EstadoEquipo.RESERVADO),
        Equipo(4, "Cautín de Soldar", CategoriaEquipo.HERRAMIENTAS, EstadoEquipo.DISPONIBLE)
    )

    private val solicitudes = mutableListOf<SolicitudPrestamo>()

    override fun obtenerEquipos(): List<Equipo> = equipos.toList()

    override fun obtenerEquipo(id: Int): Equipo? = equipos.find { it.id == id }

    override fun obtenerSolicitudes(): List<SolicitudPrestamo> = solicitudes.toList()

    override fun obtenerSolicitud(id: Int): SolicitudPrestamo? = solicitudes.find { it.id == id }

    override fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> {
        val equipoIndex = equipos.indexOfFirst { it.id == solicitud.equipoId }
        if (equipoIndex == -1) {
            return Result.failure(IllegalArgumentException("El equipo no existe."))
        }

        val equipo = equipos[equipoIndex]
        if (equipo.estado != EstadoEquipo.DISPONIBLE) {
            return Result.failure(IllegalStateException("El equipo no está disponible."))
        }

        solicitudes.add(solicitud)
        equipos[equipoIndex] = equipo.copy(estado = EstadoEquipo.RESERVADO)
        return Result.success(Unit)
    }

    override fun cancelarSolicitud(id: Int): Result<Unit> {
        val solicitudIndex = solicitudes.indexOfFirst { it.id == id }
        if (solicitudIndex == -1) {
            return Result.failure(IllegalArgumentException("La solicitud no existe."))
        }

        val solicitud = solicitudes[solicitudIndex]
        if (solicitud.estado != EstadoSolicitud.SOLICITADA) {
            return Result.failure(IllegalStateException("Solo se pueden cancelar solicitudes en estado SOLICITADA."))
        }

        solicitudes[solicitudIndex] = solicitud.copy(estado = EstadoSolicitud.CANCELADA)

        val equipoIndex = equipos.indexOfFirst { it.id == solicitud.equipoId }
        if (equipoIndex != -1) {
            equipos[equipoIndex] = equipos[equipoIndex].copy(estado = EstadoEquipo.DISPONIBLE)
        }

        return Result.success(Unit)
    }
}