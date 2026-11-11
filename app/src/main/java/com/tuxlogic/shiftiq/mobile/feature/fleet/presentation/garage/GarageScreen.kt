package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.SeverityAmber
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate300
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate400
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate50
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate700
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate800
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.TelematicsGreen
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.B2CVehicleItem
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.MaintenanceHistoryItem
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.components.CarSilhouetteCanvas

@Composable
fun GarageScreen(
    viewModel: GarageViewModel = hiltViewModel(),
    onAddVehicle: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate50)
    ) {
        // =========================================================================
        // TOP BAR
        // =========================================================================
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
                Text(
                    text = "Mi Garaje - Historial",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

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
        // CONTENIDO SCROLLABLE
        // =========================================================================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cabecera: Flota Personal / Vehículos Registrados
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FLOTA PERSONAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate500,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Vehículos Registrados",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                }

                OutlinedButton(
                    onClick = onAddVehicle,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, PrimaryBlue),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Agregar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Listado de Vehículos
            uiState.vehicles.forEach { vehicle ->
                VehicleFleetCard(vehicle = vehicle)
            }

            // Espacio Disponible (Tarjeta con borde punteado)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = BorderStroke(1.dp, Slate300)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 18.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Espacio Disponible",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate500
                    )
                }
            }

            // =========================================================================
            // SECCIÓN: TELEMETRÍA EN TIEMPO REAL
            // =========================================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Telemetría en Tiempo Real",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                }
                Text(
                    text = "Sync: Hace 3m",
                    fontSize = 11.sp,
                    color = Slate500
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GarageMetricCard(
                    title = "Odómetro Actual",
                    value = "48, 120",
                    unit = "km",
                    subtitle = "+45 km esta semana",
                    subtitleColor = TelematicsGreen,
                    icon = Icons.Default.Edit,
                    modifier = Modifier.weight(1f)
                )
                GarageMetricCard(
                    title = "Próx. Servicio",
                    value = "50, 000",
                    unit = "km",
                    subtitle = "En 1,880 km",
                    subtitleColor = Slate500,
                    icon = Icons.Default.CalendarToday,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GarageMetricCard(
                    title = "Salud Batería",
                    value = "13.8",
                    unit = "V",
                    subtitle = "Estado Óptimo",
                    subtitleColor = TelematicsGreen,
                    icon = Icons.Default.ElectricBolt,
                    modifier = Modifier.weight(1f)
                )
                GarageMetricCard(
                    title = "Aceite Motor",
                    value = "78%",
                    unit = "vida",
                    subtitle = "Nivel Adecuado",
                    subtitleColor = Slate500,
                    icon = Icons.Default.Opacity,
                    modifier = Modifier.weight(1f)
                )
            }

            // =========================================================================
            // HISTORIAL DE INTERVENCIONES
            // =========================================================================
            Column {
                Text(
                    text = "Historial de Intervenciones",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Text(
                    text = "Registros certificados y facturas",
                    fontSize = 12.sp,
                    color = Slate500
                )
            }

            uiState.maintenanceHistory.forEach { item ->
                MaintenanceTimelineCard(item = item)
            }

            // Banner Inferior: Servicio fuera de red
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Slate200)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "¿Realizaste un servicio fuera de red?",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = "Registra factura y kilometraje manual",
                            fontSize = 11.sp,
                            color = Slate500
                        )
                    }

                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Navy900)
                    ) {
                        Text(text = "Añadir", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun VehicleFleetCard(vehicle: B2CVehicleItem) {
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
                .padding(16.dp)
        ) {
            // Fila Superior: Badges y Placa
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (vehicle.isMonitored) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFECFDF5))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(TelematicsGreen)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Monitoreo Activo",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TelematicsGreen
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFFBEB))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = SeverityAmber,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sin Dispositivo",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SeverityAmber
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = vehicle.plateNumber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Slate900
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre del auto
            Text(
                text = "${vehicle.brand} ${vehicle.model} ${vehicle.year}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Silueta / Ilustración
            CarSilhouetteCanvas()

            Spacer(modifier = Modifier.height(12.dp))

            // Pie de tarjeta
            if (vehicle.isMonitored) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OBD2 Enlazado",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0284C7)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF8FAFC))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${vehicle.batteryVoltage}V",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate800
                        )
                    }
                }
            } else {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, PrimaryBlue),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
                ) {
                    Icon(imageVector = Icons.Default.Link, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Vincular OBD2", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun GarageMetricCard(
    title: String,
    value: String,
    unit: String,
    subtitle: String,
    subtitleColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Slate200)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Slate500)
                Icon(imageVector = icon, contentDescription = null, tint = Slate400, modifier = Modifier.size(14.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Slate900)
                Spacer(modifier = Modifier.width(3.dp))
                Text(text = unit, fontSize = 11.sp, color = Slate500, modifier = Modifier.padding(bottom = 1.dp))
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = subtitleColor)
        }
    }
}

@Composable
private fun MaintenanceTimelineCard(item: MaintenanceHistoryItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Slate200)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.date,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
                Text(
                    text = "${String.format(java.util.Locale.US, "%,d", item.mileage)} km",
                    fontSize = 11.sp,
                    color = Slate500
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${item.workshopName} • ${item.description}",
                fontSize = 12.sp,
                color = Slate700,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Total Invertido", fontSize = 10.sp, color = Slate500)
                    Text(
                        text = "S/ ${String.format(java.util.Locale.US, "%.2f", item.totalCostPen)} PEN",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Slate900
                    )
                }

                OutlinedButton(
                    onClick = {},
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Slate300)
                ) {
                    Text(text = "PDF Comprobante", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate800)
                }
            }
        }
    }
}
