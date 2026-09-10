package com.example.miprestamoslab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.miprestamoslab.model.EstadoSolicitud
import com.example.miprestamoslab.model.SolicitudPrestamo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisSolicitudesScreen(
    solicitudes: List<SolicitudPrestamo>,
    onSolicitudClick: (Int) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Solicitudes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (solicitudes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No has realizado solicitudes aún")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(solicitudes.sortedByDescending { it.id }, key = { it.id }) { solicitud ->
                    SolicitudCard(solicitud = solicitud, onClick = { onSolicitudClick(solicitud.id) })
                }
            }
        }
    }
}

@Composable
fun SolicitudCard(solicitud: SolicitudPrestamo, onClick: () -> Unit) {
    val estadoColor = when (solicitud.estado) {
        EstadoSolicitud.SOLICITADA -> MaterialTheme.colorScheme.primary
        EstadoSolicitud.APROBADA -> MaterialTheme.colorScheme.secondary
        EstadoSolicitud.CANCELADA, EstadoSolicitud.RECHAZADA -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Solicitud #${solicitud.id}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Equipo ID: ${solicitud.equipoId}")
            Text(text = "Duración: ${solicitud.duracionHoras} horas")
            Text(
                text = "Estado: ${solicitud.estado.name}",
                color = estadoColor
            )
            if (solicitud.estado == EstadoSolicitud.SOLICITADA) {
                Text(
                    text = "Toca para ver detalles o cancelar",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}