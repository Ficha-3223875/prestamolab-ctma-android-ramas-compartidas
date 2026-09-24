package com.example.miprestamoslab.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.miprestamoslab.data.capabilities.FuenteLuz
import com.example.miprestamoslab.data.capabilities.LuzAmbientalSensor
import com.example.miprestamoslab.data.local.PrestamoDatabase
import com.example.miprestamoslab.data.local.SesionDataStore
import com.example.miprestamoslab.data.local.SesionStore
import com.example.miprestamoslab.data.remote.RetrofitFactory
import com.example.miprestamoslab.data.remote.SincronizadorRemoto
import com.example.miprestamoslab.data.repository.EvidenciaRepository
import com.example.miprestamoslab.data.repository.PrestamoRepository
import com.example.miprestamoslab.data.repository.RoomEvidenciaRepository
import com.example.miprestamoslab.data.repository.RoomPrestamoRepository
import com.example.miprestamoslab.domain.ambienteValido
import com.example.miprestamoslab.domain.duracionValida
import com.example.miprestamoslab.domain.propositoValido
import com.example.miprestamoslab.model.CategoriaEquipo
import com.example.miprestamoslab.model.EstadoEquipo
import com.example.miprestamoslab.model.EstadoSolicitud
import com.example.miprestamoslab.model.Evidencia
import com.example.miprestamoslab.model.Rol
import com.example.miprestamoslab.model.SolicitudPrestamo
import com.example.miprestamoslab.model.Usuario
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PrestamoViewModel(
    private val repository: PrestamoRepository,
    private val sesionStore: SesionStore,
    private val sincronizador: SincronizadorRemoto? = null,
    private val evidenciaRepository: EvidenciaRepository? = null,
    private val fuenteLuz: FuenteLuz? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamoUiState())
    val uiState: StateFlow<PrestamoUiState> = _uiState.asStateFlow()

    /** Evidencias de la solicitud seleccionada (HU-13). */
    private val _evidencias = MutableStateFlow<List<Evidencia>>(emptyList())
    val evidencias: StateFlow<List<Evidencia>> = _evidencias.asStateFlow()

    /** Lectura del sensor de luz ambiente (HU-14); `null` si el dispositivo no lo tiene. */
    val luzAmbiente: StateFlow<Float?> = fuenteLuz?.lux ?: MutableStateFlow(null)

    private var observacionJob: Job? = null
    private var evidenciasJob: Job? = null

    init {
        iniciarObservacionDeDatos()
        viewModelScope.launch {
            sesionStore.sesion.collect { usuario ->
                _uiState.update { it.copy(usuarioAutenticado = usuario) }
            }
        }
    }

    /**
     * Observa los flujos del Repository y traduce el resultado a un [EstadoCarga] explícito.
     * Si el Repository falla se entra en [EstadoCarga.ERROR] con mensaje recuperable (Semana 7).
     */
    private fun iniciarObservacionDeDatos() {
        observacionJob?.cancel()
        observacionJob = viewModelScope.launch {
            try {
                repository.equipos.combine(repository.solicitudes) { eq, sol -> eq to sol }
                    .collect { (equipos, solicitudes) ->
                        val estado = if (equipos.isEmpty() && solicitudes.isEmpty()) {
                            EstadoCarga.VACIO
                        } else {
                            EstadoCarga.CONTENIDO
                        }
                        _uiState.update {
                            it.copy(
                                equipos = equipos,
                                solicitudes = solicitudes,
                                estadoCarga = estado,
                                errorCarga = null
                            )
                        }
                    }
            } catch (cancel: CancellationException) {
                throw cancel
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        estadoCarga = EstadoCarga.ERROR,
                        errorCarga = error.message ?: "No fue posible cargar los datos"
                    )
                }
            }
        }
    }

    /** Estado recuperable: reintenta la suscripción tras un fallo de carga. */
    fun reintentarCarga() {
        _uiState.update { it.copy(estadoCarga = EstadoCarga.CARGANDO, errorCarga = null) }
        iniciarObservacionDeDatos()
    }

    // --- HU-13: evidencia fotográfica ---

    /** Observa las evidencias de la solicitud abierta. */
    fun cargarEvidencias(solicitudId: Int) {
        val repo = evidenciaRepository
        if (repo == null || solicitudId <= 0) {
            _evidencias.value = emptyList()
            return
        }

        evidenciasJob?.cancel()
        evidenciasJob = viewModelScope.launch {
            try {
                repo.observarEvidencias(solicitudId).collect { lista -> _evidencias.value = lista }
            } catch (cancel: CancellationException) {
                throw cancel
            } catch (error: Exception) {
                // Estado recuperable: se muestran sin evidencias y el aviso llega por el mensaje
                _evidencias.value = emptyList()
                _uiState.update { it.copy(mensaje = error.message ?: "No fue posible leer las evidencias") }
            }
        }
    }

    /** Adjunta la URI elegida en el Photo Picker (nunca el Bitmap). */
    fun registrarEvidencia(solicitudId: Int, uri: String) {
        val repo = evidenciaRepository
        if (repo == null) {
            _uiState.update { it.copy(mensaje = "Las evidencias no están habilitadas en este entorno") }
            return
        }
        if (_uiState.value.guardando) return

        _uiState.update { it.copy(guardando = true) }
        viewModelScope.launch {
            repo.registrarEvidencia(solicitudId, uri)
                .onSuccess {
                    _uiState.update { it.copy(guardando = false, mensaje = "Evidencia adjuntada correctamente") }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(guardando = false, mensaje = error.message ?: "No se pudo adjuntar la evidencia")
                    }
                }
        }
    }

    // --- HU-14: sensor de luz ambiente ---

    /** Activa el sensor solo mientras se necesita (mínimo consumo de batería). */
    fun iniciarLecturaLuz() {
        fuenteLuz?.iniciar()
    }

    fun detenerLecturaLuz() {
        fuenteLuz?.detener()
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

    /**
     * Sincroniza Room con el servicio remoto (HU-07 / Semana 8).
     * Estrategia local-first: la UI sigue leyendo de Room mientras la red actualiza la base local.
     */
    fun sincronizar() {
        val remoto = sincronizador
        if (remoto == null) {
            _uiState.update { it.copy(mensaje = "La sincronización remota no está habilitada en este entorno") }
            return
        }
        if (_uiState.value.sincronizando) return

        _uiState.update { it.copy(sincronizando = true, mensaje = null) }

        viewModelScope.launch {
            val equipos = remoto.sincronizarEquipos()
            val solicitudes = if (equipos.isSuccess) {
                remoto.sincronizarSolicitudes()
            } else {
                Result.success(-1)
            }

            _uiState.update { it.copy(sincronizando = false) }

            solicitudes
                .onSuccess {
                    _uiState.update {
                        it.copy(mensaje = "Sincronización completada (${equipos.getOrNull() ?: 0} equipos remotos)")
                    }
                }
                .onFailure { error ->
                    // RedError ya traduce 401/404/5xx/timeouts a mensajes recuperables
                    _uiState.update { it.copy(mensaje = error.message ?: "No fue posible sincronizar") }
                }
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
        evidenciasJob?.cancel()
        fuenteLuz?.detener()
        repository.liberarRecursos()
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val database = PrestamoDatabase.getInstance(app)
                PrestamoViewModel(
                    repository = RoomPrestamoRepository(database),
                    sesionStore = SesionDataStore(app),
                    sincronizador = SincronizadorRemoto(
                        api = RetrofitFactory.crear(),
                        dao = database.prestamoDao()
                    ),
                    evidenciaRepository = RoomEvidenciaRepository(database.prestamoDao()),
                    fuenteLuz = LuzAmbientalSensor(app)
                )
            }
        }
    }
}