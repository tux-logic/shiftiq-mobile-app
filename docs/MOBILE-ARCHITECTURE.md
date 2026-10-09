# ShiftIQ Mobile — Arquitectura y Plan de Desarrollo

Documento de referencia para desarrollar la app Android (`com.tuxlogic.shiftiq.mobile`) contra el
backend **ShiftIQ Platform**. Define el modelo de dominio del backend, el flujo de autenticación,
el catálogo de endpoints y la organización DDD + Clean Architecture en módulos Gradle.

> Backend de referencia local: `C:\Users\alanj\Proyectos\shiftiq-platform`.
> Docs del backend a consultar: `docs/api-endpoints.md` (request/response completos) y
> `docs/auth-and-roles-flow.md` (roles, jerarquía y onboarding de personal).

---

## 1. Qué es ShiftIQ Platform

Monolito **Spring Boot 4.0.6 / Java 26 / Maven**, PostgreSQL 16 con migraciones **Flyway** (última `V14`),
seguridad **JWT**, construido con **DDD + Arquitectura Hexagonal (puertos/adaptadores) + CQRS + EDA**
(eventos de dominio de Spring con `@TransactionalEventListener(AFTER_COMMIT)`).

- **URLs base:** producción Render `https://shiftiq-platform.onrender.com`; local `http://localhost:8080`.
- **Prefijo REST:** `/api/v1`. Errores primarios en envelope `{ "code", "message", "details" }`, con soporte secundario para RFC 7807 `ProblemDetail` (`type`, `title`, `status`, `detail`) emitido en IoT/Spring native.
- **Multi-tenancy:** el backend resuelve la pertenencia a sedes (`branchIds`) y talleres por cada petición consultando la BD a partir del usuario. En la app móvil, el usuario selecciona una sede activa (`activeBranchId`) que se persiste en `SessionDataStore` y se envía en los endpoints operativos que la requieren.

### Bounded contexts

| Contexto | Rol | Agregados clave |
| :--- | :--- | :--- |
| `iam` | Autenticación, usuarios, JWT | `User`, `PasswordRecoveryToken` |
| `core` | Estructura organizacional | `Owner`, `Workshop`, `Branch`, `Employee`, `Customer`, `WorkshopSpecialty`, `SubscriptionPlan` |
| `fleet` | Citas y vinculaciones de personal/clientes | `Appointment`, `CustomerRegistration`, `EmployeeRegistration` |
| `operations` | Motor operativo del taller | `WorkOrder`, `WorkOrderTask`, `Service` |
| `inventory` | Stock y lotes (FIFO) | `Product`, `ProductBatch` |
| `billing` | Cotizaciones, comprobantes, pagos | `Quote`, `Voucher`, `Payment` |
| `iot` | Vehículos, dispositivos OBD-II, telemetría | `Vehicle`, `Obd2Device`, `Obd2DeviceRegistration`, `TelemetryBatch`, `DtcAlert` |
| `analytics` | KPIs por sede y red | `BranchAnalyticsSnapshot` |
| `shared` | Kernel compartido, seguridad, VO, eventos cross-context | — |

### Eventos de dominio cross-context (EDA)

- `ProductReservedEvent` (Operations → Inventory): descuenta stock.
- `PaymentProcessedEvent` (Billing → Operations): marca la orden como `PAID`.
- `DtcAlertTriggeredEvent` (IoT → Fleet).
- `EmployeeRegistrationApprovedEvent` (Fleet → IAM): otorga acceso a la sede.
- Los eventos cross-context viven en `shared.domain.model.events` con **UUIDs planos**.

---

## 2. Autenticación, tokens y roles

### Flujo de tokens (IAM)

1. **Login:** `POST /api/v1/authentication/sessions` con `{ email, password }`.
   Devuelve:
   ```json
   {
     "id": "uuid",
     "email": "user@example.com",
     "role": "ROLE_EMPLOYEE",
     "token": "<access JWT>",
     "refreshToken": "uuid",
     "accessTokenExpiresInSeconds": 900
   }
   ```
2. **Peticiones protegidas:** `Authorization: Bearer <token>`.
3. **401 → refresh:** `POST /api/v1/authentication/sessions/refresh` con `{ refreshToken }`.
   Devuelve un par nuevo `token` + `refreshToken`.
4. **Logout:** `DELETE /api/v1/authentication/sessions` con `{ refreshToken }` → `204 No Content`.

