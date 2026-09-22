package com.example.miprestamoslab.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [EquipoEntity::class, SolicitudEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PrestamoDatabase : RoomDatabase() {
    abstract fun prestamoDao(): PrestamoDao

    companion object {
        private const val DB_NAME = "prestamolab.db"

        @Volatile
        private var INSTANCE: PrestamoDatabase? = null

        fun getInstance(context: Context): PrestamoDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PrestamoDatabase::class.java,
                    DB_NAME
                )
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