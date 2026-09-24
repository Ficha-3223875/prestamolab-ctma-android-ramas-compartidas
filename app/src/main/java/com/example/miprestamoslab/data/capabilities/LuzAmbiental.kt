package com.example.miprestamoslab.data.capabilities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fuente de lectura de luz ambiente (HU-14, Semana 9).
 * Interfaz para poder sustituirla por una doble falsa en pruebas.
 */
interface FuenteLuz {

    /** Luz ambiente en lux; `null` mientras no haya lectura o si el dispositivo no tiene sensor. */
    val lux: StateFlow<Float?>

    fun iniciar()

    fun detener()
}

/**
 * Capacidad física adicional: sensor de luz ambiental del dispositivo.
 *
 * Justificación técnica (obligatoria según la guía, sección 9):
 * - **Propósito:** mostrar la condición de iluminación del espacio donde se retira o usa el equipo,
 *   como dato contextual del préstamo.
 * - **API utilizada:** [SensorManager] con `Sensor.TYPE_LIGHT` (sensor integrado).
 * - **Permisos requeridos:** ninguno. Android no exige permiso para leer sensores integrados,
 *   por lo que cumple el principio de mínimo privilegio.
 * - **Privacidad:** la lectura se mantiene en memoria de la app, no se persiste, no se envía a
 *   ningún servicio ni se combina con datos personales.
 * - **Errores:** si el dispositivo no expone el sensor, [lux] permanece en `null` y la UI muestra
 *   "no disponible" sin afectar el resto de la funcionalidad.
 */
class LuzAmbientalSensor(context: Context) : FuenteLuz, SensorEventListener {

    private val _lux = MutableStateFlow<Float?>(null)
    override val lux: StateFlow<Float?> = _lux.asStateFlow()

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val sensorLuz: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    private val registrado = MutableStateFlow(false)

    override fun iniciar() {
        val sensor = sensorLuz ?: return
        if (!registrado.value) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
            registrado.value = true
        }
    }

    override fun detener() {
        if (registrado.value) {
            sensorManager.unregisterListener(this)
            registrado.value = false
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        _lux.value = event.values.firstOrNull()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}
