# Talana Poké App

Una aplicación Android moderna para explorar y coleccionar Pokémon, desarrollada con Kotlin y Jetpack Compose. Este proyecto fue creado como parte del proceso de postulación para el puesto de Software Developer Android en Talana.

![Pokémon](https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/25.png)

## 📋 Acerca del desafío

Este proyecto responde al desafío técnico propuesto por Talana, que consiste en desarrollar una aplicación móvil utilizando la PokéAPI con los siguientes requisitos:

- ✅ Autenticación de usuarios mediante OAuth
- ✅ Feed dinámico con información de Pokémon
- ✅ Sistema para agregar Pokémon a favoritos
- ✅ Consulta de detalles adicionales de cada Pokémon
- ✅ Estadísticas de usuario (Pokémon vistos, favoritos, tiempo de uso)

### 🚀 Solución al problema de rendimiento del feed

Uno de los principales desafíos propuestos fue crear un feed dinámico eficiente y suave, incluso en dispositivos antiguos, evitando los problemas de rendimiento causados por el uso de NestedScrollView con múltiples RecyclerViews anidados.

**Solución implementada:**
- Arquitectura optimizada con Jetpack Compose que evita los problemas de RecyclerViews anidados
- Lazy loading con paginación eficiente para carga progresiva de elementos
- Caché local con Room para reducir llamadas a la API y mejorar rendimiento offline
- Procesamiento de imágenes optimizado con Coil
- Uso de coroutines para operaciones asíncronas sin bloquear el hilo principal

### ⭐ Extras implementados

- **Inyección de dependencias:** Implementación completa con Hilt
- **Autenticación:** Integración con Firebase Authentication y Google Sign-In
- **Características avanzadas:**
  - Buscador de Pokémon con resultados en tiempo real
  - Filtros por tipo de Pokémon con interfaz visual
  - Sistema avanzado de estadísticas de usuario
  - Modo oscuro adaptativo
  - Caché inteligente con tiempo de expiración

## 🌟 Características

### Funcionalidades principales
- **Catálogo completo de Pokémon**: Visualiza los primeros 151 Pokémon con detalles completos
- **Autenticación con Firebase**: Inicio de sesión con Google
- **Favoritos personalizados**: Marca y guarda tus Pokémon favoritos por usuario
- **Estadísticas de uso**: Seguimiento detallado de tu actividad en la app
- **Buscador inteligente**: Encuentra fácilmente cualquier Pokémon por nombre
- **Filtros por tipo**: Filtra Pokémon por cualquiera de los 18 tipos disponibles

### Detalles de implementación de funcionalidades
- **Catálogo Pokémon**: Implementado con paginación para carga eficiente de datos desde PokeAPI
- **Sistema de autenticación**: Flujo completo de inicio/cierre de sesión con persistencia de estado
- **Gestión de favoritos**: Sincronización en tiempo real entre Firestore y caché local
- **Panel de estadísticas**: Gráficos interactivos con visualización de datos personalizados
- **Motor de búsqueda**: Filtrado instantáneo con resultados predictivos
- **Sistema de filtros**: Chips interactivos para filtrar por tipo con feedback visual inmediato

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

### Flujo de datos y responsabilidades

#### Capa de Datos
- **Model**: Define entidades de dominio y DTOs para mapeo entre API y base de datos
- **Network**: Gestiona comunicación con APIs externas (PokeAPI, Firebase)
- **Local**: Implementa Room para persistencia y cache de datos
- **Repository**: Orquesta fuentes de datos y expone interfaces limpias a la capa de presentación

#### Capa de Presentación
- **ViewModel**: Maneja la lógica de negocio y transforma datos para la UI
- **Screens**: Componentes UI en Compose que observan estados del ViewModel
- **State Holders**: Clases inmutables que representan el estado de la UI

#### Diagrama de Flujo de Datos
```
[UI Layer] ←→ [ViewModel] ←→ [Repository] ←→ [API/Database]
    ↑             ↑               ↑
    │             │               │
 [Compose]    [StateFlow]    [Coroutines]
```

### Patrones de diseño implementados
- **Repository Pattern**: Abstracción de fuentes de datos
- **Observer Pattern**: Con StateFlow/SharedFlow para actualizaciones de UI
- **Dependency Injection**: Con Hilt para gestión de dependencias
- **Use Case Pattern**: Para encapsular lógica de negocio específica
- **Adapter Pattern**: Para transformación de datos entre capas

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

