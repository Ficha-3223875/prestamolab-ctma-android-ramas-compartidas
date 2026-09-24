package com.example.miprestamoslab.data.remote

import com.example.miprestamoslab.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Timeouts explícitos (Semana 8): un cliente sin timeout dejan a la UI bloqueada en "Cargando".
 * Se parametrizan para poder probar el comportamiento de timeout con MockWebServer.
 */
data class TimeoutsRed(
    val conexionSegundos: Long = 10,
    val lecturaSegundos: Long = 15,
    val escrituraSegundos: Long = 15
)

/**
 * Punto único de construcción del cliente HTTP.
 *
 * - La URL base proviene de `BuildConfig.BASE_URL`, configurable por ambiente
 *   (`-PPRESTAMOLAB_BASE_URL=...`) sin versionar secretos.
 * - OkHttp aplica timeouts y reintenta conexiones fallidas de forma controlada.
 */
object RetrofitFactory {

    fun crear(
        baseUrl: String = BuildConfig.BASE_URL,
        timeouts: TimeoutsRed = TimeoutsRed()
    ): PrestamoApiService {
        val cliente = OkHttpClient.Builder()
            .connectTimeout(timeouts.conexionSegundos, TimeUnit.SECONDS)
            .readTimeout(timeouts.lecturaSegundos, TimeUnit.SECONDS)
            .writeTimeout(timeouts.escrituraSegundos, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        return Retrofit.Builder()
            .baseUrl(normalizar(baseUrl))
            .client(cliente)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PrestamoApiService::class.java)
    }

    /** Retrofit exige que la URL base termine en "/". */
    private fun normalizar(url: String): String = if (url.endsWith("/")) url else "$url/"
}
