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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
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
import androidx.compose.material3.Surface
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
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ObdTeal
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSuccess
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQWarning
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate100
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate700
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.spacing
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.WorkshopItem

@Composable
fun WorkshopsRoute(
    onNavigateToBooking: (String) -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToMyAppointments: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: WorkshopsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    WorkshopsScreen(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onFilterSelected = viewModel::onFilterSelected,
        onNavigateToBooking = onNavigateToBooking,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToMyAppointments = onNavigateToMyAppointments,
        modifier = modifier
    )
}

@Composable
fun WorkshopsScreen(
    onNavigateToBooking: (String) -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToMyAppointments: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: WorkshopsViewModel = hiltViewModel()
) {
    WorkshopsRoute(
        onNavigateToBooking = onNavigateToBooking,
        onNavigateToDetail = onNavigateToDetail,
        onNavigateToMyAppointments = onNavigateToMyAppointments,
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
    onNavigateToDetail: (String) -> Unit,
    onNavigateToMyAppointments: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        WorkshopsTopBar(
            onNavigateToMyAppointments = onNavigateToMyAppointments,
            statusBarPadding = statusBarPadding
        )

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
            WorkshopsCatalogListHeader(
                count = (uiState.filteredWorkshops.ifEmpty { uiState.allWorkshops }).size,
                onNavigateToMyAppointments = onNavigateToMyAppointments
            )
            WorkshopsCatalogList(
                workshops = uiState.filteredWorkshops.ifEmpty { uiState.allWorkshops },
                onNavigateToBooking = onNavigateToBooking,
                onNavigateToDetail = onNavigateToDetail
            )
            Spacer(modifier = Modifier.height(spacing.medium))
        }
    }
}

@Composable
private fun WorkshopsTopBar(
    onNavigateToMyAppointments: () -> Unit,
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
                .height(spacing.topBarHeight)
                .padding(horizontal = spacing.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(spacing.radiusFull))
                    .background(ObdTeal.copy(alpha = 0.2f))
                    .padding(horizontal = spacing.small, vertical = spacing.micro)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(spacing.indicatorHeight)
                            .clip(CircleShape)
                            .background(ObdTeal)
                    )
                    Spacer(modifier = Modifier.width(spacing.extraSmall))
                    Text(
                        text = "OBD2 CONECTADO",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ObdTeal
                    )
                }
            }

            Text(
                text = "Talleres Afiliados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            IconButton(onClick = onNavigateToMyAppointments) {
                Icon(
                    imageVector = Icons.Default.EventNote,
                    contentDescription = "Mis Citas",
                    tint = Color.White
                )
            }
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
            placeholder = { Text("Buscar taller por distrito o servicio...", style = MaterialTheme.typography.bodySmall) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(spacing.iconMedium)
                )
            },
            shape = RoundedCornerShape(spacing.radiusMedium),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(spacing.small))
        IconButton(
            onClick = {},
            modifier = Modifier
                .size(spacing.buttonHeight)
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
        WorkshopFilter.entries.forEach { filter ->
            FilterChip(
                selected = filter == selectedFilter,
                onClick = { onSelectFilter(filter) },
                label = { Text(filter.label, style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryBlue,
                    selectedLabelColor = Color.White
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
                drawCircle(PrimaryBlue, radius = 14f, center = Offset(w * 0.45f, h * 0.50f))
                drawCircle(PrimaryBlue, radius = 28f, center = Offset(w * 0.45f, h * 0.50f), style = Stroke(width = 3f))
                drawCircle(ObdTeal, radius = 12f, center = Offset(w * 0.25f, h * 0.30f))
                drawCircle(ShiftIQWarning, radius = 12f, center = Offset(w * 0.75f, h * 0.65f))
            }

            // GPS Pill
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(spacing.smallMedium)
                    .clip(RoundedCornerShape(spacing.radiusFull))
                    .background(Color.White.copy(alpha = 0.95f))
                    .padding(horizontal = spacing.small, vertical = spacing.micro),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(spacing.indicatorHeight)
                        .clip(CircleShape)
                        .background(ObdTeal)
                )
                Spacer(modifier = Modifier.width(spacing.extraSmall))
                Text(
                    text = "GPS EN VIVO",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Slate700
                )
            }

            // Map Controls Right
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(spacing.smallMedium),
                verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)
            ) {
                Box(
                    modifier = Modifier
                        .size(spacing.controlButtonSize)
                        .clip(RoundedCornerShape(spacing.radiusSmall))
                        .background(Color.White)
                        .clickable {},
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", fontWeight = FontWeight.Bold, color = Slate700, style = MaterialTheme.typography.labelMedium)
                }
                Box(
                    modifier = Modifier
                        .size(spacing.controlButtonSize)
                        .clip(RoundedCornerShape(spacing.radiusSmall))
                        .background(Color.White)
                        .clickable {},
                    contentAlignment = Alignment.Center
                ) {
                    Text("−", fontWeight = FontWeight.Bold, color = Slate700, style = MaterialTheme.typography.labelMedium)
                }
                Box(
                    modifier = Modifier
                        .size(spacing.controlButtonSize)
                        .clip(RoundedCornerShape(spacing.radiusSmall))
                        .background(Color.White)
                        .clickable {},
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.MyLocation, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(spacing.iconSmall))
                }
            }

            // Pins overlay
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = spacing.extraExtraLarge)
                    .clip(RoundedCornerShape(spacing.radiusSmall))
                    .background(Navy900)
                    .padding(horizontal = spacing.small, vertical = spacing.micro)
            ) {
                Text(text = "Silva (1.2 km)", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun WorkshopsCatalogListHeader(
    count: Int,
    onNavigateToMyAppointments: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Talleres Recomendados",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )
            Spacer(modifier = Modifier.width(spacing.small))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(spacing.radiusExtraSmall))
                    .background(Slate200)
                    .padding(horizontal = spacing.extraSmall, vertical = spacing.micro)
            ) {
                Text(
                    text = "$count Cercanos",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate700
                )
            }
        }

        Row(
            modifier = Modifier.clickable(onClick = onNavigateToMyAppointments),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mis Citas",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
            Spacer(modifier = Modifier.width(spacing.micro))
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(spacing.iconSmall)
            )
        }
    }
}

