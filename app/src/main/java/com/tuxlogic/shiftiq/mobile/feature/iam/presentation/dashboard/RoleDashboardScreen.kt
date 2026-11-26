package com.tuxlogic.shiftiq.mobile.feature.iam.presentation.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import com.tuxlogic.shiftiq.mobile.core.localization.LocalizedText as Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPageHeader
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQPrimaryButton
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQBorderSlate
import androidx.compose.ui.graphics.Color
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQSecondaryButton
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQTheme
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.model.Role
import com.tuxlogic.shiftiq.mobile.core.model.UserId

@Composable
fun RoleDashboardScreen(
    userRole: Role?,
    activeBranchId: BranchId?,
    userId: UserId?,
    onLogoutClick: () -> Unit,
    onNavigateToWorkshops: () -> Unit = {},
    onNavigateToOwnerProfile: () -> Unit = {},
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToStaff: () -> Unit = {},
    onNavigateToInventory: () -> Unit = {},
    onNavigateToCustomerVehicles: () -> Unit = {},
    onNavigateToCustomerDashboard: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ShiftIQPageHeader(
            title = "¡Sesión Iniciada!",
            subtitle = userRole?.displayName ?: "Panel B2B ShiftIQ"
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, ShiftIQBorderSlate),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Información de Sesión Activa",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )

                        InfoRow(label = "Rol del Sistema", value = userRole?.name ?: "N/A")
                        InfoRow(label = "ID de Usuario", value = userId?.toString()?.take(8) ?: "N/A")
                        InfoRow(
                            label = "Sucursal Activa",
                            value = activeBranchId?.let { "Sede ${it.toString().take(8)}..." } ?: "Sin sucursal asignada"
                        )
                        InfoRow(label = "Estado del Token", value = "JWT Válido (Bearer)")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Hito 1: IAM completado",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Autenticación, persistencia en DataStore y control de acceso basados en rol listos para los siguientes bloques de negocio.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ShiftIQPrimaryButton(
                    text = "Gestionar Talleres y Sedes",
                    onClick = onNavigateToWorkshops,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                androidx.compose.material3.OutlinedButton(
                    onClick = onNavigateToOwnerProfile,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Ver / Editar Mi Perfil de Dueño")
                }

                Spacer(modifier = Modifier.height(10.dp))

                ShiftIQPrimaryButton(
                    text = "Agenda y Citas (Fleet)",
                    onClick = onNavigateToAppointments,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                androidx.compose.material3.OutlinedButton(
                    onClick = onNavigateToStaff,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Equipo y Personal de Taller (Fleet)")
                }

                Spacer(modifier = Modifier.height(10.dp))

                ShiftIQPrimaryButton(
                    text = "Inventario y Productos",
                    onClick = onNavigateToInventory,
                    modifier = Modifier.fillMaxWidth()
                )

                if (userRole == Role.ROLE_USER) {
                    Spacer(modifier = Modifier.height(10.dp))

                    ShiftIQPrimaryButton(
                        text = "Driver Dashboard B2C (Salud & Talleres)",
                        onClick = onNavigateToCustomerDashboard,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ShiftIQPrimaryButton(
                        text = "Mis vehículos y diagnóstico IoT",
                        onClick = onNavigateToCustomerVehicles,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            ShiftIQSecondaryButton(
                text = "Cerrar Sesión",
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Preview(name = "Role Dashboard Preview", showBackground = true)
@Composable
private fun RoleDashboardPreview() {
    ShiftIQTheme {
        RoleDashboardScreen(
            userRole = Role.ROLE_OWNER,
            activeBranchId = BranchId("b1000000-0000-0000-0000-000000000001"),
            userId = UserId("u1000000-0000-0000-0000-000000000001"),
            onLogoutClick = {}
        )
    }
}
