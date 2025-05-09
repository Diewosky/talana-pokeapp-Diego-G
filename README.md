# TalanaPokeApp

TalanaPokeApp es una aplicación Android sencilla construida con Kotlin y Jetpack Compose que muestra una lista de Pokémon obtenida de la [PokeAPI](https://pokeapi.co/). La aplicación carga y muestra los nombres e imágenes de los Pokémon.

## Características Actuales

*   Muestra una lista de los primeros 20 Pokémon.
*   Para cada Pokémon, se muestra su nombre y su sprite (imagen) oficial.
*   Interfaz de usuario construida enteramente con Jetpack Compose.
*   Carga de datos de red utilizando Retrofit.
*   Carga de imágenes asíncrona utilizando Coil.
*   Arquitectura básica siguiendo patrones MVVM (Model-View-ViewModel) con un Repositorio para el acceso a datos.

## Tecnologías Utilizadas

*   **Lenguaje:** Kotlin
*   **UI Toolkit:** Jetpack Compose
*   **Networking:** Retrofit (para consumir la PokeAPI)
*   **Conversión JSON:** Gson (con Retrofit)
*   **Carga de Imágenes:** Coil (Compose)
*   **Componentes de Arquitectura de Android:** ViewModel, StateFlow
*   **API:** [PokeAPI (v2)](https://pokeapi.co/api/v2/)

## Cómo Empezar

1.  **Clona el repositorio (o abre el proyecto si ya lo tienes).**
2.  **Abre el proyecto en Android Studio.**
    *   Espera a que Android Studio sincronice el proyecto con los archivos Gradle. Esto descargará todas las dependencias necesarias.
3.  **Ejecuta la aplicación.**
    *   Selecciona un emulador de Android o conecta un dispositivo físico.
    *   Presiona el botón "Run" (▶️) en Android Studio.

## Estructura del Proyecto (Simplificada)

*   `app/src/main/java/com/example/talanapokeapp/`
    *   `data/`
        *   `model/`: Clases de datos (data classes) para representar las respuestas de la API y los ítems de la UI.
        *   `network/`: Interfaz de servicio Retrofit (`PokeApiService`) e instancia de Retrofit (`RetrofitInstance`).
        *   `repository/`: `PokemonRepository` para gestionar la obtención de datos.
    *   `presentation/`
        *   `pokemonlist/`: Contiene el `PokemonViewModel`, `PokemonListUiState`, `PokemonDisplayItem` y los Composables de la pantalla (`PokemonListScreen.kt`).
    *   `ui/theme/`: Archivos de tema generados por Jetpack Compose.
    *   `MainActivity.kt`: Actividad principal que aloja la UI de Jetpack Compose.
*   `app/build.gradle.kts`: Archivo de configuración de Gradle para el módulo de la aplicación (donde se declaran las dependencias).
*   `app/src/main/AndroidManifest.xml`: Manifiesto de la aplicación (donde se declara el permiso de Internet, etc.).

## Posibles Mejoras Futuras

*   Implementar paginación para cargar más Pokémon.
*   Añadir una pantalla de detalle para cada Pokémon.
*   Mejorar el manejo de errores y estados de carga.
*   Añadir tests unitarios y de UI.
*   Implementar persistencia de datos local (e.g., con Room) para cachear los Pokémon.
*   Filtrar o buscar Pokémon.

---

Este README proporciona una buena visión general del proyecto. 