@Composable
private fun WorkshopsCatalogList(
    workshops: List<WorkshopItem>,
    onNavigateToBooking: (String) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        workshops.forEach { workshop ->
            WorkshopCardItem(
                workshop = workshop,
                onBook = { onNavigateToBooking(workshop.id) },
                onViewProfile = { onNavigateToDetail(workshop.id) }
            )
        }
    }
}

@Composable
private fun WorkshopCardItem(
    workshop: WorkshopItem,
    onBook: () -> Unit,
    onViewProfile: () -> Unit,
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
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            WorkshopCardHeader(workshop = workshop)
            WorkshopTrustBadgesRow(isCertified = workshop.isCertifiedShiftIQ, hasFreeBays = workshop.hasFreeBaysToday)
            if (!workshop.linkedIssue.isNullOrBlank()) {
                DtcAlignmentBanner(issue = workshop.linkedIssue)
            }
            WorkshopCardPricingRow(
                minPen = workshop.estimatedCostMinPen,
                maxPen = workshop.estimatedCostMaxPen,
                slot = workshop.nextAvailableSlot
            )
            WorkshopCardActions(
                onBook = onBook,
                onViewProfile = onViewProfile
            )
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
        verticalAlignment = Alignment.Top
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(spacing.avatarMedium)
                    .clip(RoundedCornerShape(spacing.radiusMedium))
                    .background(Slate100),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(spacing.iconLarge)
                )
            }
            Spacer(modifier = Modifier.width(spacing.smallMedium))
            Column {
                Text(
                    text = workshop.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "★ ${workshop.rating}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ShiftIQWarning
                    )
                    Spacer(modifier = Modifier.width(spacing.extraSmall))
                    Text(
                        text = "(${workshop.reviewCount} reseñas) • ${workshop.district}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate500
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Slate500,
                        modifier = Modifier.size(spacing.iconSmall)
                    )
                    Spacer(modifier = Modifier.width(spacing.micro))
                    Text(
                        text = "A ${workshop.distanceKm} km de tu ubicación",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate500
                    )
                }
            }
        }

        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.BookmarkBorder,
                contentDescription = "Favorito",
                tint = Slate500,
                modifier = Modifier.size(spacing.iconLarge)
            )
        }
    }
}

@Composable
private fun WorkshopTrustBadgesRow(
    isCertified: Boolean,
    hasFreeBays: Boolean,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        if (isCertified) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(spacing.radiusSmall))
                    .background(PrimaryBlue.copy(alpha = 0.1f))
                    .padding(horizontal = spacing.small, vertical = spacing.micro)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(spacing.iconSmall))
                    Spacer(modifier = Modifier.width(spacing.extraSmall))
                    Text("Certificado ShiftIq", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                }
            }
        }
        if (hasFreeBays) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(spacing.radiusSmall))
                    .background(Slate100)
                    .padding(horizontal = spacing.small, vertical = spacing.micro)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ShiftIQSuccess, modifier = Modifier.size(spacing.iconSmall))
                    Spacer(modifier = Modifier.width(spacing.extraSmall))
                    Text("Bahías libres hoy", style = MaterialTheme.typography.labelSmall, color = Slate700)
                }
            }
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
            .background(Slate100)
            .padding(spacing.small),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(spacing.controlButtonSize)
                    .clip(RoundedCornerShape(spacing.radiusSmall))
                    .background(Navy900),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color.White, modifier = Modifier.size(spacing.iconMedium))
            }
            Spacer(modifier = Modifier.width(spacing.small))
            Column {
                Text(
                    text = "VINCULADO A FALLA DTC",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Slate500
                )
                Text(
                    text = issue,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
            }
        }
        Text(
            text = "S/ 140 - S/ 180 ESTIMADO",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue
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
            Text(text = "Presupuesto Estimado", style = MaterialTheme.typography.labelSmall, color = Slate500)
            Text(text = "S/ ${minPen.toInt()} - S/ ${maxPen.toInt()} PEN", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Slate900)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = "Próximo Turno Libre", style = MaterialTheme.typography.labelSmall, color = Slate500)
            Text(text = slot, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        }
    }
}

@Composable
private fun WorkshopCardActions(
    onBook: () -> Unit,
    onViewProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        OutlinedButton(
            onClick = onViewProfile,
            modifier = Modifier.weight(1f).height(spacing.buttonHeight),
            border = BorderStroke(spacing.strokeThin, PrimaryBlue),
            shape = RoundedCornerShape(spacing.radiusMedium)
        ) {
            Text("Ver Perfil", color = PrimaryBlue, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
        Button(
            onClick = onBook,
            modifier = Modifier.weight(1f).height(spacing.buttonHeight),
            shape = RoundedCornerShape(spacing.radiusMedium),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = Color.White)
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
            onNavigateToBooking = {},
            onNavigateToDetail = {},
            onNavigateToMyAppointments = {}
        )
    }
}
