package com.example.miprestamoslab.data

import com.example.miprestamoslab.data.local.entity.EquipoEntity
import com.example.miprestamoslab.data.local.entity.SolicitudPrestamoEntity
import com.example.miprestamoslab.data.remote.EquipoDto
import com.example.miprestamoslab.data.remote.SolicitudPrestamoDto
import com.example.miprestamoslab.model.*

fun EquipoEntity.toDomain(): Equipo {
    return Equipo(
        id = id,
        nombre = nombre,
        categoria = try { CategoriaEquipo.valueOf(categoria) } catch (e: Exception) { CategoriaEquipo.OTRO },
        estado = try { EstadoEquipo.valueOf(estado) } catch (e: Exception) { EstadoEquipo.DISPONIBLE },
        descripcion = descripcion
    )
}

fun Equipo.toEntity(syncStatus: String = "SYNCED", fotoUri: String? = null, metadata: String? = null): EquipoEntity {
    return EquipoEntity(
        id = id,
        nombre = nombre,
        categoria = categoria.name,
        estado = estado.name,
        descripcion = descripcion,
        fotoUri = fotoUri,
        syncStatus = syncStatus,
        metadata = metadata
    )
}

fun EquipoDto.toEntity(syncStatus: String = "SYNCED"): EquipoEntity {
    return EquipoEntity(
        id = id,
        nombre = nombre,
        categoria = categoria,
        estado = estado,
        descripcion = descripcion,
        fotoUri = fotoUri,
        syncStatus = syncStatus,
        metadata = metadata
    )
}

fun SolicitudPrestamoEntity.toDomain(): SolicitudPrestamo {
    return SolicitudPrestamo(
        id = id,
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        estado = try { EstadoSolicitud.valueOf(estado) } catch (e: Exception) { EstadoSolicitud.SOLICITADA },
        razonRechazo = razonRechazo
    )
}

fun SolicitudPrestamo.toEntity(syncStatus: String = "SYNCED", fotoDevolucionUri: String? = null, metadata: String? = null): SolicitudPrestamoEntity {
    return SolicitudPrestamoEntity(
        id = id,
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        estado = estado.name,
        razonRechazo = razonRechazo,
        fotoDevolucionUri = fotoDevolucionUri,
        syncStatus = syncStatus,
        metadata = metadata
    )
}

fun SolicitudPrestamoDto.toEntity(syncStatus: String = "SYNCED"): SolicitudPrestamoEntity {
    return SolicitudPrestamoEntity(
        id = id,
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        estado = estado,
        razonRechazo = razonRechazo,
        fotoDevolucionUri = fotoDevolucionUri,
        syncStatus = syncStatus,
        metadata = metadata
    )
}

fun SolicitudPrestamoEntity.toDto(): SolicitudPrestamoDto {
    return SolicitudPrestamoDto(
        id = id,
        equipoId = equipoId,
        ambienteDestino = ambienteDestino,
        proposito = proposito,
        duracionHoras = duracionHoras,
        estado = estado,
        razonRechazo = razonRechazo,
        fotoDevolucionUri = fotoDevolucionUri,
        metadata = metadata
    )
}