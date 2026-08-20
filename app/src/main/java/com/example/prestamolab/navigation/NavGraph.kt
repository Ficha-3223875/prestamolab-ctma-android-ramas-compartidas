package com.example.prestamolab.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.prestamolab.ui.catalogo.CatalogoScreen
import com.example.prestamolab.ui.equipo.EquipoDetalleScreen
import com.example.prestamolab.ui.misprestamos.MisSolicitudesScreen
import com.example.prestamolab.ui.solicitud.SolicitudDetalleScreen
import com.example.prestamolab.ui.solicitud.SolicitudFormScreen
import com.example.prestamolab.ui.viewmodel.PrestamoViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: PrestamoViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Destino.Catalogo.ruta
    ) {
        // Pantalla 1: Catálogo de Equipos
        composable(Destino.Catalogo.ruta) {
            CatalogoScreen(
                uiState = uiState,
                onEquipoClick = { equipoId ->
                    navController.navigate(Destino.EquipoDetalle.crearRuta(equipoId))
                },
                onVerMisSolicitudesClick = {
                    navController.navigate(Destino.MisSolicitudes.ruta)
                }
            )
        }

        // Pantalla 2: Detalle del Equipo
        composable(
            route = Destino.EquipoDetalle.ruta,
            arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val equipoId = backStackEntry.arguments?.getInt("equipoId") ?: -1
            EquipoDetalleScreen(
                equipoId = equipoId,
                uiState = uiState,
                onSolicitarClick = { id ->
                    navController.navigate(Destino.SolicitudForm.crearRuta(id))
                },
                onVolverClick = { navController.popBackStack() }
            )
        }

        // Pantalla 3: Formulario de Solicitud
        composable(
            route = Destino.SolicitudForm.ruta,
            arguments = listOf(navArgument("equipoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val equipoId = backStackEntry.arguments?.getInt("equipoId") ?: -1
            SolicitudFormScreen(
                equipoId = equipoId,
                uiState = uiState,
                onCrearSolicitud = { id, ambiente, proposito, duracion ->
                    viewModel.crearSolicitud(id, ambiente, proposito, duracion)
                },
                onLimpiarMensaje = { viewModel.limpiarMensajes() },
                onExito = {
                    navController.popBackStack(Destino.Catalogo.ruta, false)
                },
                onVolverClick = { navController.popBackStack() }
            )
        }

        // Pantalla 4: Mis Solicitudes
        composable(Destino.MisSolicitudes.ruta) {
            MisSolicitudesScreen(
                uiState = uiState,
                onSolicitudClick = { solicitudId ->
                    navController.navigate(Destino.SolicitudDetalle.crearRuta(solicitudId))
                },
                onVolverClick = { navController.popBackStack() }
            )
        }

        // Pantalla 5: Detalle de la Solicitud
        composable(
            route = Destino.SolicitudDetalle.ruta,
            arguments = listOf(navArgument("solicitudId") { type = NavType.IntType })
        ) { backStackEntry ->
            val solicitudId = backStackEntry.arguments?.getInt("solicitudId") ?: -1
            SolicitudDetalleScreen(
                solicitudId = solicitudId,
                uiState = uiState,
                onCancelarSolicitud = { id ->
                    viewModel.cancelarSolicitud(id)
                },
                onVolverClick = { navController.popBackStack() }
            )
        }
    }
}