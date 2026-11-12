package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.b2c

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQBottomNavBar
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.SeverityCrimson
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSurfaceLight
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate100
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate400
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate700
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.TelematicsGreen
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.diagnostic.DiagnosticsRoute
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.diagnostic.DtcDetailScreen
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.diagnostic.OfflineTelemetryScreen
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.GarageScreen
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.external.ExternalServiceRegistrationRoute
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.history.ServiceVoucherPdfRoute
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.history.TechnicalInterventionDetailRoute
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.registration.VehicleRegistrationScreen
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.DriverHomeRoute
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.DriverHomeViewModel
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.AppointmentBookingRoute
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.WorkshopsScreen
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.appointments.MyAppointmentsRoute
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.detail.WorkshopDetailRoute
import com.tuxlogic.shiftiq.mobile.feature.operations.presentation.workorder.LiveWorkOrderScreen
import kotlinx.coroutines.launch

enum class B2CTab(val title: String, val icon: ImageVector) {
    HOME("Inicio", Icons.Default.Home),
    DIAGNOSTIC("Diagnóstico", Icons.Default.Build),
    WORKSHOPS("Talleres", Icons.Default.Place),
    GARAGE("Mi Garaje", Icons.Default.DirectionsCar)
}

@Composable
fun MainB2CScreen(
    onNavigateToLogin: () -> Unit = {},
    driverHomeViewModel: DriverHomeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showLogoutConfirmation by rememberSaveable { mutableStateOf(false) }
    var showProfileModal by rememberSaveable { mutableStateOf(false) }
    var showSettingsModal by rememberSaveable { mutableStateOf(false) }
    var activeChildScreen by rememberSaveable { mutableStateOf<String?>(null) }

    val homeUiState by driverHomeViewModel.uiState.collectAsState()

    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = ShiftIQSurfaceLight,
                drawerShape = RoundedCornerShape(topEnd = spacing.radiusExtraLarge, bottomEnd = spacing.radiusExtraLarge),
                modifier = Modifier.width(spacing.drawerWidth)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // =====================================================================
                    // HEADER DEL NAVIGATION DRAWER (NAVY900)
                    // =====================================================================
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Navy900)
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .padding(spacing.large)
                    ) {
                        Column {
                            // Avatar circular grande con la inicial dinámica del conductor
                            Box(
                                modifier = Modifier
                                    .size(spacing.avatarLarge)
                                    .clip(CircleShape)
                                    .background(PrimaryBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = homeUiState.avatarInitial,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(spacing.medium))

                            Text(
                                text = homeUiState.driverName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            if (homeUiState.driverEmail.isNotBlank()) {
                                Spacer(modifier = Modifier.height(spacing.micro))
                                Text(
                                    text = homeUiState.driverEmail,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Slate400
                                )
                            }

                            Spacer(modifier = Modifier.height(spacing.small))

                            // Rol del Conductor con Check Verde
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(spacing.radiusMedium))
                                    .background(TelematicsGreen.copy(alpha = 0.2f))
                                    .border(spacing.strokeThin, TelematicsGreen.copy(alpha = 0.4f), RoundedCornerShape(spacing.radiusMedium))
                                    .padding(horizontal = spacing.small, vertical = spacing.micro)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = TelematicsGreen,
                                        modifier = Modifier.size(spacing.timelineDotSize)
                                    )
                                    Spacer(modifier = Modifier.width(spacing.extraSmall))
                                    Text(
                                        text = homeUiState.driverRole,
                                        color = TelematicsGreen,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.smallMedium))

                    // =====================================================================
                    // OPCIONES DEL MENÚ LATERAL
                    // =====================================================================
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = spacing.smallMedium),
                        verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                    ) {
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryBlue) },
                            label = { Text("Mi Perfil", fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium) },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                showProfileModal = true
                            },
                            shape = RoundedCornerShape(spacing.radiusMedium),
                            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = PrimaryBlue) },
                            label = { Text("Mis Vehículos", fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium) },
                            selected = selectedTab == 3,
                            onClick = {
                                selectedTab = 3
                                scope.launch { drawerState.close() }
                            },
                            shape = RoundedCornerShape(spacing.radiusMedium)
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Build, contentDescription = null, tint = PrimaryBlue) },
                            label = { Text("Diagnóstico OBD-II", fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium) },
                            selected = selectedTab == 1,
                            onClick = {
                                selectedTab = 1
                                scope.launch { drawerState.close() }
                            },
                            shape = RoundedCornerShape(spacing.radiusMedium)
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Place, contentDescription = null, tint = PrimaryBlue) },
                            label = { Text("Talleres Certificados", fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium) },
                            selected = selectedTab == 2,
                            onClick = {
                                selectedTab = 2
                                scope.launch { drawerState.close() }
                            },
                            shape = RoundedCornerShape(spacing.radiusMedium)
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = Slate700) },
                            label = { Text("Configuración", fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium) },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                showSettingsModal = true
                            },
                            shape = RoundedCornerShape(spacing.radiusMedium),
                            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                        )
                    }

                    // =====================================================================
                    // PIE DE PÁGINA CON BOTÓN DE CERRAR SESIÓN
                    // =====================================================================
                    HorizontalDivider(color = Slate200, thickness = spacing.strokeThin)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.medium, vertical = spacing.smallMedium)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(spacing.radiusMedium))
                                .clickable {
                                    scope.launch { drawerState.close() }
                                    showLogoutConfirmation = true
                                }
                                .padding(vertical = spacing.smallMedium, horizontal = spacing.smallMedium),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar Sesión",
                                tint = SeverityCrimson,
                                modifier = Modifier.size(spacing.iconLarge)
                            )
                            Spacer(modifier = Modifier.width(spacing.smallMedium))
                            Text(
                                text = "Cerrar Sesión",
                                color = SeverityCrimson,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) {
        when {
            activeChildScreen == "vehicle_registration" -> {
                VehicleRegistrationScreen(
                    onNavigateBack = { activeChildScreen = null },
                    onRegistrationComplete = { activeChildScreen = null }
                )
            }
            activeChildScreen?.startsWith("dtc_detail:") == true -> {
                val code = activeChildScreen?.removePrefix("dtc_detail:") ?: "P0135"
                DtcDetailScreen(
                    dtcCode = code,
                    onNavigateBack = { activeChildScreen = null },
                    onNavigateToWorkshops = {
                        activeChildScreen = null
                        selectedTab = 2
                    }
                )
            }
            activeChildScreen == "offline_mode" -> {
                OfflineTelemetryScreen(
                    onNavigateBack = { activeChildScreen = null }
                )
            }
            activeChildScreen?.startsWith("workshop_detail:") == true -> {
                val workshopId = activeChildScreen?.removePrefix("workshop_detail:") ?: "w1"
                WorkshopDetailRoute(
                    workshopId = workshopId,
                    onNavigateBack = { activeChildScreen = null },
                    onNavigateToBooking = { id -> activeChildScreen = "appointment_booking:$id" }
                )
            }
            activeChildScreen == "my_appointments" -> {
                MyAppointmentsRoute(
                    onNavigateBack = { activeChildScreen = null }
                )
            }
            activeChildScreen?.startsWith("appointment_booking:") == true -> {
                val workshopId = activeChildScreen?.removePrefix("appointment_booking:") ?: "workshop-1"
                AppointmentBookingRoute(
                    workshopId = workshopId,
                    onNavigateBack = { activeChildScreen = null },
                    onBookingConfirmed = {
                        activeChildScreen = null
                        selectedTab = 0
                    },
                    onNavigateToMyAppointments = {
                        activeChildScreen = "my_appointments"
                    }
                )
            }
            activeChildScreen?.startsWith("live_work_order:") == true -> {
                val orderId = activeChildScreen?.removePrefix("live_work_order:") ?: "OT-2026-0418"
                LiveWorkOrderScreen(
                    workOrderId = orderId,
                    onNavigateBack = { activeChildScreen = null }
                )
            }
            activeChildScreen == "external_service_registration" -> {
                ExternalServiceRegistrationRoute(
                    onNavigateBack = { activeChildScreen = null }
                )
            }
            activeChildScreen?.startsWith("intervention_detail:") == true -> {
                val interventionId = activeChildScreen?.removePrefix("intervention_detail:") ?: "hist-2"
                TechnicalInterventionDetailRoute(
                    interventionId = interventionId,
                    onNavigateBack = { activeChildScreen = null },
                    onNavigateToVoucherPdf = { voucherId ->
                        activeChildScreen = "voucher_pdf:$voucherId"
                    }
                )
            }
            activeChildScreen?.startsWith("voucher_pdf:") == true -> {
                val voucherId = activeChildScreen?.removePrefix("voucher_pdf:") ?: "v-0004921"
                ServiceVoucherPdfRoute(
                    voucherId = voucherId,
                    onNavigateBack = { activeChildScreen = null }
                )
            }
            else -> {
                Scaffold(
                    bottomBar = {
                        ShiftIQBottomNavBar(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it }
                        )
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = paddingValues.calculateBottomPadding())
                    ) {
                        when (selectedTab) {
                            0 -> DriverHomeRoute(
                                viewModel = driverHomeViewModel,
                                onNavigateToWorkshops = { selectedTab = 2 },
                                onNavigateToDiagnostic = { selectedTab = 1 },
                                onNavigateToGarage = { selectedTab = 3 },
                                onOpenDrawer = { scope.launch { drawerState.open() } },
                                onViewLiveOrder = { orderId -> activeChildScreen = "live_work_order:$orderId" }
                            )
                            1 -> DiagnosticsRoute(
                                onNavigateToWorkshops = { selectedTab = 2 },
                                onNavigateToDtcDetail = { code -> activeChildScreen = "dtc_detail:$code" },
                                onNavigateToOfflineMode = { activeChildScreen = "offline_mode" }
                            )
                            2 -> WorkshopsScreen(
                                onNavigateToBooking = { workshopId -> activeChildScreen = "appointment_booking:$workshopId" },
                                onNavigateToDetail = { workshopId -> activeChildScreen = "workshop_detail:$workshopId" },
                                onNavigateToMyAppointments = { activeChildScreen = "my_appointments" }
                            )
                            3 -> GarageScreen(
                                onAddVehicle = { activeChildScreen = "vehicle_registration" },
                                onNavigateToProfile = { showProfileModal = true },
                                onNavigateToExternalServiceRegistration = {
                                    activeChildScreen = "external_service_registration"
                                },
                                onNavigateToInterventionDetail = { interventionId ->
                                    activeChildScreen = "intervention_detail:$interventionId"
                                },
                                onNavigateToVoucherPdf = { voucherId ->
                                    activeChildScreen = "voucher_pdf:$voucherId"
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // =========================================================================
    // DIÁLOGO DE CONFIRMACIÓN DE CERRAR SESIÓN
    // =========================================================================
    if (showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmation = false },
            title = {
                Text(
                    text = "¿Cerrar Sesión?",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = Slate900
                )
            },
            text = {
                Text(
                    text = "Se cerrará tu sesión activa de conductor en este dispositivo. Tendrás que volver a ingresar tu correo y contraseña.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate700
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirmation = false
                        onNavigateToLogin()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SeverityCrimson),
                    shape = RoundedCornerShape(spacing.radiusMedium)
                ) {
                    Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutConfirmation = false },
                    shape = RoundedCornerShape(spacing.radiusMedium)
                ) {
                    Text("Cancelar", color = Slate700)
                }
            },
            shape = RoundedCornerShape(spacing.radiusLarge),
            containerColor = ShiftIQSurfaceLight
        )
    }

    // =========================================================================
    // DIÁLOGO DE MI PERFIL
    // =========================================================================
    if (showProfileModal) {
        AlertDialog(
            onDismissRequest = { showProfileModal = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(spacing.avatarCompact)
                            .clip(CircleShape)
                            .background(PrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = homeUiState.avatarInitial,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(spacing.smallMedium))
                    Text(
                        text = "Mi Perfil de Conductor",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = Slate900
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing.extraSmall),
                    verticalArrangement = Arrangement.spacedBy(spacing.smallMedium)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(spacing.radiusMedium))
                            .background(Slate100)
                            .padding(spacing.smallMedium)
                    ) {
                        Column {
                            Text("Nombre Completo:", style = MaterialTheme.typography.labelSmall, color = Slate500)
                            Text(homeUiState.driverName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Slate900)
                            Spacer(modifier = Modifier.height(spacing.compact))
                            Text("Correo Electrónico:", style = MaterialTheme.typography.labelSmall, color = Slate500)
                            Text(homeUiState.driverEmail.ifBlank { "No registrado" }, style = MaterialTheme.typography.bodyMedium, color = Slate700)
                            Spacer(modifier = Modifier.height(spacing.compact))
                            Text("Rol Asignado:", style = MaterialTheme.typography.labelSmall, color = Slate500)
                            Text(homeUiState.driverRole, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showProfileModal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(spacing.radiusMedium)
                ) {
                    Text("Cerrar", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(spacing.radiusLarge),
            containerColor = ShiftIQSurfaceLight
        )
    }

    // =========================================================================
    // DIÁLOGO DE CONFIGURACIÓN
    // =========================================================================
    if (showSettingsModal) {
        AlertDialog(
            onDismissRequest = { showSettingsModal = false },
            title = {
                Text(
                    text = "Configuración",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = Slate900
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(spacing.smallMedium)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Notificaciones Push DTC", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                        Text("Activadas", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TelematicsGreen)
                    }
                    HorizontalDivider(color = Slate200)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sincronización Bluetooth OBD-II", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                        Text("Automática", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }
                    HorizontalDivider(color = Slate200)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Versión de ShiftIQ", style = MaterialTheme.typography.bodyMedium, color = Slate700)
                        Text("v1.0.0 (B2C)", style = MaterialTheme.typography.labelMedium, color = Slate500)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSettingsModal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(spacing.radiusMedium)
                ) {
                    Text("Aceptar", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(spacing.radiusLarge),
            containerColor = ShiftIQSurfaceLight
        )
    }
}
