package com.example.miprestamoslab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.miprestamoslab.model.EstadoSolicitud
import com.example.miprestamoslab.model.SolicitudPrestamo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudDetalleScreen(
    solicitud: SolicitudPrestamo?,
    mensaje: String?,
    onLimpiarMensaje: () -> Unit,
    onCancelar: (Int) -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            kotlinx.coroutines.delay(3000)
            onLimpiarMensaje()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Solicitud") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (solicitud == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Solicitud no encontrada", style = MaterialTheme.typography.headlineSmall)
                    Button(onClick = onBack) { Text("Volver") }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(text = "Solicitud #${solicitud.id}", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(16.dp))

                    DetalleItem("Equipo ID", solicitud.equipoId.toString())
                    DetalleItem("Ambiente/Destino", solicitud.ambienteDestino)
                    DetalleItem("Propósito", solicitud.proposito)
                    DetalleItem("Duración", "${solicitud.duracionHoras} horas")
                    DetalleItem("Estado", solicitud.estado.name)

                    Spacer(modifier = Modifier.height(24.dp))

                    if (solicitud.estado == EstadoSolicitud.SOLICITADA) {
                        Button(
                            onClick = { onCancelar(solicitud.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cancelar Solicitud")
                        }
                        Text(
                            text = "Solo las solicitudes en estado SOLICITADA pueden cancelarse.",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else {
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.outlinedCardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = "Esta solicitud no puede cancelarse porque su estado es ${solicitud.estado.name}.",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }

            if (mensaje != null) {
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                ) {
                    Text(mensaje)
                }
            }
        }
    }
}

@Composable
fun DetalleItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}