# ShiftIQ Mobile — Bloque 3: Fleet (Flota, Citas y Personal)

Documento técnico de implementación del **Bloque 3 (Fleet)** correspondiente al hito **H2 ("Configuro mi taller")**, desarrollado sobre la rama `feature/fleet` bajo la metodología GitFlow, Clean Architecture y DDD.

---

## 1. Resumen del Bloque y Objetivos

El **Bloque 3: Fleet** gestiona la flota operativa, la agenda de citas y el equipo técnico de las sedes:
- **Citas de Taller (`Appointment`)**: Programación de revisiones y mantenimientos preventivos vinculando cliente, vehículo y sede. Permite filtrado en tiempo real por estado (`PENDING`, `COMPLETED`, `CANCELED`), actualización de ciclo de vida y eliminación.
- **Personal Técnico (`EmployeeRegistration`)**: Gestión del personal asignado a una sede con especialidad técnica, salario mensual y rol. Soporta el flujo de postulaciones autónomas de mecánicos (`PENDING_APPROVAL`) con acciones de **Aprobación** (que sincroniza e integra la sede en IAM) o **Rechazo**.
- **Vinculaciones de Clientes (`CustomerRegistration`)**: Asociación formal de clientes a sedes del taller.

---

## 2. Endpoints de Render Consumidos y Validados

Todos los endpoints han sido validados contra el backend en la nube (**`https://shiftiq-platform.onrender.com`**):