Reglas críticas para el cliente:

- El **access token dura 15 min**; el **refresh token 7 días**.
- **Rotación de un solo uso:** cada refresh consume el refresh token anterior. Persiste el nuevo par
  **antes** de reintentar peticiones; si el refresh devuelve 401, la sesión terminó → volver a login.
- El refresh token tiene audiencia distinta (`shiftiq-refresh`), por lo que **nunca** sirve como Bearer.
- **Claims del JWT vs. Sesión:** El JWT únicamente firma el `subject` (email); no lleva `role` ni `branchIds`
  en sus claims. El `role` principal se obtiene en el cuerpo de `AuthenticatedUserResource` durante login o
  refresh. Las sedes (`branchIds`) no viajan en el token ni en la sesión inicial; se consultan mediante los
  endpoints de perfil/talleres (`/workshops`, `/branches`, `/employee-registrations`). La app debe persistir
  en `SessionDataStore` la sede seleccionada (`activeBranchId`) para enviarla en las consultas operativas.
- **Logout en Retrofit:** `DELETE /api/v1/authentication/sessions` requiere `@RequestBody RevokeSessionResource`.
  En Retrofit se debe anotar con `@HTTP(method = "DELETE", path = "api/v1/authentication/sessions", hasBody = true)`
  porque `@DELETE` estándar no admite body y lanzará una excepción.

### Google Sign-In

`POST /api/v1/authentication/sessions/google` con `{ "idToken": "<Google JWT>" }`. Si el usuario no
existe, se auto-registra con `ROLE_USER`. Devuelve el mismo `AuthenticatedUserResource`.

### Roles (jerarquía)

| Rol | Rango | Descripción |
| :--- | :---: | :--- |
| `ROLE_ADMIN` | 4 | Administrador global de la plataforma (acceso total). |
| `ROLE_OWNER` | 3 | Dueño del taller; gestiona talleres, sedes y personal superior. |
| `ROLE_BRANCH_MANAGER` | 2 | Gerente de sede. |
| `ROLE_ASSISTANT` | 1 | Asistente/recepción de sede; da de alta técnicos. |
| `ROLE_EMPLOYEE` | 0 | Personal técnico (mecánico, electricista, etc.). |
| `ROLE_USER` | 0 | Cliente particular / usuario base antes de ser contratado. |

Alta de personal (`POST /api/v1/employee-registrations`): `OWNER` en taller multi-sede solo puede
nombrar `BRANCH_MANAGER`; `BRANCH_MANAGER` nomina `ASSISTANT`/`EMPLOYEE` en sus sedes; `ASSISTANT`
solo `EMPLOYEE`; `EMPLOYEE` no gestiona personal. Infracciones → `403`; roles reservados de
plataforma → `400`. Flujo autónomo: `POST /employee-registrations/request-join` (queda `PENDING_APPROVAL`)
y el encargado aprueba con `POST /employee-registrations/{id}/approve`.

`GET /api/v1/profiles/roles?userId={userId}` devuelve los roles asignados al usuario especificado (`userId`
es obligatorio; solo el propio usuario o un admin tienen acceso).

---

## 3. Catálogo de endpoints (por contexto)

Todos bajo `/api/v1` salvo los de sistema. `§` = requiere Bearer JWT.

### Sistema

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/` | Ping raíz. |
| `GET` | `/health` | Health check. |
| `POST` | `/media/upload` (multipart) | Subir imagen a Cloudinary → `{ url }`. |

### IAM — `/api/v1`

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/authentication/sessions` | Login con email y contraseña. |
| `POST` | `/authentication/sessions/google` | Login con Google (`idToken`). |
| `POST` | `/authentication/sessions/refresh` | Renovar sesión (`refreshToken`). |
| `DELETE` | `/authentication/sessions` | Logout (`refreshToken` en body; usar `@HTTP(hasBody = true)` en Retrofit). |
| `POST` | `/authentication/password-recoveries` | Solicitar recuperación de contraseña por email. |
| `POST` | `/authentication/password-resets` | Restablecer contraseña con token de recuperación. |
| `POST` | `/users` | Registrar usuario nuevo (sign-up). |
| `GET` | `/users?email=` | Buscar usuario por email (no existe listado global). |
| `GET` | `/users/{userId}` | Obtener usuario por ID. |
| `PUT` | `/users/{userId}/email` | Actualizar email del usuario. |
| `PUT` | `/users/{userId}/password` | Actualizar contraseña del usuario. |

