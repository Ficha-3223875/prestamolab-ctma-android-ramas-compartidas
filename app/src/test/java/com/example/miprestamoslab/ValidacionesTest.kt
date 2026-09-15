package com.example.miprestamoslab

import com.example.miprestamoslab.domain.ambienteValido
import com.example.miprestamoslab.domain.duracionValida
import com.example.miprestamoslab.domain.propositoValido
import com.example.miprestamoslab.domain.razonRechazoValida
import org.junit.Assert
import org.junit.Test

class ValidacionesTest {

    @Test
    fun propositoValido_debeAceptarEntre10Y180Caracteres() {
        Assert.assertTrue(propositoValido("Solicitar equipo para realizar práctica de electrónica"))
        Assert.assertTrue(propositoValido("1234567890"))
        Assert.assertFalse(propositoValido("Corto"))
        Assert.assertFalse(propositoValido(""))
    }

    @Test
    fun duracionValida_debeAceptarEntre1Y8Horas() {
        Assert.assertTrue(duracionValida(1))
        Assert.assertTrue(duracionValida(4))
        Assert.assertTrue(duracionValida(8))

        Assert.assertFalse(duracionValida(0))
        Assert.assertFalse(duracionValida(9))
    }

    @Test
    fun ambienteValido_noDebeAceptarTextoVacio() {
        Assert.assertTrue(ambienteValido("Ambiente de Electrónica"))
        Assert.assertTrue(ambienteValido("Laboratorio 204"))

        Assert.assertFalse(ambienteValido(""))
        Assert.assertFalse(ambienteValido("   "))
    }

    @Test
    fun razonRechazoValida_debeAceptarEntre5Y180Caracteres() {
        Assert.assertTrue(razonRechazoValida("Equipo reservado"))
        Assert.assertTrue(razonRechazoValida("No hay disponibilidad para la fecha solicitada."))

        Assert.assertFalse(razonRechazoValida(""))
        Assert.assertFalse(razonRechazoValida("No"))
    }
}