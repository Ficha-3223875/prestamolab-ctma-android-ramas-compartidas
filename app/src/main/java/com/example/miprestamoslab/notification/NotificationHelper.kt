package com.example.miprestamoslab.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "loan_expiration_channel"
        const val CHANNEL_NAME = "Avisos de Vencimiento de Préstamos"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones para avisar sobre préstamos próximos a vencer"
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showLoanExpirationNotification(loanId: String, equipmentName: String, daysRemaining: Int) {
        val title = "Aviso de Vencimiento de Préstamo"
        val message = if (daysRemaining <= 0) {
            "El préstamo del equipo '$equipmentName' vence hoy."
        } else {
            "El préstamo del equipo '$equipmentName' vence en $daysRemaining día(s)."
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(loanId.hashCode(), builder.build())
    }
}