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

## 2. Los 9 bloques (construcción)

| # | Bloque | Contexto backend | Módulo(s) | Depende de |
| :---: | :--- | :--- | :--- | :--- |
| 0 | Fundación | `shared` | `:core:*`, `:app`, `build-logic` | — |
| 1 | IAM | `iam` | `:feature:iam` | 0 |
| 2 | Core | `core` | `:feature:core` | 1 |
| 3 | Fleet | `fleet` | `:feature:fleet` | 2 |
| 4 | Operations | `operations` | `:feature:operations` | 3 |
| 5 | Inventory | `inventory` | `:feature:inventory` | 4 |
| 6 | Billing | `billing` | `:feature:billing` | 4 |
| 7 | IoT | `iot` | `:feature:iot` | 2 |
| 8 | Analytics | `analytics` | `:feature:analytics` | 2–7 |

---

## 3. Los 6 hitos (entrega)

| Hito | Bloques | Objetivo | Depende de | Demostración |
| :--- | :---: | :--- | :--- | :--- |
| **H1 — "Existe y autentica"** | 0 + 1 | Fundación + login/sesión/navegación por rol | — | Login real, refresh transparente, logout |
| **H2 — "Configuro mi taller"** | 2 + 3 | Core + Fleet | H1 | Talleres, sedes, personal y citas |
| **H3 — "Atiendo vehículos"** | 4 + 5 | Operations + Inventory | H2 | Orden de trabajo con tareas y repuestos |
| **H4 — "Cobro"** | 6 | Billing | H3 | Cotización → comprobante → pago |
| **H5 — "Conecto IoT"** | 7 | IoT | H2 | Vehículo, telemetría y alertas DTC |
| **H6 — "Mido y libero"** | 8 | Analytics + polish/release | H2–H5 | Dashboards, offline, E2E, firma/Play |

---

## 4. Matriz de trazabilidad (bloque ⇄ hito ⇄ estado)

Sirve para marcar el avance durante el desarrollo.

| # | Bloque | Hito | Estado |
| :---: | :--- | :--- | :--- |
| 0 | Fundación | H1 | ✅ Completado |
| 1 | IAM | H1 | ✅ Completado |
| 2 | Core | H2 | ✅ Completado |
| 3 | Fleet | H2 | ✅ Completado |
| 4 | Operations | H3 | ⬜ Pendiente |
| 5 | Inventory | H3 | ⬜ Pendiente |
| 6 | Billing | H4 | ⬜ Pendiente |
| 7 | IoT | H5 | ⬜ Pendiente |
| 8 | Analytics | H6 | ⬜ Pendiente |

Leyenda: ⬜ Pendiente · 🟡 En curso · ✅ Hecho.

---

## 5. Patrón interno de cada bloque de contexto (1–8)

Cada bloque se construye como **rebanada vertical Clean Architecture** y esto define sus sub-partes,
en este orden:

1. **`domain/`** — modelos, VOs, interfaces de repositorio y casos de uso (Kotlin puro, sin Android).
2. **`data/`** — Api Retrofit + DTOs + mappers + `RepositoryImpl`.
3. **`presentation/`** — Compose + ViewModel + `UiState` + rutas del feature.
4. **Pruebas** — unit de casos de uso/ViewModel (y MockWebServer donde aplique).

Se respeta el orden **domain → data → presentation** para fijar el contrato antes de acoplar la UI;
el bloque se cierra con sus tests y su integración en `:app`.

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
