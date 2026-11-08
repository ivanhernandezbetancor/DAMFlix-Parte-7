package com.example.damflix.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.damflix.PantallaInicio
import com.example.damflix.R
import com.example.damflix.viewmodel.PeliculaViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.damflix.DAMFlixApplication
import com.example.damflix.PantallaDetalleScreen
import com.example.damflix.viewmodel.DetallePeliculaViewModel
import com.example.damflix.viewmodel.DetallePeliculaViewModelFactory
import com.example.damflix.view.Ruta

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun AppDAMFlixMainScreen(viewModel: PeliculaViewModel) {
    val navController = rememberNavController()

    // 1. Estado del Drawer (Abierto / Cerrado)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // 2. CoroutineScope necesario para animar la apertura/cierre
    val coroutineScope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    val opcionesMenu = listOf(Ruta.Inicio, Ruta.Catalogo, Ruta.Formulario)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Cabecera del Menú Lateral
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                    Text(
                        text = stringResource(R.string.cabecera_drawer),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // Opciones editables del menú
                opcionesMenu.forEach { opcion ->
                    NavigationDrawerItem(
                        icon = { Icon(opcion.icono, contentDescription = stringResource(opcion.titulo)) },
                        label = { Text(stringResource(opcion.titulo)) },
                        selected = rutaActual == opcion.path,
                        onClick = {
                            // Cierre asíncrono del panel
                            coroutineScope.launch { drawerState.close() }
                            if (rutaActual != opcion.path) {
                                navController.navigate(opcion.path) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        // Contenido Principal envuelto en Scaffold
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.titulo_app)) },
                    navigationIcon = {
                        // Botón de hamburguesa para abrir el Drawer
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.abrir_menu))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Ruta.Inicio.path,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Ruta.Inicio.path) {
                    PantallaInicio()
                }

                composable(Ruta.Catalogo.path) {
                    ListaPeliculasScreen(
                        viewModel = viewModel,
                        onPeliculaClick = { peliculaId ->
                            navController.navigate(Ruta.Detalle.crearRuta(peliculaId))
                        }
                    )
                }

                composable(Ruta.Formulario.path) {
                    FormularioAgregarPeliculaScreen(
                        viewModel = viewModel,
                        onPeliculaGuardada = {
                            navController.navigate(Ruta.Catalogo.path) {
                                popUpTo(Ruta.Inicio.path) { saveState = true }
                            }
                        }
                    )
                }

                composable(
                    route = Ruta.Detalle.path,
                    arguments = listOf(
                        navArgument("peliculaId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("peliculaId") ?: 0
                    val context = LocalContext.current
                    val app = context.applicationContext as DAMFlixApplication
                    val detalleViewModel: DetallePeliculaViewModel = viewModel(
                        factory = DetallePeliculaViewModelFactory(app.repository)
                    )
                    PantallaDetalleScreen(
                        peliculaId = id,
                        viewModel = detalleViewModel,
                        onVolver = { navController.popBackStack() }
                    )
                }
            }}}}