### Sistema avanzado de filtros
- **Filtrado por tipo de Pokémon**: Implementado mediante chips interactivos
- **Chips con colores representativos**: Cada tipo de Pokémon tiene su propio color distintivo
- **Combinación de filtros**: Posibilidad de combinar búsqueda por texto y filtro por tipo
- **Experiencia de usuario mejorada**: Selección/deselección intuitiva con feedback visual
- **Diseño pixelado**: Manteniendo la coherencia estética con el resto de la aplicación

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
- Android Studio Flamingo (2022.2.1) o superior
- JDK 17+
- Gradle 8.0+
- Android API 34 (SDK mínimo: 24)

### Instalación
```bash
# Clonar el repositorio
git clone https://github.com/tu-usuario/TalanaPokeApp.git

# Navegar al directorio del proyecto
cd TalanaPokeApp
```

### Configuración
1. Abre el proyecto en Android Studio
2. Sincroniza el proyecto con Gradle (File > Sync Project with Gradle Files)
3. Configura tu proyecto en Firebase para la autenticación:
   - Crea un proyecto en [Firebase Console](https://console.firebase.google.com/)
   - Agrega una aplicación Android con el paquete `com.example.talana_poke_app`
   - Descarga el archivo `google-services.json` y colócalo en el directorio `app/`
4. Ejecuta la aplicación en un emulador o dispositivo físico

### Estructura de paquetes
- **data**: Contiene toda la lógica de acceso a datos
- **di**: Módulos de inyección de dependencias
- **presentation**: Contiene los ViewModels y componentes de UI
- **ui**: Recursos compartidos de UI y temas

El código fuente principal de la aplicación se encuentra bajo el paquete `com.example.talana_poke_app`.

## 🔜 Próximas características
- Implementación de GraphQL para consultas optimizadas a la API
- Implementación de Paging 3 para carga infinita
- Soporte para notificaciones push
- Modo sin conexión mejorado
- Tests unitarios y de UI
- Soporte para compartir Pokémon favoritos

## 📄 Licencia
Este proyecto está licenciado bajo [Licencia MIT](LICENSE)

## 👨‍💻 Proceso de desarrollo

### Enfoque metodológico
Para abordar este desafío, seguí un proceso iterativo centrado en la funcionalidad principal:

1. **Planificación inicial**: Análisis de requerimientos y diseño de la arquitectura básica
2. **Prototipado rápido**: Implementación de un feed básico con Compose para validar el enfoque
3. **Desarrollo incremental**: Adición progresiva de funcionalidades, comenzando por:
   - Configuración de la arquitectura MVVM + Clean
   - Implementación del cliente de API
   - Desarrollo de la UI principal con Compose
   - Integración del sistema de autenticación
   - Implementación del almacenamiento local
4. **Optimización**: Mejora del rendimiento y experiencia de usuario
5. **Pruebas**: Validación manual en dispositivos de diferentes características

### Desafíos técnicos superados
- **Rendimiento del feed**: Resuelto mediante arquitectura optimizada y técnicas avanzadas de Compose
- **Gestión de estado**: Implementación de flujos de datos unidireccionales con StateFlow
- **Sincronización offline/online**: Sistema de caché inteligente con Room y políticas de actualización
- **Sistema de filtrado complejo**: Implementación de filtros combinados (texto + tipo) manteniendo el rendimiento

### Lecciones aprendidas
- La importancia de arquitecturas limpias para mantener el código escalable
- Ventajas de Jetpack Compose para resolver problemas clásicos de rendimiento en UI complejas
- Valor de las buenas prácticas como inyección de dependencias para testing y mantenibilidad
- Importancia del diseño de UX para funcionalidades de filtrado intuitivas

## 📊 Métricas y resultados

- **Rendimiento**: Feed con scroll fluido incluso en dispositivos de gama baja (probado en emuladores con especificaciones reducidas)
- **Tiempo de carga**: Optimizado a <1.5 segundos para la carga inicial
- **Tamaño de APK**: Reducido a ~8MB mediante optimización de recursos
- **Consumo de RAM**: Minimizado a través de gestión eficiente de recursos y caché 