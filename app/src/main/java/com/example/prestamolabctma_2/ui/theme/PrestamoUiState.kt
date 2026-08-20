package com.example.prestamolabctma_2.ui

import com.example.prestamolabctma_2.model.Equipo
import com.example.prestamolabctma_2.model.SolicitudPrestamo

data class PrestamoUiState(
    val equipos: List<Equipo> = emptyList(),
    val solicitudes: List<SolicitudPrestamo> = emptyList(),
    val mensaje: String? = null,
    val guardando: Boolean = false
)