package com.example.miprestamoslab.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PrestamoDao {

    @Query("SELECT * FROM equipos ORDER BY id ASC")
    fun observarEquipos(): Flow<List<EquipoEntity>>

    @Query("SELECT * FROM equipos WHERE id = :id")
    suspend fun obtenerEquipoPorId(id: Int): EquipoEntity?

    @Query("SELECT * FROM solicitudes ORDER BY id ASC")
    fun observarSolicitudes(): Flow<List<SolicitudEntity>>

    @Query("SELECT * FROM solicitudes WHERE id = :id")
    suspend fun obtenerSolicitudPorId(id: Int): SolicitudEntity?

    @Query("SELECT * FROM solicitudes WHERE equipoId = :equipoId ORDER BY id ASC")
    suspend fun obtenerSolicitudesPorEquipo(equipoId: Int): List<SolicitudEntity>

    @Query("SELECT MAX(id) FROM equipos")
    suspend fun obtenerMaxIdEquipo(): Int?

    @Query("SELECT MAX(id) FROM solicitudes")
    suspend fun obtenerMaxIdSolicitud(): Int?

    @Insert
    suspend fun insertarEquipo(equipo: EquipoEntity): Long

    @Insert
    suspend fun insertarSolicitud(solicitud: SolicitudEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarEquiposIniciales(equipos: List<EquipoEntity>)

    /** Upsert de catálogo proveniente del servicio remoto (local-first, Room es la fuente canónica). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEquiposRemotos(equipos: List<EquipoEntity>)

    /** Upsert de solicitudes provenientes del servicio remoto. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarSolicitudesRemotas(solicitudes: List<SolicitudEntity>)

    @Query("UPDATE equipos SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstadoEquipo(id: Int, estado: String)

    @Query("UPDATE equipos SET nombre = :nombre, categoria = :categoria, descripcion = :descripcion WHERE id = :id")
    suspend fun actualizarDatosEquipo(id: Int, nombre: String, categoria: String, descripcion: String)

    @Query("UPDATE solicitudes SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstadoSolicitud(id: Int, estado: String)

    @Query("UPDATE solicitudes SET estado = :estado, razonRechazo = :razon WHERE id = :id")
    suspend fun actualizarSolicitudRechazada(id: Int, estado: String, razon: String)

    @Query("SELECT COUNT(*) FROM equipos")
    suspend fun contarEquipos(): Int

    // --- HU-13: evidencias fotográficas ---

    @Query("SELECT * FROM evidencias WHERE solicitudId = :solicitudId ORDER BY id ASC")
    fun observarEvidencias(solicitudId: Int): Flow<List<EvidenciaEntity>>

    @Insert
    suspend fun insertarEvidencia(evidencia: EvidenciaEntity): Long
}