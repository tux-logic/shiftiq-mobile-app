package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.diagnostic

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.SeverityAmber
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBackgroundLight
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSurfaceLight
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate100
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate300
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate400
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate600
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate700
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate800
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.TelematicsGreen

@Composable
fun DiagnosticScreen(
    onNavigateToWorkshops: () -> Unit,
    viewModel: DiagnosticViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = ShiftIQBackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // Header Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Navy900)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Diagnóstico OBD-II",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Protocolo ISO 15765-4 CAN",
                            color = Slate400,
                            fontSize = 12.sp
                        )
                    }

                    // Dongle badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(TelematicsGreen.copy(alpha = 0.2f))
                            .border(1.dp, TelematicsGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(TelematicsGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = uiState.dongleName,
                                color = TelematicsGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))

                    // Scanner Action Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = ShiftIQSurfaceLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Escáner en Tiempo Real",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900
                                    )
                                    Text(
                                        text = "Lee códigos DTC activos y pendientes del ECU",
                                        fontSize = 12.sp,
                                        color = Slate500
                                    )
                                }

                                Button(
                                    onClick = viewModel::startDiagnosticScan,
                                    enabled = !uiState.isScanning,
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                ) {
                                    if (uiState.isScanning) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Escanear", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            if (uiState.isScanning) {
                                Spacer(modifier = Modifier.height(12.dp))
                                LinearProgressIndicator(
                                    progress = { uiState.scanProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = PrimaryBlue,
                                    trackColor = Slate200
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Interrogando módulos ECU... ${(uiState.scanProgress * 100).toInt()}%",
                                    fontSize = 11.sp,
                                    color = Slate500
                                )
                            }
                        }
                    }
                }

                // Active DTC Alert or Positive State
                item {
                    if (uiState.activeDtcAlert != null) {
                        val dtc = uiState.activeDtcAlert!!
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = ShiftIQSurfaceLight),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, SeverityAmber),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(SeverityAmber.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = null,
                                                tint = SeverityAmber,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = dtc.code,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Slate900
                                            )
                                            Text(
                                                text = "Sensor O2 (Banco 1, Sensor 1)",
                                                fontSize = 12.sp,
                                                color = Slate600
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(SeverityAmber.copy(alpha = 0.15f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Severidad: ${dtc.severity}",
                                            color = SeverityAmber,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Didactic Explanation
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Slate100)
                                        .padding(12.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "Diagnóstico en Lenguaje Simple (US023)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryBlue
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = dtc.description,
                                            fontSize = 12.sp,
                                            color = Slate700,
                                            lineHeight = 17.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Impacto Estimado", fontSize = 11.sp, color = Slate500)
                                        Text("+8% Consumo Combustible", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate800)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Revisión Sugerida", fontSize = 11.sp, color = Slate500)
                                        Text("En próximos 500 km", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate800)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = onNavigateToWorkshops,
                                        modifier = Modifier.weight(1.5f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                    ) {
                                        Text("Agendar en Taller", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = viewModel::clearDtcCodes,
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Borrar Código", fontSize = 11.sp, color = Slate700)
                                    }
                                }
                            }
                        }
                    } else {
                        // Positive Green Banner
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = TelematicsGreen.copy(alpha = 0.08f)),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, TelematicsGreen.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(18.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(TelematicsGreen.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = TelematicsGreen,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "Vehículo Saludable: 0 Fallas",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TelematicsGreen
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Todos los subsistemas ECU operan dentro de tolerancia.",
                                        fontSize = 12.sp,
                                        color = Slate600
                                    )
                                }
                            }
                        }
                    }
                }

                // Extended Live Telemetry Monitors Header
                item {
                    Text(
                        text = "Sensores en Vivo (Live Data)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                }

                item {
                    // 2x3 Grid of Live Sensors
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SensorCard(
                                title = "RPM Motor",
                                value = "${uiState.telemetry.rpm}",
                                unit = "RPM",
                                status = "Normal (Ralentí)",
                                isOptimal = true,
                                modifier = Modifier.weight(1f)
                            )
                            SensorCard(
                                title = "Refrigerante",
                                value = "${uiState.telemetry.temperatureCelsius}",
                                unit = "°C",
                                status = "Óptimo (88°C)",
                                isOptimal = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SensorCard(
                                title = "Batería / Alt.",
                                value = "${uiState.telemetry.batteryVoltage}",
                                unit = "V",
                                status = "Alternador Activo",
                                isOptimal = true,
                                modifier = Modifier.weight(1f)
                            )
                            SensorCard(
                                title = "Carga de Motor",
                                value = "${uiState.engineLoadPercent}",
                                unit = "%",
                                status = "Baja Carga",
                                isOptimal = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SensorCard(
                                title = "Presión MAP",
                                value = "${uiState.intakePressureKpa}",
                                unit = "kPa",
                                status = "Presión de Vacío",
                                isOptimal = true,
                                modifier = Modifier.weight(1f)
                            )
                            SensorCard(
                                title = "Ajuste STFT",
                                value = "+${uiState.fuelTrimPercent}",
                                unit = "%",
                                status = "En Límite",
                                isOptimal = uiState.activeDtcAlert == null,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun SensorCard(
    title: String,
    value: String,
    unit: String,
    status: String,
    isOptimal: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ShiftIQSurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontSize = 11.sp,
                color = Slate500,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    fontSize = 12.sp,
                    color = Slate500,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "● $status",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isOptimal) TelematicsGreen else SeverityAmber
            )
        }
    }
}