### Core — `/api/v1`

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST`/`GET` | `/workshops` | Crear taller / Listar talleres de un dueño (`?ownerId=` obligatorio). |
| `PUT`/`GET` | `/workshops/{workshopId}` | Actualizar / obtener taller por ID. |
| `POST`/`GET` | `/workshops/{workshopId}/specialties` | Crear / listar especialidades de un taller. |
| `GET`/`PUT`/`DELETE` | `/workshops/{workshopId}/specialties/{specialtyId}` | Detalle / actualizar / desactivar especialidad. |
| `POST`/`GET` | `/branches` | Crear sede / Listar sedes de un taller (`?workshopId=` obligatorio). |
| `PUT`/`GET` | `/branches/{branchId}` | Actualizar / obtener sede por ID. |
| `POST` | `/branches/{branchId}/subscriptions` | Asignar suscripción simulada a la sede. |
| `DELETE` | `/branches/{branchId}/subscription` | Cancelar suscripción activa de la sede. |
| `POST`/`GET` | `/owners` | Crear perfil de dueño / Obtener dueño por usuario (`?userId=` obligatorio, objeto único). |
| `PUT`/`GET`/`DELETE` | `/owners/{ownerId}` | Actualizar / obtener / borrar dueño por ID. |
| `POST`/`GET` | `/employees` | Crear perfil empleado / Obtener empleado (`?userId=` o `?documentNumber=`, objeto único). |
| `PUT`/`GET`/`DELETE` | `/employees/{employeeId}` | Actualizar / obtener / borrar empleado por ID. |
| `POST`/`GET` | `/customers` | Crear perfil cliente / Obtener cliente por usuario (`?userId=` obligatorio, objeto único). |
| `PUT`/`GET`/`DELETE` | `/customers/{customerId}` | Actualizar / obtener / borrar cliente por ID. |
| `GET` | `/profiles/roles?userId=` | Obtener roles del usuario (`?userId=` obligatorio). |
| `GET` | `/profiles?documentNumber=` | Buscar perfil resumido por DNI/RUC. |

### Fleet — `/api/v1`

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST`/`GET` | `/appointments` | Agendar cita / Listar citas (`?branchId=`, `?status=`, `?customerId=`, `?vehicleId=`). |
| `GET`/`PUT`/`DELETE` | `/appointments/{appointmentId}` | Detalle / actualizar / cancelar cita. |
| `POST`/`GET` | `/customer-registrations` | Vincular cliente a sede / Listar clientes vinculados (`?branchId=`, `?customerId=`). |
| `GET`/`PUT`/`DELETE` | `/customer-registrations/{registrationId}` | Detalle / actualizar / desactivar vinculación de cliente. |
| `POST`/`GET` | `/employee-registrations` | Registrar personal en sede / Listar personal de sede (`?branchId=` obligatorio, `?status=`). |
| `GET`/`PUT`/`DELETE` | `/employee-registrations/{id}` | Detalle / actualizar especialidad y salario / desactivar personal. |
| `POST` | `/employee-registrations/request-join` | Postulación autónoma de un técnico a una sede (`PENDING_APPROVAL`). |
| `POST` | `/employee-registrations/{id}/approve` | Aprobar postulación de personal técnico (asigna sede en IAM). |
| `POST` | `/employee-registrations/{id}/reject` | Rechazar postulación de personal técnico. |

