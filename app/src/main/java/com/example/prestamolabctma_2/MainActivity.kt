package com.example.prestamolabctma_2

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.prestamolabctma_2.data.InMemoryPrestamoRepository
import com.example.prestamolabctma_2.ui.CatalogoScreen
import com.example.prestamolabctma_2.ui.MisSolicitudesScreen
import com.example.prestamolabctma_2.ui.PrestamoViewModel
import com.example.prestamolabctma_2.ui.SolicitudScreen
import com.example.prestamolabctma_2.ui.theme.PrestamoLabCTMA2Theme

class MainActivity : ComponentActivity() {

    private val repository = InMemoryPrestamoRepository()
    private val viewModel = PrestamoViewModel(repository)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrestamoLabCTMA2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    viewModel: PrestamoViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var pantallaActual by remember { mutableStateOf("catalogo") }
    var equipoSeleccionadoId by remember { mutableStateOf<Int?>(null) }

    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.limpiarMensaje()
        }
    }

    when (pantallaActual) {
        "catalogo" -> {
            CatalogoScreen(
                equipos = uiState.equipos,
                onSolicitarClick = { id ->
                    equipoSeleccionadoId = id
                    pantallaActual = "solicitud"
                },
                onVerSolicitudesClick = {
                    pantallaActual = "mis_solicitudes"
                }
            )
        }
        "solicitud" -> {
            val equipo = uiState.equipos.find { it.id == equipoSeleccionadoId }
            SolicitudScreen(
                equipoNombre = equipo?.nombre ?: "Equipo",
                guardando = uiState.guardando,
                onEnviarSolicitud = { ambiente, proposito, horas ->
                    equipoSeleccionadoId?.let { id ->
                        val exito = viewModel.crearSolicitud(id, ambiente, proposito, horas)
                        if (exito) {
                            pantallaActual = "catalogo"
                        }
                    }
                },
                onVolver = {
                    pantallaActual = "catalogo"
                }
            )
        }
        "mis_solicitudes" -> {
            MisSolicitudesScreen(
                solicitudes = uiState.solicitudes,
                equipos = uiState.equipos,
                onCancelarSolicitud = { id ->
                    viewModel.cancelarSolicitud(id)
                },
                onVolver = {
                    pantallaActual = "catalogo"
                }
            )
        }
    }
}