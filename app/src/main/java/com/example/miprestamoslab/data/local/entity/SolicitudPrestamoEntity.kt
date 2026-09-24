package com.example.miprestamoslab.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "solicitudes_prestamo")
data class SolicitudPrestamoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val estado: String,
    val razonRechazo: String? = null,
    val fotoDevolucionUri: String? = null,
    val syncStatus: String = "SYNCED",
    val metadata: String? = null
)