### Inventory — `/api/v1/inventory/products`

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/inventory/products` | Crear producto/repuesto en inventario para una sede. |
| `GET` | `/inventory/products?branchId=` | Listar catálogo de la sede (`?branchId=` obligatorio; filtros: `name`, `category`, `lowStockOnly`). |
| `GET` | `/inventory/products/branch/{branchId}` | Ruta alternativa para catálogo de sede con los mismos filtros. |
| `GET`/`PUT`/`DELETE` | `/inventory/products/{productId}` | Detalle (con lotes) / actualizar / borrar producto. |
| `POST` | `/inventory/products/{productId}/batches` | Ingreso de lote o ajuste de stock (cantidad positiva o negativa). |

### Operations — `/api/v1`

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST`/`GET` | `/services` | Crear servicio / Listar servicios del catálogo de la sede (`?branchId=` obligatorio). |
| `PUT`/`DELETE` | `/services/{serviceId}` | Actualizar / borrar servicio. |
| `POST`/`GET` | `/work-orders` | Crear orden / Listar órdenes (requiere obligatoriamente `?branchId=` o `?vehicleId=`). |
| `GET`/`PUT`/`DELETE` | `/work-orders/{id}` | Detalle / actualizar resumen y kilometraje / borrar orden. |
| `POST` | `/work-orders/{id}/complete` | Completar orden (valida que todas las tareas estén finalizadas). |
| `POST` | `/work-orders/{id}/tasks` | Agregar tarea a la orden. |
| `PUT`/`DELETE` | `/work-orders/{id}/tasks/{taskId}` | Actualizar / borrar tarea de la orden. |
| `POST`/`PUT`/`DELETE` | `/work-order-tasks/{taskId}/products[/{productId}]` | Añadir repuesto (emite `ProductReservedEvent`), actualizar cantidad o quitar. |
| `POST` | `/work-order-tasks/{taskId}/start` | Iniciar tarea (pasa a `IN_PROGRESS`). |
| `POST` | `/work-order-tasks/{taskId}/complete` | Completar tarea (pasa a `COMPLETED`). |
| `POST` | `/work-order-tasks/{taskId}/reopen` | Reabrir tarea completada. |
| `POST` | `/work-order-tasks/{taskId}/assign-mechanic` | Asignar mecánico responsable a la tarea. |

### Billing — `/api/v1`

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST`/`GET` | `/quotes` | Crear cotización basada en orden / Listar cotizaciones (`?branchId=` obligatorio). |
| `GET`/`PUT` | `/quotes/{id}` | Detalle de cotización / actualizar descuento en cotización `DRAFT`. |
| `POST` | `/quotes/{id}/approvals` | Aprobar cotización. |
| `POST` | `/quotes/{id}/cancellations` | Cancelar cotización. |
| `POST`/`GET` | `/vouchers` | Emitir comprobante electrónico / Listar comprobantes (`?branchId=` obligatorio). |
| `GET` | `/vouchers/{voucherId}` | Detalle del comprobante emitido. |
| `POST` | `/vouchers/{voucherId}/payments` | Registrar pago individual contra el comprobante. |
| `POST` | `/checkouts` | Checkout todo-en-uno (genera comprobante y procesa pago en una sola transacción). |
| `POST` | `/checkouts/mercadopago` | Checkout integrado con verificación de pago en Mercado Pago y emisión SUNAT. |
| `POST` | `/payments/mercadopago/preferences` | Crear preferencia de pago en Mercado Pago. |
| `POST` | `/payments/mercadopago/webhooks` | Webhook de Mercado Pago (procesamiento server-side). |

### IoT — `/api/v1`

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST`/`GET` | `/vehicles` | Registrar vehículo del cliente / Listar vehículos vinculables (`?branchId=` y `?status=available-for-linking` obligatorios). |
| `GET`/`PUT`/`DELETE` | `/vehicles/{id}` | Detalle / actualizar / borrar vehículo. |
| `GET` | `/customers/{customerId}/vehicles` | Listar vehículos activos del cliente. |
| `GET` | `/vehicles/{vehicleId}/telemetry-snapshots` | Historial de telemetría del vehículo. |
| `GET` | `/vehicles/{vehicleId}/dtc-alerts` | Historial de alertas DTC (fallas OBD-II) del vehículo. |
| `POST`/`GET` | `/obd2-devices` | Registrar dongle OBD-II / Listar dispositivos (`?branchId=` obligatorio; `?status=available` opcional). |
| `GET`/`PUT`/`DELETE` | `/obd2-devices/{id}` | Detalle / actualizar / borrar dispositivo. |
| `GET` | `/obd2-devices/{deviceId}/telemetry-snapshots[/latest]` | Snapshots de telemetría del dispositivo / última captura. |
| `POST`/`GET` | `/obd2-device-registrations` | Instalar dongle en vehículo / Listar acoplamientos (`?branchId=` y `?status=` obligatorios). |
| `PATCH` | `/obd2-device-registrations/{id}` | Desactivar instalación (`status: "INACTIVE"`). |
| `GET` | `/obd2-device-registrations/{id}/telemetry-snapshots` | Telemetría paginada de la instalación. |
| `GET` | `/obd2-device-registrations/{id}/dtc-alerts` | Alertas DTC de la instalación. |
| `POST` | `/telemetry-batches` | Ingesta masiva de telemetría (uso de hardware/dispositivo). |

