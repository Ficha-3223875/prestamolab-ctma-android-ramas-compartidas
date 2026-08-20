package com.example.prestamolabctma_2.ui

import androidx.lifecycle.ViewModel
import com.example.prestamolabctma_2.data.PrestamoRepository
import com.example.prestamolabctma_2.model.EstadoSolicitud
import com.example.prestamolabctma_2.model.SolicitudPrestamo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PrestamoViewModel(
    private val repository: PrestamoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamoUiState())
    val uiState: StateFlow<PrestamoUiState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        _uiState.update {
            it.copy(
                equipos = repository.obtenerEquipos(),
                solicitudes = repository.obtenerSolicitudes()
            )
        }
    }

    fun propositoValido(texto: String): Boolean = texto.trim().length in 10..180

    fun duracionValida(horas: Int): Boolean = horas in 1..8

    fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int
    ): Boolean {
        if (_uiState.value.guardando) return false

        if (ambienteDestino.isBlank() || !propositoValido(proposito) || !duracionValida(duracionHoras)) {
            _uiState.update { it.copy(mensaje = "Datos del formulario inválidos.") }
            return false
        }

        _uiState.update { it.copy(guardando = true) }

        val nuevaSolicitud = SolicitudPrestamo(
            id = (repository.obtenerSolicitudes().size + 1),
            equipoId = equipoId,
            ambienteDestino = ambienteDestino.trim(),
            proposito = proposito.trim(),
            duracionHoras = duracionHoras,
            estado = EstadoSolicitud.SOLICITADA
        )

        val resultado = repository.crearSolicitud(nuevaSolicitud)

        return resultado.fold(
            onSuccess = {
                cargarDatos()
                _uiState.update { it.copy(guardando = false, mensaje = "Solicitud creada con éxito.") }
                true
            },
            onFailure = { error ->
                _uiState.update { it.copy(guardando = false, mensaje = error.message) }
                false
            }
        )
    }

    fun cancelarSolicitud(solicitudId: Int) {
        val resultado = repository.cancelarSolicitud(solicitudId)
        resultado.fold(
            onSuccess = {
                cargarDatos()
                _uiState.update { it.copy(mensaje = "Solicitud cancelada.") }
            },
            onFailure = { error ->
                _uiState.update { it.copy(mensaje = error.message) }
            }
        )
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensaje = null) }
    }
}