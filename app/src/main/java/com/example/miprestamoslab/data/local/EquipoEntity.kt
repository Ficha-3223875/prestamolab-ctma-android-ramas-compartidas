package com.example.miprestamoslab.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.miprestamoslab.model.CategoriaEquipo
import com.example.miprestamoslab.model.Equipo
import com.example.miprestamoslab.model.EstadoEquipo

@Entity(tableName = "equipos")
data class EquipoEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val categoria: String,
    val estado: String,
    val descripcion: String = ""
) {
    fun toModel(): Equipo = Equipo(
        id = id,
        nombre = nombre,
        categoria = CategoriaEquipo.valueOf(categoria),
        estado = EstadoEquipo.valueOf(estado),
        descripcion = descripcion
    )

    companion object {
        fun fromModel(equipo: Equipo): EquipoEntity = EquipoEntity(
            id = equipo.id,
            nombre = equipo.nombre,
            categoria = equipo.categoria.name,
            estado = equipo.estado.name,
            descripcion = equipo.descripcion
        )
    }
}