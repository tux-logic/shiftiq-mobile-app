package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Navy900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQWarning
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.WorkshopItem

@Composable
fun WorkshopsRoute(
    onNavigateToBooking: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: WorkshopsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    WorkshopsScreen(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onFilterSelected = viewModel::onFilterSelected,
        onNavigateToBooking = onNavigateToBooking,
        modifier = modifier
    )
}

@Composable
fun WorkshopsScreen(
    onNavigateToBooking: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: WorkshopsViewModel = hiltViewModel()
) {
    WorkshopsRoute(
        onNavigateToBooking = onNavigateToBooking,
        modifier = modifier,
        viewModel = viewModel
    )
}

@Composable
fun WorkshopsScreen(
    uiState: WorkshopsUiState,
    onSearchQueryChange: (String) -> Unit,
    onFilterSelected: (WorkshopFilter) -> Unit,
    onNavigateToBooking: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        WorkshopsTopBar(statusBarPadding = statusBarPadding)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.medium, vertical = spacing.smallMedium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            WorkshopSearchBar(
                query = uiState.searchQuery,
                onQueryChange = onSearchQueryChange
            )
            WorkshopFilterChips(
                selectedFilter = uiState.selectedFilter,
                onSelectFilter = onFilterSelected
            )
            VectorGpsMapCard()
            WorkshopsCatalogList(
                workshops = uiState.filteredWorkshops.ifEmpty { uiState.allWorkshops },
                onNavigateToBooking = onNavigateToBooking
            )
            Spacer(modifier = Modifier.height(spacing.medium))
        }
    }
}

@Composable
private fun WorkshopsTopBar(
    statusBarPadding: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Navy900)
            .padding(
                top = statusBarPadding,
                start = spacing.medium,
                end = spacing.medium,
                bottom = spacing.smallMedium
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Buscar Talleres Afiliados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Talleres certificados con bahías activas",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(spacing.radiusSmall))
                .background(ShiftIQSuccess.copy(alpha = 0.2f))
                .padding(horizontal = spacing.small, vertical = spacing.extraSmall)
        ) {
            Text(
                text = "OBD2 ACTIVO",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = ShiftIQSuccess
            )
        }
    }
}

@Composable
private fun WorkshopSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Buscar por distrito, especialidad o nombre...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(spacing.iconMedium)
                )
            },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(spacing.small))
        IconButton(
            onClick = {},
            modifier = Modifier
                .size(spacing.badgeHeight + spacing.smallMedium)
                .clip(RoundedCornerShape(spacing.radiusMedium))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filtros",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(spacing.iconMedium)
            )
        }
    }
}

@Composable
private fun WorkshopFilterChips(
    selectedFilter: WorkshopFilter,
    onSelectFilter: (WorkshopFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        WorkshopFilter.values().forEach { filter ->
            FilterChip(
                selected = filter == selectedFilter,
                onClick = { onSelectFilter(filter) },
                label = { Text(filter.label, style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
private fun VectorGpsMapCard(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(spacing.mapHeight),
        shape = RoundedCornerShape(spacing.radiusLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        border = BorderStroke(spacing.strokeThin, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val lineColor = Color.LightGray.copy(alpha = 0.5f)
                drawLine(lineColor, Offset(0f, h * 0.35f), Offset(w, h * 0.35f), strokeWidth = 3f)
                drawLine(lineColor, Offset(0f, h * 0.70f), Offset(w, h * 0.70f), strokeWidth = 3f)
                drawLine(lineColor, Offset(w * 0.30f, 0f), Offset(w * 0.30f, h), strokeWidth = 3f)
                drawLine(lineColor, Offset(w * 0.70f, 0f), Offset(w * 0.70f, h), strokeWidth = 3f)
                drawCircle(Color(0xFF0F62FE), radius = 14f, center = Offset(w * 0.45f, h * 0.50f))
                drawCircle(Color(0xFF0F62FE), radius = 28f, center = Offset(w * 0.45f, h * 0.50f), style = Stroke(width = 3f))
                drawCircle(Color(0xFFBA1A1A), radius = 12f, center = Offset(w * 0.25f, h * 0.30f))
                drawCircle(Color(0xFFBA1A1A), radius = 12f, center = Offset(w * 0.75f, h * 0.65f))
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(spacing.smallMedium)
                    .clip(RoundedCornerShape(spacing.radiusSmall))
                    .background(Navy900)
                    .padding(horizontal = spacing.small, vertical = spacing.extraSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Navigation, contentDescription = null, tint = Color.White, modifier = Modifier.size(spacing.iconSmall))
                Spacer(modifier = Modifier.width(spacing.extraSmall))
                Text("GPS Activo: San Miguel", style = MaterialTheme.typography.labelSmall, color = Color.White)
            }
        }
    }
}

@Composable
private fun WorkshopsCatalogList(
    workshops: List<WorkshopItem>,
    onNavigateToBooking: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        workshops.forEach { workshop ->
            WorkshopCardItem(workshop = workshop, onBook = { onNavigateToBooking(workshop.id) })
        }
    }
}

@Composable
private fun WorkshopCardItem(
    workshop: WorkshopItem,
    onBook: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.radiusLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(spacing.strokeThin, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = spacing.elevationSmall)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium)
        ) {
            WorkshopCardHeader(workshop = workshop)
            if (!workshop.linkedIssue.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(spacing.small))
                DtcAlignmentBanner(issue = workshop.linkedIssue)
            }
            Spacer(modifier = Modifier.height(spacing.smallMedium))
            WorkshopCardPricingRow(minPen = workshop.estimatedCostMinPen, maxPen = workshop.estimatedCostMaxPen, slot = workshop.nextAvailableSlot)
            Spacer(modifier = Modifier.height(spacing.smallMedium))
            WorkshopCardActions(onBook = onBook)
        }
    }
}

