package com.example.miprestamoslab.data.remote

import com.example.miprestamoslab.data.local.EquipoEntity
import com.example.miprestamoslab.data.local.PrestamoDao
import com.example.miprestamoslab.data.local.SolicitudEntity
import kotlinx.coroutines.CancellationException

/**
 * Sincronización local-first (Semana 8).
 *
 * Estrategia:
 * 1. Room sigue siendo la fuente local canónica: la UI siempre lee de Room.
 * 2. Esta clase descarga el estado del servicio remoto y lo persiste en Room.
 * 3. Cualquier fallo se expone como [RedError] tipado (nunca llega un stack trace a la UI).
 * 4. La cancelación de la corrutina se propaga: no se convierte en "error de red".
 */
class SincronizadorRemoto(
    private val api: PrestamoApiService,
    private val dao: PrestamoDao
) {

    /** Descarga el catálogo remoto y lo persiste. Devuelve la cantidad de equipos sincronizados. */
    suspend fun sincronizarEquipos(): Result<Int> = ejecutar {
        val remotos = api.obtenerEquipos()
        val dominio = remotos.map { it.aDominio() }
        dao.insertarEquiposRemotos(dominio.map { EquipoEntity.fromModel(it) })
        dominio.size
    }

    /** Descarga las solicitudes remotas y las persiste. Devuelve la cantidad sincronizada. */
    suspend fun sincronizarSolicitudes(): Result<Int> = ejecutar {
        val remotas = api.obtenerSolicitudes()
        // Solo se persisten solicitudes con identificador del servidor: las locales (id 0)
        // aún no están publicadas y reinsertarlas duplicaría registros en Room.
        val dominio = remotas.map { it.aDominio() }.filter { it.id > 0 }
        dao.insertarSolicitudesRemotas(dominio.map { SolicitudEntity.fromModel(it) })
        dominio.size
    }

    private suspend inline fun <T> ejecutar(accion: suspend () -> T): Result<T> = try {
        Result.success(accion())
    } catch (cancelacion: CancellationException) {
        throw cancelacion
    } catch (error: Throwable) {
        Result.failure(RedError.desde(error))
    }
}
