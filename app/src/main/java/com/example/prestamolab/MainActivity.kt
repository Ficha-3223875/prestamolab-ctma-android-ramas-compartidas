package com.example.prestamolab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.prestamolab.data.repository.InMemoryPrestamoRepository
import com.example.prestamolab.navigation.AppNavGraph
import com.example.prestamolab.ui.theme.PrestamolabTheme
import com.example.prestamolab.ui.viewmodel.PrestamoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = InMemoryPrestamoRepository()
        val viewModel = PrestamoViewModel(repository)

        setContent {
            PrestamolabTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavGraph(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}