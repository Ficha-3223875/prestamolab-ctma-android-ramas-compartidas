package com.example.miprestamoslab.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipos")
data class EquipoEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val categoria: String,
    val estado: String,
    val descripcion: String,
    val fotoUri: String? = null,
    val syncStatus: String = "SYNCED",
    val metadata: String? = null
)