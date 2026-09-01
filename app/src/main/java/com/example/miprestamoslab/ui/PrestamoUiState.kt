package com.example.miprestamoslab.ui

import com.example.miprestamoslab.model.Equipo
import com.example.miprestamoslab.model.SolicitudPrestamo
import com.example.miprestamoslab.model.Usuario

data class PrestamoUiState(
    val equipos: List<Equipo> = emptyList(),
    val solicitudes: List<SolicitudPrestamo> = emptyList(),
    val mensaje: String? = null,
    val mensajeError: String? = null,
    val guardando: Boolean = false,
    val equipoSeleccionado: Equipo? = null,
    val solicitudSeleccionada: SolicitudPrestamo? = null,
    val usuarioAutenticado: Usuario? = null
)
