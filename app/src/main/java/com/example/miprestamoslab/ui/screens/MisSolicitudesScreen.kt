package com.example.miprestamoslab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.miprestamoslab.model.EstadoSolicitud
import com.example.miprestamoslab.model.SolicitudPrestamo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudesScreen(
    solicitudes: List<SolicitudPrestamo>,
    onAprobar: (Int) -> Unit,
    onEntregar: (Int) -> Unit,
    onDevolver: (Int) -> Unit,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Solicitudes") },
                navigationIcon = {
                    TextButton(onClick = onVolver) { Text("Volver") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(solicitudes, key = { it.id }) { solicitud ->
                SolicitudCardItem(
                    solicitud = solicitud,
                    onAprobar = { onAprobar(solicitud.id) },
                    onEntregar = { onEntregar(solicitud.id) },
                    onDevolver = { onDevolver(solicitud.id) }
                )
            }
        }
    }
}

@Composable
fun SolicitudCardItem(
    solicitud: SolicitudPrestamo,
    onAprobar: () -> Unit,
    onEntregar: () -> Unit,
    onDevolver: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Solicitud #${solicitud.id}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Ambiente: ${solicitud.ambienteDestino}")
            Text(text = "Propósito: ${solicitud.proposito}")
            Text(text = "Estado: ${solicitud.estado.name}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // HU_07: Aprobar
                if (solicitud.estado == EstadoSolicitud.SOLICITADA) {
                    Button(onClick = onAprobar) {
                        Text("Aprobar")
                    }
                }

                // HU_08: Registrar Entrega Física
                if (solicitud.estado == EstadoSolicitud.APROBADA) {
                    Button(onClick = onEntregar) {
                        Text("Registrar Entrega (HU_08)")
                    }
                }

                // HU_09: Registrar Devolución
                if (solicitud.estado == EstadoSolicitud.ENTREGADA) {
                    Button(
                        onClick = onDevolver,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Registrar Devolución (HU_09)")
                    }
                }
            }
        }
    }
}