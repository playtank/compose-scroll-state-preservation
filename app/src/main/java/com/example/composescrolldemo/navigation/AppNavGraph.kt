package com.example.composescrolldemo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.composescrolldemo.presentation.home.HomeScreen
import com.example.composescrolldemo.presentation.hoteldetail.HotelDetailScreen
import com.example.composescrolldemo.presentation.hotellist.HotelListScreen

/**
 * Root [NavHost] wiring together all destinations.
 *
 * The app starts at [Home], which lists all available demos as buttons.
 * Each demo feature owns its own set of routes and back-stack entries.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Home.route,
        modifier = modifier,
    ) {

        composable(route = NavRoutes.Home.route) {
            HomeScreen(
                onNavigateToComposeScroll = {
                    navController.navigate(NavRoutes.HotelList.route)
                },
            )
        }

        composable(route = NavRoutes.HotelList.route) {
            HotelListScreen(
                onNavigateToDetail = { hotelId ->
                    navController.navigate(NavRoutes.HotelDetail.createRoute(hotelId))
                },
                // Pop back to Home explicitly so rapid double-taps can't empty the back stack.
                onNavigateBack = {
                    navController.popBackStack(
                        route = NavRoutes.Home.route,
                        inclusive = false,
                    )
                },
            )
        }

        composable(
            route = NavRoutes.HotelDetail.route,
            arguments = listOf(
                navArgument(NavRoutes.HotelDetail.ARG_HOTEL_ID) {
                    type = NavType.IntType
                }
            ),
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments
                ?.getInt(NavRoutes.HotelDetail.ARG_HOTEL_ID)
                ?: return@composable

            HotelDetailScreen(
                hotelId = hotelId,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