### Analytics — `/api/v1/analytics`

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/analytics/branches/{branchId}/summary` | Resumen ejecutivo del día para la sede (`branchId`). |
| `GET` | `/analytics/branches/{branchId}/financial` | Snapshots diarios en rango de fechas (`?startDate=`, `?endDate=`, máx 365 días). |
| `GET` | `/analytics/network/summary` | Resumen consolidado multi-sede (requiere rol `OWNER` o `ADMIN`). |

---

## 4. Arquitectura mobile (DDD + Clean Architecture)

**Decisión:** multi-módulo Gradle desde el inicio. **1 bounded context del backend = 1 módulo Gradle
`feature`**, con las capas de Clean Architecture como paquetes internos
(`domain / data / presentation`). La infraestructura transversal vive en módulos `core`.

### 4.1 Mapa de módulos

```
:app                       # @HiltAndroidApp, MainActivity, NavHost raíz, agregación DI
build-logic/               # convention plugins: shiftiq.android.application|library|feature, shiftiq.kotlin.library

:core:common               # AppResult, DispatcherProvider, mappers base, extensiones (Kotlin puro)
:core:model                # Value objects compartidos: UserId, BranchId, Money, Role (Kotlin puro)
:core:network              # Retrofit3 + Gson, OkHttp, AuthInterceptor, TokenAuthenticator, ApiError
:core:datastore            # SessionDataStore (token, refreshToken, userId, role, activeBranchId)
:core:database             # Room3 (caché local)
:core:designsystem         # Tema Material3, color, tipografía, componentes reutilizables
:core:navigation           # Contratos de rutas y navegación (route keys)

:feature:iam               # Auth, sesión, perfil/roles
:feature:core              # Talleres, sedes, empleados, clientes
:feature:fleet             # Citas, registros de cliente/personal
:feature:operations        # Órdenes de trabajo, servicios
:feature:inventory         # Productos y lotes
:feature:billing           # Cotizaciones, comprobantes, checkout
:feature:iot               # Vehículos, dispositivos, telemetría
:feature:analytics         # Dashboards y KPIs
```

### 4.2 Regla de dependencias

- `:app` → todos los `:feature:*` + `:core:designsystem` + `:core:navigation`.
- `:feature:*` → `:core:network`, `:core:datastore`, `:core:database`, `:core:model`,
  `:core:common`, `:core:designsystem`, `:core:navigation`.
- `:core:*` **nunca** depende de `:feature:*`.
- Un `:feature:*` **no** importa código de otro `:feature:*`: se comunican por `:core:model` o por
  rutas de navegación en `:core:navigation`.

Analogía con el backend: `:core:*` ≈ `shared`; cada `:feature:*` ≈ un bounded context; la regla
"`shared` nunca depende de otros contextos" se refleja en la dirección de dependencias de Gradle.

### 4.3 Features vs. módulos core (no-feature)

Los **features** representan el *qué* del negocio (un bounded context cada uno). Todo lo demás es
plomería transversal que existe para que los features no se repitan y no se acoplen entre sí; es el
equivalente móvil del `shared` del backend más el *host* de la app.

**Por qué existen módulos no-feature:**

1. **Reutilización sin copiar:** red, sesión, tema y rutas los usan los 8 features; sin core, cada
   feature duplicaría Retrofit e interceptors.
2. **Aislamiento (DDD):** un feature no debe importar código de otro; lo realmente común sube a core.
3. **Enforcement del build:** Gradle hace cumplir la regla de dependencias; importar algo prohibido
   **no compila**, así la arquitectura deja de ser una convención y pasa a ser ejecutable.

**Infraestructura de build (no produce APK):**

| Módulo | Rol | Por qué / cómo |
| :--- | :--- | :--- |
| `build-logic` | *Included build* con plugins de convención | Centraliza los bloques `android {}`, Kotlin, Compose y Hilt para no repetirlos en ~14 módulos. Se engancha con `pluginManagement { includeBuild("build-logic") }`. |
| `:app` | Aplicación host / composición raíz | `@HiltAndroidApp`, `MainActivity`, `NavHost`, agregación de DI. **Depende de todos** los features; **nadie depende de él**. Es el `main()`/bootstrap. |

**Kernel compartido (`:core:*`):**

| Módulo | Contenido | Tipo | Depende de |
| :--- | :--- | :--- | :--- |
| `:core:common` | `AppResult`, `DispatcherProvider`, extensiones, `safeApiCall` | Kotlin puro (JVM) | — |
| `:core:model` | VOs compartidos: `UserId`, `BranchId`, `Money`, `Role` | Kotlin puro (JVM) | — |
| `:core:network` | Retrofit3 + Gson, OkHttp, `AuthInterceptor`, `TokenAuthenticator`, `ApiError`, DTO base | Android library | `common`, `model` |
| `:core:datastore` | `SessionDataStore` (token, refresh, userId, role, `activeBranchId`) | Android library | `common`, `model` |
| `:core:database` | Room3, DAOs, caché offline (opcional) | Android library | `common`, `model` |
| `:core:designsystem` | Tema Material3, color, tipografía, componentes reutilizables | Android + Compose | `common` |
| `:core:navigation` | Contratos de rutas / destinos | Android + Compose | `model` |
| `:core:testing` | `MainDispatcherRule`, fakes, helpers | Android library (`testImplementation`) | `common` |

Distinción clave entre los dos Kotlin puros: **`:core:common` es técnico** (no sabe de negocio),
**`:core:model` es lenguaje ubicuo compartido** (sí nombra conceptos del negocio). Es el *shared
kernel* del backend, pero mínimo: si un tipo solo lo usa un feature, vive en ese feature, **no** aquí.

**Cómo se organizan (a diferencia de los features):**

- **Features → por dominio (DDD):** cada uno es una rebanada vertical con `domain/data/presentation`
  adentro.
- **Core → por capacidad técnica (Clean Architecture horizontal):** cada módulo es una sola
  preocupación; *es* infraestructura/adaptador, por eso no tiene capas de dominio.

**Grafo de dependencias (se hace cumplir por Gradle):**

```
                         :app
                          │  (depende de todos los features + navigation + designsystem)
      ┌───────────────────┼───────────────────────────────┐
      ▼                   ▼                               ▼
