# 🧪 Estrategia de Testing - TalanaPokeApp

## 📋 Descripción General

Esta aplicación implementa una estrategia completa de testing que cubre múltiples capas de la arquitectura, garantizando la calidad y confiabilidad del código.

## 🏗️ Tipos de Tests Implementados

### 1. **Tests Unitarios** (`src/test/`)
Tests rápidos que verifican la lógica de negocio de forma aislada.

#### 📁 **Repository Tests**
- `PokemonRepositoryTest.kt`: Verifica la lógica de caché, llamadas a API y manejo de favoritos
- Cobertura: 
  - ✅ Caché multinivel (memoria + base de datos)
  - ✅ Manejo de errores de red
  - ✅ Operaciones CRUD de favoritos
  - ✅ Filtrado y búsqueda

#### 📁 **ViewModel Tests**
- `PokemonViewModelTest.kt`: Verifica la lógica de presentación y estados de UI
- `AuthViewModelTest.kt`: Verifica los flujos de autenticación
- Cobertura:
  - ✅ Estados de carga
  - ✅ Filtros y búsqueda
  - ✅ Manejo de favoritos
  - ✅ Flujos de autenticación

### 2. **Tests de Instrumentación** (`src/androidTest/`)
Tests que requieren el contexto de Android.

#### 📁 **Database Tests**
- `FavoritePokemonDaoTest.kt`: Verifica operaciones de base de datos con Room
- Cobertura:
  - ✅ Operaciones CRUD
  - ✅ Consultas complejas
  - ✅ Integridad de datos
  - ✅ Filtrado por usuario

#### 📁 **UI Tests**
- `LoginScreenTest.kt`: Verifica la interfaz de usuario con Compose Testing
- `PokemonAppE2ETest.kt`: Tests end-to-end del flujo completo
- Cobertura:
  - ✅ Elementos de UI presentes
  - ✅ Interacciones del usuario
  - ✅ Navegación entre pantallas
  - ✅ Estados de carga y error

## 📚 Librerías de Testing Utilizadas

### **Testing Core**
- `JUnit 4`: Framework base de testing
- `AndroidX Test`: Testing para componentes Android
- `Truth`: Assertions más legibles
- `Turbine`: Testing de Kotlin Flows

### **Mocking**
- `MockK`: Mocking library para Kotlin
- `Hilt Testing`: Inyección de dependencias para tests

### **Coroutines Testing**
- `kotlinx-coroutines-test`: Testing de código asíncrono
- `arch.core:core-testing`: LiveData testing

### **UI Testing**
- `Compose Testing`: Testing específico para Jetpack Compose
- `Espresso`: Testing de UI tradicional

### **Database Testing**
- `Room Testing`: Testing específico para Room

## 🚀 Cómo Ejecutar los Tests

### **Tests Unitarios**
```bash
# Ejecutar todos los tests unitarios
./gradlew test

# Ejecutar tests específicos
./gradlew testDebugUnitTest

# Con reporte de cobertura
./gradlew testDebugUnitTest jacocoTestReport
```

### **Tests de Instrumentación**
```bash
# Ejecutar todos los tests de instrumentación
./gradlew connectedAndroidTest

# Ejecutar en dispositivo específico
./gradlew connectedDebugAndroidTest
```

### **Tests Específicos por Clase**
```bash
# Repository tests
./gradlew test --tests "*.PokemonRepositoryTest"

# ViewModel tests  
./gradlew test --tests "*.PokemonViewModelTest"

# DAO tests
./gradlew connectedAndroidTest --tests "*.FavoritePokemonDaoTest"
```

## 📊 Estructura de Tests

```
app/src/
├── test/java/com/example/talana_poke_app/
│   ├── data/repository/
│   │   └── PokemonRepositoryTest.kt
│   ├── presentation/
│   │   ├── pokemonlist/PokemonViewModelTest.kt
│   │   └── auth/AuthViewModelTest.kt
│   ├── di/TestAppModule.kt
│   └── TestUtils.kt
│
└── androidTest/java/com/example/talana_poke_app/
    ├── data/local/dao/
    │   └── FavoritePokemonDaoTest.kt
    ├── presentation/auth/
    │   └── LoginScreenTest.kt
    ├── PokemonAppE2ETest.kt
    └── HiltTestRunner.kt
```

