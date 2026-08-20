package com.example.prestamolab.ui.viewmodel

import com.example.prestamolab.data.model.Equipo
import com.example.prestamolab.data.model.SolicitudPrestamo

data class PrestamoUiState(
    val equipos: List<Equipo> = emptyList(),
    val solicitudes: List<SolicitudPrestamo> = emptyList(),
    val mensajeError: String? = null,
    val mensajeExito: String? = null,
    val guardando: Boolean = false
)