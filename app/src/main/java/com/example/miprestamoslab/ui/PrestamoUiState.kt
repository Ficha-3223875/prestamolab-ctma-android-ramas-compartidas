package com.example.miprestamoslab.ui

import com.example.miprestamoslab.model.Equipo
import com.example.miprestamoslab.model.SolicitudPrestamo
import com.example.miprestamoslab.model.Usuario

/**
 * Estados de carga del contenido principal (Semana 7).
 * La UI debe representar siempre uno de estos estados: nunca queda "colgada" sin información.
 */
enum class EstadoCarga {
    /** Todavía no llega el primer valor de los flujos del Repository. */
    CARGANDO,

    /** Hay datos para mostrar. */
    CONTENIDO,

    /** Los flujos respondieron correctamente pero no hay registros. */
    VACIO,

    /** El Repository falló; se debe ofrecer un estado recuperable (reintentar), no un crash. */
    ERROR
}

data class PrestamoUiState(
    val equipos: List<Equipo> = emptyList(),
    val solicitudes: List<SolicitudPrestamo> = emptyList(),
    val estadoCarga: EstadoCarga = EstadoCarga.CARGANDO,
    val errorCarga: String? = null,
    val sincronizando: Boolean = false,
    val mensaje: String? = null,
    val mensajeError: String? = null,
    val guardando: Boolean = false,
    val equipoSeleccionado: Equipo? = null,
    val solicitudSeleccionada: SolicitudPrestamo? = null,
    val usuarioAutenticado: Usuario? = null
)