| Método | Endpoint | Request Body | Response Body | Descripción |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/appointments` | Query: `branchId`, `status`, `customerId`, `vehicleId` | `List<AppointmentDto>` | Lista citas operativas de la sede activa con filtros opcionales. |
| `POST` | `/api/v1/appointments` | `CreateAppointmentRequestDto` (`branchId`, `customerId`, `vehicleId`, `scheduledStart`, `notes`) | `AppointmentDto` | Agenda una nueva cita técnica con fecha/hora ISO 8601. |
| `GET` | `/api/v1/appointments/{id}` | N/A | `AppointmentDto` | Obtiene el detalle de una cita por su UUID. |
| `PUT` | `/api/v1/appointments/{id}` | `UpdateAppointmentRequestDto` (`branchId`, `customerId`, `vehicleId`, `scheduledStart`, `status`, `notes`) | `AppointmentDto` | Actualiza estado (ej. `COMPLETED`, `CANCELED`) o notas de la cita. |
| `DELETE` | `/api/v1/appointments/{id}` | N/A | `Unit` (200 OK) | Soft-delete / cancelación de la cita programada. |
| `GET` | `/api/v1/employee-registrations` | Query: `branchId`, `status`, `employeeId` | `List<EmployeeRegistrationDto>` | Lista miembros del equipo técnico y postulaciones por sede. |
| `POST` | `/api/v1/employee-registrations` | `CreateEmployeeRegistrationRequestDto` (`employeeId`, `branchId`, `speciality`, `specialityName`, `salary`, `role`) | `EmployeeRegistrationDto` | Vincula directamente un miembro al equipo de la sede. |
| `POST` | `/api/v1/employee-registrations/request-join` | `RequestEmployeeJoinRequestDto` (`employeeId`, `branchId`, `speciality`, `salary`) | `EmployeeRegistrationDto` | Postulación autónoma de mecánico en estado `PENDING_APPROVAL`. |
| `POST` | `/api/v1/employee-registrations/{id}/approve` | N/A | `EmployeeRegistrationDto` | Aprueba la postulación del técnico y le asigna la sede en IAM. |
| `POST` | `/api/v1/employee-registrations/{id}/reject` | `RejectEmployeeRegistrationRequestDto` (`reason`) | `EmployeeRegistrationDto` | Rechaza la postulación técnica con motivo opcional. |
| `GET` | `/api/v1/customer-registrations` | Query: `branchId`, `customerId`, `status` | `List<CustomerRegistrationDto>` | Consulta clientes registrados en la sede activa. |
| `POST` | `/api/v1/customer-registrations` | `CreateCustomerRegistrationRequestDto` (`customerId`, `branchId`) | `CustomerRegistrationDto` | Vincula un cliente a la sede del taller. |

---

## 3. Arquitectura del Feature (`feature/fleet`)

Construido como rebanada vertical Clean Architecture en `app/src/main/java/com/tuxlogic/shiftiq/mobile/feature/fleet/`:

```
app/src/main/java/com/tuxlogic/shiftiq/mobile/feature/fleet/
├── domain/
│   ├── model/
│   │   └── FleetModels.kt              # Appointment, AppointmentStatus, EmployeeRegistration, CustomerRegistration
│   ├── repository/
│   │   └── FleetRepository.kt          # Contrato de repositorio de dominio
│   └── usecase/
│       ├── AppointmentUseCases.kt      # GetAppointments, CreateAppointment, UpdateAppointment, DeleteAppointment
│       ├── StaffUseCases.kt            # GetEmployeeRegistrations, CreateEmployeeRegistration, Approve/Reject/RequestJoin
│       └── CustomerRegistrationUseCases.kt # GetCustomerRegistrations, CreateCustomerRegistration
├── data/
│   ├── remote/
│   │   ├── dto/
│   │   │   └── FleetDtos.kt            # DTOs Retrofit con Gson y mappers .toDomain()
│   │   └── api/
│   │       └── FleetApiService.kt      # Contratos Retrofit con Bearer JWT automático
│   ├── repository/
│   │   └── FleetRepositoryImpl.kt      # Implementación con safeApiCall y DispatcherProvider
│   └── di/
│       └── FleetDataModule.kt          # Inyección de dependencias Hilt (@Singleton)
└── presentation/
    ├── appointments/
    │   ├── AppointmentListScreen.kt    # Lista de citas, filtros, badges y FAB con @Preview
    │   ├── AppointmentListViewModel.kt # StateFlow, filtros en memoria y mutaciones de estado
    │   ├── CreateAppointmentScreen.kt  # Formulario validado con @Preview
    │   └── CreateAppointmentViewModel.kt # Validación de UUIDs y formato ISO 8601
    └── staff/
        ├── StaffManagementScreen.kt    # Pestañas Activos/Solicitudes, botones Aprobar/Rechazar y @Preview
        └── StaffManagementViewModel.kt # Carga por sede activa y gestión de aprobaciones
