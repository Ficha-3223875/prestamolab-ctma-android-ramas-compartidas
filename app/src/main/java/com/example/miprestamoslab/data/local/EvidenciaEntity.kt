package com.example.miprestamoslab.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.miprestamoslab.model.EstadoEvidencia
import com.example.miprestamoslab.model.Evidencia

/**
 * Persistencia de la evidencia fotográfica (HU-13, Semana 9).
 * Solo URI + metadatos: el bitmap jamás entra a la base de datos.
 */
@Entity(tableName = "evidencias")
data class EvidenciaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val solicitudId: Int,
    val uri: String,
    val fechaRegistro: Long,
    val estado: String = EstadoEvidencia.LOCAL.name
) {
    fun toModel(): Evidencia = Evidencia(
        id = id,
        solicitudId = solicitudId,
        uri = uri,
        fechaRegistro = fechaRegistro,
        estado = EstadoEvidencia.valueOf(estado)
    )

    companion object {
        fun fromModel(evidencia: Evidencia): EvidenciaEntity = EvidenciaEntity(
            id = evidencia.id,
            solicitudId = evidencia.solicitudId,
            uri = evidencia.uri,
            fechaRegistro = evidencia.fechaRegistro,
            estado = evidencia.estado.name
        )
    }
}
