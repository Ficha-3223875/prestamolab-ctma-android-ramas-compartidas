package com.example.miprestamoslab.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.miprestamoslab.model.EstadoSolicitud
import com.example.miprestamoslab.model.SolicitudPrestamo
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudDetalleScreen(
    solicitud: SolicitudPrestamo?,
    mensaje: String?,
    onLimpiarMensaje: () -> Unit,
    onCancelar: (Int) -> Unit,
    onRegistrarDevolucion: (Int, String) -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            kotlinx.coroutines.delay(3000)
            onLimpiarMensaje()
        }
    }

    val context = LocalContext.current
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var showPhotoDialog by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempPhotoUri != null && solicitud != null) {
            onRegistrarDevolucion(solicitud.id, tempPhotoUri.toString())
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null && solicitud != null) {
            onRegistrarDevolucion(solicitud.id, uri.toString())
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val photoFile = File(context.cacheDir, "devolucion_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(context, "com.example.miprestamoslab.fileprovider", photoFile)
            tempPhotoUri = uri
            cameraLauncher.launch(uri)
        }
    }

    if (showPhotoDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoDialog = false },
            title = { Text("Evidencia de Devolución") },
            text = { Text("¿Cómo deseas adjuntar la fotografía de devolución?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPhotoDialog = false
                        permissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                ) {
                    Text("Tomar Foto")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPhotoDialog = false
                        galleryLauncher.launch("image/*")
                    }
                ) {
                    Text("Elegir de Galería")
                }
            }
        )
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

                    if (solicitud.fotoDevolucionUri != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        DetalleItem("Foto de evidencia", "Adjunta (" + solicitud.syncStatus + ")")
                        Text(
                            text = "URI: ${solicitud.fotoDevolucionUri}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val uri = Uri.parse(solicitud.fotoDevolucionUri)
                                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                                    setDataAndType(uri, "image/*")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    // Handle if no app available to view image
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Abrir Evidencia Fotográfica")
                        }
                    }

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

                    if (solicitud.estado == EstadoSolicitud.APROBADA || solicitud.estado == EstadoSolicitud.ENTREGADA) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showPhotoDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Adjuntar/Capturar Foto de Devolución")
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
