# Bloque 0: Fundación — Informe Técnico y Guía de Verificación

Este documento detalla la implementación técnica del **Bloque 0: Fundación** de la aplicación móvil **ShiftIQ Mobile** (`com.tuxlogic.shiftiq.mobile`), siguiendo **Clean Architecture**, **Domain-Driven Design (DDD)** y las especificaciones de `docs/MOBILE-ARCHITECTURE.md`, `docs/DEVELOPMENT-PLAN.md` y `AGENTS.md`.

---

## 1. Arquitectura y Estructura en `:app`

Todo el código fundacional y transversal se encuentra consolidado dentro del módulo principal `:app` bajo el paquete `com.tuxlogic.shiftiq.mobile.core`, desacoplado por responsabilidades:

```
app/
├── src/
│   ├── main/
│   │   ├── AndroidManifest.xml
│   │   └── java/com/tuxlogic/shiftiq/mobile/
│   │       ├── ShiftIQApplication.kt          # Anotada @HiltAndroidApp
│   │       ├── MainActivity.kt                # @AndroidEntryPoint con ShiftIQTheme
│   │       ├── di/
│   │       │   └── NetworkConfigModule.kt     # Inyección de BaseUrl dinámica
│   │       └── core/                          # <-- Paquete de Fundación
│   │           ├── common/                    # Resultados funcionales y Dispatchers
│   │           ├── model/                     # Lenguaje Ubicuo (Role, Ids, Money)
│   │           ├── datastore/                 # Sesión persistente reactiva (DataStore)
│   │           ├── network/                   # Retrofit, OkHttp, AuthInterceptor, SafeApiCall
│   │           ├── designsystem/              # Tema Material 3 y Componentes Compose
│   │           └── navigation/                # Contratos sellados de rutas (AppDestination)
│   └── test/
│       └── java/com/tuxlogic/shiftiq/mobile/core/
│           ├── testing/                       # MainDispatcherRule para corrutinas
│           ├── common/result/                 # AppResultTest
│           ├── model/                         # ModelTest
│           └── network/                       # ErrorEnvelopeDtoTest
```

---

## 2. Componentes Implementados

### 2.1 Dominio y Abstracciones (`core/common` & `core/model`)
- [AppResult.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/common/result/AppResult.kt): Modelado funcional de resultados (`Success<T>` y `Failure`), evitando excepciones no controladas en el flujo de negocio. Extensiones: `map`, `onSuccess`, `onFailure`.
- [AppError.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/common/result/AppError.kt): Jerarquía sellada tipada para errores de red, backend, autenticación (`Unauthorized`, `Forbidden`), validación y negocio.
- [DispatcherProvider.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/common/dispatchers/DispatcherProvider.kt) y [DispatchersModule.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/common/di/DispatchersModule.kt): Abstracción e inyección Hilt de dispatchers (`main`, `io`, `default`).
- [Role.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/model/Role.kt): Enum que refleja con exactitud el modelo RBAC del backend Spring Boot (`ROLE_SYSTEM_ADMIN`, `ROLE_WORKSHOP_OWNER`, `ROLE_BRANCH_MANAGER`, `ROLE_MECHANIC`, `ROLE_RECEPTIONIST`, `ROLE_CUSTOMER`).
- [Ids.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/model/Ids.kt): Value Objects `@JvmInline value class` para identidades fuertemente tipadas (`UserId`, `WorkshopId`, `BranchId`, etc.).
- [Money.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/model/Money.kt): Value Object inmutable para montos financieros con `BigDecimal` y código ISO-4217.

### 2.2 Infraestructura y Persistencia (`core/datastore` & `core/network`)
- [SessionDataStore.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/datastore/SessionDataStore.kt) y [SessionDataStoreImpl.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/datastore/SessionDataStoreImpl.kt):
  - Almacena de forma reactiva (`Flow<SessionState>`) tokens JWT (`accessToken`, `refreshToken`), datos de usuario (`userId`, `userRole`) y el contexto de sucursal activa (`activeBranchId`).
  - Métodos: `saveSession(...)`, `setActiveBranchId(...)`, `clearSession()`.
- [AuthInterceptor.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/network/interceptor/AuthInterceptor.kt): Inyecta cabecera `Authorization: Bearer <accessToken>` de manera síncrona en cada petición que no sea de autenticación pública.
- [TokenAuthenticator.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/network/authenticator/TokenAuthenticator.kt): Refresco automático de token ante respuestas `HTTP 401 Unauthorized` usando `kotlinx.coroutines.sync.Mutex` para single-flight execution (evita tormentas de peticiones de refresco concurrentes).
- [ErrorEnvelopeDto.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/network/dto/ErrorEnvelopeDto.kt) y [SafeApiCall.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/network/SafeApiCall.kt): Parser seguro para el contrato de errores de la API.
- [NetworkModule.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/network/di/NetworkModule.kt): Configura `OkHttpClient`, logging interceptor y `Retrofit` inyectable vía Hilt.

