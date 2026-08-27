package com.example.prestamolabctma_2.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.prestamolabctma_2.model.Equipo
import com.example.prestamolabctma_2.model.EstadoEquipo

@Composable
fun CatalogoScreen(
    equipos: List<Equipo>,
    onSolicitarClick: (Int) -> Unit,
    onVerSolicitudesClick: () -> Unit
) {
    var textoBusqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf("Todas") }

    val categorias = remember(equipos) {
        listOf("Todas") + equipos.map { it.categoria }.distinct()
    }

    val equiposFiltrados = equipos.filter { equipo ->
        val coincideNombre = equipo.nombre.contains(textoBusqueda, ignoreCase = true)
        val coincideCategoria = categoriaSeleccionada == "Todas" || equipo.categoria == categoriaSeleccionada
        coincideNombre && coincideCategoria
    }

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
                text = "Catálogo CTMA",
                style = MaterialTheme.typography.headlineMedium
            )
            Button(onClick = onVerSolicitudesClick) {
                Text("Mis Solicitudes")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = { textoBusqueda = it },
            label = { Text("Buscar equipo por nombre...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Categoría:", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categorias.forEach { cat ->
                FilterChip(
                    selected = (categoriaSeleccionada == cat),
                    onClick = { categoriaSeleccionada = cat },
                    label = { Text(cat) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (equiposFiltrados.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontraron equipos con los criterios seleccionados",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(equiposFiltrados) { equipo ->
                    EquipoItem(
                        equipo = equipo,
                        onSolicitarClick = { onSolicitarClick(equipo.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun EquipoItem(
    equipo: Equipo,
    onSolicitarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = equipo.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Categoría: ${equipo.categoria}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Estado: ${equipo.estado}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (equipo.estado == EstadoEquipo.DISPONIBLE) {
                Button(onClick = onSolicitarClick) {
                    Text("Solicitar")
                }
            }
        }
    }
}