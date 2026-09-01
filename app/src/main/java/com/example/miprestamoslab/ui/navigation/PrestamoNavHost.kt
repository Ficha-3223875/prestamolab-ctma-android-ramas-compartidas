package com.example.miprestamoslab.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.miprestamoslab.ui.PrestamoViewModel
import com.example.miprestamoslab.ui.screens.CatalogoScreen
import com.example.miprestamoslab.ui.screens.SolicitudesScreen

sealed class Screen(val route: String) {
    object Catalogo : Screen("catalogo")
    object Solicitudes : Screen("solicitudes")
}

@Composable
fun PrestamoNavHost(
    navController: NavHostController = rememberNavController(),
    viewModel: PrestamoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Catalogo.route
    ) {
        composable(Screen.Catalogo.route) {
            CatalogoScreen(
                equipos = uiState.equipos,
                usuario = uiState.usuarioAutenticado,
                onEquipoClick = { equipoId ->
                    viewModel.cargarEquipo(equipoId)
                },
                onVerMisSolicitudes = {
                    navController.navigate(Screen.Solicitudes.route)
                },
                onVerSolicitudesPendientes = {
                    navController.navigate(Screen.Solicitudes.route)
                },
                onLogout = {
                    viewModel.logout()
                }
            )
        }

        composable(Screen.Solicitudes.route) {
            SolicitudesScreen(
                solicitudes = uiState.solicitudes,
                onAprobar = { id -> viewModel.aprobarSolicitud(id) },
                onEntregar = { id -> viewModel.registrarEntregaFisica(id) }, // HU_08
                onDevolver = { id -> viewModel.registrarDevolucion(id) },   // HU_09
                onVolver = { navController.popBackStack() }
            )
        }
    }
}