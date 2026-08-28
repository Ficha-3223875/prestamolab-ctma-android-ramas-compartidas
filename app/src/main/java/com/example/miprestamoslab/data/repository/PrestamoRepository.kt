package com.example.miprestamoslab.data.repository

import com.example.miprestamoslab.model.Equipo
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
}