package com.example.prestamolabctma_2.model

enum class CategoriaEquipo {
    ELECTRONICA,
    HERRAMIENTAS,
    COMPUTACION,
    MEDICION
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