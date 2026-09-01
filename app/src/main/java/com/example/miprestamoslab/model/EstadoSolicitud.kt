package com.example.miprestamoslab.model

enum class EstadoSolicitud {
    SOLICITADA,
    APROBADA,
    ENTREGADA,  // HU_08: Entrega física realizada
    DEVUELTA,   // HU_09: Devolución realizada
    RECHAZADA,
    CANCELADA
}