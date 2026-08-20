package com.example.prestamolab.navigation

sealed class Destino(val ruta: String) {
    object Catalogo : Destino("catalogo")
    object MisSolicitudes : Destino("mis_solicitudes")
    object EquipoDetalle : Destino("equipo_detalle/{equipoId}") {
        fun crearRuta(equipoId: Int) = "equipo_detalle/$equipoId"
    }
    object SolicitudForm : Destino("solicitud_form/{equipoId}") {
        fun crearRuta(equipoId: Int) = "solicitud_form/$equipoId"
    }
    object SolicitudDetalle : Destino("solicitud_detalle/{solicitudId}") {
        fun crearRuta(solicitudId: Int) = "solicitud_detalle/$solicitudId"
    }
}