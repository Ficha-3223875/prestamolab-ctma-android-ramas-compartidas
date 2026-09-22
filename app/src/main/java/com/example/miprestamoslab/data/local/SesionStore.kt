package com.example.miprestamoslab.data.local

import com.example.miprestamoslab.model.Usuario
import kotlinx.coroutines.flow.Flow

interface SesionStore {
    val sesion: Flow<Usuario?>
    suspend fun guardarSesion(usuario: Usuario)
    suspend fun limpiarSesion()
}