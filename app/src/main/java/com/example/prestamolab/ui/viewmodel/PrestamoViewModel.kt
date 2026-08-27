package com.example.prestamolab.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.prestamolab.data.model.EstadoSolicitud
import com.example.prestamolab.data.model.Rol
import com.example.prestamolab.data.model.SolicitudPrestamo
import com.example.prestamolab.data.model.Usuario
import com.example.prestamolab.data.repository.PrestamoRepository
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
        _uiState.update { currentState ->
            currentState.copy(
                equipos = repository.obtenerEquipos(),
                solicitudes = repository.obtenerSolicitudes()
            )
        }
    }

    // Autenticación (HU_15)
    fun login(correo: String, contrasena: String, onSuccess: () -> Unit) {
        if (correo.isBlank() || contrasena.isBlank()) {
            _uiState.update { it.copy(mensajeError = "Por favor ingrese correo y contraseña") }
            return
        }

        _uiState.update { it.copy(guardando = true, mensajeError = null) }

        // Simulación de autenticación (Criterio 2)
        if (contrasena == "123456") {
            val rolSimulado = if (correo.contains("encargado")) Rol.ENCARGADO else Rol.APRENDIZ
            val usuario = Usuario(
                id = 1,
                correo = correo,
                nombre = "Usuario SENA",
                rol = rolSimulado
            )
            _uiState.update {
                it.copy(usuarioAutenticado = usuario, guardando = false, mensajeError = null)
            }
            onSuccess()
        } else {
            _uiState.update {
                it.copy(guardando = false, mensajeError = "Usuario o contraseña inválidos")
            }
        }
    }

    fun logout() {
        _uiState.update { it.copy(usuarioAutenticado = null) }
    }

    // Reglas de negocio desacopladas (RN-02, RN-03, RN-04)
    fun ambienteValido(ambiente: String): Boolean = ambiente.trim().isNotEmpty()
    fun propositoValido(proposito: String): Boolean = proposito.trim().length in 10..180
    fun duracionValida(horas: Int): Boolean = horas in 1..8

    fun crearSolicitud(
        equipoId: Int,
        ambienteDestino: String,
        proposito: String,
        duracionHoras: Int
    ) {
        if (_uiState.value.guardando) return

        if (!ambienteValido(ambienteDestino)) {
            _uiState.update { it.copy(mensajeError = "El ambiente o destino es obligatorio") }
            return
        }
        if (!propositoValido(proposito)) {
            _uiState.update { it.copy(mensajeError = "El propósito debe tener entre 10 y 180 caracteres") }
            return
        }
        if (!duracionValida(duracionHoras)) {
            _uiState.update { it.copy(mensajeError = "La duración debe estar entre 1 y 8 horas") }
            return
        }

        _uiState.update { it.copy(guardando = true, mensajeError = null) }

        val nuevaSolicitud = SolicitudPrestamo(
            id = 0,
            equipoId = equipoId,
            ambienteDestino = ambienteDestino,
            proposito = proposito,
            duracionHoras = duracionHoras,
            estado = EstadoSolicitud.SOLICITADA
        )

        val resultado = repository.crearSolicitud(nuevaSolicitud)

        resultado.onSuccess {
            cargarDatos()
            _uiState.update {
                it.copy(guardando = false, mensajeExito = "Solicitud registrada con éxito")
            }
        }.onFailure { error ->
            _uiState.update {
                it.copy(guardando = false, mensajeError = error.message ?: "Error al guardar")
            }
        }
    }

    fun cancelarSolicitud(solicitudId: Int) {
        val resultado = repository.cancelarSolicitud(solicitudId)
        resultado.onSuccess {
            cargarDatos()
            _uiState.update { it.copy(mensajeExito = "Solicitud cancelada correctamente") }
        }.onFailure { error ->
            _uiState.update { it.copy(mensajeError = error.message ?: "Error al cancelar") }
        }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(mensajeError = null, mensajeExito = null) }
    }
}