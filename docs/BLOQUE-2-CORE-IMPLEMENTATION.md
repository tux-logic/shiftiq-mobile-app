# ShiftIQ Mobile — Bloque 2: Core (Talleres y Sedes)

Documento técnico de implementación del **Bloque 2 (Core)** correspondiente al hito **H2 ("Configuro mi taller")**, desarrollado sobre la rama `feature/core` bajo la metodología GitFlow, Clean Architecture y DDD.

---

## 1. Resumen del Bloque y Objetivos

El **Bloque 2: Core** gestiona la estructura organizacional de la plataforma:
- **Perfil del Dueño (`OwnerProfile`)**: Identidad de negocio asociada a la cuenta del usuario (`DNI`, nombres, teléfono).
- **Talleres (`Workshop`)**: Entidad raíz del taller mecánico con razón social, nombre comercial, RUC (11 dígitos) y configuración de kilometraje para mantenimientos preventivos.
- **Sedes (`Branch`)**: Sucursales físicas del taller con código de sede, nombre, dirección física y teléfono de contacto. Permite alternar y fijar la **Sede Activa (`activeBranchId`)** en `SessionDataStore`.

---

## 2. Endpoints de Render Consumidos y Validados

Todos los endpoints han sido verificados contra el backend en la nube (**`https://shiftiq-platform.onrender.com`**):

| Método | Endpoint | Request Body | Response Body | Descripción |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/owners` | `CreateOwnerRequestDto` (`userId`, `firstName`, `lastName`, `documentType`, `documentNumber`, `phone`) | `OwnerResourceDto` | Da de alta el perfil de dueño y le asigna el rol `OWNER`. |
| `GET` | `/api/v1/owners?userId=` | N/A | `OwnerResourceDto` | Consulta el perfil de dueño por `userId`. |
| `GET` | `/api/v1/workshops?ownerId=` | N/A | `List<WorkshopResourceDto>` | Lista los talleres registrados por el dueño. |
| `POST` | `/api/v1/workshops` | `CreateWorkshopRequestDto` (`ownerId`, `businessName`, `brandName`, `taxId`, `mileageIntervalConfig`) | `WorkshopResourceDto` | Registra un nuevo taller con RUC y marca. |
| `GET` | `/api/v1/workshops/{id}` | N/A | `WorkshopResourceDto` | Obtiene el detalle de un taller específico. |
| `GET` | `/api/v1/branches?workshopId=` | N/A | `List<BranchResourceDto>` | Lista las sedes físicas de un taller. |
| `POST` | `/api/v1/branches` | `CreateBranchRequestDto` (`workshopId`, `code`, `name`, `address`, `phone`) | `BranchResourceDto` | Registra una nueva sede asociada al taller. |
| `GET` | `/api/v1/branches/{id}` | N/A | `BranchResourceDto` | Obtiene el detalle de una sede específica. |

---

## 3. Arquitectura del Feature (`feature/core`)

Implementado como una rebanada vertical dentro de `app/src/main/java/com/tuxlogic/shiftiq/mobile/feature/core/`:

```
app/src/main/java/com/tuxlogic/shiftiq/mobile/feature/core/
├── domain/
│   ├── model/
│   │   └── CoreModels.kt           # Workshop, Branch, WorkshopSpecialty, OwnerProfile
│   ├── repository/
│   │   └── CoreRepository.kt       # Contrato de repositorio
│   └── usecase/
│       ├── WorkshopUseCases.kt     # GetWorkshopsUseCase, CreateWorkshopUseCase
│       ├── BranchUseCases.kt       # GetBranchesUseCase, CreateBranchUseCase, SetActiveBranchUseCase
│       └── OwnerProfileUseCases.kt # GetOwnerProfileUseCase, CreateOwnerProfileUseCase
├── data/
│   ├── remote/
│   │   ├── dto/
│   │   │   └── CoreDtos.kt         # DTOs y mappers a dominio
│   │   └── api/
│   │       └── CoreApiService.kt   # Interfaz Retrofit
│   ├── repository/
│   │   └── CoreRepositoryImpl.kt   # Implementación con safeApiCall y corrutinas
│   └── di/
│       └── CoreDataModule.kt       # Módulo Hilt (CoreApiService, CoreRepository)
└── presentation/
    ├── workshops/
    │   ├── WorkshopListScreen.kt   # Lista de talleres con @Preview
    │   ├── WorkshopListViewModel.kt
    │   ├── CreateWorkshopScreen.kt # Formulario de taller con @Preview
    │   └── CreateWorkshopViewModel.kt
    ├── branches/
    │   ├── BranchManagementScreen.kt # Gestión de sedes y sede activa con @Preview
    │   ├── BranchManagementViewModel.kt
    │   ├── CreateBranchScreen.kt   # Alta de sede física con @Preview
    │   └── CreateBranchViewModel.kt
    └── owner/
        ├── OwnerProfileScreen.kt   # Visualización y registro de dueño con @Preview
        └── OwnerProfileViewModel.kt
