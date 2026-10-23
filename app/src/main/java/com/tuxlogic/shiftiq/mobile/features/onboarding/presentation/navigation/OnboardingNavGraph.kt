package com.tuxlogic.shiftiq.mobile.features.onboarding.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable
import com.tuxlogic.shiftiq.mobile.features.auth.presentation.navigation.AuthNavGraphRoute
import com.tuxlogic.shiftiq.mobile.features.auth.presentation.navigation.SignInRoute
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
                    navController.navigate(SignInRoute)
                },
                onDriverPhoneSignInClick = {
                    navController.navigate(SignInRoute)
                },
                onDriverAuthSuccess = {
                    navController.navigate(AuthNavGraphRoute) {
                        popUpTo<OnboardingRoute> { inclusive = true }
                    }
                },
                onRegisterWorkshopClick = {
                    navController.navigate(WorkshopRegisterRoute)
                },
                onWorkshopSignInClick = {
                    navController.navigate(SignInRoute)
                }
            )
        }
    }
}
