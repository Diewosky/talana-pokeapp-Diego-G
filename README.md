# Talana Poké App

Una aplicación Android moderna para explorar y coleccionar Pokémon, desarrollada con Kotlin y Jetpack Compose.

![Pokémon]() <!-- Puedes agregar una captura de pantalla de la app aquí -->

## 🌟 Características

### Funcionalidades principales
- **Catálogo completo de Pokémon**: Visualiza los primeros 151 Pokémon con detalles completos
- **Autenticación con Firebase**: Inicio de sesión con Google
- **Favoritos personalizados**: Marca y guarda tus Pokémon favoritos por usuario
- **Estadísticas de uso**: Seguimiento detallado de tu actividad en la app
- **Buscador inteligente**: Encuentra fácilmente cualquier Pokémon por nombre

### Características técnicas
- **UI moderna con Jetpack Compose**: Interfaz fluida y reactiva
- **Arquitectura MVVM**: Código organizado, testeable y mantenible
- **Inyección de dependencias con Hilt**: Componentes desacoplados y fácilmente testeables
- **Estado reactivo con Kotlin Flows**: Actualizaciones en tiempo real de la UI
- **Persistencia con Room**: Caché local y almacenamiento de favoritos
- **Navegación con Navigation Compose**: Transiciones fluidas entre pantallas

## 🏗️ Arquitectura

La aplicación sigue la arquitectura MVVM (Model-View-ViewModel) e implementa los principios de Clean Architecture:

```
app/
├── data/                  # Capa de datos
│   ├── local/             # Persistencia local con Room
│   ├── model/             # Modelos de datos
│   ├── network/           # Cliente API con Retrofit
│   └── repository/        # Implementaciones de repositorios
├── di/                    # Módulos de inyección de dependencias con Hilt
├── presentation/          # Capa de presentación
│   ├── auth/              # Autenticación con Firebase
│   ├── mainmenu/          # Pantalla de menú principal
│   ├── navigation/        # Configuración de navegación
│   ├── pokemonlist/       # Listado y detalle de Pokémon
│   └── stats/             # Estadísticas de uso
└── ui/                    # Temas, componentes UI compartidos
```

## 🔧 Tecnologías utilizadas

### UI y presentación
- **Jetpack Compose**: Framework de UI declarativo
- **Material Design 3**: Componentes modernos y tematización
- **Navigation Compose**: Navegación entre pantallas
- **Coil**: Carga eficiente de imágenes

### Datos y lógica
- **Retrofit**: Cliente HTTP para la comunicación con la API
- **Gson**: Serialización/deserialización JSON
- **Room**: Persistencia de datos local
- **Kotlin Coroutines & Flows**: Operaciones asíncronas reactivas
- **Kotlinx Serialization**: Serialización eficiente

### Inyección de dependencias
- **Hilt**: Framework de inyección de dependencias de Android

### Autenticación
- **Firebase Auth**: Autenticación de usuarios
- **Google Sign-In**: Inicio de sesión con Google

## 🚀 Recientes mejoras

### Optimización de rendimiento
- Caché local con tiempo de expiración para reducir llamadas a la API
- Procesamiento paralelo de datos con Coroutines
- Carga eficiente de imágenes con precargas

### Mejoras en la arquitectura
- Implementación de inyección de dependencias con Hilt
- Migración de datos compartidos a específicos por usuario
- Optimización del ciclo de vida de los ViewModels

### Estadísticas personalizadas
- Sistema de seguimiento de uso por usuario
- Contador de Pokémon vistos y favoritos
- Seguimiento de tiempo de uso y sesiones

### Visuales y experiencia de usuario
- Tematización personalizada con colores de Pokémon
- Transiciones y animaciones fluidas
- Modo oscuro adaptativo

## 📝 Notas para desarrolladores

### Requisitos
- Android Studio Arctic Fox o superior
- JDK 11+
- Gradle 7.0+

### Configuración
1. Clona el repositorio
2. Abre el proyecto en Android Studio
3. Sincroniza con Gradle
4. Ejecuta la aplicación

### Estructura de paquetes
- **data**: Contiene toda la lógica de acceso a datos
- **di**: Módulos de inyección de dependencias
- **presentation**: Contiene los ViewModels y componentes de UI
- **ui**: Recursos compartidos de UI y temas

## 🔜 Próximas características
- Implementación de Paging 3 para carga infinita
- Soporte para notificaciones push
- Modo sin conexión mejorado
- Tests unitarios y de UI
- Soporte para compartir Pokémon favoritos

## 📄 Licencia
Este proyecto está licenciado bajo [Licencia MIT](LICENSE) 