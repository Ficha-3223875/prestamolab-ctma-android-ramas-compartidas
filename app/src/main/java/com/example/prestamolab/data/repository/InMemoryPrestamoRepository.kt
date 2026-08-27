package com.example.prestamolab.data.repository

import com.example.prestamolab.data.model.CategoriaEquipo
import com.example.prestamolab.data.model.Equipo
import com.example.prestamolab.data.model.EstadoEquipo
import com.example.prestamolab.data.model.EstadoSolicitud
import com.example.prestamolab.data.model.SolicitudPrestamo

class InMemoryPrestamoRepository : PrestamoRepository {

    // Lista mutable interna para simular el catálogo de equipos
    private val equipos = mutableListOf(
        Equipo(1, "Multímetro Digital", CategoriaEquipo.HERRAMIENTAS, EstadoEquipo.DISPONIBLE),
        Equipo(2, "Kit Arduino Uno", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE),
        Equipo(3, "Tableta Android Test", CategoriaEquipo.DISPOSITIVOS, EstadoEquipo.DISPONIBLE),
        Equipo(4, "Cámara Fotográfica DSLR", CategoriaEquipo.PERIFERICOS, EstadoEquipo.RESERVADO)
    )

    // Lista mutable para almacenar las solicitudes creadas en tiempo de ejecución
    private val solicitudes = mutableListOf<SolicitudPrestamo>()

    override fun obtenerEquipos(): List<Equipo> = equipos.toList()

    override fun obtenerEquipo(id: Int): Equipo? = equipos.find { it.id == id }

    override fun obtenerSolicitudes(): List<SolicitudPrestamo> = solicitudes.toList()

    override fun obtenerSolicitud(id: Int): SolicitudPrestamo? = solicitudes.find { it.id == id }

    override fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit> {
        val equipoIndex = equipos.indexOfFirst { it.id == solicitud.equipoId }

        if (equipoIndex == -1) {
            return Result.failure(Exception("El equipo no existe"))
        }

        val equipo = equipos[equipoIndex]
        if (equipo.estado != EstadoEquipo.DISPONIBLE) {
            return Result.failure(Exception("El equipo no está disponible para préstamo"))
        }

        // Asignar ID incremental a la solicitud y guardar
        val nuevaSolicitudId = solicitudes.size + 1
        val solicitudConId = solicitud.copy(id = nuevaSolicitudId)
        solicitudes.add(solicitudConId)

        // RN-06: Cambiar el estado del equipo a RESERVADO
        equipos[equipoIndex] = equipo.copy(estado = EstadoEquipo.RESERVADO)

        return Result.success(Unit)
    }

    override fun cancelarSolicitud(id: Int): Result<Unit> {
        val index = solicitudes.indexOfFirst { it.id == id }
        if (index == -1) return Result.failure(Exception("Solicitud no encontrada"))

        val solicitud = solicitudes[index]

        // RN-07: Solo solicitudes en estado SOLICITADA se pueden cancelar
        if (solicitud.estado != EstadoSolicitud.SOLICITADA) {
            return Result.failure(Exception("Solo se pueden cancelar solicitudes en estado SOLICITADA"))
        }

        // Actualizar estado de la solicitud
        solicitudes[index] = solicitud.copy(estado = EstadoSolicitud.CANCELADA)

        // Liberar el equipo de nuevo a DISPONIBLE
        val equipoIndex = equipos.indexOfFirst { it.id == solicitud.equipoId }
        if (equipoIndex != -1) {
            equipos[equipoIndex] = equipos[equipoIndex].copy(estado = EstadoEquipo.DISPONIBLE)
        }

        return Result.success(Unit)
    }
}