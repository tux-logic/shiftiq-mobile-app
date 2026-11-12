package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.detail

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.SeverityCrimson
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQWarning
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate100
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate700
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing

@Composable
fun WorkshopDetailRoute(
    workshopId: String,
    onNavigateBack: () -> Unit,
    onNavigateToBooking: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WorkshopDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WorkshopDetailScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToBooking = { onNavigateToBooking(workshopId) },
        onToggleFavorite = viewModel::toggleFavorite,
        modifier = modifier
    )
}

@Composable
fun WorkshopDetailScreen(
    uiState: WorkshopDetailUiState,
    onNavigateBack: () -> Unit,
    onNavigateToBooking: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            WorkshopDetailTopBar(
                title = (uiState as? WorkshopDetailUiState.Success)?.workshop?.name ?: "Detalle del Taller",
                isFavorite = (uiState as? WorkshopDetailUiState.Success)?.workshop?.isFavorite ?: false,
                onNavigateBack = onNavigateBack,
                onToggleFavorite = onToggleFavorite,
                statusBarPadding = statusBarPadding
            )

            when (uiState) {
                is WorkshopDetailUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                is WorkshopDetailUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize().padding(spacing.large), contentAlignment = Alignment.Center) {
                        Text(
                            text = uiState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is WorkshopDetailUiState.Success -> {
                    WorkshopDetailContent(
                        workshop = uiState.workshop,
                        onNavigateToBooking = onNavigateToBooking,
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = spacing.medium, vertical = spacing.smallMedium)
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkshopDetailTopBar(
    title: String,
    isFavorite: Boolean,
    onNavigateBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    statusBarPadding: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Navy900,
        shadowElevation = spacing.elevationSmall
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = statusBarPadding)
                .height(spacing.topBarHeight)
                .padding(horizontal = spacing.small),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                modifier = Modifier.weight(1f).padding(horizontal = spacing.small)
            )
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (isFavorite) SeverityCrimson else Color.White
                )
            }
        }
    }
}

@Composable
private fun WorkshopDetailContent(
    workshop: WorkshopDetailData,
    onNavigateToBooking: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        WorkshopHeroCard(workshop = workshop)
        WorkshopCompatibilityCard(notice = workshop.compatibilityNotice)
        WorkshopInfrastructureCard(
            activeBays = workshop.activeBaysCount,
            technicians = workshop.certifiedTechniciansCount
        )
        WorkshopSpecialtiesSection(specialties = workshop.specialties)
        Spacer(modifier = Modifier.height(spacing.small))
        Button(
            onClick = onNavigateToBooking,
            modifier = Modifier
                .fillMaxWidth()
                .height(spacing.buttonHeight),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(spacing.radiusMedium)
        ) {
            Icon(imageVector = Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(spacing.iconMedium))
            Spacer(modifier = Modifier.width(spacing.small))
            Text(text = "Agendar Cita en este Taller", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(spacing.large))
    }
}

@Composable
private fun WorkshopHeroCard(
    workshop: WorkshopDetailData,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(spacing.strokeThin, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = ShiftIQWarning, modifier = Modifier.size(spacing.iconMedium))
                    Spacer(modifier = Modifier.width(spacing.micro))
                    Text(text = "${workshop.rating}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Slate900)
                    Spacer(modifier = Modifier.width(spacing.extraSmall))
                    Text(text = "(${workshop.reviewCount} reseñas)", style = MaterialTheme.typography.bodySmall, color = Slate500)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(spacing.radiusSmall))
                        .background(ShiftIQSuccess.copy(alpha = 0.15f))
                        .padding(horizontal = spacing.small, vertical = spacing.micro)
                ) {
                    Text(text = "Abierto ahora", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = ShiftIQSuccess)
                }
            }

            Text(text = workshop.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Slate900)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Slate500, modifier = Modifier.size(spacing.iconSmall))
                Spacer(modifier = Modifier.width(spacing.extraSmall))
                Text(text = workshop.address, style = MaterialTheme.typography.bodySmall, color = Slate700)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = Slate500, modifier = Modifier.size(spacing.iconSmall))
                Spacer(modifier = Modifier.width(spacing.extraSmall))
                Text(text = workshop.openingHours, style = MaterialTheme.typography.bodySmall, color = Slate700)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = Slate500, modifier = Modifier.size(spacing.iconSmall))
                Spacer(modifier = Modifier.width(spacing.extraSmall))
                Text(text = workshop.phone, style = MaterialTheme.typography.bodySmall, color = Slate700)
            }
        }
    }
}

