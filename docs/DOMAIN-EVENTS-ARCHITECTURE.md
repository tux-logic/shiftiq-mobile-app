# Arquitectura de Eventos de Dominio Locales en Memoria y Clean DDD

Documento de referencia técnica sobre la arquitectura de desacoplamiento reactivo mediante eventos de dominio locales en memoria (`DomainEventBus`) y el estándar de diseño Clean Architecture + Domain-Driven Design (DDD) en cuatro capas implementado en la aplicación móvil ShiftIQ Mobile (`com.tuxlogic.shiftiq.mobile`).

---

## 1. Fundamentos y Filosofia: Ambito Local vs. Ambito Global

Una distincion critica en la arquitectura de software empresarial es el ambito de ejecucion y ciclo de vida de los eventos de dominio:

### 1.1 Eventos de Dominio Globales (Backend EDA)
- **Entorno de ejecucion:** Servidor centralizado (Spring Boot 4 / Java 26 / PostgreSQL).
- **Proposito:** Integrar agregados transaccionales a nivel de base de datos corporativa.
- **Mecanismo:** Despachados de forma diferida tras la confirmacion de una transaccion ACID en PostgreSQL utilizando `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)`.
- **Ejemplos:** `ProductReservedEvent` (descuenta inventario tras confirmar tarea), `PaymentProcessedEvent` (marca orden como pagada tras cobro).
- **Alcance:** Afecta a toda la organizacion, requiere durabilidad y consistencia eventual distribuida.

### 1.2 Eventos de Dominio Locales (Mobile In-Memory EDA)
- **Entorno de ejecucion:** Cliente movil Android en la memoria de la aplicacion (`com.tuxlogic.shiftiq.mobile`).
- **Proposito:** Coordinar reactivamente Bounded Contexts independientes sin acoplamiento en tiempo de compilacion ni circularidad de dependencias.
- **Mecanismo:** Despachados en memoria a traves de Kotlin Coroutines y un bus reactivo centralizado (`DomainEventBus` en `:core:common:event`) basado en `MutableSharedFlow`.
- **Ejemplos:** `ActiveBranchChangedEvent` (el usuario alterno la sede activa), `SessionExpiredEvent` (los tokens de sesion quedaron revocados), `UserLoggedOutEvent` (el usuario cerro sesion voluntariamente).
- **Alcance:** Estrictamente local al ciclo de vida del proceso de la app Android. No requiere persistencia transaccional en servidor ni viaja por la red.

---

## 2. Aislamiento Estricto entre Features (Sibling Feature Dependency Isolation)

La arquitectura de ShiftIQ Mobile prohibe taxativamente que un feature importe codigo de otro feature hermano:

```
[feature:iam]     [feature:core]     [feature:operations]     [feature:billing]
      │                  │                    │                       │
      X ─────────────────X ────────────────── X ──────────────────────X (PROHIBIDO)
      │                  │                    │                       │
      ▼                  ▼                    ▼                       ▼
                     :core:common:event  (DomainEventBus)
                     :core:model         (Shared Kernel)
                     :core:navigation    (Contratos de rutas)
```

### Reglas de comunicacion entre Bounded Contexts:
1. **Un `:feature:A` NUNCA depende de `:feature:B`.** No existen dependencias horizontales directas en el grafo de Gradle.
2. **Tipos de identidad y conceptos universales:** Viven en el Shared Kernel (`:core:model`) como Value Objects inmutables (`UserId`, `BranchId`, `Role`, `Money`).
3. **Navegacion visual:** Se gestiona mediante contratos de rutas y deep links en `:core:navigation`.
4. **Coordinacion reactiva del negocio:** Se realiza mediante la publicacion y escucha asincrona de eventos en memoria a traves de `DomainEventBus` (`:core:common:event`).

---

## 3. Diseno del Bus de Eventos (`DomainEventBus`)

### 3.1 Contrato del Evento (`DomainEvent`)

Todo evento que circule por el bus debe implementar la interfaz marcadora `DomainEvent`:

```kotlin
package com.tuxlogic.shiftiq.mobile.core.common.event

interface DomainEvent {
    val occurredOn: Long
        get() = System.currentTimeMillis()
}
```

Cada evento representa un hecho inmutable ya acontecido en el dominio.

### 3.2 Contrato del Bus (`DomainEventBus`)

