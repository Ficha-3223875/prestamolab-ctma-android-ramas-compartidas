package com.example.prestamolabctma_2.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamolabctma_2.model.Equipo
import com.example.prestamolabctma_2.model.EstadoSolicitud
import com.example.prestamolabctma_2.model.SolicitudPrestamo

@Composable
fun MisSolicitudesScreen(
    solicitudes: List<SolicitudPrestamo>,
    equipos: List<Equipo>,
    onCancelarSolicitud: (Int) -> Unit,
    onVolver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mis Solicitudes",
                style = MaterialTheme.typography.headlineMedium
            )
            Button(onClick = onVolver) {
                Text("Volver")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (solicitudes.isEmpty()) {
            Text(
                text = "No tienes solicitudes registradas.",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(solicitudes) { solicitud ->
                    val equipo = equipos.find { it.id == solicitud.equipoId }
                    SolicitudItem(
                        solicitud = solicitud,
                        equipoNombre = equipo?.nombre ?: "Equipo #${solicitud.equipoId}",
                        onCancelarSolicitud = { onCancelarSolicitud(solicitud.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun SolicitudItem(
    solicitud: SolicitudPrestamo,
    equipoNombre: String,
    onCancelarSolicitud: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = equipoNombre,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Ambiente: ${solicitud.ambienteDestino}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Propósito: ${solicitud.proposito}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Duración: ${solicitud.duracionHoras} hora(s)",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Estado: ${solicitud.estado}",
                style = MaterialTheme.typography.bodyMedium
            )

            if (solicitud.estado == EstadoSolicitud.SOLICITADA) {
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedButton(onClick = onCancelarSolicitud) {
                    Text("Cancelar Solicitud")
                }
            }
        }
    }
}