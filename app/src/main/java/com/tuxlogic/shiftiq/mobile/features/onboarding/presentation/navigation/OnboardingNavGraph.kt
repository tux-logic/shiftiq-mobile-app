package com.tuxlogic.shiftiq.mobile.features.onboarding.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import com.tuxlogic.shiftiq.mobile.features.auth.presentation.navigation.AuthNavGraphRoute
import com.tuxlogic.shiftiq.mobile.features.auth.presentation.navigation.LoginRoute
import com.tuxlogic.shiftiq.mobile.features.auth.presentation.navigation.WorkshopRegisterRoute
import com.tuxlogic.shiftiq.mobile.features.onboarding.presentation.OnboardingScreen

@Serializable
data object OnboardingNavGraphRoute

@Serializable
data object OnboardingRoute

fun NavGraphBuilder.onboardingNavGraph(navController: NavController) {
    navigation<OnboardingNavGraphRoute>(startDestination = OnboardingRoute) {
        composable<OnboardingRoute> {
            OnboardingScreen(
                onDriverEmailSignInClick = {
                    navController.navigate(LoginRoute)
                },
                onDriverPhoneSignInClick = {
                    navController.navigate(LoginRoute)
                },
                onDriverAuthSuccess = {
                    navController.navigate(AuthNavGraphRoute) {
                        popUpTo<OnboardingRoute> { inclusive = true }
                    }
                }
            )
        }
    }
}
