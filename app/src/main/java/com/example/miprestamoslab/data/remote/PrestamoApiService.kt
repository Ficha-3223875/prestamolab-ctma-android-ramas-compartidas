package com.example.miprestamoslab.data.remote

import retrofit2.http.*

data class EquipoDto(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val estado: String,
    val descripcion: String,
    val fotoUri: String? = null,
    val metadata: String? = null
)

data class SolicitudPrestamoDto(
    val id: Int,
    val equipoId: Int,
    val ambienteDestino: String,
    val proposito: String,
    val duracionHoras: Int,
    val estado: String,
    val razonRechazo: String? = null,
    val fotoDevolucionUri: String? = null,
    val metadata: String? = null
)

interface PrestamoApiService {
    @GET("equipos")
    suspend fun obtenerEquipos(): List<EquipoDto>

    @GET("solicitudes")
    suspend fun obtenerSolicitudes(): List<SolicitudPrestamoDto>

    @POST("solicitudes")
    suspend fun crearSolicitud(@Body solicitud: SolicitudPrestamoDto): SolicitudPrestamoDto

    @PUT("solicitudes/{id}")
    suspend fun actualizarSolicitud(@Path("id") id: Int, @Body solicitud: SolicitudPrestamoDto): SolicitudPrestamoDto
}