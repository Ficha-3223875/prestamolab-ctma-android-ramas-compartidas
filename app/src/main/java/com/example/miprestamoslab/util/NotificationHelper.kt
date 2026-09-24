package com.example.miprestamoslab.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {
    private const val CHANNEL_ID = "prestamo_channel"
    private const val CHANNEL_NAME = "Solicitudes de Préstamo"
    private const val CHANNEL_DESC = "Notificaciones para nuevas solicitudes de préstamo"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    fun mostrarNotificacionNuevaSolicitud(context: Context, solicitudId: Int, equipoId: Int, proposito: String) {
        try {
            createNotificationChannel(context)

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Nueva Solicitud de Préstamo #$solicitudId")
                .setContentText("Equipo #$equipoId: $proposito")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                notify(solicitudId, builder.build())
            }
        } catch (_: Throwable) {
            // Handled for unit tests where Android context methods are not mocked
        }
    }
}
