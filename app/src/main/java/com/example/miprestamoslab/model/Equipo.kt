package com.example.miprestamoslab.model

data class Equipo(
    val id: Int,
    val nombre: String,
    val categoria: CategoriaEquipo,
    val estado: EstadoEquipo
)