La abstraccion del bus ofrece publicacion suspendible y suscripcion tipada reactiva basada en Kotlin `Flow`:

```kotlin
package com.tuxlogic.shiftiq.mobile.core.common.event

import kotlinx.coroutines.flow.Flow

interface DomainEventBus {
    suspend fun emit(event: DomainEvent)
    fun <T : DomainEvent> subscribe(eventType: Class<T>): Flow<T>
}

inline fun <reified T : DomainEvent> DomainEventBus.subscribe(): Flow<T> =
    subscribe(T::class.java)
```

### 3.3 Implementacion Concurrente (`DomainEventBusImpl`)

```kotlin
package com.tuxlogic.shiftiq.mobile.core.common.event

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DomainEventBusImpl @Inject constructor() : DomainEventBus {

    private val _events = MutableSharedFlow<DomainEvent>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override suspend fun emit(event: DomainEvent) {
        _events.emit(event)
    }

    override fun <T : DomainEvent> subscribe(eventType: Class<T>): Flow<T> {
        return _events.filterIsInstance(eventType)
    }
}
```

### 3.4 Decisiones de Ingenieria del Bus

- **`replay = 0` (Flujo caliente estricto):** Un nuevo suscriptor no recibe eventos acontecidos en el pasado. Esto previene que una pantalla o caso de uso nuevo reaccione ante un cambio de sede o sesion que ya fue procesado antes de su creacion.
- **`extraBufferCapacity = 64`:** Otorga amortiguacion para ráfagas concurrentes de eventos emitidas por subprocesos o DataStore sin suspender inmediatamente las corrutinas emisoras.
- **`onBufferOverflow = BufferOverflow.DROP_OLDEST`:** Garantiza tolerancia a fallos. Si un suscriptor lento o bloqueado retiene el procesamiento, el bus descarta los eventos mas antiguos sin bloquear a los emisores ni provocar fugas de memoria o deadlocks.
- **Inyeccion Singleton:** Configurado en Hilt mediante `:core:common:di:EventModule` asegurando una unica instancia compartida por toda la aplicacion.

---

## 4. Catalogo de Eventos de Dominio Locales

Los eventos nativos implementados residen en `core/common/event/Events.kt`:

### 4.1 `ActiveBranchChangedEvent`
```kotlin
data class ActiveBranchChangedEvent(
    val branchId: String
) : DomainEvent
```
- **Origen:** Emitido de forma automatica por `SessionDataStoreImpl.setActiveBranchId(branchId)` cuando el usuario conmuta de sede activa.
- **Significado de negocio:** El contexto operativo de la aplicacion cambio de ubicacion fisica.
- **Efecto en los modulos:** Invalida catalogos de repuestos, listas de ordenes de trabajo, citas pendientes y telemetria asociada a la sede previa, forzando la recarga para `branchId`.

### 4.2 `SessionExpiredEvent`
```kotlin
object SessionExpiredEvent : DomainEvent
```
- **Origen:** Emitido cuando un access token y su refresh token fallan en el refresco automatico (`HTTP 401` no recuperable) o cuando la sesion es invalidada desde el DataStore.
- **Significado de negocio:** La identidad del usuario ya no es valida para interactuar con la plataforma.
- **Efecto en los modulos:** Limpia caches en memoria, detiene observadores reactivos y notifica a la capa de navegacion para forzar la transicion hacia `auth/login`.

### 4.3 `UserLoggedOutEvent`
```kotlin
object UserLoggedOutEvent : DomainEvent
```
- **Origen:** Emitido de forma explicita durante el cierre voluntario de sesion en `LogoutUseCase` o `SessionDataStoreImpl.clearSession()`.
- **Significado de negocio:** El usuario decidio terminar su actividad en el dispositivo.
- **Efecto en los modulos:** Descarta todo dato sensible del usuario en memoria, resetea borradores de formularios y cancela corrutinas activas de segundo plano.

---

## 5. Matriz de Bounded Contexts y Roles de Eventos

