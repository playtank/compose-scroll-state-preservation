package com.example.composescrolldemo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.composescrolldemo.presentation.hoteldetail.HotelDetailScreen
import com.example.composescrolldemo.presentation.hotellist.HotelListScreen

/**
 * Root [NavHost] wiring together all destinations.
 *
 * Each composable destination defines a unique back-stack entry. When navigating
 * to [HotelDetail], the [HotelList] entry stays on the back-stack while its
 * Composable is destroyed — which triggers the scroll-reset behavior.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HotelList.route,
        modifier = modifier,
    ) {

        composable(route = NavRoutes.HotelList.route) {
            HotelListScreen(
                onNavigateToDetail = { hotelId ->
                    navController.navigate(NavRoutes.HotelDetail.createRoute(hotelId))
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
