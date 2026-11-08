# ShiftIQ Mobile — Plan de Desarrollo (por Hitos y Bloques)

Plan de ejecución completo del desarrollo de la app Android (`com.tuxlogic.shiftiq.mobile`).
Complementa a `docs/MOBILE-ARCHITECTURE.md` (arquitectura de módulos y contrato con el backend).

---

## 1. Propósito

El desarrollo se organiza con **dos lentes que se cruzan**:

- **Hito** = *entrega demostrable*. Agrupa uno o más bloques y sirve para mostrar avance y validar
  con stakeholders. Se define por el **valor de negocio** que habilita.
- **Bloque** = *unidad de construcción*. Es **1 bounded context del backend = 1 conjunto de módulos
  Gradle**, o la fundación transversal. Se define por las **dependencias del dominio**.

Un hito no es más que "los bloques que ya puedes mostrar juntos"; por eso casi siempre
**1 bloque = media entrega o una entrega**.

---

## 2. Los bloques de construcción (Bounded Contexts)

| # | Bloque | Contexto backend | Módulo(s) | Tipo DDD | Depende de |
| :---: | :--- | :--- | :--- | :--- | :--- |
| 0 | Fundación | `shared` | `:core:*`, `:app`, `build-logic` | Shared Kernel | — |
| 1 | IAM | `iam` | `:feature:iam` | Soporte | 0 |
| 2 | Core | `core` | `:feature:core` | Core Domain | 1 |
| 3 | Fleet | `fleet` | `:feature:fleet` | Core Domain | 2 |
| 4 | Operations | `operations` | `:feature:operations` | Core Domain | 3 |
| 5 | Inventory | `inventory` | `:feature:inventory` | Core Domain | 4 |
| 6 | Billing | `billing` | `:feature:billing` | Soporte | 4 |
| 7 | IoT | `iot` | `:feature:iot` | Soporte | 2 |
| 8 | Analytics | `analytics` | `:feature:analytics` | Soporte | 2–7 |
| 9 | Shared Services | `shared` | `:feature:shared` | Subdominio Genérico | 0 |

---

## 3. Los 6 hitos (entrega)

| Hito | Bloques | Objetivo | Depende de | Demostración |
| :--- | :---: | :--- | :--- | :--- |
| **H1 — "Existe y autentica"** | 0 + 1 | Fundación + login/sesión/navegación por rol | — | Login real, refresh transparente, logout |
| **H2 — "Configuro mi taller"** | 2 + 3 | Core + Fleet | H1 | Talleres, sedes, personal y citas |
| **H3 — "Atiendo vehículos"** | 4 + 5 | Operations + Inventory | H2 | Orden de trabajo con tareas y repuestos |
| **H4 — "Cobro"** | 6 | Billing | H3 | Cotización → comprobante → pago |
| **H5 — "Conecto IoT"** | 7 | IoT | H2 | Vehículo, telemetría y alertas DTC |
| **H6 — "Mido y libero"** | 8 + 9 | Analytics + Shared + polish/release | H2–H5 | Dashboards, offline, validaciones RENIEC/SUNAT, firma/Play |

---

## 4. Matriz de trazabilidad (bloque / hito / estado)

Sirve para registrar el avance de implementación técnica de la aplicación.

| # | Bloque | Hito | Estado | Arquitectura |
| :---: | :--- | :--- | :--- | :--- |
| 0 | Fundación | H1 | [Completado] | Shared Kernel, SessionDataStore, DomainEventBus en :core:common |
| 1 | IAM | H1 | [Completado] | Clean DDD 4 capas (domain, application, infrastructure, presentation) |
| 2 | Core | H2 | [Completado] | Clean DDD 4 capas (domain, application, infrastructure, presentation) |
| 3 | Fleet | H2 | [Completado] | Clean DDD 4 capas (domain, application, infrastructure, presentation) |
| 4 | Operations | H3 | [Estructurado] | Clean DDD 4 capas base y tests de scaffolding completados |
| 5 | Inventory | H3 | [Estructurado] | Clean DDD 4 capas base y tests de scaffolding completados |
| 6 | Billing | H4 | [Estructurado] | Clean DDD 4 capas base y tests de scaffolding completados |
| 7 | IoT | H5 | [Estructurado] | Clean DDD 4 capas base y tests de scaffolding completados |
| 8 | Analytics | H6 | [Estructurado] | Clean DDD 4 capas base y tests de scaffolding completados |
| 9 | Shared | H6 | [Estructurado] | Subdominio genérico con 4 capas (RENIEC/SUNAT, Cloudinary, Health) |

Leyenda de estado: [Pendiente] · [En curso] · [Estructurado] · [Completado].

---

## 5. Patrón interno de cada bounded context (Clean Architecture + DDD)

Cada bounded context se organiza en cuatro capas concéntricas con estricta inversión de dependencias:

1. **`domain/`** — Entidades de negocio, Value Objects inmutables e interfaces de repositorio (`repository/`). Kotlin puro sin dependencias de Android, Room o Retrofit.
2. **`application/`** — Casos de uso e interactores orquestadores (`usecase/`) que implementan las reglas de interacción del usuario.
3. **`infrastructure/`** — Adaptadores técnicos concretos: API Retrofit (`remote/api/`), DTOs (`remote/dto/`), mappers bidireccionales (`mapper/`), implementaciones de repositorio (`repository/`) y módulos Hilt (`di/`).
4. **`presentation/`** — Vistas declarativas en Jetpack Compose, ViewModels (`StateFlow<UiState>`) y destinos de navegación.
5. **Pruebas unitarias** — Tests JVM puros para `domain/`, `application/` e `infrastructure/mapper`, y MockWebServer para endpoints.

Se respeta la regla de dependencias:
`presentation -> application -> domain <- infrastructure`

Ningún feature depende de otro feature hermano (principio de aislamiento estricto). La comunicación cross-context se efectúa mediante el `DomainEventBus` en memoria (`:core:common:event`) o mediante el Shared Kernel (`:core:model`).

---

## 6. Reglas de orden y dependencias

- **Dependencia estricta:** H1 → H2 → H3 → H4.
- **Paralelizable:** H5 (IoT) puede ejecutarse en paralelo con H3/H4.
- **Al final:** H6 (Analytics) depende de datos de todos los contextos.
- **Transversal y continuo** (no es un hito aparte): testing, revisión del contrato de endpoints y
  ajustes de build corren dentro de cada bloque.
- **Enforcement por Gradle:** `:core:*` nunca depende de `:feature:*`; un `:feature:*` nunca depende
  de otro `:feature:*` (ver `MOBILE-ARCHITECTURE.md`, sección 4).

---

## 7. Siguiente paso

Empezar por el **Hito 1 (Bloques 0 y 1)**, desglosado en 3 partes:

1. **Esqueleto de build** — `build-logic`, `settings.gradle` con módulos, `:app @HiltAndroidApp`,
   tema en `:core:designsystem`, `buildConfig`/`BASE_URL`.
2. **Infraestructura transversal** — `:core:common`, `:core:model`, `:core:network`
   (AuthInterceptor + TokenAuthenticator con refresh single-flight), `:core:datastore`
   (`SessionDataStore` con token, refresh, userId, role y `activeBranchId`).
3. **IAM + navegación por rol** — `:feature:iam` (domain → data → presentation), login/Google/refresh/
   logout, *session gate*, selección y persistencia de sede activa (`activeBranchId`), y `NavHost` según `role`.

Ver `docs/MOBILE-ARCHITECTURE.md` para el detalle de módulos, endpoints, convenciones y gotchas.