@Composable
private fun WorkshopCardHeader(
    workshop: WorkshopItem,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = workshop.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (workshop.isCertifiedShiftIQ) {
                    Spacer(modifier = Modifier.width(spacing.extraSmall))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Certificado",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(spacing.iconSmall)
                    )
                }
            }
            Text(
                text = "${workshop.district} • ${workshop.distanceKm} km",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = null, tint = ShiftIQWarning, modifier = Modifier.size(spacing.iconMedium))
            Spacer(modifier = Modifier.width(spacing.micro))
            Text(
                text = workshop.rating.toString(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun DtcAlignmentBanner(
    issue: String,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(spacing.radiusSmall))
            .background(ShiftIQWarning.copy(alpha = 0.12f))
            .padding(horizontal = spacing.small, vertical = spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Info, contentDescription = null, tint = ShiftIQWarning, modifier = Modifier.size(spacing.iconSmall))
        Spacer(modifier = Modifier.width(spacing.extraSmall))
        Text(
            text = "Especialista para resolver: $issue",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = ShiftIQWarning
        )
    }
}

@Composable
private fun WorkshopCardPricingRow(
    minPen: Double,
    maxPen: Double,
    slot: String,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "Presupuesto Estimado", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "S/ $minPen - S/ $maxPen", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurface)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = "Próximo Turno", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = slot, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun WorkshopCardActions(
    onBook: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        OutlinedButton(
            onClick = {},
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(spacing.radiusMedium)
        ) {
            Text("Ver Perfil", style = MaterialTheme.typography.labelMedium)
        }
        Button(
            onClick = onBook,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(spacing.radiusMedium),
            colors = ButtonDefaults.buttonColors(containerColor = Navy900, contentColor = Color.White)
        ) {
            Text("Reservar Cita", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@PreviewLightDark
@PreviewFontScale
@Composable
private fun WorkshopsScreenPreview() {
    ShiftIQTheme {
        WorkshopsScreen(
            uiState = WorkshopsUiState(
                allWorkshops = listOf(
                    WorkshopItem(
                        id = "w1",
                        name = "Taller Automotriz Silva",
                        rating = 4.8,
                        reviewCount = 115,
                        district = "San Miguel",
                        distanceKm = 1.2,
                        isCertifiedShiftIQ = true,
                        hasFreeBaysToday = true,
                        estimatedCostMinPen = 140.0,
                        estimatedCostMaxPen = 180.0,
                        nextAvailableSlot = "Hoy, 3:30 PM",
                        linkedIssue = "Sensor O2 Banco 1"
                    )
                )
            ),
            onSearchQueryChange = {},
            onFilterSelected = {},
            onNavigateToBooking = {}
        )
    }
}
