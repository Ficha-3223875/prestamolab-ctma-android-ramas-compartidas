package com.example.miprestamoslab.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.miprestamoslab.data.local.PrestamoDatabase
import com.example.miprestamoslab.data.local.SesionDataStore
import com.example.miprestamoslab.data.local.SesionStore
import com.example.miprestamoslab.data.repository.PrestamoRepository
import com.example.miprestamoslab.data.repository.RoomPrestamoRepository
import com.example.miprestamoslab.domain.ambienteValido
import com.example.miprestamoslab.domain.duracionValida
import com.example.miprestamoslab.domain.propositoValido
import com.example.miprestamoslab.model.CategoriaEquipo
import com.example.miprestamoslab.model.EstadoEquipo
import com.example.miprestamoslab.model.EstadoSolicitud
import com.example.miprestamoslab.model.Rol
import com.example.miprestamoslab.model.SolicitudPrestamo
import com.example.miprestamoslab.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PrestamoViewModel(
    private val repository: PrestamoRepository,
    private val sesionStore: SesionStore
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

        viewModelScope.launch {
            sesionStore.sesion.collect { usuario ->
                _uiState.update { it.copy(usuarioAutenticado = usuario) }
            }
        }
    }

    // Autenticación (HU_15)
    fun login(correo: String, contrasena: String, onSuccess: () -> Unit) {
        if (correo.isBlank() || contrasena.isBlank()) {
            _uiState.update { it.copy(mensajeError = "Por favor ingrese correo y contraseña") }
            return
        }

        if (_uiState.value.guardando) return

        _uiState.update { it.copy(guardando = true, mensajeError = null) }

        viewModelScope.launch {
            if (contrasena == "123456") {
                val rolSimulado = if (correo.contains("encargado")) Rol.ENCARGADO else Rol.APRENDIZ
                val usuario = Usuario(
                    id = (1..100).random(),
                    correo = correo,
                    nombre = "Usuario SENA",
                    rol = rolSimulado
                )
                sesionStore.guardarSesion(usuario)
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
    }

    fun logout() {
        _uiState.update { it.copy(usuarioAutenticado = null) }
        viewModelScope.launch {
            sesionStore.limpiarSesion()
        }
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

        viewModelScope.launch {
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
    }

    fun cancelarSolicitud(solicitudId: Int, onSuccess: () -> Unit = {}) {
        if (_uiState.value.guardando) return
        _uiState.update { it.copy(guardando = true) }

        viewModelScope.launch {
            val resultado = repository.cancelarSolicitud(solicitudId)
            _uiState.update { it.copy(guardando = false) }

            resultado
                .onSuccess {
                    _uiState.update { it.copy(mensaje = "Solicitud cancelada correctamente") }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(mensaje = error.message ?: "Error al cancelar solicitud") }
                }
        }
    }

    fun aprobarSolicitud(solicitudId: Int) {
        if (_uiState.value.guardando) return
        _uiState.update { it.copy(guardando = true) }

        viewModelScope.launch {
            val resultado = repository.aprobarSolicitud(solicitudId)
            _uiState.update { it.copy(guardando = false) }

            resultado
                .onSuccess { _uiState.update { it.copy(mensaje = "Solicitud aprobada correctamente") } }
                .onFailure { error -> _uiState.update { it.copy(mensaje = error.message ?: "Error al aprobar solicitud") } }
        }
    }

    fun rechazarSolicitud(solicitudId: Int, razon: String) {
        if (_uiState.value.guardando) return
        _uiState.update { it.copy(guardando = true) }

        viewModelScope.launch {
            val resultado = repository.rechazarSolicitud(solicitudId, razon)
            _uiState.update { it.copy(guardando = false) }

            resultado
                .onSuccess { _uiState.update { it.copy(mensaje = "Solicitud rechazada correctamente") } }
                .onFailure { error -> _uiState.update { it.copy(mensaje = error.message ?: "Error al rechazar solicitud") } }
        }
    }

    // --- SPRINT 4: GESTIÓN DE INVENTARIO (HU 10, HU 11, HU 12) ---

    fun agregarEquipo(nombre: String, categoria: CategoriaEquipo, descripcion: String, onSuccess: () -> Unit = {}) {
        if (_uiState.value.guardando) return
        _uiState.update { it.copy(guardando = true) }

        viewModelScope.launch {
            val resultado = repository.agregarEquipo(nombre, categoria, descripcion)
            _uiState.update { it.copy(guardando = false) }

            resultado
                .onSuccess {
                    _uiState.update { it.copy(mensaje = "Equipo agregado correctamente") }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(mensaje = error.message ?: "Error al agregar equipo") }
                }
        }
    }

    fun editarEquipo(id: Int, nombre: String, categoria: CategoriaEquipo, descripcion: String, onSuccess: () -> Unit = {}) {
        if (_uiState.value.guardando) return
        _uiState.update { it.copy(guardando = true) }

        viewModelScope.launch {
            val resultado = repository.editarEquipo(id, nombre, categoria, descripcion)
            _uiState.update { it.copy(guardando = false) }

            resultado
                .onSuccess {
                    _uiState.update { it.copy(mensaje = "Equipo actualizado correctamente") }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(mensaje = error.message ?: "Error al actualizar equipo") }
                }
        }
    }

    fun cambiarEstadoEquipo(id: Int, nuevoEstado: EstadoEquipo, onSuccess: () -> Unit = {}) {
        if (_uiState.value.guardando) return
        _uiState.update { it.copy(guardando = true) }

        viewModelScope.launch {
            val resultado = repository.cambiarEstadoEquipo(id, nuevoEstado)
            _uiState.update { it.copy(guardando = false) }

            resultado
                .onSuccess {
                    _uiState.update { it.copy(mensaje = "Estado del equipo actualizado a $nuevoEstado") }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(mensaje = error.message ?: "Error al cambiar estado") }
                }
        }
    }

    override fun onCleared() {
        repository.liberarRecursos()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val database = PrestamoDatabase.getInstance(app)
                PrestamoViewModel(
                    repository = RoomPrestamoRepository(database),
                    sesionStore = SesionDataStore(app)
                )
            }
        }
    }
}