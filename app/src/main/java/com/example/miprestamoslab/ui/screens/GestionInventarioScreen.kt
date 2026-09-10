package com.example.miprestamoslab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.miprestamoslab.model.CategoriaEquipo
import com.example.miprestamoslab.model.Equipo
import com.example.miprestamoslab.model.EstadoEquipo
import com.example.miprestamoslab.ui.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionInventarioScreen(
    viewModel: PrestamoViewModel,
    onVolver: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    var mostrarDialogoCrear by remember { mutableStateOf(false) }
    var equipoAEditar by remember { mutableStateOf<Equipo?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Inventario (Sprint 4)") },
                actions = {
                    TextButton(onClick = onVolver) {
                        Text("Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogoCrear = true }) {
                Text("+")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            uiState.mensaje?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Text(
                text = "Catálogo de Equipos",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.equipos) { equipo ->
                    ItemEquipoAdmin(
                        equipo = equipo,
                        onEditar = { equipoAEditar = equipo },
                        onCambiarEstado = { nuevoEstado ->
                            viewModel.cambiarEstadoEquipo(equipo.id, nuevoEstado)
                        }
                    )
                }
            }
        }
    }

    // Modal para HU10: Agregar Nuevo Equipo
    if (mostrarDialogoCrear) {
        DialogoFormularioEquipo(
            equipo = null,
            onDismiss = { mostrarDialogoCrear = false },
            onGuardar = { nombre, cat, desc ->
                viewModel.agregarEquipo(nombre, cat, desc)
                mostrarDialogoCrear = false
            }
        )
    }

    // Modal para HU11: Editar Equipo
    equipoAEditar?.let { equipo ->
        DialogoFormularioEquipo(
            equipo = equipo,
            onDismiss = { equipoAEditar = null },
            onGuardar = { nombre, cat, desc ->
                viewModel.editarEquipo(equipo.id, nombre, cat, desc)
                equipoAEditar = null
            }
        )
    }
}

@Composable
fun ItemEquipoAdmin(
    equipo: Equipo,
    onEditar: () -> Unit,
    onCambiarEstado: (EstadoEquipo) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = equipo.nombre, style = MaterialTheme.typography.titleMedium)
            Text(text = "Categoría: ${equipo.categoria.name}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Estado: ${equipo.estado.name}", style = MaterialTheme.typography.bodySmall)
            if (equipo.descripcion.isNotBlank()) {
                Text(text = "Descripción: ${equipo.descripcion}", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = onEditar) {
                    Text("Editar")
                }

                // HU12: Cambiar Estado
                if (equipo.estado != EstadoEquipo.PRESTADO) {
                    if (equipo.estado != EstadoEquipo.EN_MANTENIMIENTO) {
                        Button(onClick = { onCambiarEstado(EstadoEquipo.EN_MANTENIMIENTO) }) {
                            Text("Mantenimiento")
                        }
                    }
                    if (equipo.estado != EstadoEquipo.DADO_DE_BAJA) {
                        Button(onClick = { onCambiarEstado(EstadoEquipo.DADO_DE_BAJA) }) {
                            Text("Dar de baja")
                        }
                    }
                    if (equipo.estado != EstadoEquipo.DISPONIBLE) {
                        OutlinedButton(onClick = { onCambiarEstado(EstadoEquipo.DISPONIBLE) }) {
                            Text("Habilitar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DialogoFormularioEquipo(
    equipo: Equipo?,
    onDismiss: () -> Unit,
    onGuardar: (String, CategoriaEquipo, String) -> Unit
) {
    var nombre by remember { mutableStateOf(equipo?.nombre ?: "") }
    var descripcion by remember { mutableStateOf(equipo?.descripcion ?: "") }
    var categoriaSeleccionada by remember { mutableStateOf(equipo?.categoria ?: CategoriaEquipo.ELECTRONICA) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (equipo == null) "Agregar Equipo (HU10)" else "Editar Equipo (HU11)") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del equipo") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombre.isNotBlank()) {
                        onGuardar(nombre, categoriaSeleccionada, descripcion)
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}