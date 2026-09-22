package com.example.miprestamoslab.data.repository

import com.example.miprestamoslab.model.CategoriaEquipo
import com.example.miprestamoslab.model.Equipo
import com.example.miprestamoslab.model.EstadoEquipo
import com.example.miprestamoslab.model.SolicitudPrestamo
import kotlinx.coroutines.flow.StateFlow

interface PrestamoRepository {
    val equipos: StateFlow<List<Equipo>>
    val solicitudes: StateFlow<List<SolicitudPrestamo>>

    fun obtenerEquipos(): List<Equipo>
    fun obtenerEquipo(id: Int): Equipo?
    fun obtenerSolicitudes(): List<SolicitudPrestamo>
    fun obtenerSolicitud(id: Int): SolicitudPrestamo?

    suspend fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit>
    suspend fun cancelarSolicitud(id: Int): Result<Unit>
    suspend fun aprobarSolicitud(id: Int): Result<Unit>
    suspend fun rechazarSolicitud(id: Int, razon: String): Result<Unit>

    // --- SPRINT 4: GESTIÓN DE INVENTARIO ---
    suspend fun agregarEquipo(nombre: String, categoria: CategoriaEquipo, descripcion: String): Result<Unit> // HU 10
    suspend fun editarEquipo(id: Int, nombre: String, categoria: CategoriaEquipo, descripcion: String): Result<Unit> // HU 11
    suspend fun cambiarEstadoEquipo(id: Int, nuevoEstado: EstadoEquipo): Result<Unit> // HU 12

    fun liberarRecursos() {}
}