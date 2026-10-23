package com.tuxlogic.shiftiq.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.tuxlogic.shiftiq.mobile.core.designsystem.theme.ShiftIqTheme
import com.tuxlogic.shiftiq.mobile.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            ShiftIqTheme {
                AppNavHost(navController = navController)
            }
        }
    }
}