### 2.3 Capa Visual y Navegación (`core/designsystem` & `core/navigation`)
- Tokens de tema: [Color.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/designsystem/theme/Color.kt), [Type.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/designsystem/theme/Type.kt) y [Theme.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/designsystem/theme/Theme.kt).
- Componentes reutilizables:
  - [ShiftIQButton.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/designsystem/components/ShiftIQButton.kt): Botón primario con soporte para estado de carga (`loading`).
  - [ShiftIQTextField.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/designsystem/components/ShiftIQTextField.kt): Entrada de texto con manejo de errores y validación.
  - [ShiftIQFeedback.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/designsystem/components/ShiftIQFeedback.kt): Pantalla de carga centralizada, banners de error y estados vacíos.
- [AppDestination.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/navigation/AppDestination.kt): Contrato sellado de rutas y deep links de la aplicación.

---

## 3. ¿Cuántas pantallas muestra actualmente la aplicación?

Actualmente en el **Bloque 0 (Fundación)**:
- **Pantallas activas en ejecución:** **1 pantalla base visible** en [MainActivity.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/MainActivity.kt). Muestra el contenedor `Scaffold` con el tema de la marca (`ShiftIQTheme`) y el texto centrado `"ShiftIQ Mobile Platform"`.
- **Previsualizaciones en Compose (Previews):** Existen vistas previas aisladas e interactivas para cada componente de diseño en `core/designsystem/components/` sin necesidad de ejecutar toda la aplicación.
- **¿Cuándo habrá más pantallas navegables?** En el **Bloque 1 (IAM)** se construirán las primeras **2 pantallas interactivas de negocio**:
  1. `LoginScreen`: Formulario de credenciales con llamada al backend.
  2. `BranchSelectionScreen`: Selección de sucursal para roles con múltiples sedes.

---

## 4. Guía de Verificación Visual en Android Studio (Paso a Paso)

Para probar e interactuar con la app visualmente sin tocar código:

### Método A: Ejecutar en un Emulador con Device Manager

1. **Abrir Android Studio:** Asegúrate de que el proyecto esté sincronizado (`Sync Project with Gradle Files`).
2. **Abrir el Device Manager:**
   - En la barra lateral derecha de Android Studio, haz clic en el icono de **Device Manager** (parece un teléfono).
   - Si ya tienes un dispositivo virtual creado (por ejemplo *Pixel 7 API 34*), haz clic en el botón **▶ (Play / Launch)** para encenderlo.
   - Si no tienes uno, haz clic en **+ Create Virtual Device**, elige un dispositivo (ej. *Pixel 8*) y selecciona una imagen del sistema (*API 34 o 35*).
3. **Seleccionar el Destino y Ejecutar:**
   - En la barra de herramientas superior, asegúrate de que el desplegable muestre el módulo `app` y a su lado el nombre de tu emulador en ejecución.
   - Haz clic en el botón verde **Run 'app' ▶** (o presiona `Shift + F10`).
4. **Qué verás en la pantalla del teléfono:**
   - La aplicación abrirá automáticamente.
   - Verás el fondo claro/oscuro adaptativo de Material 3 con el texto centrado en azul primario:
     `ShiftIQ Mobile Platform`
   - Esto confirma que `ShiftIQApplication` inicializó Hilt, `MainActivity` montó Compose con `ShiftIQTheme`, y el APK está sano.

---

### Método B: Probar Componentes Visuales con Compose Preview (Sin Emulador)

Puedes probar visualmente los componentes del sistema de diseño directamente en el editor:

1. Abre el archivo [ShiftIQButton.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/designsystem/components/ShiftIQButton.kt) o [ShiftIQTextField.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/designsystem/components/ShiftIQTextField.kt).
2. En la esquina superior derecha del editor de código, haz clic en el botón **Split** (muestra código a la izquierda y diseño a la derecha) o **Design**.
3. Verás los componentes renderizados en tiempo real:
   - Botón habilitado, botón deshabilitado y botón en estado de carga (con spinner animado).
   - Campo de texto normal y con mensaje de error de validación.
4. **Modo Interactivo:** En la parte superior de cada tarjeta de preview, haz clic en el icono **Interactive Mode** (parece un dedo/puntero). Te permitirá hacer clic y escribir en el teclado simulado dentro de Android Studio sin necesidad de abrir un emulador.

---

## 5. Consumo de APIs y Conexión con Backend

- **En Bloque 0:** La capa de red (`OkHttpClient`, `Retrofit`, `AuthInterceptor`, `TokenAuthenticator`) está completamente configurada y registrada en Hilt, pero **no realiza peticiones automáticas al inicio** porque aún no hay flujo de autenticación que lo dispare.
- **En Bloque 1 (IAM):** Se conectarán y consumirán las siguientes APIs del backend Spring Boot:
  - `POST /api/v1/auth/login`: Envía `username` (email) y `password`. Devuelve `AuthenticatedUserResource` con `accessToken`, `refreshToken`, `id`, `email`, `role`.
  - `POST /api/v1/auth/refresh`: Se invoca automáticamente cuando un token expira (HTTP 401).
  - `POST /api/v1/auth/logout`: Invalida la sesión en el servidor y limpia `SessionDataStore`.
