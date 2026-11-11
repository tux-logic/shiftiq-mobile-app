package com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.b2c

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIQSurfaceLight
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate400
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.diagnostic.DiagnosticScreen
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.garage.GarageScreen
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.home.DriverHomeScreen
import com.tuxlogic.shiftiq.mobile.feature.fleet.presentation.workshops.WorkshopsScreen

enum class B2CTab(val title: String, val icon: ImageVector) {
    HOME("Inicio", Icons.Default.Home),
    DIAGNOSTIC("Diagnóstico", Icons.Default.Build),
    WORKSHOPS("Talleres", Icons.Default.Place),
    GARAGE("Mi Garaje", Icons.Default.DirectionsCar)
}

@Composable
fun MainB2CScreen(
    onNavigateToLogin: () -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = ShiftIQSurfaceLight,
                tonalElevation = 8.dp
            ) {
                B2CTab.values().forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(imageVector = tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBlue,
                            selectedTextColor = PrimaryBlue,
                            indicatorColor = PrimaryBlue.copy(alpha = 0.12f),
                            unselectedIconColor = Slate400,
                            unselectedTextColor = Slate400
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            when (selectedTab) {
                0 -> DriverHomeScreen(
                    onNavigateToWorkshops = { selectedTab = 2 },
                    onNavigateToDiagnostic = { selectedTab = 1 },
                    onNavigateToGarage = { selectedTab = 3 },
                    onNavigateToProfile = onNavigateToLogin
                )
                1 -> DiagnosticScreen(
                    onNavigateToWorkshops = { selectedTab = 2 }
                )
                2 -> WorkshopsScreen()
                3 -> GarageScreen(
                    onAddVehicle = {},
                    onNavigateToProfile = onNavigateToLogin
                )
            }
        }
    }
}
