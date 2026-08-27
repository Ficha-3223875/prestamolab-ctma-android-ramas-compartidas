package com.example.prestamolab.ui.misprestamos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamolab.data.model.SolicitudPrestamo
import com.example.prestamolab.ui.viewmodel.PrestamoUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisSolicitudesScreen(
    uiState: PrestamoUiState,
    onSolicitudClick: (Int) -> Unit,
    onVolverClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Solicitudes") },
                navigationIcon = {
                    TextButton(onClick = onVolverClick) {
                        Text("< Volver", style = MaterialTheme.typography.titleMedium)
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.solicitudes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No has registrado ninguna solicitud aún.") // Estado vacío (RN-05)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.solicitudes) { solicitud ->
                    val equipo = uiState.equipos.find { it.id == solicitud.equipoId }
                    SolicitudItem(
                        solicitud = solicitud,
                        nombreEquipo = equipo?.nombre ?: "Equipo #${solicitud.equipoId}",
                        onClick = { onSolicitudClick(solicitud.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun SolicitudItem(
    solicitud: SolicitudPrestamo,
    nombreEquipo: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = nombreEquipo, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Ambiente: ${solicitud.ambienteDestino}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = solicitud.estado.name,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}