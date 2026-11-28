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
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQBottomNavBar
import com.tuxlogic.shiftiq.mobile.core.designsystem.components.ShiftIQNavItemData
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
    ShiftIQBottomNavBar(
        selectedTab = selectedIndex,
        onTabSelected = { index -> onNavigate(tabs[index].destination.route) },
        items = tabs.map { tab ->
            ShiftIQNavItemData(
                title = tab.label,
                icon = tab.icon,
                selectedIcon = tab.selectedIcon
            )
        }
    )
}

private fun roleTabs(role: Role?): List<RoleTab> = buildList {
    add(RoleTab(AppDestination.Dashboard, "Panel", Icons.Outlined.Dashboard, Icons.Filled.Dashboard))
    add(RoleTab(AppDestination.WorkOrders, "Órdenes", Icons.Outlined.Build, Icons.Filled.Build))
    if (role == Role.ROLE_OWNER || role == Role.ROLE_ADMIN) {
        add(RoleTab(AppDestination.Appointments, "Citas", Icons.Outlined.CalendarMonth, Icons.Filled.CalendarMonth))
        add(RoleTab(AppDestination.Inventory, "Inventario", Icons.Outlined.Inventory2, Icons.Filled.Inventory2))
    }
    if (role != Role.ROLE_USER) {
        add(RoleTab(AppDestination.Telemetry, "Telemetría", Icons.Outlined.Sensors, Icons.Filled.Sensors))
    }
}
