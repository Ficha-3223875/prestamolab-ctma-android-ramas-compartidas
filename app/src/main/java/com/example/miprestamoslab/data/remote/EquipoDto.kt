package com.example.miprestamoslab.data.remote

import com.example.miprestamoslab.model.CategoriaEquipo
import com.example.miprestamoslab.model.Equipo
import com.example.miprestamoslab.model.EstadoEquipo

/**
 * DTO de transporte para /equipos (Semana 8).
 *
 * Reglas de la guía:
 * - El DTO solo representa el contrato JSON: todos los campos son anulables porque el
 *   servicio remoto puede omitirlos o cambiar sin previo aviso.
 * - El mapeo DTO -> dominio es la única forma en que el resto de la app conoce el JSON.
 */
data class EquipoDto(
    val id: Int? = null,
    val nombre: String? = null,
    val categoria: String? = null,
    val estado: String? = null,
    val descripcion: String? = null
) {
    /**
     * Convierte el DTO a modelo de dominio.
     * @throws IllegalArgumentException si la respuesta no cumple el contrato esperado.
     */
    fun aDominio(): Equipo {
        val idEquipo = requireNotNull(id) { "El campo 'id' es obligatorio" }
        val nombreEquipo = requireNotNull(nombre) { "El campo 'nombre' es obligatorio" }
        val categoriaEquipo = requireNotNull(categoria) { "El campo 'categoria' es obligatorio" }
        val estadoEquipo = estado ?: EstadoEquipo.DISPONIBLE.name

        return Equipo(
            id = idEquipo,
            nombre = nombreEquipo,
            categoria = CategoriaEquipo.valueOf(categoriaEquipo),
            estado = EstadoEquipo.valueOf(estadoEquipo),
            descripcion = descripcion.orEmpty()
        )
    }
}

/**
 * Convierte un modelo de dominio a DTO para envío al servicio.
 * Nunca se envía el modelo de dominio directamente: eso acopla la UI a la API.
 */
fun Equipo.aDto(): EquipoDto = EquipoDto(
    id = id,
    nombre = nombre,
    categoria = categoria.name,
    estado = estado.name,
    descripcion = descripcion
)