```

---

## 4. Pantallas Implementadas y Cómo Probarlas

### 1. `AppointmentListScreen` (Agenda de Citas)
- **Ruta**: `fleet/appointments` (accesible desde el botón *"Agenda y Citas (Fleet)"* en el Dashboard).
- **Características**:
  - Filtros superiores por chips: *Todas*, *Pendientes*, *Completadas*, *Canceladas* con contador en tiempo real.
  - Tarjetas de citas con fecha/hora formateada, badges de estado con colores temáticos (Naranja/Verde/Rojo).
  - Acciones rápidas: botones para marcar como **Completar**, **Cancelar** o **Eliminar**.
  - Estado vacío informativo con llamada a la acción.
  - Botón flotante (+) para agendar nueva cita.
- **Previews disponibles en Android Studio**:
  - `AppointmentListPreview` (con datos mockeados).
  - `EmptyAppointmentListPreview` (estado vacío ilustrado).
  - `AppointmentCardPreview` (tarjeta individual aislada).

### 2. `CreateAppointmentScreen` (Formulario de Cita)
- **Ruta**: `fleet/appointments/create`.
- **Características**:
  - Pre-llenado automático de la sede activa (`branchId`) desde `SessionDataStore`.
  - Campos de entrada para `customerId`, `vehicleId`, fecha/hora programada y notas de revisión.
  - Validación previa de formato UUID antes de emitir la petición a red.
  - Botón primario con spinner de carga integrado.
- **Preview disponible**:
  - `CreateAppointmentPreview`.

### 3. `StaffManagementScreen` (Personal del Taller y Solicitudes)
- **Ruta**: `fleet/staff` (accesible desde el botón *"Equipo y Personal de Taller (Fleet)"* en el Dashboard).
- **Características**:
  - Pestañas superiores sincronizadas: **Activos** y **Solicitudes**.
  - En solicitudes: Muestra postulaciones técnicas con especialidad y salario solicitado, incluyendo botones directos de **Aprobar** y **Rechazar**.
  - En activos: Muestra miembros confirmados con su especialidad y salario en soles (`S/.`).
  - Botón flotante (+) que despliega un diálogo emergente validado para registrar nuevo personal en la sede.
- **Previews disponibles**:
  - `StaffManagementActivePreview`.
  - `StaffManagementPendingPreview`.
  - `StaffCardPreview`.

---

## 5. Pruebas Unitarias Ejecutadas y Aprobadas

Se implementó el archivo de pruebas [`FleetUseCasesTest.kt`](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/test/java/com/tuxlogic/shiftiq/mobile/feature/fleet/FleetUseCasesTest.kt) con MockK y Coroutines Test:

1. `getAppointments returns list of appointments from repository` &rarr; Verifica la recuperación y mapeo de citas por sede activa.
2. `createAppointment delegates to repository and returns created appointment` &rarr; Valida el envío de parámetros y respuesta del repositorio.
3. `updateAppointment changes status to COMPLETED` &rarr; Comprueba la transición de estado a completado.
4. `getEmployeeRegistrations returns staff registrations filtered by branch` &rarr; Comprueba la consulta de personal técnico filtrado por sede.
5. `approveEmployeeRegistration calls approve on repository and returns active registration` &rarr; Valida el llamado al endpoint de aprobación de mecánicos.
6. `rejectEmployeeRegistration calls reject on repository with reason` &rarr; Valida el rechazo de postulaciones con motivo.

**Resultado de ejecución:**
```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.tuxlogic.shiftiq.mobile.feature.fleet.FleetUseCasesTest"
# BUILD SUCCESSFUL in 37s (33 actionable tasks: 1 executed, 32 up-to-date)
```

---

## 6. Historial de Commits Atómicos (Rama `feature/fleet`)

| Commit | Mensaje | Descripción |
| :--- | :--- | :--- |
| `78968a3` | `feat(fleet-domain-data): implement appointments, staff and customer registrations domain and data layers` | Modelos de dominio, repositorios, casos de uso, DTOs, API Retrofit y módulo Hilt. |
| `6d362a0` | `feat(fleet-ui): implement appointments and staff management screens with previews and navigation` | Pantallas Compose de Citas y Personal, ViewModels, navegación en `AppDestination` y `MainActivity`. |
| `de15e1a` | `test(fleet): add unit tests for fleet use cases and fix role dashboard scroll` | Pruebas unitarias completas con MockK y ajuste de scroll en `RoleDashboardScreen`. |
| `XXXXXXX` | `docs(fleet): add block 3 fleet implementation report and verification guide` | Documentación técnica del Bloque 3 y actualización de la matriz de trazabilidad. |

---

## 7. Verificación en Emulador / Dispositivo Físico

1. Abre el emulador **Pixel 10a (AVD)** en Android Studio.
2. Inicia sesión con tus credenciales de Dueño (`alan_workshop_owner@tuxlogic.com` / `Password123!` o tu nuevo usuario creado por Swagger).
3. En el **Dashboard Principal**, pulsa:
   - **Agenda y Citas (Fleet)** &rarr; Explora el listado, filtra por estado o pulsa `+` para programar una cita.
   - **Equipo y Personal de Taller (Fleet)** &rarr; Revisa el equipo activo, cambia a solicitudes y aprueba o rechaza postulaciones.
