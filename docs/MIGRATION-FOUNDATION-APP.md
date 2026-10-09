# Migración y Estructuración de Bloque 0 (Fundación) dentro de `:app`

Este documento registra detalladamente la migración arquitectónica del **Bloque 0: Fundación**, el flujo de ramas según **GitFlow** y la bitácora de commits realizados paso a paso.

---

## 1. Contexto y Decisión Arquitectónica

Siguiendo el flujo de desarrollo, se consolidó la fundación transversal dentro del módulo de la aplicación (`:app`), organizando las capas bajo el patrón **Clean Architecture / Package-by-Feature** en `app/src/main/java/com/tuxlogic/shiftiq/mobile/core/`.

### Ventajas de la Estructura Consolidada:
1. **Cohesión y Simplicidad:** Todos los componentes fundacionales residen dentro de la estructura central de la app, eliminando sobrecarga de configuración de Gradle por submódulo.
2. **Respeto a Clean Architecture y DDD:**
   - La capa de dominio (`core/common`, `core/model`) se mantiene libre de dependencias del framework de Android.
   - La capa de infraestructura (`core/network`, `core/datastore`) encapsula Retrofit, OkHttp y DataStore.
   - La capa de presentación (`core/designsystem`, `core/navigation`) define componentes visuales Material 3 y contratos de navegación.

---

## 2. GitFlow y Gestión de Ramas

- **Rama Base:** `develop`
- **Nueva Rama Creada:** `feature/foundation`
- **Convención:** GitFlow en inglés con Conventional Commits (`feat`, `chore`, `test`, `docs`).

```bash
git checkout -b feature/foundation
```

---

## 3. Estructura de Directorios Resultante

```
app/
├── src/
│   ├── main/
│   │   ├── AndroidManifest.xml
│   │   └── java/com/tuxlogic/shiftiq/mobile/
│   │       ├── ShiftIQApplication.kt          # @HiltAndroidApp
│   │       ├── MainActivity.kt                # @AndroidEntryPoint con ShiftIQTheme
│   │       ├── di/
│   │       │   └── NetworkConfigModule.kt     # Provee BaseUrl (local 10.0.2.2 vs producción)
│   │       └── core/                          # <-- Fundación Clean Architecture
│   │           ├── common/
│   │           │   ├── dispatchers/           # DispatcherProvider, DefaultDispatcherProvider
│   │           │   ├── result/                # AppResult (Success/Failure), AppError
│   │           │   └── di/                    # DispatchersModule (Hilt)
│   │           ├── model/                     # Ubiquitous Language & Value Objects
│   │           │   ├── Role.kt                # RBAC con Roles del backend Spring Boot
│   │           │   ├── Ids.kt                 # UserId, BranchId, WorkshopId, etc. (@JvmInline)
│   │           │   └── Money.kt               # BigDecimal inmutable con ISO Currency
│   │           ├── datastore/                 # Persistencia de Sesión
│   │           │   ├── SessionDataStore.kt    # Interfaz reactiva (Flow<SessionState>)
│   │           │   ├── SessionDataStoreImpl.kt# Implementación Jetpack DataStore Preferences
│   │           │   └── di/                    # DataStoreModule (Hilt)
│   │           ├── network/                   # Conectividad Backend
│   │           │   ├── interceptor/           # AuthInterceptor (Bearer token síncrono)
│   │           │   ├── authenticator/         # TokenAuthenticator (Mutex single-flight refresh)
│   │           │   ├── dto/                   # ErrorEnvelopeDto (code, message, details)
│   │           │   ├── SafeApiCall.kt         # Wrapper a prueba de fallos -> AppResult
│   │           │   └── di/                    # NetworkModule (OkHttp, Retrofit, Gson)
│   │           ├── designsystem/              # Sistema de Diseño Compose
│   │           │   ├── theme/                 # Color.kt, Type.kt, Theme.kt (Material 3)
│   │           │   └── components/            # ShiftIQButton, ShiftIQTextField, ShiftIQFeedback
│   │           └── navigation/                # Contratos de Rutas
│   │               └── AppDestination.kt      # Login, Register, BranchSelection, Dashboard, etc.
│   └── test/
│       └── java/com/tuxlogic/shiftiq/mobile/
│           └── core/
│               ├── testing/                   # MainDispatcherRule (StandardTestDispatcher)
│               ├── common/result/             # AppResultTest
│               ├── model/                     # ModelTest
│               └── network/                   # ErrorEnvelopeDtoTest
```

---

## 4. Bitácora de Commits Realizados en `feature/foundation`

| Hash | Tipo / Scope | Mensaje del Commit | Archivos y Componentes Clave |
| :---: | :--- | :--- | :--- |
| `0a8bfc5` | `chore(build)` | configure jdk toolchain and version catalog dependencies | `gradle-daemon-jvm.properties`, `libs.versions.toml`, `build.gradle.kts` |
| `88fb3f7` | `feat(core-domain)` | add ubiquitous models and functional result abstractions | `AppResult.kt`, `AppError.kt`, `DispatcherProvider.kt`, `Role.kt`, `Ids.kt`, `Money.kt`, `DispatchersModule.kt` |
| `7ac06ce` | `feat(core-infra)` | add session datastore and authenticated network layer | `SessionDataStore`, `AuthInterceptor`, `TokenAuthenticator`, `ErrorEnvelopeDto`, `SafeApiCall`, `NetworkModule`, `DataStoreModule` |
| `e2807e5` | `feat(core-ui)` | add design system tokens, components and navigation contracts | `Theme.kt`, `Color.kt`, `Type.kt`, `ShiftIQButton`, `ShiftIQTextField`, `ShiftIQFeedback`, `AppDestination` |
| `d3f1ca8` | `feat(app)` | configure application entrypoint, hilt di and app manifest | `ShiftIQApplication.kt`, `MainActivity.kt`, `NetworkConfigModule.kt`, `AndroidManifest.xml`, `app/build.gradle.kts` |
| `5ae0ab3` | `test(core)` | add unit tests for result, domain models, and error envelopes | `MainDispatcherRule.kt`, `AppResultTest.kt`, `ModelTest.kt`, `ErrorEnvelopeDtoTest.kt` |
| *(siguiente)* | `docs(foundation)` | add block 0 foundation migration and architecture documentation | `docs/MIGRATION-FOUNDATION-APP.md`, `docs/DEVELOPMENT-PLAN.md`, `AGENTS.md` |

---

## 5. Verificación de Compilación y Pruebas Unitarias

Se ejecutó la suite completa de compilación y pruebas en la rama `feature/foundation`:

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug
```

**Resultado:**
- `BUILD SUCCESSFUL in 1m 45s`
- `52 actionable tasks: 23 executed, 29 up-to-date`
- Todos los tests unitarios pasaron sin fallas.
