package com.example.prestamolabctma_2.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SolicitudScreen(
    equipoNombre: String,
    guardando: Boolean,
    onEnviarSolicitud: (String, String, Int) -> Unit,
    onVolver: () -> Unit
) {
    var ambiente by remember { mutableStateOf("") }
    var proposito by remember { mutableStateOf("") }
    var duracionTexto by remember { mutableStateOf("1") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Solicitar: $equipoNombre",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = ambiente,
            onValueChange = { ambiente = it },
            label = { Text("Ambiente de destino (ej: Lab 302)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = proposito,
            onValueChange = { proposito = it },
            label = { Text("Propósito (mínimo 10 caracteres)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        OutlinedTextField(
            value = duracionTexto,
            onValueChange = { duracionTexto = it },
            label = { Text("Duración en horas (1 a 8)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val horas = duracionTexto.toIntOrNull() ?: 1
                onEnviarSolicitud(ambiente, proposito, horas)
            },
            enabled = !guardando && ambiente.isNotBlank() && proposito.trim().length >= 10,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (guardando) "Enviando..." else "Confirmar Solicitud")
        }

        Button(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar y Volver")
        }
    }
}