@Composable
private fun WorkshopCompatibilityCard(
    notice: String,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusMedium),
        colors = CardDefaults.cardColors(containerColor = Slate100),
        border = BorderStroke(spacing.strokeThin, Slate200)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(spacing.avatarSmall)
                    .clip(CircleShape)
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = Color.White, modifier = Modifier.size(spacing.iconMedium))
            }
            Spacer(modifier = Modifier.width(spacing.smallMedium))
            Column {
                Text(text = "Compatibilidad Certificada", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                Spacer(modifier = Modifier.height(spacing.micro))
                Text(text = notice, style = MaterialTheme.typography.bodySmall, color = Slate700)
            }
        }
    }
}

@Composable
private fun WorkshopInfrastructureCard(
    activeBays: Int,
    technicians: Int,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusMedium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(spacing.strokeThin, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "$activeBays Bahías Activas", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Slate900)
                Text(text = "Elevadores hidráulicos", style = MaterialTheme.typography.labelSmall, color = Slate500)
            }
            Box(modifier = Modifier.width(spacing.strokeThin).height(spacing.large).background(Slate200))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "$technicians Técnicos", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Slate900)
                Text(text = "Especialistas certificados", style = MaterialTheme.typography.labelSmall, color = Slate500)
            }
        }
    }
}

@Composable
private fun WorkshopSpecialtiesSection(
    specialties: List<WorkshopSpecialtyItem>,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        Text(text = "Servicios y Especialidades", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Slate900)

        specialties.forEach { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(spacing.radiusMedium),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(spacing.strokeThin, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(spacing.micro)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = item.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Slate900, modifier = Modifier.weight(1f))
                        Text(text = "S/ ${item.estimatedPricePen.toInt()}", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }
                    Text(text = item.description, style = MaterialTheme.typography.bodySmall, color = Slate500)
                    Spacer(modifier = Modifier.height(spacing.micro))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "⏱ ~${item.estimatedDurationMinutes} min", style = MaterialTheme.typography.labelSmall, color = Slate700)
                        Text(text = "🛡 Garantía ${item.warrantyDays} días", style = MaterialTheme.typography.labelSmall, color = ShiftIQSuccess)
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun WorkshopDetailScreenPreview() {
    ShiftIQTheme {
        WorkshopDetailScreen(
            uiState = WorkshopDetailUiState.Success(
                WorkshopDetailData(
                    id = "w1",
                    name = "Taller Automotriz Silva - San Miguel",
                    rating = 4.8,
                    reviewCount = 115,
                    address = "Av. La Marina 2450, San Miguel",
                    district = "San Miguel",
                    phone = "+51 987 654 321",
                    openingHours = "Lun - Sáb: 08:00 AM - 06:30 PM",
                    isOpenNow = true,
                    compatibilityNotice = "Scanner CAN-BUS y repuestos en almacén para tu vehículo.",
                    activeBaysCount = 3,
                    certifiedTechniciansCount = 4,
                    isFavorite = true,
                    specialties = listOf(
                        WorkshopSpecialtyItem("1", "Diagnóstico OBD-II", "Escaneo general", 80.0, 45, 30)
                    )
                )
            ),
            onNavigateBack = {},
            onNavigateToBooking = {},
            onToggleFavorite = {}
        )
    }
}
