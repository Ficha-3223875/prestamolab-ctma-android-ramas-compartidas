package com.example.miprestamoslab.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.miprestamoslab.data.local.PrestamoDatabase
import com.example.miprestamoslab.data.repository.PrestamoRepository
import com.example.miprestamoslab.data.repository.RealPrestamoRepository
import com.example.miprestamoslab.data.remote.PrestamoApiService
import com.example.miprestamoslab.data.remote.EquipoDto
import com.example.miprestamoslab.data.remote.SolicitudPrestamoDto
import com.example.miprestamoslab.domain.ambienteValido
import com.example.miprestamoslab.domain.duracionValida
import com.example.miprestamoslab.domain.propositoValido
import com.example.miprestamoslab.model.*
import com.example.miprestamoslab.util.NotificationHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PrestamoViewModel(
    application: Application,
    repository: PrestamoRepository? = null,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AndroidViewModel(application) {

    constructor(application: Application) : this(application, null, Dispatchers.IO)

    private val actualRepository: PrestamoRepository = repository ?: run {
        val database = PrestamoDatabase.getDatabase(application, viewModelScope)
        val mockApiService = object : PrestamoApiService {
            override suspend fun obtenerEquipos(): List<EquipoDto> = emptyList()
            override suspend fun obtenerSolicitudes(): List<SolicitudPrestamoDto> = emptyList()
            override suspend fun crearSolicitud(solicitud: SolicitudPrestamoDto): SolicitudPrestamoDto = solicitud
            override suspend fun actualizarSolicitud(id: Int, solicitud: SolicitudPrestamoDto): SolicitudPrestamoDto = solicitud
        }
        RealPrestamoRepository(
            database.equipoDao(),
            database.solicitudPrestamoDao(),
            mockApiService
        )
    }

    private val _uiState = MutableStateFlow(PrestamoUiState())
    val uiState: StateFlow<PrestamoUiState> = _uiState.asStateFlow()

    private val _biometricVerificado = MutableStateFlow(false)
    val biometricVerificado: StateFlow<Boolean> = _biometricVerificado.asStateFlow()

    private var isProcessing = false

    init {
        // Observar equipos de forma reactiva
        viewModelScope.launch {
            actualRepository.obtenerEquiposFlow()
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

        // Observar solicitudes de forma reactiva
        viewModelScope.launch {
            actualRepository.obtenerSolicitudesFlow().collect { lista ->
                _uiState.update { it.copy(solicitudes = lista) }
            }
        }
    }

    fun setBiometricVerificado(verificado: Boolean) {
        _biometricVerificado.value = verificado
    }

    fun login(correo: String, contrasena: String, onSuccess: () -> Unit) {
        if (correo.isBlank() || contrasena.isBlank()) {
            _uiState.update { it.copy(mensajeError = "Por favor ingrese correo y contraseña") }
            return
        }

        if (isProcessing) return
        isProcessing = true
        _uiState.update { it.copy(operacionState = OperacionUiState.EnCurso, guardando = true, mensajeError = null) }

        viewModelScope.launch {
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
                isProcessing = false
                onSuccess()
            } else {
                _uiState.update {
                    it.copy(
                        operacionState = OperacionUiState.Fallida("Usuario o contraseña inválidos"),
                        guardando = false,
                        mensajeError = "Usuario o contraseña inválidos"
                    )
                }
                isProcessing = false
            }
        }
    }

    fun logout() {
        _uiState.update { it.copy(usuarioAutenticado = null, operacionState = OperacionUiState.Inactiva) }
        _biometricVerificado.value = false
    }

    fun cargarEquipo(equipoId: Int) {
        viewModelScope.launch {
            val equipo = withContext(ioDispatcher) { actualRepository.obtenerEquipo(equipoId) }
            _uiState.update { it.copy(equipoSeleccionado = equipo) }
        }
    }

    fun cargarSolicitud(solicitudId: Int) {
        viewModelScope.launch {
            val solicitud = withContext(ioDispatcher) { actualRepository.obtenerSolicitud(solicitudId) }
            _uiState.update { it.copy(solicitudSeleccionada = solicitud) }
        }
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

        if (isProcessing) return
        isProcessing = true
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

            val resultado = withContext(ioDispatcher) { actualRepository.crearSolicitud(solicitud) }

            resultado.onSuccess {
                val createdSolicitud = withContext(ioDispatcher) {
                    actualRepository.obtenerSolicitudes().lastOrNull { it.equipoId == equipoId }
                }
                if (createdSolicitud != null) {
                    NotificationHelper.mostrarNotificacionNuevaSolicitud(
                        getApplication(),
                        createdSolicitud.id,
                        createdSolicitud.equipoId,
                        createdSolicitud.proposito
                    )
                }
                _uiState.update {
                    it.copy(
                        mensaje = "Solicitud registrada correctamente",
                        operacionState = OperacionUiState.Exitosa,
                        guardando = false
                    )
                }
                isProcessing = false
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
                isProcessing = false
            }
        }
    }

    fun cancelarSolicitud(solicitudId: Int, onSuccess: () -> Unit = {}) {
        if (isProcessing) return
        isProcessing = true
        _uiState.update { it.copy(operacionState = OperacionUiState.EnCurso) }
        
        viewModelScope.launch {
            val res = withContext(ioDispatcher) { actualRepository.cancelarSolicitud(solicitudId) }
            res.onSuccess {
                _uiState.update {
                    it.copy(
                        mensaje = "Solicitud cancelada correctamente",
                        operacionState = OperacionUiState.Exitosa
                    )
                }
                isProcessing = false
                onSuccess()
            }.onFailure { error ->
                val errorMsg = error.message ?: "Error al cancelar solicitud"
                _uiState.update {
                    it.copy(
                        mensaje = errorMsg,
                        operacionState = OperacionUiState.Fallida(errorMsg)
                    )
                }
                isProcessing = false
            }
        }
    }

    fun aprobarSolicitud(solicitudId: Int) {
        if (isProcessing) return
        isProcessing = true
        _uiState.update { it.copy(operacionState = OperacionUiState.EnCurso) }
        
        viewModelScope.launch {
            val res = withContext(ioDispatcher) { actualRepository.aprobarSolicitud(solicitudId) }
            res.onSuccess {
                _uiState.update { it.copy(mensaje = "Solicitud aprobada correctamente", operacionState = OperacionUiState.Exitosa) }
                isProcessing = false
            }.onFailure { error ->
                _uiState.update { it.copy(mensaje = error.message ?: "Error al aprobar solicitud", operacionState = OperacionUiState.Fallida(error.message ?: "Error")) }
                isProcessing = false
            }
        }
    }

    fun rechazarSolicitud(solicitudId: Int, razon: String) {
        if (isProcessing) return
        isProcessing = true
        _uiState.update { it.copy(operacionState = OperacionUiState.EnCurso) }
        
        viewModelScope.launch {
            val res = withContext(ioDispatcher) { actualRepository.rechazarSolicitud(solicitudId, razon) }
            res.onSuccess {
                _uiState.update { it.copy(mensaje = "Solicitud rechazada correctamente", operacionState = OperacionUiState.Exitosa) }
                isProcessing = false
            }.onFailure { error ->
                _uiState.update { it.copy(mensaje = error.message ?: "Error al rechazar solicitud", operacionState = OperacionUiState.Fallida(error.message ?: "Error")) }
                isProcessing = false
            }
        }
    }

    fun agregarEquipo(nombre: String, categoria: CategoriaEquipo, descripcion: String, onSuccess: () -> Unit = {}) {
        if (isProcessing) return
        isProcessing = true
        _uiState.update { it.copy(operacionState = OperacionUiState.EnCurso) }
        
        viewModelScope.launch {
            val res = withContext(ioDispatcher) { actualRepository.agregarEquipo(nombre, categoria, descripcion) }
            res.onSuccess {
                _uiState.update { it.copy(mensaje = "Equipo agregado correctamente", operacionState = OperacionUiState.Exitosa) }
                isProcessing = false
                onSuccess()
            }.onFailure { error ->
                _uiState.update { it.copy(mensaje = error.message ?: "Error al agregar equipo", operacionState = OperacionUiState.Fallida(error.message ?: "Error")) }
                isProcessing = false
            }
        }
    }

    fun editarEquipo(id: Int, nombre: String, categoria: CategoriaEquipo, descripcion: String, onSuccess: () -> Unit = {}) {
        if (isProcessing) return
        isProcessing = true
        _uiState.update { it.copy(operacionState = OperacionUiState.EnCurso) }
        
        viewModelScope.launch {
            val res = withContext(ioDispatcher) { actualRepository.editarEquipo(id, nombre, categoria, descripcion) }
            res.onSuccess {
                _uiState.update { it.copy(mensaje = "Equipo actualizado correctamente", operacionState = OperacionUiState.Exitosa) }
                isProcessing = false
                onSuccess()
            }.onFailure { error ->
                _uiState.update { it.copy(mensaje = error.message ?: "Error al actualizar equipo", operacionState = OperacionUiState.Fallida(error.message ?: "Error")) }
                isProcessing = false
            }
        }
    }

    fun cambiarEstadoEquipo(id: Int, nuevoEstado: EstadoEquipo, onSuccess: () -> Unit = {}) {
        if (isProcessing) return
        isProcessing = true
        _uiState.update { it.copy(operacionState = OperacionUiState.EnCurso) }
        
        viewModelScope.launch {
            val res = withContext(ioDispatcher) { actualRepository.cambiarEstadoEquipo(id, nuevoEstado) }
            res.onSuccess {
                _uiState.update { it.copy(mensaje = "Estado del equipo actualizado a $nuevoEstado", operacionState = OperacionUiState.Exitosa) }
                isProcessing = false
                onSuccess()
            }.onFailure { error ->
                _uiState.update { it.copy(mensaje = error.message ?: "Error al cambiar estado", operacionState = OperacionUiState.Fallida(error.message ?: "Error")) }
                isProcessing = false
            }
        }
    }

    fun registrarDevolucion(solicitudId: Int, fotoUri: String, onSuccess: () -> Unit = {}) {
        if (isProcessing) return
        isProcessing = true
        _uiState.update { it.copy(operacionState = OperacionUiState.EnCurso) }

        viewModelScope.launch {
            val res = withContext(ioDispatcher) { actualRepository.registrarDevolucion(solicitudId, fotoUri) }
            res.onSuccess {
                _uiState.update { it.copy(mensaje = "Devolución y foto registradas correctamente", operacionState = OperacionUiState.Exitosa) }
                isProcessing = false
                // Recargar la solicitud seleccionada si corresponde
                if (_uiState.value.solicitudSeleccionada?.id == solicitudId) {
                    val actualizada = withContext(ioDispatcher) { actualRepository.obtenerSolicitud(solicitudId) }
                    _uiState.update { it.copy(solicitudSeleccionada = actualizada) }
                }
                onSuccess()
            }.onFailure { error ->
                _uiState.update { it.copy(mensaje = error.message ?: "Error al registrar devolución", operacionState = OperacionUiState.Fallida(error.message ?: "Error")) }
                isProcessing = false
            }
        }
    }
}