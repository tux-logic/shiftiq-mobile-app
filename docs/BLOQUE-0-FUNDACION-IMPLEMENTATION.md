# Bloque 0: Fundación — Informe de Implementación

Este documento detalla la implementación técnica del **Bloque 0: Fundación** de la aplicación móvil **ShiftIQ Mobile** (`com.tuxlogic.shiftiq.mobile`), siguiendo estrictamente los principios de **Clean Architecture**, **Domain-Driven Design (DDD)** y las especificaciones de `MOBILE-ARCHITECTURE.md`, `DEVELOPMENT-PLAN.md` y `AGENTS.md`.

---

## 1. Arquitectura y Estructura Multi-Módulo

El proyecto ha evolucionado exitosamente de un esqueleto monomódulo a una arquitectura multi-módulo desacoplada:

```
shiftiq-mobile-app/
├── core/
│   ├── common/           # Pure Kotlin: AppResult, AppError, DispatcherProvider
│   ├── model/            # Pure Kotlin: Ubiquitous Language (Role, Money, Ids)
│   ├── datastore/        # Android Library: SessionDataStore (tokens, branchId)
│   ├── network/          # Android Library: OkHttp, Retrofit, AuthInterceptor, SafeApiCall
│   ├── designsystem/     # Android Library: Jetpack Compose, Material3 Tokens, Components
│   ├── navigation/       # Android Library: Contratos de rutas (AppDestination)
│   └── testing/          # Android Library: MainDispatcherRule, test utilities
└── app/                  # Application Host: ShiftIQApplication (@HiltAndroidApp), DI BaseUrl, MainActivity
```

### Regla de Dependencias (Clean Architecture)
- `:core:common` y `:core:model` son módulos **puros Kotlin JVM** (`jvmTarget = 11`) con **cero dependencias de Android framework**.
- `:core:network` y `:core:datastore` dependen de `:core:common` y `:core:model`.
- `:core:designsystem` provee componentes atómicos y temas para las capas de presentación.
- `:app` actúa únicamente como orquestador / composition root, vinculando todos los módulos e inyectando dependencias mediante Hilt.

---

## 2. Componentes Implementados por Módulo

### 2.1 `:core:common` (Abstracciones de Dominio)
- [AppResult.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/core/common/src/main/java/com/tuxlogic/shiftiq/mobile/core/common/result/AppResult.kt): Modelado funcional de resultados (`Success<T>` y `Failure`), evitando excepciones no controladas en el flujo de negocio. Extensiones funcionales: `map`, `onSuccess`, `onFailure`.
- [AppError.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/core/common/src/main/java/com/tuxlogic/shiftiq/mobile/core/common/result/AppError.kt): Jerarquía sellada tipada para errores de red, backend, autenticación (`Unauthorized`, `Forbidden`), validación y negocio.
- [DispatcherProvider.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/core/common/src/main/java/com/tuxlogic/shiftiq/mobile/core/common/dispatchers/DispatcherProvider.kt) y [DefaultDispatcherProvider.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/core/common/src/main/java/com/tuxlogic/shiftiq/mobile/core/common/dispatchers/DefaultDispatcherProvider.kt): Abstracción para inyección controlada de coroutine dispatchers (`main`, `io`, `default`, `unconfined`).

### 2.2 `:core:model` (Ubiquitous Language & DDD Value Objects)
- [Role.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/core/model/src/main/java/com/tuxlogic/shiftiq/mobile/core/model/Role.kt): Enum que refleja con exactitud el modelo RBAC del backend Spring Boot (`ROLE_SYSTEM_ADMIN`, `ROLE_WORKSHOP_OWNER`, `ROLE_BRANCH_MANAGER`, `ROLE_MECHANIC`, `ROLE_RECEPTIONIST`, `ROLE_CUSTOMER`).
- [Ids.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/core/model/src/main/java/com/tuxlogic/shiftiq/mobile/core/model/Ids.kt): Value Objects `@JvmInline value class` para identidades fuertemente tipadas (`UserId`, `WorkshopId`, `BranchId`, `AppointmentId`, `VehicleId`, etc.).
- [Money.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/core/model/src/main/java/com/tuxlogic/shiftiq/mobile/core/model/Money.kt): Value Object inmutable para montos financieros con `BigDecimal` y código de moneda ISO-4217, previniendo errores de precisión decimal.

### 2.3 `:core:datastore` (Persistencia de Sesión y Contexto)
- [SessionDataStore.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/core/datastore/src/main/java/com/tuxlogic/shiftiq/mobile/core/datastore/SessionDataStore.kt) y [SessionDataStoreImpl.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/core/datastore/src/main/java/com/tuxlogic/shiftiq/mobile/core/datastore/SessionDataStoreImpl.kt):
  - Almacena de forma reactiva (`Flow<SessionState>`) tokens JWT (`accessToken`, `refreshToken`), datos de usuario (`userId`, `userRole`) y el crucial contexto de sucursal activa (`activeBranchId`).
  - Provee métodos `saveSession(...)`, `setActiveBranchId(...)` y `clearSession()`.
