package com.tuxlogic.shiftiq.mobile.features.auth.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.serialization.Serializable

@Serializable
data object AuthNavGraphRoute

@Serializable
data object SignInRoute

@Serializable
data object SignUpRoute

@Serializable
data object WorkshopRegisterRoute

fun NavGraphBuilder.authNavGraph(navController: NavController) {
    navigation<AuthNavGraphRoute>(startDestination = SignInRoute) {
        composable<SignInRoute> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Pantalla de Inicio de Sesión B2C")
            }
        }
        composable<SignUpRoute> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Pantalla de Registro B2C")
            }
        }
        composable<WorkshopRegisterRoute> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Portal de Registro de Taller B2B")
            }
        }
    }
}
