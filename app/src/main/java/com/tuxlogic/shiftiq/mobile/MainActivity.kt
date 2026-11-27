package com.tuxlogic.shiftiq.mobile

import android.os.Bundle
import androidx.biometric.BiometricManager
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionState
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQLoadingScreen
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.model.Role
import com.tuxlogic.shiftiq.mobile.core.localization.AppLanguageStore
import com.tuxlogic.shiftiq.mobile.core.localization.AppTranslations
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import com.tuxlogic.shiftiq.mobile.core.navigation.AppDestination
import com.tuxlogic.shiftiq.mobile.core.navigation.RoleBottomNavigationBar
import com.tuxlogic.shiftiq.mobile.core.navigation.roleTabRoutes
import com.tuxlogic.shiftiq.mobile.core.security.BiometricLockOverlay
import com.tuxlogic.shiftiq.mobile.core.security.BiometricPreferences
import com.tuxlogic.shiftiq.mobile.feature.iam.application.usecase.LogoutUseCase
import com.tuxlogic.shiftiq.mobile.feature.iam.presentation.branch.BranchSelectionScreen
import com.tuxlogic.shiftiq.mobile.feature.iam.presentation.branch.BranchSelectionViewModel
import com.tuxlogic.shiftiq.mobile.feature.analytics.presentation.dashboard.EmployeeDashboardRoute
import com.tuxlogic.shiftiq.mobile.feature.analytics.presentation.dashboard.OwnerDashboardRoute
import com.tuxlogic.shiftiq.mobile.feature.operations.presentation.workorder.LiveWorkOrderRoute
import com.tuxlogic.shiftiq.mobile.feature.operations.presentation.workorders.WorkOrderListRoute
import com.tuxlogic.shiftiq.mobile.feature.operations.presentation.workorders.DtcWorkOrderPrefill
import com.tuxlogic.shiftiq.mobile.feature.iam.presentation.login.LoginScreen
import com.tuxlogic.shiftiq.mobile.feature.iam.presentation.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

