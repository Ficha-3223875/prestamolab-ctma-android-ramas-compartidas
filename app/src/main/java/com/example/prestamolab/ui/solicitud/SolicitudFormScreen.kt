package com.example.prestamolab.ui.solicitud

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.prestamolab.ui.viewmodel.PrestamoUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudFormScreen(
    equipoId: Int,
    uiState: PrestamoUiState,
    onCrearSolicitud: (Int, String, String, Int) -> Unit,
    onLimpiarMensaje: () -> Unit,
    onExito: () -> Unit,
    onVolverClick: () -> Unit
) {
    val equipo = uiState.equipos.find { it.id == equipoId }

    var ambienteDestino by remember { mutableStateOf("") }
    var proposito by remember { mutableStateOf("") }
    var duracionHorasText by remember { mutableStateOf("1") }

    val duracionHoras = duracionHorasText.toIntOrNull() ?: 0

    // Manejo de redirección tras éxito o muestra de errores
    LaunchedEffect(uiState.mensajeExito) {
        if (uiState.mensajeExito != null) {
            onLimpiarMensaje()
            onExito()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitar Préstamo") },
                navigationIcon = {
                    TextButton(onClick = onVolverClick) {
                        Text("< Volver", style = MaterialTheme.typography.titleMedium)
                    }
                }
            )
        }
    ) { padding ->
        if (equipo == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Equipo no encontrado") // RN-08
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Equipo: ${equipo.nombre}",
                    style = MaterialTheme.typography.titleLarge
                )

                // Mensaje de error general si falla una validación o regla
                if (uiState.mensajeError != null) {
                    Text(
                        text = uiState.mensajeError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                OutlinedTextField(
                    value = ambienteDestino,
                    onValueChange = {
                        ambienteDestino = it
                        onLimpiarMensaje()
                    },
                    label = { Text("Ambiente / Destino (Obligatorio)") }, // RN-02
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = proposito,
                    onValueChange = {
                        proposito = it
                        onLimpiarMensaje()
                    },
                    label = { Text("Propósito (10 a 180 caracteres)") }, // RN-03
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    supportingText = {
                        Text("${proposito.trim().length}/180")
                    }
                )

                OutlinedTextField(
                    value = duracionHorasText,
                    onValueChange = {
                        duracionHorasText = it
                        onLimpiarMensaje()
                    },
                    label = { Text("Duración en horas (1 a 8 hrs)") }, // RN-04
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        onCrearSolicitud(equipoId, ambienteDestino, proposito, duracionHoras)
                    },
                    enabled = !uiState.guardando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.guardando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Confirmar Solicitud")
                    }
                }
            }
        }
    }
}