| Bounded Context | Rol Arquitectonico | Eventos Producidos | Eventos Consumidos | Justificacion y Comportamiento |
| :--- | :--- | :--- | :--- | :--- |
| `iam` | Productor Principal | `UserLoggedOutEvent`, `SessionExpiredEvent` | — | Autoridad de autenticacion y ciclo de vida de tokens. Notifica a toda la app cuando la sesion finaliza. |
| `core` | Productor Principal | `ActiveBranchChangedEvent` | `SessionExpiredEvent`, `UserLoggedOutEvent` | Gestiona sedes y talleres. Notifica a los demas cuando el usuario selecciona una sede distinta. Al expirar sesion, limpia cache de organizaciones. |
| `iot` | Productor Secundario | Telemetria en tiempo real, alertas DTC | `ActiveBranchChangedEvent`, `UserLoggedOutEvent` | Produce alertas OBD-II locales; consume cambio de sede para filtrar vehiculos vinculables y desconecta sockets de telemetria al cerrar sesion. |
| `operations` | Consumidor Reactivo | — | `ActiveBranchChangedEvent`, `SessionExpiredEvent`, `UserLoggedOutEvent` | Invalida y recarga ordenes de trabajo y tareas activas para la nueva sede. Limpia borradores al cerrar sesion. |
| `fleet` | Consumidor Reactivo | — | `ActiveBranchChangedEvent`, `SessionExpiredEvent`, `UserLoggedOutEvent` | Invalida y recarga citas tecnicas y personal asignado a la nueva sede. Limpia estado al expirar sesion. |
| `inventory` | Consumidor Reactivo | — | `ActiveBranchChangedEvent`, `UserLoggedOutEvent` | Invalida catalogo de productos y lotes FIFO en memoria; recarga stock disponible en la nueva sede. |
| `billing` | Consumidor Reactivo | — | `ActiveBranchChangedEvent`, `SessionExpiredEvent`, `UserLoggedOutEvent` | Invalida cotizaciones y comprobantes en curso; cancela flujos de pago o checkout en progreso ante cambio de sede o cierre de sesion. |
| `analytics` | Consumidor Reactivo | — | `ActiveBranchChangedEvent`, `UserLoggedOutEvent` | Invalida graficos y snapshots de metricas financieras; solicita al backend las metricas de la nueva sede activa. |
| `shared` | Contexto Excluido | — | — | Subdominio generico sin retencion de estado. Operaciones puramente transaccionales de validacion DNI/RUC y subida a Cloudinary independientes de sede o usuario. |

---

## 6. Arquitectura Concentrica Clean DDD en Cuatro Capas

Cada uno de los 9 bounded contexts de la aplicacion movil sigue rigurosamente la estructura estandarizada de cuatro capas concéntricas con inversion de dependencias:

```
[presentation] (UI Compose, ViewModels StateFlow)
      │
      ▼
[application]  (Casos de Uso / Interactores orquestadores)
      │
      ▼
   [domain]    (Entidades, Value Objects, Reglas, Interfaces de Repositorio)
      ▲
      │
[infrastructure] (Retrofit API, DTOs, Mappers, Repositorios Impl, Modulos Hilt)
```

### 6.1 Detalle de Responsabilidades por Capa

#### 1. `domain/` (Nucleo Puro)
- **Subpaquetes:** `domain/model/` y `domain/repository/`.
- **Regla estricta:** Kotlin puro. Cero importaciones de Android (`android.*`), Retrofit, OkHttp, Room, Compose o DataStore.
- **Contenido:** Entidades inmutables, Value Objects que encapsulan invariantes y contratos de repositorio puros.

#### 2. `application/` (Orquestacion)
- **Subpaquetes:** `application/usecase/`.
- **Regla estricta:** Kotlin puro con Coroutines. Modela operaciones de negocio concretas con interfaz ejecutable `operator fun invoke(...)`.
- **Contenido:** Casos de uso independientes que coordinan repositorios y validaciones sin conocer la procedencia de los datos.

#### 3. `infrastructure/` (Adaptadores Tecnicos)
- **Subpaquetes:** `infrastructure/remote/api/`, `infrastructure/remote/dto/`, `infrastructure/mapper/`, `infrastructure/repository/`, `infrastructure/di/`.
- **Regla estricta:** Implementa los contratos definidos por `domain/repository/`.
- **Contenido:** Clientes HTTP Retrofit, DTOs con anotaciones Gson, mappers bidireccionales de aislamiento y modulos `@Module` `@InstallIn(SingletonComponent::class)` de Hilt.

#### 4. `presentation/` (Interfaz de Usuario)
- **Subpaquetes:** Paquetes por pantalla (ej. `presentation/login/`, `presentation/workshops/`).
- **Regla estricta:** Unidireccionalidad de datos (UDF).
- **Contenido:** ViewModels inyectados con `@HiltViewModel` que exponen `StateFlow<UiState>` y funciones composable declarativas en Jetpack Compose con `@Preview` interactivo.

