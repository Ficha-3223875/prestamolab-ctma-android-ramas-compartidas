package com.example.miprestamoslab.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.miprestamoslab.data.local.dao.EquipoDao
import com.example.miprestamoslab.data.local.dao.SolicitudPrestamoDao
import com.example.miprestamoslab.data.local.entity.EquipoEntity
import com.example.miprestamoslab.data.local.entity.SolicitudPrestamoEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [EquipoEntity::class, SolicitudPrestamoEntity::class], version = 1, exportSchema = false)
abstract class PrestamoDatabase : RoomDatabase() {
    abstract fun equipoDao(): EquipoDao
    abstract fun solicitudPrestamoDao(): SolicitudPrestamoDao

    companion object {
        @Volatile
        private var INSTANCE: PrestamoDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): PrestamoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PrestamoDatabase::class.java,
                    "prestamo_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        scope.launch(Dispatchers.IO) {
                            val database = INSTANCE ?: return@launch
                            database.equipoDao().insertarEquipos(
                                listOf(
                                    EquipoEntity(1, "Multímetro Digital", "ELECTRONICA", "DISPONIBLE", ""),
                                    EquipoEntity(2, "Kit Arduino Uno", "ELECTRONICA", "DISPONIBLE", ""),
                                    EquipoEntity(3, "Tablet Samsung", "TABLETA", "DISPONIBLE", ""),
                                    EquipoEntity(4, "Cámara DSLR Canon", "CAMARA", "DISPONIBLE", ""),
                                    EquipoEntity(5, "Soldador de Estaño", "HERRAMIENTA", "DISPONIBLE", ""),
                                    EquipoEntity(6, "Teclado Mecánico", "PERIFERICO", "DISPONIBLE", ""),
                                    EquipoEntity(7, "Osciloscopio USB", "ELECTRONICA", "DISPONIBLE", ""),
                                    EquipoEntity(8, "Set Destornilladores", "HERRAMIENTA", "DISPONIBLE", "")
                                )
                            )
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}