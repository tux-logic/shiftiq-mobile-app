# Bloque 1: IAM (Identidad y Acceso) — Informe de Implementación y Guía de Verificación

Este documento registra la implementación técnica del **Bloque 1: IAM** de la aplicación móvil **ShiftIQ Mobile** (`com.tuxlogic.shiftiq.mobile`), completando el **Hito 1 ("Existe y autentica")** bajo los principios de **Clean Architecture**, **Domain-Driven Design (DDD)** y **Google Fonts Material 3**.

---

## 1. Rama de Trabajo (GitFlow)

El desarrollo del Bloque 1 se aisló en una rama feature dedicada creada a partir de `develop`:

- **Rama Base:** `develop`
- **Rama Feature Activa:** `feature/iam`

---

## 2. Tipografía con Google Fonts (Material 3)

Se integró la fuente moderna **Inter** desde Google Fonts descargable de forma dinámica en Compose:

- **Certificados Oficiales:** [app/src/main/res/values/font_certs.xml](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/res/values/font_certs.xml) con los certificados de Google Play Services.
- **Tipografía Material 3:** [app/src/main/java/com/tuxlogic/shiftiq/mobile/core/designsystem/theme/Type.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/core/designsystem/theme/Type.kt) configurada con `GoogleFont("Inter")` y variantes `Normal`, `Medium`, `SemiBold` y `Bold`.

---

## 3. Estructura de Capas (Clean Architecture)

```
app/src/main/java/com/tuxlogic/shiftiq/mobile/feature/iam/
├── data/
│   ├── remote/
│   │   ├── dto/AuthDtos.kt                    # SignInRequestDto, RefreshTokenRequestDto, AuthenticatedUserResponseDto
│   │   └── api/AuthApiService.kt              # Endpoints Retrofit (login, refresh, logout con hasBody=true)
│   ├── repository/AuthRepositoryImpl.kt       # Implementación con safeApiCall y SessionDataStore
│   └── di/IamDataModule.kt                    # Módulos Hilt (AuthApiService y AuthRepository)
├── domain/
│   ├── model/AuthenticatedUser.kt             # Entidad de dominio (id, email, role, tokens)
│   ├── repository/AuthRepository.kt           # Contrato de repositorio
│   └── usecase/
│       ├── LoginUseCase.kt                    # Validación de formato y autenticación
│       └── SessionUseCases.kt                 # LogoutUseCase, ObserveSessionUseCase, SelectBranchUseCase
└── presentation/
    ├── login/
    │   ├── LoginViewModel.kt                  # StateFlow<LoginUiState>
    │   └── LoginScreen.kt                     # UI Compose con @Preview interactivo
    ├── branch/
    │   ├── BranchSelectionViewModel.kt        # StateFlow<BranchSelectionUiState>
    │   └── BranchSelectionScreen.kt           # Selector de sedes con RadioButtons y @Preview
    └── dashboard/
        └── RoleDashboardScreen.kt             # Dashboard por rol con sesión activa, badges y logout
```

---

## 4. Pantallas Visibles en Navegación y Flujo de Interacción

Con el Bloque 1 se implementaron **4 pantallas interactivas reales**:

| Pantalla | Composable | Rol y Propósito | Destino NavHost |
| :--- | :--- | :--- | :--- |
| **1. Login** | [LoginScreen.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/feature/iam/presentation/login/LoginScreen.kt) | Formulario de email/contraseña con validación visual, toggle de contraseña y enlace a registro. | `auth/login` |
| **2. Registro (Sign-Up)** | [RegisterScreen.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/feature/iam/presentation/register/RegisterScreen.kt) | Creación de cuenta nueva con selección de rol (`ROLE_OWNER`, `ROLE_EMPLOYEE`, `ROLE_USER`) y auto-login. | `auth/register` |
| **3. Selección de Sucursal** | [BranchSelectionScreen.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/feature/iam/presentation/branch/BranchSelectionScreen.kt) | Permite a roles multi-sucursal (como `ROLE_OWNER`) seleccionar el taller activo. | `branches/select` |
| **4. Dashboard por Rol** | [RoleDashboardScreen.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/feature/iam/presentation/dashboard/RoleDashboardScreen.kt) | Muestra el badge del rol (`Dueño`, `Gerente`, `Mecánico`, etc.), los datos de sesión activa y botón de "Cerrar Sesión". | `dashboard` |

### Flujo de Navegación Reactivo en [MainActivity.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/MainActivity.kt):
1. **Inicio de la App:** La app observa `sessionDataStore.sessionState`.
   - Si no está autenticado $\rightarrow$ Inicia en `LoginScreen`.
   - Si pulsa *"Regístrate aquí"* $\rightarrow$ Navega a `RegisterScreen`.
   - Si ya está autenticado y es `ROLE_OWNER` sin sucursal $\rightarrow$ Inicia en `BranchSelectionScreen`.
   - Si ya tiene sesión completa $\rightarrow$ Inicia en `RoleDashboardScreen`.