:feature:iam   :feature:core  ... :feature:analytics   :core:navigation
      │                   │                               │
      └───────────────────┴───────────────┬───────────────┘
                                          ▼
                    :core:network   :core:datastore   :core:database
                                          │
                         :core:designsystem│
                                          ▼
                                   :core:model
                                          │
                                          ▼
                                   :core:common
```

**Reglas duras:**

1. `:core:*` **nunca** depende de `:feature:*` ni de `:app`.
2. Un `:feature:*` **nunca** depende de otro `:feature:*`.
3. `:core:common` y `:core:model` no dependen de nadie (son la base).
4. `:app` no expone lógica: solo cablea.

Las flechas apuntan siempre "hacia dentro/hacia abajo": así `:core:model` y `:core:common` se testean
en JVM puro, se puede reemplazar Retrofit sin tocar features, y un feature compila sin los demás.

**Ajustes opcionales:**

- `:core:database` solo si se implementa caché offline; si no, se omite al inicio.
- `:core:navigation` puede fusionarse en `:core:designsystem` para reducir módulos, pero separado
  permite que un feature navegue a otro **sin** depender de él (solo de las rutas).
- `:core:model` y `:core:common` pueden fusionarse al principio; separados dejan claro qué tipo es
  "compartido de negocio" y cuál es "utilidad".

### 4.4 Capas dentro de un feature

```
:feature:iam/src/main/java/com/tuxlogic/shiftiq/mobile/feature/iam/
├── domain/
│   ├── model/            # Entidades y VOs propios (User, Session, Role)
│   ├── repository/       # Interfaz AuthRepository
│   └── usecase/          # SignInUseCase, RefreshSessionUseCase, SignOutUseCase
├── data/
│   ├── remote/           # AuthApi (Retrofit), DTOs (SignInRequest, AuthenticatedUserDto)
│   ├── mapper/           # DTO <-> dominio
│   └── repository/       # AuthRepositoryImpl
└── presentation/
    ├── login/            # LoginScreen, LoginViewModel, LoginUiState
    └── navigation/       # Destinos que expone el feature
