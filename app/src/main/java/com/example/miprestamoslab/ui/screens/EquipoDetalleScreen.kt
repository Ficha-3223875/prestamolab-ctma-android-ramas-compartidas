package com.example.miprestamoslab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.miprestamoslab.model.Equipo
import com.example.miprestamoslab.model.EstadoEquipo
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipoDetalleScreen(
    equipo: Equipo?,
    mensaje: String?,
    luzAmbiente: Float? = null,
    onLimpiarMensaje: () -> Unit,
    onSolicitar: (Int) -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            kotlinx.coroutines.delay(3000)
            onLimpiarMensaje()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Equipo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (equipo == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Equipo no encontrado", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onBack) {
                        Text("Volver al catálogo")
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(text = equipo.nombre, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Categoría: ${equipo.categoria.name}")
                    Text(text = "Estado: ${equipo.estado.name}")

                    // HU-14: capacidad física adicional (sensor integrado, sin permisos)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (luzAmbiente == null) {
                            "Luz ambiente: no disponible en este dispositivo"
                        } else {
                            "Luz ambiente: ${luzAmbiente.roundToInt()} lx"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.semantics {
                            contentDescription = if (luzAmbiente == null) {
                                "Luz ambiente no disponible"
                            } else {
                                "Luz ambiente de ${luzAmbiente.roundToInt()} lux"
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    val puedeSolicitar = equipo.estado == EstadoEquipo.DISPONIBLE

                    Button(
                        onClick = { onSolicitar(equipo.id) },
                        enabled = puedeSolicitar,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (puedeSolicitar) "Solicitar Préstamo" else "No Disponible")
                    }

                    if (!puedeSolicitar) {
                        Text(
                            text = "Este equipo no puede solicitarse en este momento.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            // Snackbar para mensajes
            if (mensaje != null) {
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                ) {
                    Text(mensaje)
                }
            }
        }
    }
}