package com.example.prestamolab.data.model

enum class Rol {
    APRENDIZ,
    ENCARGADO
}

data class Usuario(
    val id: Int,
    val correo: String,
    val nombre: String,
    val rol: Rol
)