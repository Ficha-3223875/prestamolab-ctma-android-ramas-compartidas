package com.example.miprestamoslab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.miprestamoslab.model.SolicitudPrestamo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudesPendientesScreen(
    solicitudesPendientes: List<SolicitudPrestamo>,
    onAprobar: (Int) -> Unit,
    onRechazar: (Int, String) -> Unit,
    onBack: () -> Unit
) {
    var solicitudARechazar by remember { mutableStateOf<Int?>(null) }
    var razon by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitudes Pendientes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (solicitudesPendientes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay solicitudes pendientes por revisar")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(solicitudesPendientes, key = { it.id }) { solicitud ->
                    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Solicitud #${solicitud.id}", style = MaterialTheme.typography.titleMedium)
                            Text("Equipo ID: ${solicitud.equipoId}")
                            Text("Ambiente: ${solicitud.ambienteDestino}")
                            Text("Propósito: ${solicitud.proposito}")
                            Text("Duración: ${solicitud.duracionHoras} horas")

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { onAprobar(solicitud.id) }) {
                                    Text("Aprobar")
                                }
                                OutlinedButton(
                                    onClick = {
                                        solicitudARechazar = solicitud.id
                                        razon = ""
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Rechazar")
                                }
                            }
                        }
                    }
                }
            }
        }

        if (solicitudARechazar != null) {
            AlertDialog(
                onDismissRequest = { solicitudARechazar = null },
                title = { Text("Razón del rechazo") },
                text = {
                    OutlinedTextField(
                        value = razon,
                        onValueChange = { razon = it },
                        label = { Text("Explica por qué se rechaza") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onRechazar(solicitudARechazar!!, razon)
                            solicitudARechazar = null
                        },
                        enabled = razon.trim().length >= 5
                    ) {
                        Text("Confirmar rechazo")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { solicitudARechazar = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}