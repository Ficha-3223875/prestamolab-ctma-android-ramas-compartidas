package com.example.miprestamoslab.data.remote

import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Errores de red tipados (Semana 8).
 *
 * La UI jamás ve un stack trace: el Repository traduce cualquier fallo a uno de estos
 * casos y la capa de presentación muestra un mensaje recuperable.
 */
sealed class RedError(message: String) : Exception(message) {

    /** Sin conectividad, host desconocido o fallo de conexión. */
    class SinConexion : RedError("Sin conexión con el servidor. Verifica tu red.")

    /** El servidor no respondió dentro del tiempo configurado. */
    class Timeout : RedError("El servidor tardó demasiado en responder.")

    /** Respuesta HTTP 4xx/5xx (401, 404, 500…). */
    class Http(val codigo: Int) : RedError(
        when {
            codigo == 401 -> "No autorizado (401): la sesión no es válida."
            codigo == 404 -> "Recurso no encontrado (404)."
            codigo in 500..599 -> "Error del servidor ($codigo). Intenta más tarde."
            else -> "Error HTTP $codigo."
        }
    )

    /** 200 OK pero el cuerpo no cumple el contrato JSON esperado. */
    class RespuestaInvalida(detalle: String) : RedError("Respuesta inválida del servidor: $detalle")

    /** Cualquier otro fallo no clasificado. */
    class Desconocido(detalle: String) : RedError("Error inesperado: $detalle")

    companion object {
        /** Traduce cualquier excepción del cliente HTTP a un [RedError] tipado. */
        fun desde(throwable: Throwable): RedError = when (throwable) {
            is RedError -> throwable
            is SocketTimeoutException -> Timeout()
            is UnknownHostException -> SinConexion()
            is HttpException -> Http(throwable.code())
            is JsonParseException -> RespuestaInvalida(throwable.message ?: "JSON ilegible")
            is IOException -> SinConexion()
            is IllegalArgumentException, is IllegalStateException ->
                RespuestaInvalida(throwable.message ?: "Contrato incumplido")
            else -> Desconocido(throwable.message ?: throwable.javaClass.simpleName)
        }
    }
}