```

- `presentation → domain ← data` (la capa `data` implementa interfaces de `domain`).
- `domain` es **Kotlin puro** (sin Android, sin Retrofit, sin Room): testeable en JVM.
- La inyección (Hilt) usa un `@Module` por feature que liga `RepositoryImpl` → interfaz de `domain`.

> Si más adelante se requiere enforcement estricto, se puede subdividir cada feature en
> `:feature:x:domain` / `:feature:x:data` / `:feature:x:presentation`. No es necesario al inicio.

### 4.5 Mapeo backend → feature mobile

| Bounded context backend | Módulo mobile | Pantallas núcleo |
| :--- | :--- | :--- |
| `iam` | `:feature:iam` | Login, Google, recuperar contraseña, perfil/roles |
| `core` | `:feature:core` | Talleres, sedes, empleados, clientes, especialidades, suscripciones |
| `fleet` | `:feature:fleet` | Citas, vincular clientes, alta/aprobación de personal |
| `operations` | `:feature:operations` | Servicios, órdenes de trabajo, tareas y repuestos |
| `inventory` | `:feature:inventory` | Productos, lotes/stock |
| `billing` | `:feature:billing` | Cotizaciones, comprobantes, checkout |
| `iot` | `:feature:iot` | Vehículos, dispositivos OBD-II, telemetría y DTC |
| `analytics` | `:feature:analytics` | Dashboards por sede y red |

---

## 5. Convenciones

- **DTO ↔ dominio:** los DTO usan los nombres camelCase del backend (`accessTokenExpiresInSeconds`,
  `refreshToken`, `branchId`, ...). Gson mapea 1:1; **no** configurar estrategia de nombres.
- **Resultado de dominio:** envolver la red en `AppResult<T>` (`Success` / `Failure(ApiError)`); no
  propagar `Response<T>` ni excepciones de Retrofit a `presentation`.
- **Errores:** parsear el envelope `{ code, message, details }` y el formato RFC 7807 `ProblemDetail` (`title`, `status`, `detail`) a un `ApiError` tipado; `401` dispara el flujo de refresh, `403` mapea a "sin permisos", `404` a "no encontrado".
- **Corrutinas:** `suspend` en `domain`/`data`; despachadores vía `DispatcherProvider` (no hardcodear
  `Dispatchers.IO`).
- **DI:** un `@Module` por feature; `:app` expone `Application @HiltAndroidApp`. Hilt + **KSP** (no kapt).
- **Estado:** `StateFlow<UiState>` en los ViewModels; estados exhaustivos `Loading/Content/Error/Empty`.
- **Sesión:** `SessionDataStore` es la única fuente de verdad de tokens y `activeBranchId`; el
  `AuthInterceptor` lee el access token desde ahí. Los roles se cargan al iniciar sesión/refrescar y
  la sede activa se mantiene persistida para contextualizar todas las consultas operativas.

---

## 6. Cambios de build necesarios

### 6.1 Catálogo `gradle/libs.versions.toml` (agregar)

- `androidx.navigation:navigation-compose` (navegación; hoy **no** está declarado).
- `com.squareup.okhttp3:logging-interceptor` (debug).
- `androidx.security:security-crypto` para tokens en reposo, o usar Android Keystore directamente.
- `org.jetbrains.kotlin.jvm` plugin (módulos Kotlin puros `:core:common`, `:core:model`, `domain`).
- `com.android.tools:desugar_jdk_libs` + `isCoreLibraryDesugaringEnabled` (ver gotcha de `java.time`).
- Room3 ktx/extensiones si se usan (verificar disponibilidad del artefacto `androidx.room3`).
- Testing: `mockwebserver`, `kotlinx-coroutines-test`, `turbine`, `mockk`.

### 6.2 `:app`

- `buildFeatures { buildConfig = true }` y `buildConfigField("String", "BASE_URL", ...)` por `buildType`:
  - `debug`: `http://10.0.2.2:8080/` (emulador → localhost del host).
  - `release`: `https://shiftiq-platform.onrender.com/`.
- **Retrofit exige que la URL base termine en `/`.**

### 6.3 `build-logic` (convention plugins)

Evita repetir bloques `android { }`/`kotlin`/Hilt en cada módulo. Plugins sugeridos:
`shiftiq.android.application`, `shiftiq.android.library`, `shiftiq.android.library.compose`,
`shiftiq.android.feature` (library + compose + Hilt + deps `:core:*`), `shiftiq.kotlin.library`.
Se incluye en `settings.gradle.kts` vía `pluginManagement { includeBuild("build-logic") }`.

### 6.4 Notas del toolchain (no cambiar)

