package com.example.prestamolab.data.model

enum class CategoriaEquipo {
    ELECTRONICA,
    HERRAMIENTAS,
    DISPOSITIVOS,
    PERIFERICOS
}

enum class EstadoEquipo {
    DISPONIBLE,
    RESERVADO,
    PRESTADO
}

enum class EstadoSolicitud {
    SOLICITADA,
    APROBADA,
    ENTREGADA,
    DEVUELTA,
    CANCELADA,
    RECHAZADA
}