package com.example.miprestamoslab.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.miprestamoslab.ui.PrestamoViewModel
import com.example.miprestamoslab.ui.screens.*

sealed class Screen(val route: String) {
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
}

@Composable
fun PrestamoNavHost(viewModel: PrestamoViewModel = viewModel()) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    NavHost(navController = navController, startDestination = Screen.Catalogo.route) {

        composable(Screen.Catalogo.route) {
            CatalogoScreen(
                equipos = uiState.equipos,
                onEquipoClick = { equipoId ->
                    navController.navigate(Screen.EquipoDetalle.createRoute(equipoId))
                },
                onVerMisSolicitudes = {
                    navController.navigate(Screen.MisSolicitudes.route)
                }
            )
        }

        composable(
            route = Screen.EquipoDetalle.route,
            arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val equipoId = backStackEntry.arguments?.getInt("equipoId") ?: -1
            viewModel.cargarEquipo(equipoId)

            EquipoDetalleScreen(
                equipo = uiState.equipoSeleccionado,
                mensaje = uiState.mensaje,
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

            SolicitudDetalleScreen(
                solicitud = uiState.solicitudSeleccionada,
                mensaje = uiState.mensaje,
                onLimpiarMensaje = { viewModel.limpiarMensaje() },
                onCancelar = { id ->
                    viewModel.cancelarSolicitud(id) {
                        navController.popBackStack()
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}