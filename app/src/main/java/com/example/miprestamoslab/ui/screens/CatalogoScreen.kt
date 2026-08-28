package com.example.miprestamoslab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.miprestamoslab.model.Equipo
import com.example.miprestamoslab.model.EstadoEquipo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    equipos: List<Equipo>,
    onEquipoClick: (Int) -> Unit,
    onVerMisSolicitudes: () -> Unit,
    onVerSolicitudesPendientes: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo de Equipos") },
                actions = {
                    IconButton(onClick = onVerSolicitudesPendientes) {
                        Icon(Icons.Default.List, contentDescription = "Solicitudes pendientes")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onVerMisSolicitudes,
                icon = { Icon(Icons.Default.List, contentDescription = null) },
                text = { Text("Mis Solicitudes") }
            )
        }
    ) { padding ->
        if (equipos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay equipos disponibles")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(equipos, key = { it.id }) { equipo ->
                    EquipoCard(equipo = equipo, onClick = { onEquipoClick(equipo.id) })
                }
            }
        }
    }
}

@Composable
fun EquipoCard(equipo: Equipo, onClick: () -> Unit) {
    val disponible = equipo.estado == EstadoEquipo.DISPONIBLE
    val estadoTexto = when (equipo.estado) {
        EstadoEquipo.DISPONIBLE -> "Disponible"
        EstadoEquipo.RESERVADO -> "Reservado"
        EstadoEquipo.PRESTADO -> "Prestado"
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        enabled = disponible
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = equipo.nombre, style = MaterialTheme.typography.titleMedium)
            Text(text = "Categoría: ${equipo.categoria.name.lowercase().replaceFirstChar { it.uppercase() }}")
            Text(
                text = "Estado: $estadoTexto",
                color = if (disponible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.semantics {
                    contentDescription = "Estado del equipo: $estadoTexto"
                }
            )
            if (!disponible) {
                Text(
                    text = "No disponible para préstamo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}