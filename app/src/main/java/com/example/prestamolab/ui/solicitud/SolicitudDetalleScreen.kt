package com.example.prestamolab.ui.solicitud

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamolab.data.model.EstadoSolicitud
import com.example.prestamolab.ui.viewmodel.PrestamoUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudDetalleScreen(
    solicitudId: Int,
    uiState: PrestamoUiState,
    onCancelarSolicitud: (Int) -> Unit,
    onVolverClick: () -> Unit
) {
    val solicitud = uiState.solicitudes.find { it.id == solicitudId }
    val equipo = uiState.equipos.find { it.id == solicitud?.equipoId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de la Solicitud") },
                navigationIcon = {
                    TextButton(onClick = onVolverClick) {
                        Text("< Volver", style = MaterialTheme.typography.titleMedium)
                    }
                }
            )
        }
    ) { padding ->
        if (solicitud == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Solicitud no encontrada") // RN-08
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Equipo: ${equipo?.nombre ?: "ID ${solicitud.equipoId}"}",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Estado: ${solicitud.estado}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Ambiente / Destino: ${solicitud.ambienteDestino}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Duración: ${solicitud.duracionHoras} hora(s)",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Propósito: ${solicitud.proposito}",
                    style = MaterialTheme.typography.bodyMedium
                )

                if (uiState.mensajeError != null) {
                    Text(
                        text = uiState.mensajeError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // RN-07: Botón de cancelación (solo si está en estado SOLICITADA)
                if (solicitud.estado == EstadoSolicitud.SOLICITADA) {
                    Button(
                        onClick = { onCancelarSolicitud(solicitud.id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancelar Solicitud")
                    }
                }
            }
        }
    }
}