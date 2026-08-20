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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(equipos) { equipo ->
                EquipoItem(
                    equipo = equipo,
                    onSolicitarClick = { onSolicitarClick(equipo.id) }
                )
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