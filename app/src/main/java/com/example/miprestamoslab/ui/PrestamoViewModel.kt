package com.example.miprestamoslab.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miprestamoslab.data.repository.InMemoryPrestamoRepository
import com.example.miprestamoslab.domain.ambienteValido
import com.example.miprestamoslab.domain.duracionValida
import com.example.miprestamoslab.domain.propositoValido
import com.example.miprestamoslab.model.CategoriaEquipo
import com.example.miprestamoslab.model.EstadoEquipo
import com.example.miprestamoslab.model.EstadoSolicitud
import com.example.miprestamoslab.model.Rol
import com.example.miprestamoslab.model.SolicitudPrestamo
import com.example.miprestamoslab.model.Usuario
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PrestamoViewModel(
    private val repository: InMemoryPrestamoRepository = InMemoryPrestamoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamoUiState())
    val uiState: StateFlow<PrestamoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.equipos.combine(repository.solicitudes) { eq, sol ->
                PrestamoUiState(equipos = eq, solicitudes = sol)
            }.collect { combined ->
                _uiState.update { it.copy(equipos = combined.equipos, solicitudes = combined.solicitudes) }
            }
        }
    }

    // Autenticación (HU_15)
    fun login(correo: String, contrasena: String, onSuccess: () -> Unit) {
        if (correo.isBlank() || contrasena.isBlank()) {
            _uiState.update { it.copy(mensajeError = "Por favor ingrese correo y contraseña") }
            return
        }

        _uiState.update { it.copy(guardando = true, mensajeError = null) }

        if (contrasena == "123456") {
            val rolSimulado = if (correo.contains("encargado")) Rol.ENCARGADO else Rol.APRENDIZ
            val usuario = Usuario(
                id = (1..100).random(),
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

    fun cargarEquipo(equipoId: Int) {
        val equipo = repository.obtenerEquipo(equipoId)
        _uiState.update { it.copy(equipoSeleccionado = equipo) }
    }

    fun cargarSolicitud(solicitudId: Int) {
        val solicitud = repository.obtenerSolicitud(solicitudId)
        _uiState.update { it.copy(solicitudSeleccionada = solicitud) }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensaje = null) }
    }

    fun limpiarSeleccion() {
        _uiState.update { it.copy(equipoSeleccionado = null, solicitudSeleccionada = null) }
    }

    fun crearSolicitud(
        equipoId: Int,
        ambiente: String,
        proposito: String,
        duracion: String,
        onSuccess: () -> Unit
    ) {
        val errores = mutableListOf<String>()
        if (!ambienteValido(ambiente)) errores.add("El ambiente o destino es obligatorio.")
        if (!propositoValido(proposito)) errores.add("El propósito debe tener entre 10 y 180 caracteres.")
        val duracionInt = duracion.toIntOrNull()
        if (duracionInt == null || !duracionValida(duracionInt)) errores.add("La duración debe estar entre 1 y 8 horas.")

        if (errores.isNotEmpty()) {
            _uiState.update { it.copy(mensaje = errores.joinToString("\n")) }
            return
        }

        if (_uiState.value.guardando) return

        _uiState.update { it.copy(guardando = true) }

        val solicitud = SolicitudPrestamo(
            id = 0,
            equipoId = equipoId,
            ambienteDestino = ambiente.trim(),
            proposito = proposito.trim(),
            duracionHoras = duracionInt!!,
            estado = EstadoSolicitud.SOLICITADA
        )

        val resultado = repository.crearSolicitud(solicitud)

        _uiState.update { it.copy(guardando = false) }

        resultado
            .onSuccess {
                _uiState.update { it.copy(mensaje = "Solicitud registrada correctamente") }
                onSuccess()
            }
            .onFailure { error ->
                _uiState.update { it.copy(mensaje = error.message ?: "Error al crear solicitud") }
            }
    }

    fun cancelarSolicitud(solicitudId: Int, onSuccess: () -> Unit = {}) {
        val resultado = repository.cancelarSolicitud(solicitudId)
        resultado
            .onSuccess {
                _uiState.update { it.copy(mensaje = "Solicitud cancelada correctamente") }
                onSuccess()
            }
            .onFailure { error ->
                _uiState.update { it.copy(mensaje = error.message ?: "Error al cancelar solicitud") }
            }
    }

    fun aprobarSolicitud(solicitudId: Int) {
        repository.aprobarSolicitud(solicitudId)
            .onSuccess { _uiState.update { it.copy(mensaje = "Solicitud aprobada correctamente") } }
            .onFailure { error -> _uiState.update { it.copy(mensaje = error.message ?: "Error al aprobar solicitud") } }
    }

    fun rechazarSolicitud(solicitudId: Int, razon: String) {
        repository.rechazarSolicitud(solicitudId, razon)
            .onSuccess { _uiState.update { it.copy(mensaje = "Solicitud rechazada correctamente") } }
            .onFailure { error -> _uiState.update { it.copy(mensaje = error.message ?: "Error al rechazar solicitud") } }
    }

    // --- SPRINT 4: GESTIÓN DE INVENTARIO (HU 10, HU 11, HU 12) ---

    fun agregarEquipo(nombre: String, categoria: CategoriaEquipo, descripcion: String, onSuccess: () -> Unit = {}) {
        repository.agregarEquipo(nombre, categoria, descripcion)
            .onSuccess {
                _uiState.update { it.copy(mensaje = "Equipo agregado correctamente") }
                onSuccess()
            }
            .onFailure { error ->
                _uiState.update { it.copy(mensaje = error.message ?: "Error al agregar equipo") }
            }
    }

    fun editarEquipo(id: Int, nombre: String, categoria: CategoriaEquipo, descripcion: String, onSuccess: () -> Unit = {}) {
        repository.editarEquipo(id, nombre, categoria, descripcion)
            .onSuccess {
                _uiState.update { it.copy(mensaje = "Equipo actualizado correctamente") }
                onSuccess()
            }
            .onFailure { error ->
                _uiState.update { it.copy(mensaje = error.message ?: "Error al actualizar equipo") }
            }
    }

    fun cambiarEstadoEquipo(id: Int, nuevoEstado: EstadoEquipo, onSuccess: () -> Unit = {}) {
        repository.cambiarEstadoEquipo(id, nuevoEstado)
            .onSuccess {
                _uiState.update { it.copy(mensaje = "Estado del equipo actualizado a $nuevoEstado") }
                onSuccess()
            }
            .onFailure { error ->
                _uiState.update { it.copy(mensaje = error.message ?: "Error al cambiar estado") }
            }
    }
}