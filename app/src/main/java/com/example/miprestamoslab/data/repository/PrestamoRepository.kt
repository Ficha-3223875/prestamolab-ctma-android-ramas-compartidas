package com.example.miprestamoslab.data.repository

import com.example.miprestamoslab.model.CategoriaEquipo
import com.example.miprestamoslab.model.Equipo
import com.example.miprestamoslab.model.EstadoEquipo
import com.example.miprestamoslab.model.SolicitudPrestamo

interface PrestamoRepository {
    fun obtenerEquipos(): List<Equipo>
    fun obtenerEquipo(id: Int): Equipo?
    fun obtenerSolicitudes(): List<SolicitudPrestamo>
    fun obtenerSolicitud(id: Int): SolicitudPrestamo?
    fun crearSolicitud(solicitud: SolicitudPrestamo): Result<Unit>
    fun cancelarSolicitud(id: Int): Result<Unit>
    fun aprobarSolicitud(id: Int): Result<Unit>
    fun rechazarSolicitud(id: Int, razon: String): Result<Unit>

    // Sprint 4: Gestión de Inventario
    fun agregarEquipo(nombre: String, categoria: CategoriaEquipo, descripcion: String): Result<Unit>
    fun editarEquipo(id: Int, nombre: String, categoria: CategoriaEquipo, descripcion: String): Result<Unit>
    fun cambiarEstadoEquipo(id: Int, nuevoEstado: EstadoEquipo): Result<Unit>
}