package com.example.miprestamoslab.data.remote

import com.example.miprestamoslab.model.EstadoSolicitud
import com.example.miprestamoslab.model.SolicitudPrestamo

/**
 * DTO de transporte para /solicitudes (Semana 8).
 * Mismo criterio que [EquipoDto]: contrato JSON aislado del modelo de dominio.
 */
data class SolicitudDto(
    val id: Int? = null,
    val equipoId: Int? = null,
    val ambienteDestino: String? = null,
    val proposito: String? = null,
    val duracionHoras: Int? = null,
    val estado: String? = null,
    val razonRechazo: String? = null
) {
    /**
     * Convierte el DTO a modelo de dominio.
     * @throws IllegalArgumentException si la respuesta no cumple el contrato esperado.
     */
    fun aDominio(): SolicitudPrestamo {
        val idEquipo = requireNotNull(equipoId) { "El campo 'equipoId' es obligatorio" }
        val duracion = requireNotNull(duracionHoras) { "El campo 'duracionHoras' es obligatorio" }

        return SolicitudPrestamo(
            id = id ?: 0,
            equipoId = idEquipo,
            ambienteDestino = ambienteDestino.orEmpty(),
            proposito = proposito.orEmpty(),
            duracionHoras = duracion,
            estado = estado?.let { EstadoSolicitud.valueOf(it) } ?: EstadoSolicitud.SOLICITADA,
            razonRechazo = razonRechazo
        )
    }
}

/** Modelo de dominio -> DTO para POST/PUT. */
fun SolicitudPrestamo.aDto(): SolicitudDto = SolicitudDto(
    id = id,
    equipoId = equipoId,
    ambienteDestino = ambienteDestino,
    proposito = proposito,
    duracionHoras = duracionHoras,
    estado = estado.name,
    razonRechazo = razonRechazo
)
