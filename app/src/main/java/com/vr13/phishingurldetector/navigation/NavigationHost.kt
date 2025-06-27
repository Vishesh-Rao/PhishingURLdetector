package com.vr13.phishingurldetector.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vr13.phishingurldetector.mainscreen.HomeScreen
import com.vr13.phishingurldetector.splashscreen.SplashScreen
import com.vr13.phishingurldetector.Routes.NavRoutes

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash
    ) {
        composable(NavRoutes.Splash) {
            SplashScreen(navController = navController)
        }
        composable(NavRoutes.Home) {
            HomeScreen(navController = navController)
        }
    }
}
