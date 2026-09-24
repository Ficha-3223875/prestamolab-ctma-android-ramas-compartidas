package com.example.miprestamoslab.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.miprestamoslab.ui.PrestamoViewModel
import com.example.miprestamoslab.ui.screens.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Catalogo : Screen("catalogo")
    object EquipoDetalle : Screen("equipoDetalle/{equipoId}") {
        fun createRoute(equipoId: Int) = "equipoDetalle/$equipoId"
    }
    object SolicitudForm : Screen("solicitudForm/{equipoId}") {
        fun createRoute(equipoId: Int) = "solicitudForm/$equipoId"
    }
    object MisSolicitudes : Screen("misSolicitudes")
    object SolicitudDetalle : Screen("solicitudDetalle/{solicitudId}") {
        fun createRoute(solicitudId: Int) = "solicitudDetalle/$solicitudId"

    }
    object SolicitudesPendientes : Screen("solicitudesPendientes")
    object GestionInventario : Screen("gestionInventario")
}

@Composable
fun PrestamoNavHost(viewModel: PrestamoViewModel = viewModel(factory = PrestamoViewModel.Factory)) {
    val navController = rememberNavController()
    // Semana 7: recolección consciente del ciclo de vida (no se sigue emitiendo en background)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val evidencias by viewModel.evidencias.collectAsStateWithLifecycle()
    val luzAmbiente by viewModel.luzAmbiente.collectAsStateWithLifecycle()

    NavHost(navController = navController, startDestination = Screen.Login.route) {

        composable(Screen.Login.route) {
            LoginScreen(
                uiState = uiState,
                onLoginClick = { correo, contrasena ->
                    viewModel.login(correo, contrasena) {
                        navController.navigate(Screen.Catalogo.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                onLimpiarMensaje = { viewModel.limpiarMensaje() }
            )
        }

        composable(Screen.Catalogo.route) {
            CatalogoScreen(
                equipos = uiState.equipos,
                usuario = uiState.usuarioAutenticado,
                estadoCarga = uiState.estadoCarga,
                errorCarga = uiState.errorCarga,
                onReintentarCarga = { viewModel.reintentarCarga() },
                sincronizando = uiState.sincronizando,
                onSincronizar = { viewModel.sincronizar() },
                mensaje = uiState.mensaje,
                onLimpiarMensaje = { viewModel.limpiarMensaje() },
                onEquipoClick = { equipoId ->
                    navController.navigate(Screen.EquipoDetalle.createRoute(equipoId))
                },
                onVerMisSolicitudes = {
                    navController.navigate(Screen.MisSolicitudes.route)
                },
                onVerSolicitudesPendientes = { navController.navigate(Screen.SolicitudesPendientes.route) },
                onGestionInventario = { navController.navigate(Screen.GestionInventario.route) },
                onLogout = {
                    viewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Catalogo.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.EquipoDetalle.route,
            arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val equipoId = backStackEntry.arguments?.getInt("equipoId") ?: -1
            viewModel.cargarEquipo(equipoId)

            // HU-14: el sensor de luz solo está activo mientras esta pantalla está visible
            DisposableEffect(Unit) {
                viewModel.iniciarLecturaLuz()
                onDispose { viewModel.detenerLecturaLuz() }
            }

            EquipoDetalleScreen(
                equipo = uiState.equipoSeleccionado,
                mensaje = uiState.mensaje,
                luzAmbiente = luzAmbiente,
                onLimpiarMensaje = { viewModel.limpiarMensaje() },
                onSolicitar = { id ->
                    navController.navigate(Screen.SolicitudForm.createRoute(id))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.SolicitudForm.route,
            arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val equipoId = backStackEntry.arguments?.getInt("equipoId") ?: -1
            viewModel.cargarEquipo(equipoId)

            SolicitudFormScreen(
                equipo = uiState.equipoSeleccionado,
                guardando = uiState.guardando,
                mensaje = uiState.mensaje,
                onLimpiarMensaje = { viewModel.limpiarMensaje() },
                onGuardar = { ambiente, proposito, duracion ->
                    viewModel.crearSolicitud(
                        equipoId = equipoId,
                        ambiente = ambiente,
                        proposito = proposito,
                        duracion = duracion,
                        onSuccess = {
                            navController.navigate(Screen.MisSolicitudes.route) {
                                popUpTo(Screen.Catalogo.route) { inclusive = false }
                            }
                        }
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.MisSolicitudes.route) {
            MisSolicitudesScreen(
                solicitudes = uiState.solicitudes,
                cargando = uiState.estadoCarga == com.example.miprestamoslab.ui.EstadoCarga.CARGANDO,
                onSolicitudClick = { solicitudId ->
                    navController.navigate(Screen.SolicitudDetalle.createRoute(solicitudId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.SolicitudDetalle.route,
            arguments = listOf(navArgument("solicitudId") { type = NavType.IntType })
        ) { backStackEntry ->
            val solicitudId = backStackEntry.arguments?.getInt("solicitudId") ?: -1
            viewModel.cargarSolicitud(solicitudId)
            // HU-13: observa las evidencias adjuntas a esta solicitud
            viewModel.cargarEvidencias(solicitudId)

            SolicitudDetalleScreen(
                solicitud = uiState.solicitudSeleccionada,
                mensaje = uiState.mensaje,
                evidencias = evidencias,
                adjuntando = uiState.guardando,
                onAdjuntarEvidencia = { uri -> viewModel.registrarEvidencia(solicitudId, uri) },
                onLimpiarMensaje = { viewModel.limpiarMensaje() },
                onCancelar = { id ->
                    viewModel.cancelarSolicitud(id) {
                        navController.popBackStack()
                    }
                },
                onBack = { navController.popBackStack() }
            )

        }

        composable(Screen.SolicitudesPendientes.route) {
            SolicitudesPendientesScreen(
                solicitudesPendientes = uiState.solicitudes.filter {
                    it.estado == com.example.miprestamoslab.model.EstadoSolicitud.SOLICITADA
                },
                onAprobar = { id -> viewModel.aprobarSolicitud(id) },
                onRechazar = { id, razon -> viewModel.rechazarSolicitud(id, razon) },
                onBack = { navController.popBackStack() }
            )
        }

        // HU-09 / HU-10 / HU-11 / HU-12: administración del inventario
        composable(Screen.GestionInventario.route) {
            GestionInventarioScreen(
                viewModel = viewModel,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}