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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.diagnostic.DiagnosticScreen
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.GarageScreen
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.DriverHomeScreen
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.DriverHomeViewModel
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.WorkshopsScreen
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
    driverHomeViewModel: DriverHomeViewModel = hiltViewModel()
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showLogoutConfirmation by rememberSaveable { mutableStateOf(false) }
    var showProfileModal by rememberSaveable { mutableStateOf(false) }
    var showSettingsModal by rememberSaveable { mutableStateOf(false) }

    val homeUiState by driverHomeViewModel.uiState.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = ShiftIQSurfaceLight,
                drawerShape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp),
                modifier = Modifier.width(310.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    // =====================================================================
                    // HEADER DEL NAVIGATION DRAWER (NAVY900)
                    // =====================================================================
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Navy900)
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .padding(20.dp)
                    ) {
                        Column {
                            // Avatar circular grande con la inicial dinámica del conductor
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = homeUiState.avatarInitial,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = homeUiState.driverName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            if (homeUiState.driverEmail.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = homeUiState.driverEmail,
                                    fontSize = 12.sp,
                                    color = Slate400
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Rol del Conductor con Check Verde
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(TelematicsGreen.copy(alpha = 0.2f))
                                    .border(1.dp, TelematicsGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = TelematicsGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = homeUiState.driverRole,
                                        color = TelematicsGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // =====================================================================
                    // OPCIONES DEL MENÚ LATERAL
                    // =====================================================================
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryBlue) },
                            label = { Text("Mi Perfil", fontWeight = FontWeight.Medium, fontSize = 14.sp) },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                showProfileModal = true
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = PrimaryBlue) },
                            label = { Text("Mis Vehículos", fontWeight = FontWeight.Medium, fontSize = 14.sp) },
                            selected = selectedTab == 3,
                            onClick = {
                                selectedTab = 3
                                scope.launch { drawerState.close() }
                            },
                            shape = RoundedCornerShape(10.dp)
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Build, contentDescription = null, tint = PrimaryBlue) },
                            label = { Text("Diagnóstico OBD-II", fontWeight = FontWeight.Medium, fontSize = 14.sp) },
                            selected = selectedTab == 1,
                            onClick = {
                                selectedTab = 1
                                scope.launch { drawerState.close() }
                            },
                            shape = RoundedCornerShape(10.dp)
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Place, contentDescription = null, tint = PrimaryBlue) },
                            label = { Text("Talleres Certificados", fontWeight = FontWeight.Medium, fontSize = 14.sp) },
                            selected = selectedTab == 2,
                            onClick = {
                                selectedTab = 2
                                scope.launch { drawerState.close() }
                            },
                            shape = RoundedCornerShape(10.dp)
                        )

                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = Slate700) },
                            label = { Text("Configuración", fontWeight = FontWeight.Medium, fontSize = 14.sp) },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                showSettingsModal = true
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                        )
                    }

                    // =====================================================================
                    // PIE DE PÁGINA CON BOTÓN DE CERRAR SESIÓN
                    // =====================================================================
                    HorizontalDivider(color = Slate200, thickness = 1.dp)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    scope.launch { drawerState.close() }
                                    showLogoutConfirmation = true
                                }
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar Sesión",
                                tint = SeverityCrimson,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Cerrar Sesión",
                                color = SeverityCrimson,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                // Bottom Navigation Bar estilizada idéntica al mockup
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 6.dp,
                    modifier = Modifier.height(72.dp)
                ) {
                    B2CTab.values().forEachIndexed { index, tab ->
                        val isSelected = selectedTab == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { selectedTab = index },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryBlue,
                                selectedTextColor = PrimaryBlue,
                                indicatorColor = PrimaryBlue.copy(alpha = 0.12f),
                                unselectedIconColor = Slate400,
                                unselectedTextColor = Slate500
                            )
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                when (selectedTab) {
                    0 -> DriverHomeScreen(
                        viewModel = driverHomeViewModel,
                        onNavigateToWorkshops = { selectedTab = 2 },
                        onNavigateToDiagnostic = { selectedTab = 1 },
                        onNavigateToGarage = { selectedTab = 3 },
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                    1 -> DiagnosticScreen(
                        onNavigateToWorkshops = { selectedTab = 2 }
                    )
                    2 -> WorkshopsScreen()
                    3 -> GarageScreen(
                        onAddVehicle = {},
                        onNavigateToProfile = { showProfileModal = true }
                    )
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
                    fontSize = 18.sp,
                    color = Slate900
                )
            },
            text = {
                Text(
                    text = "Se cerrará tu sesión activa de conductor en este dispositivo. Tendrás que volver a ingresar tu correo y contraseña.",
                    fontSize = 13.sp,
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
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutConfirmation = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancelar", color = Slate700)
                }
            },
            shape = RoundedCornerShape(16.dp),
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
                            .size(36.dp)
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
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Mi Perfil de Conductor",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Slate900
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Slate100)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("Nombre Completo:", fontSize = 11.sp, color = Slate500)
                            Text(homeUiState.driverName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate900)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Correo Electrónico:", fontSize = 11.sp, color = Slate500)
                            Text(homeUiState.driverEmail.ifBlank { "No registrado" }, fontSize = 13.sp, color = Slate700)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Rol Asignado:", fontSize = 11.sp, color = Slate500)
                            Text(homeUiState.driverRole, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = PrimaryBlue)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showProfileModal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cerrar", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(16.dp),
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
                    fontSize = 18.sp,
                    color = Slate900
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Notificaciones Push DTC", fontSize = 13.sp, color = Slate700)
                        Text("Activadas", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TelematicsGreen)
                    }
                    HorizontalDivider(color = Slate200)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sincronización Bluetooth OBD-II", fontSize = 13.sp, color = Slate700)
                        Text("Automática", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }
                    HorizontalDivider(color = Slate200)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Versión de ShiftIQ", fontSize = 13.sp, color = Slate700)
                        Text("v1.0.0 (B2C)", fontSize = 12.sp, color = Slate500)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSettingsModal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Aceptar", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = ShiftIQSurfaceLight
        )
    }
}
