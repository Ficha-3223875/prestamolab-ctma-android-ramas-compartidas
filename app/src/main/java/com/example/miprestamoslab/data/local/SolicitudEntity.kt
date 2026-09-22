package com.example.miprestamoslab.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.miprestamoslab.model.EstadoSolicitud
import com.example.miprestamoslab.model.SolicitudPrestamo

@Entity(tableName = "solicitudes")
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val estado: String,
    val razonRechazo: String? = null
) {
    fun toModel(): SolicitudPrestamo = SolicitudPrestamo(
        id = id,
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        estado = EstadoSolicitud.valueOf(estado),
        razonRechazo = razonRechazo
    )

    companion object {
        fun fromModel(solicitud: SolicitudPrestamo): SolicitudEntity = SolicitudEntity(
            id = solicitud.id,
            equipoId = solicitud.equipoId,
            ambienteDestino = solicitud.ambienteDestino,
            proposito = solicitud.proposito,
            duracionHoras = solicitud.duracionHoras,
            estado = solicitud.estado.name,
            razonRechazo = solicitud.razonRechazo
        )
    }
}