<div align="center">

# 🎬 DAMFlix · Parte 7

**Aplicación Android para almacenar y consultar películas**

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Material 3](https://img.shields.io/badge/Material%203-757575?style=for-the-badge&logo=materialdesign&logoColor=white)
![Room](https://img.shields.io/badge/Room-003B57?style=for-the-badge&logo=sqlite&logoColor=white)
![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84?style=for-the-badge&logo=androidstudio&logoColor=white)

**Módulo:** Programación Multimedia y Dispositivos Móviles  
**Unidad de Trabajo Nº2:** Desarrollo de aplicaciones Android  
**Parte 7 · Actividad 7:** Navegación con Parámetros y Corrutinas Asíncronas

2º DAM · CIFP Villa de Agüimes

</div>

---

## 🎯 Objetivo

Completar la arquitectura de **DAMFlix** implementando el patrón **Maestro-Detalle**: al pulsar una película del catálogo se abre una pantalla de detalle. Para ello se configura la navegación con **parámetros dinámicos** (`peliculaId`) y se usan **corrutinas de Kotlin** con lectura asíncrona en un hilo secundario (`Dispatchers.IO`) para consultar la ficha en **Room** y mostrar el estado en Jetpack Compose.

## ✅ Requisitos funcionales

| Requisito | Cómo se cumple |
|---|---|
| **1. Consulta por id en el DAO y el repositorio** | `PeliculaDao.obtenerPorId(id)` con `@Query("SELECT * FROM peliculas WHERE id = :id")`, función `suspend` que devuelve `PeliculaEntity?`. `PeliculaRepository` la expone también como `suspend` |
| **2. Estado de la UI (`DetalleUiState`)** | Interfaz sellada con tres estados: `Cargando`, `Exito(pelicula)` y `Error(mensaje)` |
| **3. ViewModel dedicado** | `DetallePeliculaViewModel` expone un `StateFlow<DetalleUiState>`. `cargarDetallePelicula` lanza la corrutina con `viewModelScope.launch` y lee la base de datos con `withContext(Dispatchers.IO)` |
| **4. Navegación con argumentos** | `Ruta.Detalle` con el destino `"detalle/{peliculaId}"` y la función auxiliar `crearRuta(peliculaId)`. En el `NavHost` se registra con `navArgument("peliculaId") { type = NavType.IntType }`. El `onClick` de cada tarjeta navega al detalle con su id |
| **5. Pantalla de detalle (`PantallaDetalleScreen`)** | `LaunchedEffect(peliculaId)` dispara la carga. `CircularProgressIndicator` en `Cargando`, `Card` con toda la información en `Exito` y botón para volver con `navController.popBackStack()` |
| **Prohibición de cadenas hardcoded** | Todos los textos de las pantallas, el Drawer y los botones salen de `strings.xml` con `stringResource()` |

## 🏗️ Arquitectura

```
┌──────────────────────────────────────────────────────┐
│  UI / Jetpack Compose                                │
│  (Catálogo, Formulario, Detalle, Drawer)             │
└───────────────┬───────────────────────┬──────────────┘
                │ Observa StateFlow     │ Observa StateFlow
                ▼                       ▼
┌───────────────────────────┐ ┌────────────────────────────────┐
│ PeliculaViewModel         │ │ DetallePeliculaViewModel       │
│ (lista del catálogo)      │ │ (estado: DetalleUiState)       │
└───────────────┬───────────┘ └────────────────┬───────────────┘
                │ Flow                         │ suspend + Dispatchers.IO
                ▼                              ▼
┌──────────────────────────────────────────────────────┐
│  PeliculaRepository                                  │
└──────────────────────────┬───────────────────────────┘
                           │ Llama al DAO
                           ▼
┌──────────────────────────────────────────────────────┐
│  PeliculaDao  ←  AppDatabase (Room)                  │
└──────────────────────────────────────────────────────┘
```

## ⚙️ Funcionamiento

### Capa de datos: consulta por identificador

Se añade al DAO una consulta que devuelve una única película. Es `suspend` porque se ejecuta una sola vez (no necesita `Flow`) y el resultado puede ser `null` si el id no existe:

```kotlin
@Query("SELECT * FROM peliculas WHERE id = :id")
suspend fun obtenerPorId(id: Int): PeliculaEntity?
```

El repositorio simplemente la expone:

```kotlin
suspend fun obtenerPorId(id: Int): PeliculaEntity? {
    return dao.obtenerPorId(id)
}
```

### Estado de la UI: DetalleUiState

Una interfaz sellada obliga a la pantalla a tratar todos los casos posibles con un `when` exhaustivo:

```kotlin
sealed interface DetalleUiState {
    object Cargando : DetalleUiState
    data class Exito(val pelicula: PeliculaEntity) : DetalleUiState
    data class Error(val mensaje: String) : DetalleUiState
}
```

| Estado | Cuándo ocurre | Qué muestra la pantalla |
|---|---|---|
| `Cargando` | Estado inicial y mientras se lee la base de datos | `CircularProgressIndicator` |
| `Exito` | La película existe en la base de datos | Tarjeta con la información |
| `Error` | No existe ninguna película con ese id | Mensaje de error |

### ViewModel: DetallePeliculaViewModel

Guarda el estado en un `MutableStateFlow` privado y expone solo la versión de lectura (`StateFlow`):

```kotlin
fun cargarDetallePelicula(peliculaId: Int, mensajeNoEncontrada: String) {
    viewModelScope.launch {
        _uiState.value = DetalleUiState.Cargando

        val resultado = withContext(Dispatchers.IO) {
            repository.obtenerPorId(peliculaId)
        }

        _uiState.value = if (resultado != null) {
            DetalleUiState.Exito(resultado)
        } else {
            DetalleUiState.Error(mensajeNoEncontrada)
        }
    }
}
```

- `viewModelScope.launch` lanza la corrutina y la cancela sola si el ViewModel se destruye.
- `withContext(Dispatchers.IO)` mueve la lectura de Room a un hilo secundario para no bloquear la interfaz.
- El mensaje de error llega como parámetro porque el ViewModel no tiene acceso a `stringResource()`. Lo lee la pantalla desde `strings.xml` y se lo pasa, así no hay texto escrito en el código.
- En el código hay un `delay(1000)` de simulación para poder apreciar el indicador de carga.

El `DetallePeliculaViewModelFactory` construye el ViewModel con el repositorio, igual que se hizo con `PeliculaViewModel`.

### Navegación con argumentos dinámicos

La clase sellada `Ruta` incorpora el destino con parámetro:

```kotlin
object Detalle : Ruta("detalle/{peliculaId}", R.string.menu_detalle, Icons.Filled.Details) {
    fun crearRuta(peliculaId: Int) = "detalle/$peliculaId"
}
```

En el `NavHost` el destino declara el argumento como entero y lo lee del `backStackEntry`:

```kotlin
composable(
    route = Ruta.Detalle.path,
    arguments = listOf(navArgument("peliculaId") { type = NavType.IntType })
) { backStackEntry ->
    val id = backStackEntry.arguments?.getInt("peliculaId") ?: 0
    // ... crea DetallePeliculaViewModel con su factory
    PantallaDetalleScreen(
        peliculaId = id,
        viewModel = detalleViewModel,
        onVolver = { navController.popBackStack() }
    )
}
```

El catálogo recibe una lambda `onPeliculaClick` y cada tarjeta la llama con su id:

```kotlin
onPeliculaClick = { peliculaId ->
    navController.navigate(Ruta.Detalle.crearRuta(peliculaId))
}
```

El destino de detalle no aparece en las opciones del Drawer: solo se llega a él desde el catálogo.

### Pantalla de detalle: PantallaDetalleScreen

| Elemento | Qué hace |
|---|---|
| `LaunchedEffect(peliculaId)` | Dispara la carga al entrar en la pantalla y la repite si cambia el id |
| `collectAsStateWithLifecycle()` | Recoge el `StateFlow` respetando el ciclo de vida |
| `when (uiState)` | Dibuja la interfaz según el estado: `Cargando`, `Exito` o `Error` |
| `CircularProgressIndicator` | Se muestra mientras el estado es `Cargando` |
| `Card` | En `Exito`, muestra título, director, año, si está vista y si es favorita |
| `Button` + `onVolver` | Vuelve al catálogo con `navController.popBackStack()` |

### Textos en strings.xml

Los textos nuevos de esta actividad están en la sección `Actividad 7`: `detalle_volver`, `detalle_cargando`, `detalle_director`, `detalle_anio`, `detalle_vista`, `detalle_pendiente`, `detalle_favorita`, `detalle_no_favorita`, `detalle_no_encontrada` y `menu_detalle`. Los datos de cada película (título, director...) vienen de la base de datos, no son textos fijos de la interfaz.

## 🗂️ Estructura

```
app/src/main
├── AndroidManifest.xml
├── java/com/example/damflix
│   ├── MainActivity.kt
│   ├── DAMFlixApplication.kt                    # Base de datos y repositorio (by lazy)
│   ├── Pantallas.kt                             # PantallaInicio, perfil y PantallaDetalleScreen
│   ├── model
│   │   ├── PeliculaEntity.kt                    # @Entity
│   │   ├── PeliculaDao.kt                       # @Dao (+ obtenerPorId)
│   │   ├── AppDatabase.kt                       # @Database (Singleton)
│   │   └── PeliculaRepository.kt                # (+ obtenerPorId)
│   ├── viewmodel
│   │   ├── PeliculaViewModel.kt
│   │   ├── PeliculaViewModelFactory.kt
│   │   ├── DetalleUIState.kt                    # Estado sellado del detalle
│   │   ├── DetallePeliculaViewModel.kt          # Carga asíncrona con Dispatchers.IO
│   │   └── DetallePeliculaViewModelFactory.kt
│   ├── view
│   │   └── Ruta.kt                              # Rutas (+ Detalle con crearRuta)
│   └── ui
│       ├── navigation
│       │   ├──  AppDAMFlixMainScreen.kt          # Drawer, Scaffold y NavHost
│       ├── screens
│       │   ├── FormularioAgregarPeliculaScreen.kt
│       │   └── ListaPeliculasScreen.kt          # Tarjetas con onClick hacia el detalle
│       └── theme
└── res
    └── values
        └── strings.xml                          # Todos los textos de la interfaz
```

Las clases de Room están en el paquete `model` (capa de datos), los ViewModel en `viewmodel` y las rutas de navegación en `view`.

## 🧪 Cómo probar el patrón Maestro-Detalle

1. Abre el menú lateral y entra en **Añadir Película**. Crea una o dos películas.
2. Ve al **Catálogo**: aparecen en la lista.
3. Marca alguna como favorita con el corazón.
4. Pulsa sobre una **tarjeta**: se abre el detalle y aparece durante un segundo el indicador de carga.
5. Al terminar se muestra la tarjeta con título, director, año, estado (vista o pendiente) y si es favorita.
6. Pulsa **← Volver al Catálogo**: regresas a la lista.
7. Repite con otra película y comprueba que el detalle muestra los datos de la película elegida.

## 🚀 Cómo ejecutarlo

1. Clona el repositorio:
```bash
git clone https://github.com/ivanhernandezbetancor/DAMFlix-PARTE-7.git
```
2. Ábrelo con **Android Studio**.
3. Espera a que termine la sincronización de Gradle.
4. Ejecuta la app en un emulador o en un dispositivo físico.

## 🛠️ Tecnologías

- Kotlin
- Jetpack Compose
- Material 3 (`ModalNavigationDrawer`, `Card`)
- Navigation Compose (argumentos con `navArgument` y `NavType.IntType`)
- Room ORM + KSP
- Corrutinas (`viewModelScope`, `withContext`, `Dispatchers.IO`) y `Flow`
- ViewModel y `StateFlow`

### Dependencias necesarias

En `dependencies` de `app/build.gradle.kts`:

```kotlin
implementation("androidx.compose.material:material-icons-core")
implementation("androidx.compose.material:material-icons-extended") // Icons.Filled.Details

val roomVersion = "2.8.5"
implementation("androidx.room:room-runtime:$roomVersion")
implementation("androidx.room:room-ktx:$roomVersion")
ksp("androidx.room:room-compiler:$roomVersion")

implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
implementation("androidx.navigation:navigation-compose:2.8.7")
```
