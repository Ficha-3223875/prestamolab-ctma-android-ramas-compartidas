package com.example.miprestamoslab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.miprestamoslab.model.CategoriaEquipo
import com.example.miprestamoslab.model.Equipo
import com.example.miprestamoslab.model.EstadoEquipo
import com.example.miprestamoslab.model.Rol
import com.example.miprestamoslab.model.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    equipos: List<Equipo>,
    usuario: Usuario?,
    onEquipoClick: (Int) -> Unit,
    onVerMisSolicitudes: () -> Unit,
    onVerSolicitudesPendientes: () -> Unit,
    onLogout: () -> Unit
) {
    var textoBusqueda by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf<CategoriaEquipo?>(null) }
    val esEncargado = usuario?.rol == Rol.ENCARGADO

    val equiposFiltrados = equipos.filter { equipo ->
        val coincideNombre = equipo.nombre.contains(textoBusqueda, ignoreCase = true)
        val coincideCategoria = categoriaSeleccionada == null || equipo.categoria == categoriaSeleccionada
        coincideNombre && coincideCategoria
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Catálogo de Equipos")
                        usuario?.let {
                            Text(
                                text = "Hola, ${it.nombre} (${it.rol})",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                },
                actions = {
                    if (esEncargado) {
                        IconButton(onClick = onVerSolicitudesPendientes) {
                            Icon(Icons.Default.List, contentDescription = "Solicitudes pendientes")
                        }
                    }
                    TextButton(onClick = onLogout) {
                        Text("Salir")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onVerMisSolicitudes,
                icon = { Icon(Icons.Default.List, contentDescription = null) },
                text = { Text(if (esEncargado) "Historial" else "Mis Solicitudes") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                label = { Text("Buscar equipo...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )

            ScrollableTabRow(
                selectedTabIndex = if (categoriaSeleccionada == null) 0 else CategoriaEquipo.values()
                    .indexOf(categoriaSeleccionada) + 1,
                edgePadding = 16.dp,
                divider = {},
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = categoriaSeleccionada == null,
                    onClick = { categoriaSeleccionada = null },
                    text = { Text("Todos") }
                )
                CategoriaEquipo.values().forEach { categoria ->
                    Tab(
                        selected = categoriaSeleccionada == categoria,
                        onClick = { categoriaSeleccionada = categoria },
                        text = { Text(categoria.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

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
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(equiposFiltrados, key = { it.id }) { equipo ->
                        EquipoCard(equipo = equipo, onClick = { onEquipoClick(equipo.id) })
                    }
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
        EstadoEquipo.EN_MANTENIMIENTO -> "En Mantenimiento"
        EstadoEquipo.DADO_DE_BAJA -> "Dado de Baja"
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