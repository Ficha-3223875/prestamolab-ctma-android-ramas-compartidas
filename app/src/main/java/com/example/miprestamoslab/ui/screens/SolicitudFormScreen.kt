package com.example.miprestamoslab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.miprestamoslab.model.Equipo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudFormScreen(
    equipo: Equipo?,
    guardando: Boolean,
    mensaje: String?,
    onLimpiarMensaje: () -> Unit,
    onGuardar: (String, String, String) -> Unit,
    onBack: () -> Unit
) {
    var ambiente by remember { mutableStateOf("") }
    var proposito by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }

    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            kotlinx.coroutines.delay(4000)
            onLimpiarMensaje()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Solicitud") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (equipo == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Error: equipo no encontrado", style = MaterialTheme.typography.headlineSmall)
                    Button(onClick = onBack) { Text("Volver") }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .imePadding()
                ) {
                    Text(
                        text = "Equipo: ${equipo.nombre}",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = ambiente,
                        onValueChange = { ambiente = it },
                        label = { Text("Ambiente o destino *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = mensaje?.contains("ambiente", ignoreCase = true) == true,
                        supportingText = {
                            if (mensaje?.contains("ambiente", ignoreCase = true) == true) {
                                Text(mensaje, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = proposito,
                        onValueChange = { proposito = it },
                        label = { Text("Propósito (10-180 caracteres) *") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        isError = mensaje?.contains("propósito", ignoreCase = true) == true,
                        supportingText = {
                            Text("${proposito.length} / 180 caracteres")
                            if (mensaje?.contains("propósito", ignoreCase = true) == true) {
                                Text(mensaje, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = duracion,
                        onValueChange = { duracion = it.filter { c -> c.isDigit() } },
                        label = { Text("Duración estimada (1-8 horas) *") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = mensaje?.contains("duración", ignoreCase = true) == true,
                        supportingText = {
                            if (mensaje?.contains("duración", ignoreCase = true) == true) {
                                Text(mensaje, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { onGuardar(ambiente, proposito, duracion) },
                        enabled = !guardando,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (guardando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Guardar Solicitud")
                    }
                }
            }

            if (mensaje != null && !mensaje.contains("ambiente", true) && !mensaje.contains("propósito", true) && !mensaje.contains("duración", true)) {
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                ) {
                    Text(mensaje)
                }
            }
        }
    }
}