package com.example.prestamolab.ui.equipo

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamolab.data.model.EstadoEquipo
import com.example.prestamolab.ui.viewmodel.PrestamoUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipoDetalleScreen(
    equipoId: Int,
    uiState: PrestamoUiState,
    onSolicitarClick: (Int) -> Unit,
    onVolverClick: () -> Unit
) {
    val equipo = uiState.equipos.find { it.id == equipoId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Equipo") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (equipo == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Equipo no encontrado") // Manejo de ID inexistente (RN-08)[cite: 1]
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = equipo.nombre, style = MaterialTheme.typography.headlineMedium)
                Text(text = "Categoría: ${equipo.categoria}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Estado actual: ${equipo.estado}", style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { onSolicitarClick(equipo.id) },
                    enabled = equipo.estado == EstadoEquipo.DISPONIBLE, // Solo disponible (RN-01)[cite: 1]
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (equipo.estado == EstadoEquipo.DISPONIBLE) "Solicitar Préstamo" else "No Disponible")
                }
            }
        }
    }
}