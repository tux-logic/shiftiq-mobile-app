package com.tuxlogic.shiftiq.mobile.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.tuxlogic.shiftiq.mobile.features.auth.presentation.navigation.authNavGraph
import com.tuxlogic.shiftiq.mobile.features.main.presentation.navigation.mainNavGraph
import com.tuxlogic.shiftiq.mobile.features.onboarding.presentation.navigation.OnboardingNavGraphRoute
import com.tuxlogic.shiftiq.mobile.features.onboarding.presentation.navigation.onboardingNavGraph

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = OnboardingNavGraphRoute
    ) {
        onboardingNavGraph(navController)
        authNavGraph(navController)
        mainNavGraph(navController)
    }
}