- AGP 9.4.1, Kotlin 2.4.21, Gradle 9.6, daemon JDK 25, `compileSdk`/`targetSdk` 37, `minSdk` 24.
- AGP 9 trae **Kotlin integrado**: `:app` no aplica `org.jetbrains.kotlin.android`. Para módulos
  Kotlin puros sí se usa `org.jetbrains.kotlin.jvm`.
- Room usa artefactos **`androidx.room3`** (`room3-runtime` / `room3-compiler`), no `androidx.room`.
- R8 keep rules en `app/src/main/keepRules/*.keep`.

---

## 7. Roadmap

### Fase 0 — Fundación

1. Crear `build-logic` con convention plugins y esqueleto de módulos en `settings.gradle.kts`.
2. `:app`: `ShiftIQApplication @HiltAndroidApp` + registro `android:name` en `AndroidManifest.xml`;
   `buildConfig` y `BASE_URL`.
3. `:core:common`: `AppResult`, `DispatcherProvider`.
4. `:core:model`: VOs (`UserId`, `BranchId`, `Money`, `Role`).
5. `:core:network`: OkHttp + Retrofit + Gson; `AuthInterceptor`; `TokenAuthenticator` con refresh
   **single-flight** (Mutex) y persistencia atómica del refresh rotado; parseo de `ApiError`.
6. `:core:datastore`: `SessionDataStore`.
7. `:core:designsystem`: mover el tema actual (`ui/theme`) y unificar componentes.

### Fase 1 — IAM + navegación por rol (primer hito)

1. `:feature:iam` dominio: `User`, `Session`, `Role`; `AuthRepository`; use cases de login, Google,
   refresh, logout y `GetProfileRoles`.
2. `:feature:iam` data: `AuthApi` (Retrofit) + DTOs + mappers + `AuthRepositoryImpl`.
3. `:feature:iam` presentación: `LoginScreen` + `LoginViewModel` + `LoginUiState`; pantallas de
   recuperación de contraseña.
4. `:core:navigation`: contratos de rutas. `:app`: `NavHost` con **destino inicial según `role`** y
   *session gate* al arranque (revisar sesión almacenada y refrescar si aplica).

### Fase 2+ — Resto de contextos

Orden sugerido: `core` → `fleet` → `operations` → `inventory` → `billing` → `iot` → `analytics`.
La mayoría de operaciones requieren `branchId`; el contexto de sede activa vive en la sesión.

### Testing

- Unit de use cases y ViewModels con fakes; `MainDispatcherRule` en `:core:testing`.
- API con `MockWebServer` (incluye el escenario de `401` → refresh → reintento).
- UI Compose instrumentada donde aporte.

---

## 8. Gotchas

- **`minSdk 24` + `java.time`:** el backend usa `Instant`/ISO-8601. `java.time` nativo requiere API 26,
  así que hay que activar **core library desugaring** (`isCoreLibraryDesugaringEnabled = true` +
  `coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:...")`), o usar `kotlinx-datetime`.
- **Refresh en paralelo:** si varias peticiones reciben `401` a la vez, hay que serializar el refresh
  (Mutex / `OkHttp Authenticator`) y reintentar; evita consumir el refresh token rotado varias veces.
- **Refresh token de un solo uso:** persistirlo **antes** de reintentar o se pierde la sesión.
- **Logout con Retrofit:** `DELETE /api/v1/authentication/sessions` lleva cuerpo JSON; debe anotarse como
  `@HTTP(method = "DELETE", path = "api/v1/authentication/sessions", hasBody = true)` porque `@DELETE` estándar
  falla si se le añade un `@Body`.
- **JWT y Claims:** el access token solo firma el email (`subject`), sin claims de rol ni sedes. El rol de la sesión
  se extrae de `AuthenticatedUserResource`, y la sede activa (`activeBranchId`) se selecciona tras consultar los
  talleres/sedes del usuario y se mantiene en `SessionDataStore`.
- **Filtros obligatorios en endpoints:** casi todas las consultas de listados (`/branches`, `/workshops`,
  `/inventory/products`, `/work-orders`, `/quotes`, `/vouchers`, `/services`, etc.) exigen parámetros obligatorios
  (`?branchId=`, `?workshopId=`, `?ownerId=`). Llamarlas sin ellos devuelve `400 Bad Request`.
- **Uploads:** usar multipart `POST /media/upload` y Coil para mostrar; el backend devuelve una URL.
- **Sin linter/formatter** configurado en el repo; `assembleDebug` + tests son la única verificación.
- **Windows:** compilar/probar con `.\gradlew.bat`.
