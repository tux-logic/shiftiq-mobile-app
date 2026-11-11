package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Primary
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBackgroundLight
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSurfaceLight
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQWarning
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate50
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
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.WorkshopItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkshopsScreen(
    onNavigateBack: (() -> Unit)? = null,
    viewModel: WorkshopsViewModel = hiltViewModel()
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
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Talleres Certificados",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Red asistida por ShiftIQ con bahías en vivo",
                                color = Slate400,
                                fontSize = 12.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(TelematicsGreen.copy(alpha = 0.2f))
                                .border(1.dp, TelematicsGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(TelematicsGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "GPS ACTIVO",
                                    color = TelematicsGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
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
                    // Search bar
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::onSearchQueryChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Buscar por taller, distrito o servicio...",
                                color = Slate400,
                                fontSize = 13.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Slate500
                            )
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Limpiar",
                                        tint = Slate500
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = ShiftIQSurfaceLight,
                            unfocusedContainerColor = ShiftIQSurfaceLight,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Slate200
                        ),
                        singleLine = true
                    )
                }

                item {
                    // Filter Chips Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WorkshopFilter.values().forEach { filter ->
                            val isSelected = uiState.selectedFilter == filter
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.onFilterSelected(filter) },
                                label = {
                                    Text(
                                        text = filter.label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryBlue,
                                    selectedLabelColor = Color.White,
                                    containerColor = ShiftIQSurfaceLight,
                                    labelColor = Slate700
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) PrimaryBlue else Slate300
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                        }
                    }
                }

                item {
                    // Vector Map Preview Card
                    VectorMapCard(workshopCount = uiState.filteredWorkshops.size)
                }

                items(uiState.filteredWorkshops, key = { it.id }) { workshop ->
                    WorkshopCard(
                        workshop = workshop,
                        onBookClick = { viewModel.onSelectWorkshopForBooking(workshop) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Modal BottomSheet for Booking
    if (uiState.selectedWorkshopForBooking != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val workshop = uiState.selectedWorkshopForBooking!!

        ModalBottomSheet(
            onDismissRequest = viewModel::onDismissBookingDialog,
            sheetState = sheetState,
            containerColor = ShiftIQSurfaceLight
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Reservar Cita en Bahía",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = workshop.name,
                    fontSize = 13.sp,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Vehicle Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate100)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = Slate700,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "${uiState.selectedVehicle?.brand ?: "Kia"} ${uiState.selectedVehicle?.model ?: "Rio"} • ${uiState.selectedVehicle?.plateNumber ?: "ABC-123"}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate800
                            )
                            Text(
                                text = "VIN: ${uiState.selectedVehicle?.vin ?: "9BWCA21102P012984"}",
                                fontSize = 11.sp,
                                color = Slate500
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Date Selection
                Text(
                    text = "Selecciona la Fecha",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate700
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Hoy, 12 Nov", "Mañana, 13 Nov", "Jueves, 14 Nov").forEach { date ->
                        val isSelected = uiState.selectedDate == date
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PrimaryBlue else Slate100)
                                .border(1.dp, if (isSelected) PrimaryBlue else Slate300, RoundedCornerShape(10.dp))
                                .clickable { viewModel.onSelectDate(date) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = date,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Slate700
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Time Slot Selection
                Text(
                    text = "Turno en Bahía Disponible",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate700
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("3:30 PM", "4:15 PM", "5:00 PM").forEach { slot ->
                        val isSelected = uiState.selectedTimeSlot == slot
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PrimaryBlue else Slate100)
                                .border(1.dp, if (isSelected) PrimaryBlue else Slate300, RoundedCornerShape(10.dp))
                                .clickable { viewModel.onSelectTimeSlot(slot) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = slot,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Slate700
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Motivo / Alerta
                OutlinedTextField(
                    value = uiState.bookingReason,
                    onValueChange = viewModel::onBookingReasonChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Motivo / Diagnóstico") },
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Slate300
                    ),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Free guarantee note
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(TelematicsGreen.copy(alpha = 0.08f))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = TelematicsGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sin cargo por reserva. Pagas en el taller al finalizar el servicio.",
                            fontSize = 11.sp,
                            color = Slate700
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // CTA Button
                Button(
                    onClick = viewModel::confirmBooking,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    enabled = !uiState.isBookingLoading
                ) {
                    if (uiState.isBookingLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Confirmar Reserva Asistida",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    // Success Voucher Dialog (Figura 308a Paso 4)
    if (uiState.bookingSuccessVoucher != null) {
        val voucher = uiState.bookingSuccessVoucher!!

        AlertDialog(
            onDismissRequest = viewModel::onDismissVoucherDialog,
            confirmButton = {
                Button(
                    onClick = viewModel::onDismissVoucherDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Aceptar y Ver en Inicio", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = viewModel::onDismissVoucherDialog,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Abrir con Waze", color = PrimaryBlue)
                    }
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(TelematicsGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = TelematicsGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "¡Cita Confirmada!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Tu bahía ha sido reservada con atención prioritaria.",
                        fontSize = 12.sp,
                        color = Slate600
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Slate50),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Código:", fontSize = 11.sp, color = Slate500)
                                Text(
                                    voucher.appointmentCode,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Taller:", fontSize = 11.sp, color = Slate500)
                                Text(
                                    voucher.workshopName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate800,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Fecha y Hora:", fontSize = 11.sp, color = Slate500)
                                Text(voucher.scheduledDateTime, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate800)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Asignación:", fontSize = 11.sp, color = Slate500)
                                Text(voucher.bayNumber, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TelematicsGreen)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // QR Code Canvas representation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .border(1.dp, Slate200, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        SchematicQrCanvas()
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = ShiftIQSurfaceLight
        )
    }
}

@Composable
fun WorkshopCard(
    workshop: WorkshopItem,
    onBookClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ShiftIQSurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Name & Certified Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = workshop.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = Slate400,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${workshop.district} • a ${workshop.distanceKm} km",
                            fontSize = 12.sp,
                            color = Slate500
                        )
                    }
                }

                if (workshop.isCertifiedShiftIQ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PrimaryBlue.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = PrimaryBlue,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Certificado",
                                color = PrimaryBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Rating & Free bay status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = ShiftIQWarning,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${workshop.rating}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = " (${workshop.reviewCount} opiniones)",
                        fontSize = 11.sp,
                        color = Slate500
                    )
                }

                if (workshop.hasFreeBaysToday) {
                    Text(
                        text = "● Bahía libre hoy",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TelematicsGreen
                    )
                }
            }

            // Soluciona alerta específica chip si aplica
            if (workshop.linkedIssue != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(TelematicsGreen.copy(alpha = 0.08f))
                        .border(1.dp, TelematicsGreen.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Especialista en resolver tu código: ${workshop.linkedIssue}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = TelematicsGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Divider line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Slate100)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Row: Price estimate + Next Slot + CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Estimado S/. ${workshop.estimatedCostMinPen.toInt()} - ${workshop.estimatedCostMaxPen.toInt()}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate800
                    )
                    Text(
                        text = "Turno: ${workshop.nextAvailableSlot}",
                        fontSize = 11.sp,
                        color = if (workshop.hasFreeBaysToday) TelematicsGreen else Slate500,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = onBookClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text(
                        text = "Reservar Cita",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun VectorMapCard(workshopCount: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Cartography Canvas background
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Map background
                drawRect(color = Color(0xFFE8EEF5))

                // Roads (light grey/white lines)
                val roadColor = Color.White
                val roadStroke = Stroke(width = 12f)
                val highwayStroke = Stroke(width = 18f)

                // Coastline / Sea curve on left
                val coastPath = Path().apply {
                    moveTo(0f, 0f)
                    cubicTo(w * 0.1f, h * 0.3f, w * 0.05f, h * 0.7f, 0f, h)
                    close()
                }
                drawPath(coastPath, color = Color(0xFFCCE4F7))

                // Arterial Highway
                drawLine(
                    color = Color(0xFFFFD54F),
                    start = Offset(0f, h * 0.35f),
                    end = Offset(w, h * 0.55f),
                    strokeWidth = 14f
                )

                // Secondary roads
                drawLine(roadColor, Offset(w * 0.25f, 0f), Offset(w * 0.35f, h), strokeWidth = 10f)
                drawLine(roadColor, Offset(w * 0.65f, 0f), Offset(w * 0.55f, h), strokeWidth = 10f)
                drawLine(roadColor, Offset(w * 0.1f, h * 0.75f), Offset(w, h * 0.85f), strokeWidth = 10f)

                // User Location (Pulsing Blue Pin)
                val userPos = Offset(w * 0.42f, h * 0.48f)
                drawCircle(color = Color(0x330F62FE), radius = 24f, center = userPos)
                drawCircle(color = Color.White, radius = 10f, center = userPos)
                drawCircle(color = Color(0xFF0F62FE), radius = 6f, center = userPos)

                // Workshop Pins (Green Pins)
                val p1 = Offset(w * 0.52f, h * 0.42f) // Taller Silva (1.2 km)
                drawCircle(color = Color(0xFF10B981), radius = 9f, center = p1)
                drawCircle(color = Color.White, radius = 4f, center = p1)

                val p2 = Offset(w * 0.68f, h * 0.60f) // AutoService Express
                drawCircle(color = Color(0xFF10B981), radius = 9f, center = p2)
                drawCircle(color = Color.White, radius = 4f, center = p2)

                val p3 = Offset(w * 0.75f, h * 0.30f) // Precision Motors
                drawCircle(color = Color(0xFF10B981), radius = 9f, center = p3)
                drawCircle(color = Color.White, radius = 4f, center = p3)
            }

            // Overlay Badge
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.BottomStart)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.95f))
                    .border(1.dp, Slate200, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$workshopCount talleres cercanos con bahías",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate800
                    )
                }
            }
        }
    }
}

