package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPrimaryButton
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQTextField
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.feature.fleet.domain.model.EmployeeRegistration
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffManagementScreen(
    uiState: StaffManagementUiState,
    onTabSelected: (Int) -> Unit,
    onRefresh: () -> Unit,
    onApprove: (UUID) -> Unit,
    onReject: (UUID) -> Unit,
    onOpenAddDialog: () -> Unit,
    onCloseAddDialog: () -> Unit,
    onAddEmployeeIdChanged: (String) -> Unit,
    onAddSpecialityChanged: (String) -> Unit,
    onAddSalaryChanged: (String) -> Unit,
    onAddRoleChanged: (String) -> Unit,
    onSubmitAddStaff: () -> Unit,
    onNavigateBack: () -> Unit,
    onClearMessages: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessages()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Equipo y Personal",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = uiState.activeBranchId?.let { "Sede: ${it.toString().take(8)}..." } ?: "Todas las sedes",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Recargar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenAddDialog,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Registrar Personal")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = { onTabSelected(0) },
                    text = { Text("Activos (${uiState.activeRegistrations.size})") }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { onTabSelected(1) },
                    text = { Text("Solicitudes (${uiState.pendingRegistrations.size})") }
                )
            }

            val currentList = if (uiState.selectedTab == 0) {
                uiState.activeRegistrations
            } else {
                uiState.pendingRegistrations
            }

            if (uiState.isLoading && uiState.registrations.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (currentList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (uiState.selectedTab == 0) Icons.Default.Engineering else Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (uiState.selectedTab == 0) "Sin personal activo" else "Sin solicitudes pendientes",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (uiState.selectedTab == 0) {
                                "Registra miembros de tu equipo con el botón '+' inferior."
                            } else {
                                "Las postulaciones de técnicos aparecerán aquí para tu aprobación."
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(currentList, key = { it.id }) { item ->
                        StaffCard(
                            registration = item,
                            onApprove = { onApprove(item.id) },
                            onReject = { onReject(item.id) }
                        )
                    }
                }
            }
        }
    }

    if (uiState.showAddDialog) {
        AlertDialog(
            onDismissRequest = onCloseAddDialog,
            title = {
                Text(
                    text = "Registrar Personal",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Asigna un empleado a esta sede especificando su especialidad y salario.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    ShiftIQTextField(
                        value = uiState.addEmployeeId,
                        onValueChange = onAddEmployeeIdChanged,
                        label = "ID Empleado (UUID)",
                        placeholder = "e.g. 550e8400-e29b-41d4-a716-446655440000"
                    )

                    ShiftIQTextField(
                        value = uiState.addSpeciality,
                        onValueChange = onAddSpecialityChanged,
                        label = "Especialidad",
                        placeholder = "e.g. Mecánica General, Frenos"
                    )

                    ShiftIQTextField(
                        value = uiState.addSalary,
                        onValueChange = onAddSalaryChanged,
                        label = "Salario Mensual (S/.)",
                        placeholder = "2500.0"
                    )

                    ShiftIQTextField(
                        value = uiState.addRole,
                        onValueChange = onAddRoleChanged,
                        label = "Rol en Taller",
                        placeholder = "ROLE_TECHNICIAN"
                    )
                }
            },
            confirmButton = {
                ShiftIQPrimaryButton(
                    text = "Registrar",
                    onClick = onSubmitAddStaff,
                    isLoading = uiState.isLoading
                )
            },
            dismissButton = {
                TextButton(onClick = onCloseAddDialog) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun StaffCard(
    registration: EmployeeRegistration,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Engineering,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = registration.specialityName ?: registration.speciality,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Empleado: ${registration.employeeId.toString().take(8)}...",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                StaffStatusPill(status = registration.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Rol",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = registration.role ?: "ROLE_TECHNICIAN",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Salario",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = "S/. ${"%.2f".format(registration.salary)}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            if (registration.isPending) {
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rechazar")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    ShiftIQPrimaryButton(
                        text = "Aprobar",
                        onClick = onApprove
                    )
                }
            }
        }
    }
}

@Composable
fun StaffStatusPill(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when {
        status.equals("ACTIVE", ignoreCase = true) -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "Activo")
        status.equals("PENDING_APPROVAL", ignoreCase = true) -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), "Pendiente")
        else -> Triple(Color(0xFFEEEEEE), Color(0xFF616161), status)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = bgColor
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )
    }
}

@Preview(name = "Staff Management - Active Preview", showBackground = true)
@Composable
private fun StaffManagementActivePreview() {
    ShiftIQTheme {
        StaffManagementScreen(
            uiState = StaffManagementUiState(
                selectedTab = 0,
                activeRegistrations = listOf(
                    EmployeeRegistration(
                        id = UUID.randomUUID(),
                        employeeId = UUID.randomUUID(),
                        branchId = UUID.randomUUID(),
                        speciality = "Mecánica General",
                        specialityName = "Mecánica General",
                        salary = 2800.0,
                        role = "ROLE_TECHNICIAN",
                        status = "ACTIVE"
                    )
                ),
                activeBranchId = UUID.randomUUID()
            ),
            onTabSelected = {},
            onRefresh = {},
            onApprove = {},
            onReject = {},
            onOpenAddDialog = {},
            onCloseAddDialog = {},
            onAddEmployeeIdChanged = {},
            onAddSpecialityChanged = {},
            onAddSalaryChanged = {},
            onAddRoleChanged = {},
            onSubmitAddStaff = {},
            onNavigateBack = {},
            onClearMessages = {}
        )
    }
}

@Preview(name = "Staff Management - Pending Preview", showBackground = true)
@Composable
private fun StaffManagementPendingPreview() {
    ShiftIQTheme {
        StaffManagementScreen(
            uiState = StaffManagementUiState(
                selectedTab = 1,
                pendingRegistrations = listOf(
                    EmployeeRegistration(
                        id = UUID.randomUUID(),
                        employeeId = UUID.randomUUID(),
                        branchId = UUID.randomUUID(),
                        speciality = "Diagnóstico Electrónico",
                        specialityName = "Diagnóstico Electrónico",
                        salary = 3200.0,
                        role = "ROLE_TECHNICIAN",
                        status = "PENDING_APPROVAL"
                    )
                ),
                activeBranchId = UUID.randomUUID()
            ),
            onTabSelected = {},
            onRefresh = {},
            onApprove = {},
            onReject = {},
            onOpenAddDialog = {},
            onCloseAddDialog = {},
            onAddEmployeeIdChanged = {},
            onAddSpecialityChanged = {},
            onAddSalaryChanged = {},
            onAddRoleChanged = {},
            onSubmitAddStaff = {},
            onNavigateBack = {},
            onClearMessages = {}
        )
    }
}

@Preview(name = "Staff Card Preview", showBackground = true)
@Composable
private fun StaffCardPreview() {
    ShiftIQTheme {
        StaffCard(
            registration = EmployeeRegistration(
                id = UUID.randomUUID(),
                employeeId = UUID.randomUUID(),
                branchId = UUID.randomUUID(),
                speciality = "Frenos y Suspensión",
                specialityName = "Frenos y Suspensión",
                salary = 2500.0,
                role = "ROLE_TECHNICIAN",
                status = "ACTIVE"
            ),
            onApprove = {},
            onReject = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
