package com.example.miprestamoslab.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.miprestamoslab.data.repository.InMemoryPrestamoRepository
import com.example.miprestamoslab.model.Equipo
import com.example.miprestamoslab.model.EstadoSolicitud
import com.example.miprestamoslab.model.SolicitudPrestamo
import com.example.miprestamoslab.notification.NotificationHelper

class LoanExpirationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val notificationHelper = NotificationHelper(applicationContext)
        val repository = InMemoryPrestamoRepository()

        try {
            // 1. Obtener las solicitudes desde el StateFlow .value
            val listaSolicitudes: List<SolicitudPrestamo> = repository.solicitudes.value

            // 2. Filtrar únicamente las solicitudes APROBADAS o ENTREGADAS
            val solicitudesActivas = listaSolicitudes.filter {
                it.estado == EstadoSolicitud.APROBADA || it.estado == EstadoSolicitud.ENTREGADA
            }

            for (solicitud in solicitudesActivas) {
                // Obtener el equipo asociado
                val equipo: Equipo? = repository.obtenerEquipo(solicitud.equipoId)

                if (equipo != null) {
                    notificationHelper.showLoanExpirationNotification(
                        loanId = solicitud.id.toString(),
                        equipmentName = equipo.nombre,
                        daysRemaining = 1 // Simulación de vencimiento próximo
                    )
                }
            }
            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }
    }
}