@Composable
fun SchematicQrCanvas() {
    Canvas(modifier = Modifier.size(80.dp)) {
        val step = size.width / 7
        val dark = Color(0xFF0F172A)

        // Corner 1
        drawRect(dark, Offset(0f, 0f), Size(step * 2.5f, step * 2.5f))
        drawRect(Color.White, Offset(step * 0.5f, step * 0.5f), Size(step * 1.5f, step * 1.5f))
        drawRect(dark, Offset(step * 0.8f, step * 0.8f), Size(step * 0.9f, step * 0.9f))

        // Corner 2
        drawRect(dark, Offset(size.width - step * 2.5f, 0f), Size(step * 2.5f, step * 2.5f))
        drawRect(Color.White, Offset(size.width - step * 2f, step * 0.5f), Size(step * 1.5f, step * 1.5f))
        drawRect(dark, Offset(size.width - step * 1.7f, step * 0.8f), Size(step * 0.9f, step * 0.9f))

        // Corner 3
        drawRect(dark, Offset(0f, size.height - step * 2.5f), Size(step * 2.5f, step * 2.5f))
        drawRect(Color.White, Offset(step * 0.5f, size.height - step * 2f), Size(step * 1.5f, step * 1.5f))
        drawRect(dark, Offset(step * 0.8f, size.height - step * 1.7f), Size(step * 0.9f, step * 0.9f))

        // Center pixel patterns
        drawRect(dark, Offset(step * 3f, step * 3f), Size(step, step))
        drawRect(dark, Offset(step * 4.5f, step * 3f), Size(step * 0.8f, step))
        drawRect(dark, Offset(step * 3.2f, step * 4.5f), Size(step, step * 0.9f))
        drawRect(dark, Offset(step * 4.8f, step * 4.8f), Size(step, step))
    }
}
