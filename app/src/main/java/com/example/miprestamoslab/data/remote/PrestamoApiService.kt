package com.example.miprestamoslab.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Contrato REST de PréstamoLab CTMA (Semana 8).
 *
 * Rutas esperadas del servicio:
 * ```
 * GET  /equipos      -> [EquipoDto]
 * GET  /solicitudes  -> [SolicitudDto]
 * POST /solicitudes  -> SolicitudDto
 * ```
 * La URL base se inyecta desde `BuildConfig.BASE_URL` (ambiente dev/stage/prod),
 * nunca desde código ni con secretos versionados.
 */
interface PrestamoApiService {

    @GET("equipos")
    suspend fun obtenerEquipos(): List<EquipoDto>

    @GET("solicitudes")
    suspend fun obtenerSolicitudes(): List<SolicitudDto>

    @POST("solicitudes")
    suspend fun crearSolicitud(@Body solicitud: SolicitudDto): SolicitudDto
}
