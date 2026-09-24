package com.example.miprestamoslab.data.local.dao

import androidx.room.*
import com.example.miprestamoslab.data.local.entity.EquipoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipoDao {
    @Query("SELECT * FROM equipos")
    fun obtenerEquiposFlow(): Flow<List<EquipoEntity>>

    @Query("SELECT * FROM equipos")
    fun obtenerEquipos(): List<EquipoEntity>

    @Query("SELECT * FROM equipos WHERE id = :id")
    fun obtenerEquipoPorId(id: Int): EquipoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarEquipos(equipos: List<EquipoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarEquipo(equipo: EquipoEntity)

    @Update
    fun actualizarEquipo(equipo: EquipoEntity)
}