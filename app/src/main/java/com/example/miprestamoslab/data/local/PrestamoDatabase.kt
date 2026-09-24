package com.example.miprestamoslab.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [EquipoEntity::class, SolicitudEntity::class, EvidenciaEntity::class],
    version = 2,
    exportSchema = false
)
abstract class PrestamoDatabase : RoomDatabase() {
    abstract fun prestamoDao(): PrestamoDao

    companion object {
        private const val DB_NAME = "prestamolab.db"

        /**
         * v1 → v2 (Semana 9): alta de la tabla de evidencias fotográficas.
         * Declarada de forma explícita: los datos existentes no se pierden (sin destrucción).
         */
        private val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `evidencias` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "`solicitudId` INTEGER NOT NULL, " +
                        "`uri` TEXT NOT NULL, " +
                        "`fechaRegistro` INTEGER NOT NULL, " +
                        "`estado` TEXT NOT NULL)"
                )
            }
        }

        @Volatile
        private var INSTANCE: PrestamoDatabase? = null

        fun getInstance(context: Context): PrestamoDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PrestamoDatabase::class.java,
                    DB_NAME
                )
                    .addMigrations(MIGRACION_1_2)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            EquiposIniciales.lista.forEach { equipo ->
                                db.execSQL(
                                    "INSERT OR IGNORE INTO equipos (id, nombre, categoria, estado, descripcion) VALUES (?, ?, ?, ?, ?)",
                                    arrayOf<Any?>(equipo.id, equipo.nombre, equipo.categoria.name, equipo.estado.name, equipo.descripcion)
                                )
                            }
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }

        fun closeInstance() {
            synchronized(this) {
                INSTANCE?.close()
                INSTANCE = null
            }
        }
    }
}