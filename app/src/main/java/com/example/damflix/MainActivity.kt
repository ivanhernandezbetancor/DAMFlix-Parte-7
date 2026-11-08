package com.example.damflix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.damflix.ui.screens.AppDAMFlixMainScreen
import com.example.damflix.ui.theme.DAMflixTheme
import com.example.damflix.viewmodel.PeliculaViewModel
import com.example.damflix.viewmodel.PeliculaViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Obtenemos el contenedor global de la aplicación
        val app = application as DAMFlixApplication

        // 2. Creamos la fábrica pasándole el repositorio Singleton almacenado en Application
        val factory = PeliculaViewModelFactory(app.repository)

        setContent {
            DAMflixTheme {
                // 3. Compose obtiene y mantiene el ViewModel retenido ante rotaciones
                val viewModel: PeliculaViewModel = viewModel(factory = factory)

                // 4. Invocamos la pantalla principal pasando el ViewModel
                AppDAMFlixMainScreen(viewModel = viewModel)
            }
        }
    }
}