- [DataStoreModule.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/core/datastore/src/main/java/com/tuxlogic/shiftiq/mobile/core/datastore/di/DataStoreModule.kt): Configuración Hilt en `SingletonComponent`.

### 2.4 `:core:network` (Capa de Conectividad e Integración con Backend)
- [AuthInterceptor.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/core/network/src/main/java/com/tuxlogic/shiftiq/mobile/core/network/interceptor/AuthInterceptor.kt): Inyecta cabecera `Authorization: Bearer <accessToken>` de manera síncrona en cada petición que no sea de autenticación pública.
- [TokenAuthenticator.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/core/network/src/main/java/com/tuxlogic/shiftiq/mobile/core/network/authenticator/TokenAuthenticator.kt): Implementa el refresco automático de token ante respuestas `HTTP 401 Unauthorized` usando `kotlinx.coroutines.sync.Mutex` para single-flight execution (evita tormentas de peticiones de refresco concurrentes).
- [ErrorEnvelopeDto.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/core/network/src/main/java/com/tuxlogic/shiftiq/mobile/core/network/dto/ErrorEnvelopeDto.kt): Deserializa el formato exacto del backend (`code`, `message`, `timestamp`, `details`).
- [SafeApiCall.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/core/network/src/main/java/com/tuxlogic/shiftiq/mobile/core/network/SafeApiCall.kt): Wrapper seguro que captura `HttpException`, `IOException` y parsea errores hacia `AppResult.Failure(AppError)`.
- [NetworkModule.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/core/network/src/main/java/com/tuxlogic/shiftiq/mobile/core/network/di/NetworkModule.kt): Configura `OkHttpClient`, `HttpLoggingInterceptor`, y la instancia principal de `Retrofit` inyectable vía Hilt.

### 2.5 `:core:designsystem` (Sistema de Diseño Material 3)
- Tokens de tema: [Color.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/core/designsystem/src/main/java/com/tuxlogic/shiftiq/mobile/core/designsystem/theme/Color.kt), [Type.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/core/designsystem/src/main/java/com/tuxlogic/shiftiq/mobile/core/designsystem/theme/Type.kt) y [Theme.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/core/designsystem/src/main/java/com/tuxlogic/shiftiq/mobile/core/designsystem/theme/Theme.kt).
- Componentes reutilizables:
  - [ShiftIQButton.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/core/designsystem/src/main/java/com/tuxlogic/shiftiq/mobile/core/designsystem/components/ShiftIQButton.kt): Botón de acción principal con estado de carga integrado.
  - [ShiftIQTextField.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/core/designsystem/src/main/java/com/tuxlogic/shiftiq/mobile/core/designsystem/components/ShiftIQTextField.kt): Campo de texto con validación y visualización de errores.
  - [ShiftIQFeedback.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/core/designsystem/src/main/java/com/tuxlogic/shiftiq/mobile/core/designsystem/components/ShiftIQFeedback.kt): Pantallas de carga completa, banners de error y estados vacíos.

### 2.6 `:core:navigation`
- [AppDestination.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/core/navigation/src/main/java/com/tuxlogic/shiftiq/mobile/core/navigation/AppDestination.kt): Contrato sellado de rutas y deep links de la aplicación (`Login`, `Register`, `BranchSelection`, `Dashboard`, `Appointments`, etc.).

### 2.7 `:core:testing`
- [MainDispatcherRule.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/core/testing/src/main/java/com/tuxlogic/shiftiq/mobile/core/testing/MainDispatcherRule.kt): JUnit Rule para pruebas unitarias de corrutinas (`StandardTestDispatcher`), compatible con Turbine y MockK.

### 2.8 Host `:app`
- [ShiftIQApplication.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/ShiftIQApplication.kt): Aplicación anotada con `@HiltAndroidApp` registrada en [AndroidManifest.xml](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/AndroidManifest.xml) junto con permisos `INTERNET` y `ACCESS_NETWORK_STATE`.
- [NetworkConfigModule.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/di/NetworkConfigModule.kt): Configura `BASE_URL` inyectado según `BuildConfig.BASE_URL` (`http://10.0.2.2:8080/` para emulador local en debug).
- [MainActivity.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/MainActivity.kt): Anotada con `@AndroidEntryPoint` y usando `ShiftIQTheme`.

---

## 3. Verificación y Resultados de Pruebas

Se ejecutó la suite completa de compilación y pruebas:
1. `.\gradlew.bat :core:common:test :core:model:test :core:network:test :core:datastore:compileDebugKotlin :core:designsystem:compileDebugKotlin :core:navigation:compileDebugKotlin :core:testing:compileDebugKotlin`
   - **Resultado:** `BUILD SUCCESSFUL in 2m 35s` (60 tareas ejecutadas).
2. `.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest`
   - **Resultado:** `BUILD SUCCESSFUL in 2m 44s` (148 tareas ejecutadas / up-to-date).

El Bloque 0 está listo para la construcción del **Bloque 1: IAM (Identidad y Acceso)**.