## 🔧 Configuración de Testing

### **Hilt para Tests**
- `HiltTestRunner`: Runner personalizado para tests con Hilt
- `TestAppModule`: Módulo que proporciona mocks para tests
- `@HiltAndroidTest`: Anotación para tests que usan Hilt

### **Dispatchers para Tests**
- `MainDispatcherRule`: Regla para configurar dispatchers en tests
- `StandardTestDispatcher`: Dispatcher controlable para tests de coroutines

### **Utilidades de Test**
- `TestUtils`: Funciones helper para crear datos de prueba
- Factory methods para objetos de dominio

## 🎯 Cobertura de Testing

### **Repository Layer** ✅
- [x] Caché multinivel
- [x] Manejo de errores de red
- [x] Operaciones de favoritos
- [x] Filtrado y búsqueda

### **Presentation Layer** ✅
- [x] Estados de ViewModel
- [x] Flujos de UI
- [x] Navegación
- [x] Autenticación

### **Data Layer** ✅
- [x] Operaciones de base de datos
- [x] Integridad de datos
- [x] Consultas complejas

### **UI Layer** ✅
- [x] Componentes de Compose
- [x] Interacciones de usuario
- [x] Estados de UI

## 🚨 Buenas Prácticas Implementadas

### **Given-When-Then Pattern**
```kotlin
@Test
fun `pokemonRepository should cache data correctly`() {
    // Given
    val expectedData = createTestData()
    
    // When
    val result = repository.getData()
    
    // Then
    assertThat(result).isEqualTo(expectedData)
}
```

### **Naming Conventions**
- Tests descriptivos con template: `[método]_[condición]_[resultado]`
- Nombres en español con backticks para mayor claridad

### **Test Isolation**
- Cada test es independiente
- Setup y teardown apropiados
- Mocks configurados por test

### **Data Testing**
- Factory methods para objetos de prueba
- Datos consistentes y reutilizables
- Scenarios de edge cases

## 📈 Métricas de Testing

### **Tiempo de Ejecución**
- Tests unitarios: ~10-15 segundos
- Tests de instrumentación: ~2-3 minutos
- Tests completos: ~5 minutos

### **Cobertura Objetivo**
- Repository: >90%
- ViewModel: >85%
- DAO: >95%
- UI Critical paths: >70%

## 🔄 CI/CD Integration

Los tests están configurados para ejecutarse en:
- ✅ Pre-commit hooks
- ✅ Pull requests
- ✅ Pipeline de CI/CD
- ✅ Release builds

## 📝 Próximos Pasos

### **Mejoras Planeadas**
- [ ] Tests de performance
- [ ] Tests de accesibilidad
- [ ] Visual regression tests
- [ ] Mutation testing
- [ ] Integración con Jacoco para reportes de cobertura

### **Tests Adicionales**
- [ ] Network layer tests
- [ ] Error boundary tests
- [ ] Navigation tests completos
- [ ] Screenshot tests

## 🆘 Troubleshooting

### **Problemas Comunes**

#### **MockK Issues**
```kotlin
// Usar relaxed = true para mocks simples
val mockRepository = mockk<Repository>(relaxed = true)
```

#### **Coroutines Testing**
```kotlin
// Usar TestDispatcher apropiado
@get:Rule
val mainDispatcherRule = MainDispatcherRule()
```

#### **Compose Testing**
```kotlin
// Asegurar que el tema esté configurado
composeTestRule.setContent {
    TalanaPokeAppTheme {
        YourComposable()
    }
}
```

## 📚 Referencias

- [Android Testing Guide](https://developer.android.com/training/testing)
- [Jetpack Compose Testing](https://developer.android.com/jetpack/compose/testing)
- [MockK Documentation](https://mockk.io/)
- [Hilt Testing Guide](https://dagger.dev/hilt/testing)
- [Truth Assertions](https://truth.dev/)

---

*Esta estrategia de testing garantiza que la aplicación TalanaPokeApp mantenga alta calidad y confiabilidad en todos sus componentes.* 