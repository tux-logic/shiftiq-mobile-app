package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate50
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate800
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.TelematicsGreen
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.B2CVehicleItem
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.components.ActiveServiceCard
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.components.CarSilhouetteCanvas
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.components.DtcAlertBanner
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.components.HealthRingCanvas
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.components.LiveTelemetryGrid

@Composable
fun DriverHomeScreen(
    viewModel: DriverHomeViewModel = hiltViewModel(),
    onNavigateToWorkshops: () -> Unit,
    onNavigateToDiagnostic: () -> Unit,
    onNavigateToGarage: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
    ) {
        // =========================================================================
        // TOP BAR CON IDENTIDAD CORPORATIVA (NAVY900)
        // =========================================================================
        val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Navy900)
                .padding(top = statusBarPadding, start = 16.dp, end = 16.dp, bottom = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chip Izquierdo: OBD2 Conectado
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(TelematicsGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "OBD2 CONECTADO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }

                // Título Central
                Text(
                    text = "Home Dashboard",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Perfil de Usuario
                IconButton(
                    onClick = onNavigateToProfile,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Mi Perfil",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // =========================================================================
        // CONTENIDO PRINCIPAL SCROLLABLE
        // =========================================================================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Card de Saludo y Estado Conductor
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Slate200),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Avatar Circular con Inicial
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEFF6FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            val initial = uiState.driverName.firstOrNull()?.uppercase() ?: "R"
                            Text(
                                text = initial,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryBlue
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "¡Hola, ${uiState.driverName}!",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Conductor Principal",
                                fontSize = 12.sp,
                                color = Slate500
                            )
                        }
                    }

                    // Badge verde OBD-II Conectado
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFECFDF5))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(TelematicsGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OBD-II Conectado",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TelematicsGreen
                        )
                    }
                }
            }

            // 2. Tarjeta Dinámica de Servicio Activo (u Orden de Trabajo Vacía)
            ActiveServiceCard(
                serviceData = uiState.activeService,
                onViewLiveOrder = { _ -> onNavigateToWorkshops() },
                onNavigateToWorkshops = onNavigateToWorkshops
            )

            // 3. Tarjeta de Salud Vehicular y Selector de Vehículo
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Slate200),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Selector de Vehículo
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.toggleVehicleSelector() }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                val vehicleTitle = uiState.selectedVehicle?.let {
                                    "${it.brand} ${it.model} ${it.year} • Placa ${it.plateNumber}"
                                } ?: "Kia Rio 2019 • Placa ABC-123"

                                Text(
                                    text = vehicleTitle,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate800
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Cambiar vehículo",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryBlue
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Menú desplegable si hay múltiples vehículos
                        DropdownMenu(
                            expanded = uiState.isVehicleSelectorOpen,
                            onDismissRequest = { viewModel.toggleVehicleSelector() }
                        ) {
                            uiState.availableVehicles.forEach { vehicle ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${vehicle.brand} ${vehicle.model} (${vehicle.plateNumber})",
                                            fontWeight = if (vehicle.id == uiState.selectedVehicle?.id) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = { viewModel.onSelectVehicle(vehicle) },
                                    trailingIcon = {
                                        if (vehicle.id == uiState.selectedVehicle?.id) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryBlue)
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Anillo de Salud en Canvas (94% Score)
                    HealthRingCanvas(score = uiState.vehicleHealth.score)

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = uiState.vehicleHealth.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = uiState.vehicleHealth.description,
                        fontSize = 12.sp,
                        color = Slate500
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Ilustración Vectorial / Canvas Local del Vehículo
                    CarSilhouetteCanvas()
                }
            }

            // 4. Grilla 2x2 de Telemetría en Vivo (con variación reactiva cada 2s)
            LiveTelemetryGrid(telemetry = uiState.liveTelemetry)

            // 5. Alerta DTC (o Banner Verde Positivo si no hay fallas)
            DtcAlertBanner(
                alert = uiState.activeDtcAlert,
                onViewDiagnosticDetail = onNavigateToDiagnostic
            )

            // 6. Botón de Acción Principal: Iniciar Escaneo Completo
            Button(
                onClick = { viewModel.onStartFullScan() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Navy900,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Iniciar Escaneo Completo",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Modal de progreso durante el escaneo OBD-II
    if (uiState.isScanning) {
        Dialog(onDismissRequest = {}) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = PrimaryBlue,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Escaneo OBD-II en Curso",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.scanProgressMessage ?: "Consultando sensores...",
                        fontSize = 13.sp,
                        color = Slate500
                    )
                }
            }
        }
    }
}
