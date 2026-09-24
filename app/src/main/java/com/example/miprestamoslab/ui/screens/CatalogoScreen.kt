package com.example.miprestamoslab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
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
import com.example.miprestamoslab.ui.EstadoCarga
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    equipos: List<Equipo>,
    usuario: Usuario?,
    estadoCarga: EstadoCarga = EstadoCarga.CONTENIDO,
    errorCarga: String? = null,
    onEquipoClick: (Int) -> Unit,
    onVerMisSolicitudes: () -> Unit,
    onVerSolicitudesPendientes: () -> Unit,
    onGestionInventario: () -> Unit,
    sincronizando: Boolean = false,
    onSincronizar: () -> Unit = {},
    onReintentarCarga: () -> Unit = {},
    mensaje: String? = null,
    onLimpiarMensaje: () -> Unit = {},
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
                        // HU-07: panel de solicitudes pendientes
                        IconButton(onClick = onVerSolicitudesPendientes) {
                            Icon(Icons.Default.List, contentDescription = "Solicitudes pendientes")
                        }
                        // HU-09/HU-10/HU-11/HU-12: gestión de inventario
                        IconButton(
                            onClick = onGestionInventario,
                            modifier = Modifier.semantics {
                                contentDescription = "Gestionar inventario"
                            }
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                        }
                    }
                    // Semana 8: sincronización local <-> API (local-first)
                    IconButton(
                        onClick = onSincronizar,
                        enabled = !sincronizando,
                        modifier = Modifier.semantics { contentDescription = "Sincronizar con el servidor" }
                    ) {
                        if (sincronizando) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = null)
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
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Banner de operaciones (resultado de sincronización, avisos del ViewModel)
            if (mensaje != null) {
                LaunchedEffect(mensaje) {
                    delay(MILISEGUNDOS_BANNER)
                    onLimpiarMensaje()
                }
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Estados de carga (Semana 7): siempre hay un estado explícito en pantalla
            when (estadoCarga) {
                EstadoCarga.CARGANDO -> EstadoCargando(mensaje = "Cargando equipos…")

                EstadoCarga.ERROR -> EstadoError(
                    mensaje = errorCarga ?: "No fue posible cargar el catálogo",
                    onReintentar = onReintentarCarga
                )

                EstadoCarga.VACIO, EstadoCarga.CONTENIDO -> CatalogoContenido(
                    equiposFiltrados = equiposFiltrados,
                    textoBusqueda = textoBusqueda,
                    onTextoBusquedaChange = { textoBusqueda = it },
                    categoriaSeleccionada = categoriaSeleccionada,
                    onCategoriaChange = { categoriaSeleccionada = it },
                    catalogoVacio = equipos.isEmpty(),
                    onEquipoClick = onEquipoClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

private const val MILISEGUNDOS_BANNER = 4000L

@Composable
private fun CatalogoContenido(
    equiposFiltrados: List<Equipo>,
    textoBusqueda: String,
    onTextoBusquedaChange: (String) -> Unit,
    categoriaSeleccionada: CategoriaEquipo?,
    onCategoriaChange: (CategoriaEquipo?) -> Unit,
    catalogoVacio: Boolean,
    onEquipoClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = onTextoBusquedaChange,
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
                onClick = { onCategoriaChange(null) },
                text = { Text("Todos") }
            )
            CategoriaEquipo.values().forEach { categoria ->
                Tab(
                    selected = categoriaSeleccionada == categoria,
                    onClick = { onCategoriaChange(categoria) },
                    text = { Text(categoria.name.lowercase().replaceFirstChar { it.uppercase() }) }
                )
            }
        }

        when {
            catalogoVacio -> EstadoVacio(
                mensaje = "Aún no hay equipos registrados en el inventario",
                modifier = Modifier.fillMaxSize()
            )

            equiposFiltrados.isEmpty() -> EstadoVacio(
                mensaje = "No se encontraron equipos con los criterios seleccionados",
                modifier = Modifier.fillMaxSize()
            )

            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(equiposFiltrados, key = { it.id }) { equipo ->
                    EquipoCard(equipo = equipo, onClick = { onEquipoClick(equipo.id) })
                }
            }
        }
    }
}

/** Estado de carga: progreso visible mientras llega el primer valor de los flujos. */
@Composable
fun EstadoCargando(mensaje: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.semantics { contentDescription = "Indicador de carga" }
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = mensaje, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

/** Estado de error recuperable: ofrece reintentar en lugar de cerrar la app. */
@Composable
fun EstadoError(mensaje: String, onReintentar: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onReintentar,
                modifier = Modifier.semantics { contentDescription = "Reintentar carga" }
            ) {
                Text("Reintentar")
            }
        }
    }
}

/** Estado vacío explícito (empty state). */
@Composable
fun EstadoVacio(mensaje: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.semantics { contentDescription = mensaje }
        )
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