```

---

## 4. Pantallas Desarrolladas y Soporte `@Preview`

Cada pantalla cuenta con previsualizaciones independientes en Jetpack Compose que funcionan sin requerir un backend activo ni conexión de red:

### 1. `WorkshopListScreen`
- **Ruta:** `core/workshops`
- **Contenido:** Tarjetas de talleres con Razón Social, Nombre Comercial, Badge con RUC y resumen de kilometraje.
- **Acciones:**
  - Si no hay talleres: Estado vacío con botón *"Crear Mi Primer Taller"*.
  - Si falta perfil de dueño: Banner inteligente *"Completa tu Perfil de Dueño"* que redirige al formulario.
  - Floating Action Button (`+`) para agregar un nuevo taller.
  - Al presionar una tarjeta: Navega a la gestión de sedes de ese taller.
- **Previews:** `WorkshopListScreenPreview()` (con datos simulados) y `WorkshopListEmptyPreview()` (estado vacío).

### 2. `CreateWorkshopScreen`
- **Ruta:** `core/workshops/create/{ownerId}`
- **Contenido:** Formulario estructurado para Razón Social, Nombre Comercial, RUC (validación estricta de 11 dígitos numéricos) e intervalo de mantenimiento (km).
- **Acciones:** Envía `POST /api/v1/workshops` y redirige a la lista al completar.
- **Preview:** `CreateWorkshopScreenPreview()`.

### 3. `BranchManagementScreen`
- **Ruta:** `core/workshops/{workshopId}/branches`
- **Contenido:** Tarjetas de sedes mostrando Código, Nombre, Dirección y Teléfono.
- **Acciones:**
  - Badge verde *"Activa"* para la sede seleccionada en la sesión.
  - Botón *"Seleccionar Sede"* para activar cualquier otra sede persistiendo en `SessionDataStore`.
  - Floating Action Button (`+`) para agregar una nueva sede física.
- **Preview:** `BranchManagementScreenPreview()`.

### 4. `CreateBranchScreen`
- **Ruta:** `core/workshops/{workshopId}/branches/create`
- **Contenido:** Formulario para Código de Sede (ej. `SEDE-SUR-01`), Nombre de Sede, Dirección física y Teléfono.
- **Acciones:** Envía `POST /api/v1/branches` y vuelve a la lista de sedes.
- **Preview:** `CreateBranchScreenPreview()`.

### 5. `OwnerProfileScreen`
- **Ruta:** `core/profile/owner`
- **Contenido:**
  - Si el perfil ya existe: Muestra tarjeta de identidad con DNI, Nombres y Teléfono de contacto.
  - Si el perfil no existe: Formulario completo para registrar los datos del dueño en `POST /api/v1/owners`.
- **Previews:** `OwnerProfileViewPreview()` (tarjeta de perfil) y `OwnerProfileFormPreview()` (formulario).

---

## 5. Guía de Verificación en Android Studio

### A. Previsualización visual en Android Studio (Design / Split Mode)
1. Abre cualquiera de los archivos `.kt` en `feature/core/presentation/`.
2. En la esquina superior derecha del editor, cambia a **"Split"** o **"Design"**.
3. Android Studio renderizará inmediatamente la vista interactiva con el tema Material 3 y tipografía Inter, sin necesidad de ejecutar el emulador.

### B. Prueba interactiva en el Emulador (Pixel 10a / Device Manager)
1. Inicia sesión con la cuenta de prueba (`test_agent_user@shiftiq.com` / `Password123!`).
2. En la pantalla del Dashboard, verás dos botones nuevos:
   - **"Gestionar Talleres y Sedes"**: Entra directamente a la lista de talleres.
   - **"Ver / Editar Mi Perfil de Dueño"**: Muestra el perfil registrado en Render.
3. Desde la lista de talleres, pulsa el botón **`+`** para registrar un taller, o selecciona un taller existente para ver sus sedes y marcar la sede activa del taller.

---

## 6. Historial de Commits en `feature/core`

1. `8193fb0`: `feat(core-domain-data): implement workshop, branch, and owner entities, repository and api service`
2. `cb69fa1`: `feat(core-ui): implement workshop list, create workshop, branch management and owner profile screens with previews`
3. `fb151e4`: `test(core): add unit tests for workshop and branch use cases`
4. `[actual]`: `docs(core): add block 2 core implementation report and verification guide`
