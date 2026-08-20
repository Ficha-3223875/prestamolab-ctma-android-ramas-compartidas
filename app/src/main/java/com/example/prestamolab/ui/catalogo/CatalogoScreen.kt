package com.example.prestamolab.ui.catalogo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamolab.data.model.Equipo
import com.example.prestamolab.data.model.EstadoEquipo
import com.example.prestamolab.ui.viewmodel.PrestamoUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    uiState: PrestamoUiState,
    onEquipoClick: (Int) -> Unit,
    onVerMisSolicitudesClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PréstamoLab CTMA") },
                actions = {
                    TextButton(onClick = onVerMisSolicitudesClick) {
                        Text("Mis Solicitudes")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.equipos) { equipo ->
                EquipoItem(equipo = equipo, onClick = { onEquipoClick(equipo.id) })
            }
        }
    }
}

@Composable
fun EquipoItem(equipo: Equipo, onClick: () -> Unit) {
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
                Text(text = equipo.nombre, style = MaterialTheme.typography.titleMedium)
                Text(text = "Categoría: ${equipo.categoria}", style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = equipo.estado.name,
                style = MaterialTheme.typography.labelMedium,
                color = if (equipo.estado == EstadoEquipo.DISPONIBLE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}