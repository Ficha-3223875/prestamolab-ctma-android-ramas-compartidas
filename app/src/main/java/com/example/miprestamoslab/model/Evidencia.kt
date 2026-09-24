package com.example.miprestamoslab.model

/**
 * Estados del ciclo de vida de una evidencia adjunta (guía, sección 9):
 * LOCAL → SUBIENDO → SINCRONIZADA, con FALLIDA como caso de error recuperable.
 */
enum class EstadoEvidencia {
    LOCAL,
    SUBIENDO,
    SINCRONIZADA,
    FALLIDA
}

/**
 * Evidencia fotográfica asociada a una solicitud.
 *
 * Regla de negocio: **nunca se almacena el Bitmap ni su representación Base64**;
 * se persiste la URI del contenido (que el sistema mantiene accesible) y sus metadatos.
 */
data class Evidencia(
    val id: Int = 0,
    val solicitudId: Int,
    val uri: String,
    val fechaRegistro: Long,
    val estado: EstadoEvidencia = EstadoEvidencia.LOCAL
)
