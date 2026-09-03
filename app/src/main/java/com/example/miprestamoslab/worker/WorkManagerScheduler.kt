package com.example.miprestamoslab.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkManagerScheduler {

    private const val WORK_NAME = "LoanExpirationPeriodicWork"

    fun scheduleLoanExpirationCheck(context: Context) {
        val expirationWorkRequest = PeriodicWorkRequestBuilder<LoanExpirationWorker>(
            12, TimeUnit.HOURS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            expirationWorkRequest
        )
    }
}