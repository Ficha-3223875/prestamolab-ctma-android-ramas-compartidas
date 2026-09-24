package com.example.miprestamoslab.data.local.dao

import androidx.room.*
import com.example.miprestamoslab.data.local.entity.SolicitudPrestamoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudPrestamoDao {
    @Query("SELECT * FROM solicitudes_prestamo")
    fun obtenerSolicitudesFlow(): Flow<List<SolicitudPrestamoEntity>>

    @Query("SELECT * FROM solicitudes_prestamo")
    fun obtenerSolicitudes(): List<SolicitudPrestamoEntity>

    @Query("SELECT * FROM solicitudes_prestamo WHERE id = :id")
    fun obtenerSolicitudPorId(id: Int): SolicitudPrestamoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarSolicitud(solicitud: SolicitudPrestamoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarSolicitudes(solicitudes: List<SolicitudPrestamoEntity>)

    @Update
    fun actualizarSolicitud(solicitud: SolicitudPrestamoEntity)
}