private const val DTC_WORK_ORDER_PREFILL_KEY = "dtc_work_order_prefill"
private const val WORK_ORDER_APPOINTMENT_PREFILL_KEY = "work_order_appointment_prefill"

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var sessionDataStore: SessionDataStore

    @Inject
    lateinit var logoutUseCase: LogoutUseCase

    @Volatile
    private var isSessionReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        splash.setKeepOnScreenCondition { !isSessionReady }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShiftIQTheme {
                var sessionState by remember { mutableStateOf<SessionState?>(null) }
                var sessionLoadFallbackUsed by remember { mutableStateOf(false) }
                LaunchedEffect(sessionDataStore) {
                    val initialSession = try {
                        withTimeoutOrNull(8_000L) { sessionDataStore.sessionState.first() }
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    } catch (_: Exception) {
                        null
                    }

                    sessionState = initialSession ?: SessionState()
                    sessionLoadFallbackUsed = initialSession == null
                    isSessionReady = true

                    sessionDataStore.sessionState
                        .catch { emit(SessionState()) }
                        .collect { latestSession ->
                            sessionState = latestSession
                            isSessionReady = true
                        }
                }

                var biometricGateChecked by remember { mutableStateOf(false) }
                var biometricGateRequired by remember { mutableStateOf(false) }
                LaunchedEffect(sessionState) {
                    val session = sessionState ?: return@LaunchedEffect
                    if (!biometricGateChecked) {
                        val userId = session.userId?.value?.toString().orEmpty()
                        val biometricAvailable = BiometricManager.from(this@MainActivity).canAuthenticate(
                            BiometricManager.Authenticators.BIOMETRIC_WEAK
                        ) == BiometricManager.BIOMETRIC_SUCCESS
                        biometricGateRequired = session.isAuthenticated && userId.isNotBlank() &&
                            biometricAvailable && BiometricPreferences.isEnabled(this@MainActivity, userId)
                        biometricGateChecked = true
                    }
                }

                val navController = rememberNavController()
                val scope = rememberCoroutineScope()
                val backStackEntry by navController.currentBackStackEntryAsState()
                var loginNavigationHandled by remember { mutableStateOf(false) }

                LaunchedEffect(sessionState, sessionLoadFallbackUsed, loginNavigationHandled) {
                    val session = sessionState ?: return@LaunchedEffect
                    if (!sessionLoadFallbackUsed || !session.isAuthenticated || loginNavigationHandled) {
                        return@LaunchedEffect
                    }
                    if (navController.currentDestination?.route == AppDestination.Login.route) {
                        val destination = when {
                            session.userRole == Role.ROLE_USER -> AppDestination.CustomerDashboard.route
                            session.activeBranchId == null -> AppDestination.BranchSelection.route
                            else -> AppDestination.Dashboard.route
                        }
                        navController.navigate(destination) {
                            popUpTo(AppDestination.Login.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }

                when {
                    !isSessionReady || !biometricGateChecked -> ShiftIQLoadingScreen()
                    biometricGateRequired && sessionState?.isAuthenticated == true -> BiometricLockOverlay(
                        activity = this@MainActivity,
                        username = sessionState?.userEmail.orEmpty(),
                        onUnlocked = { biometricGateRequired = false },
                        onSwitchAccount = {
                            scope.launch { logoutUseCase() }
                        }
                    )
                    else -> Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        val role = sessionState?.userRole
                        val route = backStackEntry?.destination?.route
                        val action = if (sessionState?.isAuthenticated == true && role != Role.ROLE_USER) {
                            when (route) {
                                AppDestination.WorkOrders.route -> Triple("Nueva OT", Icons.Default.Add) {
                                    navController.navigate(AppDestination.CreateWorkOrder.route)
                                }
                                AppDestination.Appointments.route -> Triple("Nueva cita", Icons.Default.Add) {
                                    navController.navigate(
                                        AppDestination.CreateAppointment.createRoute(
                                            branchId = sessionState?.activeBranchId?.value?.toString()
                                        )
                                    )
                                }
                                AppDestination.Inventory.route -> Triple("Nuevo producto", Icons.Default.Add) {
                                    navController.navigate(
                                        AppDestination.InventoryCreateProduct.createRoute(
                                            sessionState?.activeBranchId?.value?.toString()
                                        )
                                    )
                                }
                                else -> null
                            }
                        } else null

                        action?.let { (label, icon, onClick) ->
                            ExtendedFloatingActionButton(
                                onClick = onClick,
                                icon = { Icon(icon, contentDescription = null) },
                                text = { Text(label) },
                                containerColor = Color(0xFF2563EB),
                                contentColor = Color.White,
                                shape = RoundedCornerShape(24.dp)
                            )
                        }
                    },
                    bottomBar = {
                        val role = sessionState?.userRole
                        val route = backStackEntry?.destination?.route
                        if (sessionState?.isAuthenticated == true && role != null &&
                            role != Role.ROLE_USER && route in roleTabRoutes(role)
                        ) {
                            RoleBottomNavigationBar(
                                role = role,
                                currentRoute = route,
                                onNavigate = { destination ->
                                    if (destination != route) {
                                        navController.navigate(destination) {
                                            popUpTo(AppDestination.Dashboard.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    val session = sessionState
                    if (session == null) {
                        ShiftIQLoadingScreen()
                    } else {
                        val onLogout: () -> Unit = {
                            scope.launch {
                                logoutUseCase()
                                navController.navigate(AppDestination.Login.route) {
                                    popUpTo(navController.graph.id) { inclusive = true }
                                }
                            }
                        }

                        val startDestination = when {
                            !session.isAuthenticated -> AppDestination.Login.route
                            session.userRole == Role.ROLE_USER -> AppDestination.CustomerDashboard.route
                            session.activeBranchId == null -> AppDestination.BranchSelection.route
                            else -> AppDestination.Dashboard.route
                        }

                        NavHost(
                            navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier.padding(innerPadding),
                            enterTransition = {
                                fadeIn(tween(280)) + slideInHorizontally(
                                    initialOffsetX = { width -> width / 18 },
                                    animationSpec = tween(280)
                                )
                            },
                            exitTransition = {
                                fadeOut(tween(180)) + slideOutHorizontally(
                                    targetOffsetX = { width -> -width / 24 },
                                    animationSpec = tween(180)
                                )
                            },
                            popEnterTransition = {
                                fadeIn(tween(240)) + slideInHorizontally(
                                    initialOffsetX = { width -> -width / 18 },
                                    animationSpec = tween(240)
                                )
                            },
                            popExitTransition = {
                                fadeOut(tween(180)) + slideOutHorizontally(
                                    targetOffsetX = { width -> width / 24 },
                                    animationSpec = tween(180)
                                )
                            }
                        ) {
                            composable(AppDestination.Login.route) {
                                val loginViewModel: LoginViewModel = hiltViewModel()
                                val uiState by loginViewModel.uiState.collectAsState()

                                LoginScreen(
                                    uiState = uiState,
                                    onEmailChanged = loginViewModel::onEmailChanged,
                                    onPasswordChanged = loginViewModel::onPasswordChanged,
                                    onLoginClick = { loginViewModel.login() },
                                    onLoginSuccess = { role ->
                                        loginNavigationHandled = true
                                        val destination = if (role == Role.ROLE_USER) {
                                            AppDestination.CustomerDashboard.route
                                        } else {
                                            AppDestination.BranchSelection.route
                                        }
                                        navController.navigate(destination) {
                                            popUpTo(AppDestination.Login.route) { inclusive = true }
                                        }
                                    },
                                    onNavigateToRegister = {
                                        navController.navigate(AppDestination.Register.route)
                                    },
                                    onForgotPasswordClick = {
                                        navController.navigate(AppDestination.ForgotPassword.route)
                                    },
                                    onPasswordVisibilityToggle = loginViewModel::onPasswordVisibilityToggle,
                                    onRememberMeToggle = loginViewModel::onRememberMeToggle
                                )
                            }

                            composable(AppDestination.Register.route) {
                                val registerViewModel: com.tuxlogic.shiftiq.mobile.feature.iam.presentation.register.RegisterViewModel = hiltViewModel()

                                com.tuxlogic.shiftiq.mobile.feature.iam.presentation.register.RegisterScreen(
                                    viewModel = registerViewModel,
                                    onBackToLogin = { navController.popBackStack() },
                                    onNavigateToRecovery = { navController.navigate(AppDestination.ForgotPassword.route) },
                                    onRegistrationSuccess = {
                                        navController.navigate(AppDestination.CustomerDashboard.route) {
                                            popUpTo(AppDestination.Login.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(AppDestination.ForgotPassword.route) {
                                val recoveryViewModel: com.tuxlogic.shiftiq.mobile.feature.iam.presentation.recovery.RecoveryViewModel = hiltViewModel()

                                com.tuxlogic.shiftiq.mobile.feature.iam.presentation.recovery.RecoveryScreen(
                                    viewModel = recoveryViewModel,
                                    onBackToLogin = { navController.popBackStack() }
                                )
                            }

                            composable(AppDestination.BranchSelection.route) {
                                val branchViewModel: BranchSelectionViewModel = hiltViewModel()
                                val uiState by branchViewModel.uiState.collectAsState()

                                BranchSelectionScreen(
                                    uiState = uiState,
                                    onBranchSelected = branchViewModel::onBranchSelected,
                                    onConfirmClick = branchViewModel::confirmBranchSelection,
                                    onConfirmed = {
                                        navController.navigate(AppDestination.Dashboard.route) {
                                            popUpTo(AppDestination.BranchSelection.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable(AppDestination.Dashboard.route) {
                                if (session.userRole == Role.ROLE_OWNER || session.userRole == Role.ROLE_ADMIN) {
                                    OwnerDashboardRoute(
                                        onNavigateToBilling = { navController.navigate(AppDestination.Billing.route) },
                                        onSwitchBranch = { navController.navigate(AppDestination.BranchSelection.route) },
                                        onNavigateToProfile = { navController.navigate(AppDestination.Profile.route) },
                                        onNavigateToOrders = { navController.navigate(AppDestination.WorkOrders.route) },
                                        onNavigateToAppointments = { navController.navigate(AppDestination.Appointments.route) },
                                        onNavigateToInventory = { navController.navigate(AppDestination.Inventory.route) },
                                        onNavigateToTelemetry = { navController.navigate(AppDestination.Telemetry.route) }
                                    )
                                } else {
                                    EmployeeDashboardRoute(
                                        onNavigateToBilling = { navController.navigate(AppDestination.Billing.route) },
                                        onSwitchBranch = { navController.navigate(AppDestination.BranchSelection.route) },
                                        onNavigateToProfile = { navController.navigate(AppDestination.Profile.route) },
                                        onNavigateToOrders = { navController.navigate(AppDestination.WorkOrders.route) },
                                        onNavigateToAppointments = { navController.navigate(AppDestination.Appointments.route) },
                                        onNavigateToInventory = { navController.navigate(AppDestination.Inventory.route) },
                                        onNavigateToTelemetry = { navController.navigate(AppDestination.Telemetry.route) },
                                        onOpenWorkOrder = { id -> navController.navigate(AppDestination.WorkOrderDetail.createRoute(id)) }
                                    )
                                }
                            }

                            composable(AppDestination.WorkOrders.route) {
                                WorkOrderListRoute(
                                    canCreateWorkOrder = session.userRole != Role.ROLE_USER,
                                    onCreateWorkOrder = { navController.navigate(AppDestination.CreateWorkOrder.route) },
                                    onOpenWorkOrder = { id ->
                                        navController.navigate(AppDestination.WorkOrderDetail.createRoute(id))
                                    }
                                )
                            }

                            composable(AppDestination.CreateWorkOrder.route) { backStackEntry ->
                                val initialDtcPrefill = remember(backStackEntry) {
                                    navController.previousBackStackEntry?.savedStateHandle
                                        ?.remove<DtcWorkOrderPrefill>(DTC_WORK_ORDER_PREFILL_KEY)
                                }
                                val initialAppointmentId = remember(backStackEntry) {
                                    navController.previousBackStackEntry?.savedStateHandle
                                        ?.remove<String>(WORK_ORDER_APPOINTMENT_PREFILL_KEY)
                                }
                                val canCreateFromDtcAlert =
                                    initialDtcPrefill != null && session.userRole != Role.ROLE_USER
                                if (session.userRole == Role.ROLE_OWNER ||
                                    session.userRole == Role.ROLE_ADMIN ||
                                    canCreateFromDtcAlert
                                ) {
                                    com.tuxlogic.shiftiq.mobile.feature.operations.presentation.workorders.CreateWorkOrderRoute(
                                        onNavigateBack = { navController.popBackStack() },
                                        initialDtcPrefill = initialDtcPrefill,
                                        initialAppointmentId = initialAppointmentId,
                                        onWorkOrderCreated = {
                                            navController.navigate(AppDestination.WorkOrders.route) {
                                                popUpTo(AppDestination.WorkOrders.route) { inclusive = true }
                                                launchSingleTop = true
                                            }
                                        }
                                    )
                                } else {
                                    LaunchedEffect(Unit) { navController.popBackStack() }
                                }
                            }

                            composable(AppDestination.Telemetry.route) {
                                com.tuxlogic.shiftiq.mobile.feature.iot.presentation.telemetry.TelemetryDashboardRoute(
                                    onNavigateBack = { navController.popBackStack() },
                                    onCreateWorkOrderFromAlert = { alert ->
                                        val recommendation = alert.aiDiagnosticRecommendation
                                        val diagnostic = buildString {
                                            append("${AppTranslations.translate("Alerta OBD", AppLanguageStore.current)} ${alert.dtcCode}: ${alert.description}.")
                                            if (recommendation.isNotBlank()) {
                                                append(" ${AppTranslations.translate("Recomendación de diagnóstico", AppLanguageStore.current)}: $recommendation")
                                            }
                                        }
                                        navController.currentBackStackEntry?.savedStateHandle?.set(
                                            DTC_WORK_ORDER_PREFILL_KEY,
                                            DtcWorkOrderPrefill(
                                                branchId = alert.branchId.value.toString(),
                                                vehicleId = alert.vehicleId.orEmpty(),
                                                dtcCode = alert.dtcCode,
                                                plate = alert.vehiclePlate,
                                                model = alert.vehicleModel,
                                                customerName = alert.customerName,
                                                diagnosticSummary = diagnostic
                                            )
                                        )
                                        navController.navigate(AppDestination.CreateWorkOrder.route)
                                    }
                                )
                            }

                            composable(
                                route = AppDestination.WorkOrderDetail.route,
                                arguments = listOf(
                                    androidx.navigation.navArgument("workOrderId") {
                                        type = androidx.navigation.NavType.StringType
                                    }
                                )
                            ) { backStackEntry ->
                                val workOrderId = backStackEntry.arguments?.getString("workOrderId").orEmpty()
                                LiveWorkOrderRoute(
                                    workOrderId = workOrderId,
                                    isOwnerAdmin = session.userRole == Role.ROLE_OWNER || session.userRole == Role.ROLE_ADMIN,
                                    canExecuteTasks = session.userRole != Role.ROLE_USER,
                                    onNavigateBack = { navController.popBackStack() },
                                    onOpenVoucher = { voucherId ->
                                        navController.navigate(AppDestination.BillingVoucher.createRoute(voucherId))
                                    }
                                )
                            }

                            composable(AppDestination.CustomerDashboard.route) {
                                com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.b2c.MainB2CScreen(
                                    onNavigateToLogin = {
                                        scope.launch {
                                            logoutUseCase()
                                            navController.navigate(AppDestination.Login.route) {
                                                popUpTo(navController.graph.id) {
                                                    inclusive = true
                                                }
                                            }
                                        }
                                    }
                                )
                            }

                            composable(AppDestination.CustomerVehicles.route) {
                                val viewModel: com.tuxlogic.shiftiq.mobile.feature.iot.presentation.vehicles.CustomerVehiclesViewModel = hiltViewModel()
                                com.tuxlogic.shiftiq.mobile.feature.iot.presentation.vehicles.CustomerVehiclesScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            // Bloque 2: Core (Talleres y Sedes)
                            composable(AppDestination.WorkshopList.route) {
                                val viewModel: com.tuxlogic.shiftiq.mobile.feature.core.presentation.workshops.WorkshopListViewModel = hiltViewModel()
                                com.tuxlogic.shiftiq.mobile.feature.core.presentation.workshops.WorkshopListScreen(
                                    viewModel = viewModel,
                                    onNavigateToCreateWorkshop = { ownerId ->
                                        navController.navigate(AppDestination.CreateWorkshop.createRoute(ownerId))
                                    },
                                    onNavigateToOwnerProfile = {
                                        navController.navigate(AppDestination.OwnerProfile.route)
                                    },
                                    onNavigateToBranches = { workshopId ->
                                        navController.navigate(AppDestination.BranchManagement.createRoute(workshopId))
                                    }
                                )
                            }

                            composable(
                                route = AppDestination.CreateWorkshop.route,
                                arguments = listOf(androidx.navigation.navArgument("ownerId") { type = androidx.navigation.NavType.StringType })
                            ) { backStackEntry ->
                                val ownerId = backStackEntry.arguments?.getString("ownerId") ?: ""
                                val viewModel: com.tuxlogic.shiftiq.mobile.feature.core.presentation.workshops.CreateWorkshopViewModel = hiltViewModel()
                                com.tuxlogic.shiftiq.mobile.feature.core.presentation.workshops.CreateWorkshopScreen(
                                    ownerId = ownerId,
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onWorkshopCreated = { navController.popBackStack() }
                                )
                            }

                            composable(
                                route = AppDestination.BranchManagement.route,
                                arguments = listOf(androidx.navigation.navArgument("workshopId") { type = androidx.navigation.NavType.StringType })
                            ) { backStackEntry ->
                                val workshopId = backStackEntry.arguments?.getString("workshopId") ?: ""
                                val viewModel: com.tuxlogic.shiftiq.mobile.feature.core.presentation.branches.BranchManagementViewModel = hiltViewModel()
                                com.tuxlogic.shiftiq.mobile.feature.core.presentation.branches.BranchManagementScreen(
                                    workshopId = workshopId,
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onNavigateToCreateBranch = { wId ->
                                        navController.navigate(AppDestination.CreateBranch.createRoute(wId))
                                    }
                                )
                            }

                            composable(
                                route = AppDestination.CreateBranch.route,
                                arguments = listOf(androidx.navigation.navArgument("workshopId") { type = androidx.navigation.NavType.StringType })
                            ) { backStackEntry ->
                                val workshopId = backStackEntry.arguments?.getString("workshopId") ?: ""
                                val viewModel: com.tuxlogic.shiftiq.mobile.feature.core.presentation.branches.CreateBranchViewModel = hiltViewModel()
                                com.tuxlogic.shiftiq.mobile.feature.core.presentation.branches.CreateBranchScreen(
                                    workshopId = workshopId,
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onBranchCreated = { navController.popBackStack() }
                                )
                            }

                            composable(AppDestination.OwnerProfile.route) {
                                com.tuxlogic.shiftiq.mobile.feature.core.presentation.profile.ProfileRoute(
                                    onNavigateBack = { navController.popBackStack() },
                                    onLogout = onLogout,
                                    onNavigateToServiceCatalog = { navController.navigate(AppDestination.ServiceCatalog.route) },
                                    onNavigateToBranches = { navController.navigate(AppDestination.BranchSelection.route) },
                                    onNavigateToOperatingHours = { navController.navigate(AppDestination.OperatingHours.route) }
                                )
                            }

                            composable(AppDestination.ServiceCatalog.route) {
                                if (session.userRole == Role.ROLE_OWNER || session.userRole == Role.ROLE_ADMIN) {
                                    com.tuxlogic.shiftiq.mobile.feature.operations.presentation.services.ServicesCatalogRoute(
                                        onNavigateBack = { navController.popBackStack() }
                                    )
                                } else {
                                    LaunchedEffect(Unit) { navController.popBackStack() }
                                }
                            }

                            composable(AppDestination.OperatingHours.route) {
                                com.tuxlogic.shiftiq.mobile.feature.core.presentation.operatinghours.OperatingHoursRoute(
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            // Bloque 3: Fleet (Citas y Personal de Taller)
                            composable(AppDestination.Appointments.route) {
                                val viewModel: com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.appointments.AppointmentListViewModel = hiltViewModel()
                                val uiState by viewModel.uiState.collectAsState()

                                com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.appointments.AppointmentListScreen(
                                    uiState = uiState,
                                    onRefresh = viewModel::loadAppointments,
                                    onFilterSelected = viewModel::setStatusFilter,
                                    onMarkStatus = viewModel::markAppointmentStatus,
                                    onNavigateToCreate = { navController.navigate(AppDestination.CreateAppointment.createRoute()) },
                                    onOpenWorkOrder = { appointment ->
                                        navController.currentBackStackEntry?.savedStateHandle?.set(
                                            WORK_ORDER_APPOINTMENT_PREFILL_KEY,
                                            appointment.id.toString()
                                        )
                                        navController.navigate(AppDestination.CreateWorkOrder.route)
                                    },
                                    onNavigateToCustomerDirectory = { navController.navigate(AppDestination.CustomerDirectory.route) },
                                    onSelectDate = viewModel::selectDate,
                                    onClearMessages = viewModel::clearMessages
                                )
                            }

                            composable(
                                route = AppDestination.CreateAppointment.route,
                                arguments = listOf(
                                    androidx.navigation.navArgument("branchId") { type = androidx.navigation.NavType.StringType; defaultValue = "" },
                                    androidx.navigation.navArgument("customerId") { type = androidx.navigation.NavType.StringType; defaultValue = "" }
                                )
                            ) { backStackEntry ->
                                val viewModel: com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.appointments.CreateAppointmentViewModel = hiltViewModel()
                                val uiState by viewModel.uiState.collectAsState()
                                val branchId = backStackEntry.arguments?.getString("branchId").orEmpty()
                                val customerId = backStackEntry.arguments?.getString("customerId").orEmpty()
                                LaunchedEffect(branchId, customerId) {
                                    if (branchId.isNotBlank()) viewModel.onBranchIdChanged(branchId)
                                    if (customerId.isNotBlank()) viewModel.onCustomerIdChanged(customerId)
                                }

                                com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.appointments.CreateAppointmentScreen(
                                    uiState = uiState,
                                    onBranchIdChanged = viewModel::onBranchIdChanged,
                                    onCustomerIdChanged = viewModel::onCustomerIdChanged,
                                    onVehicleIdChanged = viewModel::onVehicleIdChanged,
                                    onScheduledStartChanged = viewModel::onScheduledStartChanged,
                                    onNotesChanged = viewModel::onNotesChanged,
                                    onSubmit = viewModel::createAppointment,
                                    onNavigateBack = { navController.popBackStack() },
                                    onAppointmentCreated = { navController.popBackStack() }
                                )
                            }

                            composable(AppDestination.CustomerDirectory.route) {
                                com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.customers.CustomerDirectoryRoute(
                                    onNavigateBack = { navController.popBackStack() },
                                    onCreateAppointment = { branchId, customerId ->
                                        navController.navigate(AppDestination.CreateAppointment.createRoute(branchId, customerId))
                                    }
                                )
                            }

                            composable(AppDestination.Billing.route) {
                                com.tuxlogic.shiftiq.mobile.feature.billing.presentation.BillingDashboardRoute(
                                    onNavigateBack = { navController.popBackStack() },
                                    onOpenVoucher = { voucherId ->
                                        navController.navigate(AppDestination.BillingVoucher.createRoute(voucherId))
                                    }
                                )
                            }

                            composable(
                                route = AppDestination.BillingVoucher.route,
                                arguments = listOf(androidx.navigation.navArgument("voucherId") {
                                    type = androidx.navigation.NavType.StringType
                                })
                            ) { backStackEntry ->
                                com.tuxlogic.shiftiq.mobile.feature.billing.presentation.BillingVoucherDetailRoute(
                                    voucherId = backStackEntry.arguments?.getString("voucherId").orEmpty(),
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }

                            composable(AppDestination.Profile.route) {
                                com.tuxlogic.shiftiq.mobile.feature.core.presentation.profile.ProfileRoute(
                                    onNavigateBack = { navController.popBackStack() },
                                    onLogout = onLogout,
                                    onNavigateToServiceCatalog = { navController.navigate(AppDestination.ServiceCatalog.route) },
                                    onNavigateToBranches = { navController.navigate(AppDestination.BranchSelection.route) },
                                    onNavigateToOperatingHours = { navController.navigate(AppDestination.OperatingHours.route) }
                                )
                            }

                            composable(AppDestination.StaffManagement.route) {
                                val viewModel: com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.staff.StaffManagementViewModel = hiltViewModel()
                                val uiState by viewModel.uiState.collectAsState()

                                com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.staff.StaffManagementScreen(
                                    uiState = uiState,
                                    onTabSelected = viewModel::setSelectedTab,
                                    onRefresh = viewModel::loadRegistrations,
                                    onApprove = viewModel::approveRegistration,
                                    onReject = viewModel::rejectRegistration,
                                    onOpenAddDialog = viewModel::openAddDialog,
                                    onCloseAddDialog = viewModel::closeAddDialog,
                                    onAddEmployeeIdChanged = viewModel::onAddEmployeeIdChanged,
                                    onAddSpecialityChanged = viewModel::onAddSpecialityChanged,
                                    onAddSalaryChanged = viewModel::onAddSalaryChanged,
                                    onAddRoleChanged = viewModel::onAddRoleChanged,
                                    onSubmitAddStaff = viewModel::submitAddStaff,
                                    onNavigateBack = { navController.popBackStack() },
                                    onClearMessages = viewModel::clearMessages
                                )
                            }

                            // Bloque 4: Inventory (Gestión de Inventario y Stock)
                            // IMPORTANT: declare /create before /{productId} so "create" is not treated as an id.
                            composable(AppDestination.Inventory.route) {
                                com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.InventoryScreen(
                                    onOpenDetail = { productId ->
                                        navController.navigate(
                                            AppDestination.InventoryProductDetail.createRoute(productId)
                                        )
                                    },
                                    onCreateProduct = { branchId ->
                                        navController.navigate(
                                            AppDestination.InventoryCreateProduct.createRoute(branchId)
                                        )
                                    },
                                    onBatchIn = { productId ->
                                        navController.navigate(
                                            AppDestination.InventoryBatchIn.createRoute(productId)
                                        )
                                    }
                                )
                            }

                            composable(
                                route = AppDestination.InventoryCreateProduct.route,
                                arguments = listOf(
                                    androidx.navigation.navArgument("branchId") {
                                        type = androidx.navigation.NavType.StringType
                                        defaultValue = "none"
                                        nullable = false
                                    }
                                )
                            ) {
                                com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.form.ProductFormScreen(
                                    onBack = { navController.popBackStack() },
                                    onViewCatalog = {
                                        navController.popBackStack(AppDestination.Inventory.route, inclusive = false)
                                    }
                                )
                            }

                            composable(
                                route = AppDestination.InventoryProductDetail.route,
                                arguments = listOf(
                                    androidx.navigation.navArgument("productId") {
                                        type = androidx.navigation.NavType.StringType
                                    }
                                )
                            ) {
                                com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.detail.ProductDetailScreen(
                                    onBack = { navController.popBackStack() },
                                    onBatchIn = { productId ->
                                        navController.navigate(
                                            AppDestination.InventoryBatchIn.createRoute(productId)
                                        )
                                    }
                                )
                            }

                            composable(
                                route = AppDestination.InventoryBatchIn.route,
                                arguments = listOf(
                                    androidx.navigation.navArgument("productId") {
                                        type = androidx.navigation.NavType.StringType
                                    }
                                )
                            ) {
                                com.tuxlogic.shiftiq.mobile.feature.inventory.presentation.stock.BatchInScreen(
                                    onBack = { navController.popBackStack() }
                                )
                            }

                        }
                    }
                }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    ShiftIQTheme {
        LoginScreen(
            uiState = com.tuxlogic.shiftiq.mobile.feature.iam.presentation.login.LoginUiState(
                email = "admin@shiftiq.com"
            ),
            onEmailChanged = {},
            onPasswordChanged = {},
            onLoginClick = {},
            onLoginSuccess = {}
        )
    }
}
