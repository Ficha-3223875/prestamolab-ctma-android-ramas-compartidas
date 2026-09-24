package com.example.miprestamoslab

import com.example.miprestamoslab.data.remote.PrestamoApiService
import com.example.miprestamoslab.data.remote.RedError
import com.example.miprestamoslab.data.remote.RetrofitFactory
import com.example.miprestamoslab.data.remote.TimeoutsRed
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Semana 8 — pruebas de integración del cliente HTTP con MockWebServer.
 * Cubren los casos pedidos por la guía: éxito, errores HTTP, respuesta inválida y timeout.
 */
class PrestamoApiServiceTest {

    @get:Rule
    val server = MockWebServer()

    private lateinit var api: PrestamoApiService

    @Before
    fun configurar() {
        api = RetrofitFactory.crear(
            baseUrl = server.url("/").toString(),
            timeouts = TimeoutsRed(conexionSegundos = 1, lecturaSegundos = 1, escrituraSegundos = 1)
        )
    }

    @After
    fun cerrar() {
        runCatching { server.shutdown() }
    }

    @Test
    fun dadoServidorOk_cuandoObtenerEquipos_entoncesMapeaLaRespuestaADominio() = runTest {
        // Arrange
        server.enqueue(
            MockResponse().setBody(
                """[{"id":1,"nombre":"Multimetro Digital","categoria":"ELECTRONICA",
                   "estado":"DISPONIBLE","descripcion":"7 digitos"}]""".trimIndent()
            )
        )

        // Act
        val equipos = api.obtenerEquipos().map { it.aDominio() }

        // Assert
        assertEquals(1, equipos.size)
        assertEquals("Multimetro Digital", equipos[0].nombre)
        assertEquals(1, equipos[0].id)
    }

    @Test
    fun dadoServidor404_cuandoObtenerEquipos_entoncesSeMapeaAErrorHttp() = runTest {
        // Arrange
        server.enqueue(MockResponse().setResponseCode(404).setBody("{}"))

        // Act
        val error = runCatching { api.obtenerEquipos() }.exceptionOrNull()

        // Assert
        val redError = RedError.desde(error!!)
        assertTrue(redError is RedError.Http)
        assertEquals(404, (redError as RedError.Http).codigo)
        assertTrue(redError.message!!.contains("404"))
    }

    @Test
    fun dadoServidor500_cuandoObtenerSolicitudes_entoncesSeMapeaAErrorDeServidor() = runTest {
        // Arrange
        server.enqueue(MockResponse().setResponseCode(500).setBody(""))

        // Act
        val error = runCatching { api.obtenerSolicitudes() }.exceptionOrNull()

        // Assert
        val redError = RedError.desde(error!!)
        assertTrue(redError is RedError.Http)
        assertEquals(500, (redError as RedError.Http).codigo)
        assertTrue(redError.message!!.contains("servidor"))
    }

    @Test
    fun dadoCuerpoInesperado_cuandoObtenerEquipos_entoncesSeMapeaARespuestaInvalida() = runTest {
        // Arrange: responde 200 pero con un objeto en lugar de una lista
        server.enqueue(MockResponse().setBody("""{"id":1,"nombre":"no es una lista"}"""))

        // Act
        val error = runCatching { api.obtenerEquipos() }.exceptionOrNull()

        // Assert
        assertTrue(RedError.desde(error!!) is RedError.RespuestaInvalida)
    }

    @Test
    fun dadoServidorSinRespuesta_cuandoExpireElTimeout_entoncesSeMapeaATimeout() = runTest {
        // Arrange: el servidor nunca responde
        server.enqueue(MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE))

        // Act
        val error = runCatching { api.obtenerEquipos() }.exceptionOrNull()

        // Assert: la UI recibe un error tipado, nunca queda bloqueada en "Cargando"
        assertTrue(RedError.desde(error!!) is RedError.Timeout)
    }

    @Test
    fun dadoServidorCaido_cuandoNoHayConexion_entoncesSeMapeaASinConexion() = runTest {
        // Arrange
        server.shutdown()

        // Act
        val error = runCatching { api.obtenerEquipos() }.exceptionOrNull()

        // Assert
        assertTrue(RedError.desde(error!!) is RedError.SinConexion)
    }

    @Test
    fun dadoContratoIncumplido_cuandoSeMapeaDTO_entoncesSeRechazaLaRespuesta() = runTest {
        // Arrange: falta el campo obligatorio 'nombre'
        server.enqueue(MockResponse().setBody("""[{"id":9,"categoria":"CAMARA"}]"""))

        // Act
        val error = runCatching { api.obtenerEquipos().map { it.aDominio() } }.exceptionOrNull()

        // Assert
        val redError = RedError.desde(error!!)
        assertTrue(redError is RedError.RespuestaInvalida)
        assertTrue(redError.message!!.contains("nombre"))
    }
}