2. **Al registrarse:**
   - Envía `POST /api/v1/users`, realiza login automático con el nuevo usuario y navega a su espacio correspondiente.
3. **Al cerrar sesión:**
   - Se ejecuta `LogoutUseCase` (`DELETE /api/v1/authentication/sessions`), se limpia `SessionDataStore` y el `NavHost` vuelve a `LoginScreen` limpiando el historial de navegación.

---

## 5. Endpoints Backend Consumidos

| Endpoint | Método HTTP | Request Body | Response | Uso en la App |
| :--- | :---: | :--- | :--- | :--- |
| `/api/v1/authentication/sessions` | `POST` | `SignInRequestDto(email, password)` | `AuthenticatedUserResponseDto` | Inicia sesión, obtiene tokens JWT y rol. |
| `/api/v1/authentication/sessions/refresh` | `POST` | `RefreshTokenRequestDto(refreshToken)` | `TokenResponseDto` | Refresca el token de forma transparente vía `TokenAuthenticator`. |
| `/api/v1/authentication/sessions` | `DELETE` | `RefreshTokenRequestDto(refreshToken)` | `200 OK / 204 No Content` | Invalida la sesión en el servidor. |
| `/api/v1/users` | `POST` | `SignUpRequestDto(email, password, roles)` | `UserResourceDto` | Registro de nuevo usuario (dueño, técnico o cliente). |

---

## 6. Guía de Verificación en Android Studio (Paso a Paso)

### Método 1: En el Emulador con Device Manager

1. Abre **Device Manager** en la barra lateral derecha de Android Studio y enciende tu dispositivo virtual (ej. Pixel 7 / 8).
2. Haz clic en **Run 'app' ▶** (`Shift + F10`).
3. **Prueba Interactiva 1 (Validación de Formulario):**
   - Deja el campo de correo vacío y pulsa *"Entrar al Sistema"*: verás el mensaje `"El correo electrónico es requerido"`.
   - Escribe un correo inválido como `usuario` y pulsa entrar: verás `"Formato de correo inválido"`.
   - Escribe una contraseña corta (menos de 6 caracteres): verás `"La contraseña debe tener al menos 6 caracteres"`.
4. **Prueba Interactiva 2 (Visibilidad de Contraseña):**
   - Escribe tu clave y pulsa el botón *"Ver / Ocultar"* a la derecha para alternar la visualización del texto.
5. **Prueba Interactiva 3 (Flujo de Sesión y Logout):**
   - Con el backend corriendo (en emulador apunta a `http://10.0.2.2:8080/`), ingresa credenciales válidas.
   - Si ingresas como dueño de taller, te llevará a la pantalla de selección de sucursal con tarjetas seleccionables.
   - Al confirmar, entrarás al Dashboard con tu badge de rol y datos de sesión.
   - Pulsa *"Cerrar Sesión"*: la app limpiará los tokens locales y volverá automáticamente a la pantalla de Login.

---

### Método 2: Visualización Rápida en Compose Previews (Sin Emulador)

Abre cualquiera de estos archivos y selecciona la pestaña **Split / Design** en la esquina superior derecha:
- [LoginScreen.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/feature/iam/presentation/login/LoginScreen.kt): Previsualiza el formulario normal y en estado de error con Google Fonts.
- [BranchSelectionScreen.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/feature/iam/presentation/branch/BranchSelectionScreen.kt): Previsualiza la lista de sucursales con radio buttons.
- [RoleDashboardScreen.kt](file:///c:/Users/alanj/Proyectos/shiftiq-mobile-app/app/src/main/java/com/tuxlogic/shiftiq/mobile/feature/iam/presentation/dashboard/RoleDashboardScreen.kt): Previsualiza el dashboard con información del usuario.

---

## 7. Bitácora de Commits en `feature/iam`

| Hash | Tipo / Scope | Mensaje del Commit |
| :---: | :--- | :--- |
| `67053ef` | `feat(designsystem)` | *configure google fonts inter typography and certificates* |
| `c88aea0` | `feat(iam-domain-data)` | *implement auth api service, repository and use cases* |
| `be9e03c` | `feat(iam-ui)` | *implement login, branch selection, and dashboard with navhost* |
| `448b8c3` | `test(iam)` | *add unit tests for login use case and viewmodel* |
| *(actual)* | `docs(iam)` | *add block 1 iam implementation report and verification guide* |

---

## 8. Verificación de Pruebas Unitarias

Se ejecutó la suite completa:
```powershell
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug
```
- **Resultado:** `BUILD SUCCESSFUL in 2m 23s` (52 tareas ejecutadas / up-to-date).
- **Pruebas IAM aprobadas:**
  - `LoginUseCaseTest`: validación de campos obligatorios, regex de email, longitud mínima de password y delegación al repositorio.
  - `LoginViewModelTest`: estados reactivos `idle -> loading -> success / error` y manejo de `Unauthorized`.
