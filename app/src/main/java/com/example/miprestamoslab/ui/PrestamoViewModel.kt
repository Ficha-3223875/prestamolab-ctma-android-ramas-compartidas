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
        // Observar equipos (ListadoUiState)
        viewModelScope.launch {
            repository.equipos
                .onStart { _uiState.update { it.copy(listadoEquipos = ListadoUiState.Cargando) } }
                .catch { e -> _uiState.update { it.copy(listadoEquipos = ListadoUiState.Error(e.message ?: "Error desconocido")) } }
                .collect { lista ->
                    _uiState.update {
                        it.copy(
                            listadoEquipos = if (lista.isEmpty()) ListadoUiState.Vacio else ListadoUiState.Contenido(lista)
                        )
                    }
                }
        }

        // Observar solicitudes
        viewModelScope.launch {
            repository.solicitudes.collect { lista ->
                _uiState.update { it.copy(solicitudes = lista) }
            }
        }
    }

    // Autenticación (HU_15) con OperacionUiState
    fun login(correo: String, contrasena: String, onSuccess: () -> Unit) {
        if (correo.isBlank() || contrasena.isBlank()) {
            _uiState.update { it.copy(mensajeError = "Por favor ingrese correo y contraseña") }
            return
        }

        _uiState.update { it.copy(operacionState = OperacionUiState.EnCurso, guardando = true, mensajeError = null) }

        viewModelScope.launch {
            // Simulamos un retraso para ver el estado "EnCurso"
            if (contrasena == "123456") {
                val rolSimulado = if (correo.contains("encargado")) Rol.ENCARGADO else Rol.APRENDIZ
                val usuario = Usuario(
                    id = (1..100).random(),
                    correo = correo,
                    nombre = "Usuario SENA",
                    rol = rolSimulado
                )
                _uiState.update {
                    it.copy(
                        usuarioAutenticado = usuario,
                        operacionState = OperacionUiState.Exitosa,
                        guardando = false,
                        mensajeError = null
                    )
                }
                onSuccess()
            } else {
                _uiState.update {
                    it.copy(
                        operacionState = OperacionUiState.Fallida("Usuario o contraseña inválidos"),
                        guardando = false,
                        mensajeError = "Usuario o contraseña inválidos"
                    )
                }
            }
        }
    }

    fun logout() {
        _uiState.update { it.copy(usuarioAutenticado = null, operacionState = OperacionUiState.Inactiva) }
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
        _uiState.update { it.copy(mensaje = null, operacionState = OperacionUiState.Inactiva) }
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
            val msg = errores.joinToString("\n")
            _uiState.update { it.copy(mensaje = msg, operacionState = OperacionUiState.Fallida(msg)) }
            return
        }

        if (_uiState.value.operacionState == OperacionUiState.EnCurso) return

        _uiState.update { it.copy(operacionState = OperacionUiState.EnCurso, guardando = true) }

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

            resultado.onSuccess {
                _uiState.update { 
                    it.copy(
                        mensaje = "Solicitud registrada correctamente",
                        operacionState = OperacionUiState.Exitosa,
                        guardando = false
                    ) 
                }
                onSuccess()
            }.onFailure { error ->
                val errorMsg = error.message ?: "Error al crear solicitud"
                _uiState.update { 
                    it.copy(
                        mensaje = errorMsg,
                        operacionState = OperacionUiState.Fallida(errorMsg),
                        guardando = false
                    ) 
                }
            }
        }
    }

    fun cancelarSolicitud(solicitudId: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(operacionState = OperacionUiState.EnCurso) }
            repository.cancelarSolicitud(solicitudId)
                .onSuccess {
                    _uiState.update { 
                        it.copy(
                            mensaje = "Solicitud cancelada correctamente",
                            operacionState = OperacionUiState.Exitosa
                        ) 
                    }
                    onSuccess()
                }
                .onFailure { error ->
                    val errorMsg = error.message ?: "Error al cancelar solicitud"
                    _uiState.update { 
                        it.copy(
                            mensaje = errorMsg,
                            operacionState = OperacionUiState.Fallida(errorMsg)
                        ) 
                    }
                }
        }
    }

    fun aprobarSolicitud(solicitudId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(operacionState = OperacionUiState.EnCurso) }
            repository.aprobarSolicitud(solicitudId)
                .onSuccess { 
                    _uiState.update { it.copy(mensaje = "Solicitud aprobada correctamente", operacionState = OperacionUiState.Exitosa) } 
                }
                .onFailure { error -> 
                    _uiState.update { it.copy(mensaje = error.message ?: "Error al aprobar solicitud", operacionState = OperacionUiState.Fallida(error.message ?: "Error")) } 
                }
        }
    }

    fun rechazarSolicitud(solicitudId: Int, razon: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(operacionState = OperacionUiState.EnCurso) }
            repository.rechazarSolicitud(solicitudId, razon)
                .onSuccess { 
                    _uiState.update { it.copy(mensaje = "Solicitud rechazada correctamente", operacionState = OperacionUiState.Exitosa) } 
                }
                .onFailure { error -> 
                    _uiState.update { it.copy(mensaje = error.message ?: "Error al rechazar solicitud", operacionState = OperacionUiState.Fallida(error.message ?: "Error")) } 
                }
        }
    }

    fun agregarEquipo(nombre: String, categoria: CategoriaEquipo, descripcion: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(operacionState = OperacionUiState.EnCurso) }
            repository.agregarEquipo(nombre, categoria, descripcion)
                .onSuccess {
                    _uiState.update { it.copy(mensaje = "Equipo agregado correctamente", operacionState = OperacionUiState.Exitosa) }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(mensaje = error.message ?: "Error al agregar equipo", operacionState = OperacionUiState.Fallida(error.message ?: "Error")) }
                }
        }
    }

    fun editarEquipo(id: Int, nombre: String, categoria: CategoriaEquipo, descripcion: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(operacionState = OperacionUiState.EnCurso) }
            repository.editarEquipo(id, nombre, categoria, descripcion)
                .onSuccess {
                    _uiState.update { it.copy(mensaje = "Equipo actualizado correctamente", operacionState = OperacionUiState.Exitosa) }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(mensaje = error.message ?: "Error al actualizar equipo", operacionState = OperacionUiState.Fallida(error.message ?: "Error")) }
                }
        }
    }

    fun cambiarEstadoEquipo(id: Int, nuevoEstado: EstadoEquipo, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(operacionState = OperacionUiState.EnCurso) }
            repository.cambiarEstadoEquipo(id, nuevoEstado)
                .onSuccess {
                    _uiState.update { it.copy(mensaje = "Estado del equipo actualizado a $nuevoEstado", operacionState = OperacionUiState.Exitosa) }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update { it.copy(mensaje = error.message ?: "Error al cambiar estado", operacionState = OperacionUiState.Fallida(error.message ?: "Error")) }
                }
        }
    }
}
