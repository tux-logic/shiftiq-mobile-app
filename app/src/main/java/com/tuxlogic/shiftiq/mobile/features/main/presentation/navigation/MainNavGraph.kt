package com.tuxlogic.shiftiq.mobile.features.main.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tuxlogic.shiftiq.mobile.features.main.presentation.MainScreen
import kotlinx.serialization.Serializable

@Serializable
data object MainRoute

fun NavGraphBuilder.mainNavGraph(navController: NavController) {
    composable<MainRoute> {
        MainScreen()
    }
}