---

## 7. Shared Kernel vs. Subdominio Generico

Es esencial distinguir la diferencia arquitectonica entre los modulos del Shared Kernel y el feature de Subdominio Generico:

### 7.1 Shared Kernel (`:core:model` y `:core:common`)
- **Tipo:** Biblioteca transversal de utilidades e identidades minimas universales.
- **Naturaleza:** No posee capas de infraestructura propia ni consume servicios de red.
- **`:core:model`:** Almacena el Lenguaje Ubicuo basico compartido: `UserId`, `BranchId`, `WorkshopId`, `Role`, `Money`.
- **`:core:common`:** Almacena primitivas tecnicas transversales: `AppResult<T>`, `AppError`, `DispatcherProvider` y `DomainEventBus`.

### 7.2 Subdominio Generico (`:feature:shared`)
- **Tipo:** Bounded Context funcional completo con sus 4 capas concéntricas (`domain`, `application`, `infrastructure`, `presentation`).
- **Naturaleza:** Modela capacidades auxiliares requeridas por el negocio pero que no constituyen la ventaja competitiva esencial del taller:
  1. Validacion de identidad oficial ante registros estatales (RENIEC para DNI de 8 digitos, SUNAT para RUC de 11 digitos).
  2. Subida y optimizacion de archivos multimedia e imagenes a la CDN de Cloudinary (`/media/upload`).
  3. Ping y monitoreo de salud del sistema (`/health`).

---

## 8. Guia Practica para Desarrolladores

### 8.1 Publicar un Evento desde un Caso de Uso o DataStore

```kotlin
class SelectBranchUseCase @Inject constructor(
    private val sessionDataStore: SessionDataStore,
    private val eventBus: DomainEventBus
) {
    suspend operator fun invoke(branchId: String): AppResult<Unit> {
        sessionDataStore.setActiveBranchId(branchId)
        eventBus.emit(ActiveBranchChangedEvent(branchId))
        return AppResult.Success(Unit)
    }
}
```

### 8.2 Suscribirse a un Evento en un ViewModel

```kotlin
@HiltViewModel
class InventoryCatalogViewModel @Inject constructor(
    private val eventBus: DomainEventBus,
    private val getProductsUseCase: GetProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<InventoryUiState>(InventoryUiState.Loading)
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        observeDomainEvents()
        loadProducts()
    }

    private fun observeDomainEvents() {
        eventBus.subscribe<ActiveBranchChangedEvent>()
            .onEach { event ->
                // Recarga el inventario especifico de la nueva sede activa
                loadProductsForBranch(event.branchId)
            }
            .launchIn(viewModelScope)

        eventBus.subscribe<SessionExpiredEvent>()
            .onEach {
                // Limpia los datos de la memoria local
                _uiState.value = InventoryUiState.Empty
            }
            .launchIn(viewModelScope)
    }

    private fun loadProducts() {
        // Logica de carga normal
    }
}
```

### 8.3 Guia de Pruebas Unitarias para Eventos con Coroutines

Dado que el bus opera con `replay = 0`, cualquier suscriptor de prueba debe ser lanzado y suspendido en la recoleccion **antes** de que el emisor invoque `emit(...)`.

```kotlin
@Test
fun `multiple subscribers all receive emitted event`() = runTest {
    val bus = DomainEventBusImpl()
    val event = ActiveBranchChangedEvent("branch-999")

    val received1 = mutableListOf<ActiveBranchChangedEvent>()
    val received2 = mutableListOf<ActiveBranchChangedEvent>()

    // UnconfinedTestDispatcher ejecuta la corrutina inmediatamente hasta el punto de suspension
    val job1 = launch(UnconfinedTestDispatcher(testScheduler)) {
        bus.subscribe<ActiveBranchChangedEvent>().collect { received1.add(it) }
    }
    val job2 = launch(UnconfinedTestDispatcher(testScheduler)) {
        bus.subscribe<ActiveBranchChangedEvent>().collect { received2.add(it) }
    }

    bus.emit(event)

    assertEquals(listOf(event), received1)
    assertEquals(listOf(event), received2)

    job1.cancel()
    job2.cancel()
}
```
