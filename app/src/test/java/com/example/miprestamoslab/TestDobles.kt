package com.example.miprestamoslab

import com.example.miprestamoslab.data.local.SesionStore
import com.example.miprestamoslab.data.repository.PrestamoRepository
import com.example.miprestamoslab.model.CategoriaEquipo
import com.example.miprestamoslab.model.Equipo
import com.example.miprestamoslab.model.EstadoEquipo
import com.example.miprestamoslab.model.SolicitudPrestamo
import com.example.miprestamoslab.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Dobl de sesión en memoria (sin DataStore, ya que las pruebas son JVM). */
internal class SesionFalsa : SesionStore {
    private val _usuario = MutableStateFlow<Usuario?>(null)
    override val sesion: Flow<Usuario?> = _usuario.asStateFlow()

    override suspend fun guardarSesion(usuario: Usuario) {
        _usuario.value = usuario
    }

    override suspend fun limpiarSesion() {
        _usuario.value = null
    }
}

/** Repositorio falso configurable para aislar el comportamiento que se prueba. */
internal open class RepositorioFalso(
    equiposIniciales: List<Equipo> = emptyList(),
    solicitudesIniciales: List<SolicitudPrestamo> = emptyList()
) : PrestamoRepository {
    override val equipos: StateFlow<List<Equipo>> = MutableStateFlow(equiposIniciales)
    override val solicitudes: StateFlow<List<SolicitudPrestamo>> = MutableStateFlow(solicitudesIniciales)

    override fun obtenerEquipos(): List<Equipo> = equipos.value
    override fun obtenerEquipo(id: Int): Equipo? = equipos.value.find { it.id == id }
    override fun obtenerSolicitudes(): List<SolicitudPrestamo> = solicitudes.value
    override fun obtenerSolicitud(id: Int): SolicitudPrestamo? =
        solicitudes.value.find { it.id == id }

    override suspend fun crearSolicitud(solicitud: SolicitudPrestamo) = Result.success(Unit)
    override suspend fun cancelarSolicitud(id: Int) = Result.success(Unit)
    override suspend fun aprobarSolicitud(id: Int) = Result.success(Unit)
    override suspend fun rechazarSolicitud(id: Int, razon: String) = Result.success(Unit)
    override suspend fun agregarEquipo(nombre: String, categoria: CategoriaEquipo, descripcion: String) =
        Result.success(Unit)
    override suspend fun editarEquipo(id: Int, nombre: String, categoria: CategoriaEquipo, descripcion: String) =
        Result.success(Unit)
    override suspend fun cambiarEstadoEquipo(id: Int, nuevoEstado: EstadoEquipo) = Result.success(Unit)
}

/** Repositorio sin registros: permite verificar los estados Vacío y Error. */
internal class RepositorioVacio : RepositorioFalso()

/** Simula un fallo real de la fuente de datos (p. ej. base de datos corrupta). */
internal class RepositorioConError : RepositorioFalso() {
    override val equipos: StateFlow<List<Equipo>>
        get() = throw IllegalStateException("Room no disponible")
}
