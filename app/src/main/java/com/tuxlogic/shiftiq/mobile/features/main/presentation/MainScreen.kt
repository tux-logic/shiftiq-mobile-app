package com.tuxlogic.shiftiq.mobile.features.main.presentation

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.build
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.directionsCar
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.home
import com.tuxlogic.shiftiq.mobile.core.designsystem.icon.warningAmber
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.PrimaryBlue
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate200
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate400
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate500
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.Slate900
import com.tuxlogic.shiftiq.mobile.features.home.presentation.HomeScreen
import com.tuxlogic.shiftiq.mobile.features.main.presentation.screens.DiagnosticsScreen
import com.tuxlogic.shiftiq.mobile.features.main.presentation.screens.GarageScreen
import com.tuxlogic.shiftiq.mobile.features.main.presentation.screens.WorkshopsScreen

enum class MainTab(
    val title: String,
    val icon: ImageVector
) {
    HOME("Inicio", home),
    DIAGNOSTICS("Diagnóstico", warningAmber),
    WORKSHOPS("Talleres", build),
    GARAGE("Mi Garaje", directionsCar)
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    var currentTab by rememberSaveable { mutableStateOf(MainTab.HOME) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        contentWindowInsets = WindowInsets.statusBars,
        bottomBar = {
            NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                MainTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBlue,
                            selectedTextColor = PrimaryBlue,
                            indicatorColor = Color(0xFFEFF6FF),
                            unselectedIconColor = Slate400,
                            unselectedTextColor = Slate500
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentTab, label = "MainTabTransition") { tab ->
                when (tab) {
                    MainTab.HOME -> HomeScreen(
                        onNavigateToDiagnostics = { currentTab = MainTab.DIAGNOSTICS },
                        onNavigateToWorkshops = { currentTab = MainTab.WORKSHOPS }
                    )
                    MainTab.DIAGNOSTICS -> DiagnosticsScreen()
                    MainTab.WORKSHOPS -> WorkshopsScreen()
                    MainTab.GARAGE -> GarageScreen()
                }
            }
        }
    }
}
