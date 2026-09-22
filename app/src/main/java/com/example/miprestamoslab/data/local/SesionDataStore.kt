package com.example.miprestamoslab.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.miprestamoslab.model.Rol
import com.example.miprestamoslab.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sesionDataStore by preferencesDataStore(name = "sesion_usuario")

class SesionDataStore(private val context: Context) : SesionStore {

    private companion object Keys {
        val USUARIO_ID = intPreferencesKey("usuario_id")
        val CORREO = stringPreferencesKey("correo")
        val NOMBRE = stringPreferencesKey("nombre")
        val ROL = stringPreferencesKey("rol")
        val SESION_ACTIVA = booleanPreferencesKey("sesion_activa")
    }

    override val sesion: Flow<Usuario?> =
        context.sesionDataStore.data.map { prefs ->
            if (prefs[SESION_ACTIVA] != true) return@map null
            Usuario(
                id = prefs[USUARIO_ID] ?: return@map null,
                correo = prefs[CORREO] ?: return@map null,
                nombre = prefs[NOMBRE] ?: return@map null,
                rol = Rol.valueOf(prefs[ROL] ?: return@map null)
            )
        }

    override suspend fun guardarSesion(usuario: Usuario) {
        context.sesionDataStore.edit { prefs ->
            prefs[USUARIO_ID] = usuario.id
            prefs[CORREO] = usuario.correo
            prefs[NOMBRE] = usuario.nombre
            prefs[ROL] = usuario.rol.name
            prefs[SESION_ACTIVA] = true
        }
    }

    override suspend fun limpiarSesion() {
        context.sesionDataStore.edit { it.clear() }
    }
}