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
import com.example.prestamolab.data.model.Rol
import com.example.prestamolab.ui.viewmodel.PrestamoUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    uiState: PrestamoUiState,
    onEquipoClick: (Int) -> Unit,
    onVerMisSolicitudesClick: () -> Unit
) {
    val usuario = uiState.usuarioAutenticado
    val esEncargado = usuario?.rol == Rol.ENCARGADO

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("PréstamoLab CTMA")
                        Text(
                            text = if (esEncargado) "Rol: Encargado" else "Rol: Aprendiz",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onVerMisSolicitudesClick) {
                        // Cambia el texto del botón según el Rol
                        Text(
                            text = if (esEncargado) "Todas las Solicitudes" else "Mis Solicitudes",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.equipos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay equipos disponibles")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.equipos) { equipo ->
                    EquipoItem(
                        equipo = equipo,
                        onClick = { onEquipoClick(equipo.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun EquipoItem(
    equipo: Equipo,
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
            Column(modifier = Modifier.weight(1f)) {
                Text(text = equipo.nombre, style = MaterialTheme.typography.titleMedium)
                Text(text = "Categoría: ${equipo.categoria}", style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = equipo.estado.name,
                style = MaterialTheme.typography.labelMedium,
                color = if (equipo.estado.name == "DISPONIBLE")
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error
            )
        }
    }
}