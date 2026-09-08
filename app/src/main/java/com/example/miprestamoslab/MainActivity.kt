package com.example.miprestamoslab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.miprestamoslab.ui.navigation.PrestamoNavHost
import com.example.miprestamoslab.ui.theme.MiPrestamosLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiPrestamosLabTheme {
                PrestamoNavHost()
            }
        }
    }
}