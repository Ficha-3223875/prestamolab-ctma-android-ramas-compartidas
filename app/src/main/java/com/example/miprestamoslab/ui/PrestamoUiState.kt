package com.example.miprestamoslab.ui

import com.example.miprestamoslab.model.Equipo
import com.example.miprestamoslab.model.SolicitudPrestamo
import com.example.miprestamoslab.model.Usuario

sealed interface ListadoUiState {
    object Cargando : ListadoUiState
    data class Contenido(val equipos: List<Equipo>) : ListadoUiState
    object Vacio : ListadoUiState
    data class Error(val mensaje: String) : ListadoUiState
}

sealed interface OperacionUiState {
    object Inactiva : OperacionUiState
    object EnCurso : OperacionUiState
    object Exitosa : OperacionUiState
    data class Fallida(val error: String) : OperacionUiState
}

data class PrestamoUiState(
    val listadoEquipos: ListadoUiState = ListadoUiState.Cargando,
    val solicitudes: List<SolicitudPrestamo> = emptyList(),
    val operacionState: OperacionUiState = OperacionUiState.Inactiva,
    val mensaje: String? = null,
    val equipoSeleccionado: Equipo? = null,
    val solicitudSeleccionada: SolicitudPrestamo? = null,
    val usuarioAutenticado: Usuario? = null,
    // Compatibilidad temporal
    val guardando: Boolean = false,
    val mensajeError: String? = null
)
