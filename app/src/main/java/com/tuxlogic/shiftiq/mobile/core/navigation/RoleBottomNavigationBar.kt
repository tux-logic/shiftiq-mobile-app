package com.tuxlogic.shiftiq.mobile.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.tuxlogic.shiftiq.mobile.core.model.Role

private data class RoleTab(
    val destination: AppDestination,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)

fun roleTabRoutes(role: Role?): Set<String> = roleTabs(role).mapTo(mutableSetOf()) { it.destination.route }

@Composable
fun RoleBottomNavigationBar(
    role: Role?,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val tabs = roleTabs(role)
    val selectedIndex = tabs.indexOfFirst { it.destination.route == currentRoute }.coerceAtLeast(0)
    val colorScheme = MaterialTheme.colorScheme

    NavigationBar(
        containerColor = colorScheme.surface,
        contentColor = colorScheme.onSurface,
        tonalElevation = androidx.compose.material3.NavigationBarDefaults.Elevation
    ) {
        tabs.forEachIndexed { index, tab ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onNavigate(tab.destination.route) },
                icon = {
                    androidx.compose.material3.Icon(
                        imageVector = if (selectedIndex == index) tab.selectedIcon else tab.icon,
                        contentDescription = tab.label
                    )
                },
                label = { androidx.compose.material3.Text(tab.label) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = colorScheme.primaryContainer.copy(alpha = 80f / 255f),
                    selectedIconColor = colorScheme.primary,
                    selectedTextColor = colorScheme.onSurface,
                    unselectedIconColor = colorScheme.onSurfaceVariant,
                    unselectedTextColor = colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

private fun roleTabs(role: Role?): List<RoleTab> = buildList {
    add(RoleTab(AppDestination.Dashboard, "Panel", Icons.Outlined.Dashboard, Icons.Filled.Dashboard))
    add(RoleTab(AppDestination.WorkOrders, "Órdenes", Icons.Outlined.Build, Icons.Filled.Build))
    if (role != Role.ROLE_USER) {
        add(RoleTab(AppDestination.Appointments, "Citas", Icons.Outlined.CalendarMonth, Icons.Filled.CalendarMonth))
        add(RoleTab(AppDestination.Inventory, "Inventario", Icons.Outlined.Inventory2, Icons.Filled.Inventory2))
        add(RoleTab(AppDestination.Telemetry, "Telemetría", Icons.Outlined.Sensors, Icons.Filled.Sensors))
    }
}
