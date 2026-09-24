package com.example.miprestamoslab.data.repository

import com.example.miprestamoslab.data.local.EvidenciaEntity
import com.example.miprestamoslab.data.local.PrestamoDao
import com.example.miprestamoslab.model.Evidencia
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Acceso a las evidencias fotográficas de una solicitud (HU-13).
 * La interfaz aísla a la UI de Room, igual que [PrestamoRepository].
 */
interface EvidenciaRepository {

    fun observarEvidencias(solicitudId: Int): Flow<List<Evidencia>>

    /** Registra la URI seleccionada en el sistema con sus metadatos. */
    suspend fun registrarEvidencia(solicitudId: Int, uri: String): Result<Evidencia>
}

/** Implementación local (Room es la fuente canónica). */
class RoomEvidenciaRepository(
    private val dao: PrestamoDao
) : EvidenciaRepository {

    override fun observarEvidencias(solicitudId: Int): Flow<List<Evidencia>> =
        dao.observarEvidencias(solicitudId).map { lista -> lista.map { it.toModel() } }

    override suspend fun registrarEvidencia(solicitudId: Int, uri: String): Result<Evidencia> =
        runCatching {
            require(uri.isNotBlank()) { "La evidencia no tiene una URI válida" }
            require(solicitudId > 0) { "La evidencia debe estar asociada a una solicitud" }

            val entidad = EvidenciaEntity(
                solicitudId = solicitudId,
                uri = uri,
                fechaRegistro = System.currentTimeMillis()
            )
            val fila = dao.insertarEvidencia(entidad)
            entidad.copy(id = fila.toInt()).toModel()
        }
}

/** Implementación en memoria para pruebas unitarias (misma fidelidad que InMemoryPrestamoRepository). */
class InMemoryEvidenciaRepository : EvidenciaRepository {

    private val flujos = mutableMapOf<Int, MutableStateFlow<List<Evidencia>>>()
    private var siguienteId = 1

    override fun observarEvidencias(solicitudId: Int): Flow<List<Evidencia>> =
        flujos.getOrPut(solicitudId) { MutableStateFlow(emptyList()) }

    override suspend fun registrarEvidencia(solicitudId: Int, uri: String): Result<Evidencia> =
        runCatching {
            require(uri.isNotBlank()) { "La evidencia no tiene una URI válida" }
            require(solicitudId > 0) { "La evidencia debe estar asociada a una solicitud" }

            val evidencia = Evidencia(
                id = siguienteId++,
                solicitudId = solicitudId,
                uri = uri,
                fechaRegistro = System.currentTimeMillis()
            )
            val flujo = flujos.getOrPut(solicitudId) { MutableStateFlow(emptyList()) }
            flujo.value = flujo.value + evidencia
            evidencia
        }
}
